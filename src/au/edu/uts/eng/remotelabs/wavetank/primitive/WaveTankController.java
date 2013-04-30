/**
 * UTS Remote Labs Wave Tank Rig Client. 
 *
 * @author Michael Diponio (mdiponio)
 * @date 9th Janurary 2012
 */

package au.edu.uts.eng.remotelabs.wavetank.primitive;

import java.io.IOException;
import java.util.Arrays;

import au.edu.labshare.rigclient.action.detect.TickleActivityDetector;
import au.edu.uts.eng.remotelabs.rigclient.rig.IRigControl.PrimitiveRequest;
import au.edu.uts.eng.remotelabs.rigclient.rig.IRigControl.PrimitiveResponse;
import au.edu.uts.eng.remotelabs.rigclient.rig.primitive.IPrimitiveController;
import au.edu.uts.eng.remotelabs.rigclient.util.ConfigFactory;
import au.edu.uts.eng.remotelabs.rigclient.util.IConfig;
import au.edu.uts.eng.remotelabs.rigclient.util.ILogger;
import au.edu.uts.eng.remotelabs.rigclient.util.LoggerFactory;
import au.edu.uts.eng.remotelabs.wavetank.CRIOTcp;

/**
 * Controller for the Wave Tank controller.
 */
public class WaveTankController implements IPrimitiveController
{
    /** Number of output channels for digital. */
    private static final int DIGITAL_OUTPUT_CHANS = 8;
    
    /** Number of output channels for analog. */
    private static final int ANALOG_OUTPUT_CHANS = 8;
    
    /** Interface to cRIO. */
    private CRIOTcp crio;
    
    /** Thread to automatically pull data from the cRIO. */ 
    private Thread crioThread;
    
    /** Whether the pump is on. */
    private boolean pump;
    
    /** Whether the inverter is on. */
    private boolean inverter;
    
    /** The speed the paddle is oscillating. */
    private double paddle;
    
    /** The digital output states. */
    private final boolean[] digitalOutputs;
    
    /** The currently set analog output values. */
    private final double[] analogOutputs;
    
    /** Logger. */
    private final ILogger logger;
    
    public WaveTankController()
    {
        this.logger = LoggerFactory.getLoggerInstance();
        
        this.digitalOutputs = new boolean[DIGITAL_OUTPUT_CHANS];
        this.analogOutputs = new double[ANALOG_OUTPUT_CHANS];
    }

    @Override
    public boolean initController()
    {
        /* Configuration for connecting to Wave Tank. */
        IConfig conf = ConfigFactory.getInstance();
        String ip = conf.getProperty("Wave_Tank_IP");
        if (ip == null)
        {
            this.logger.error("The Wave Tank IP address has not been configured, failing Wave Tank initalisation.");
            return false;
        }
        
        int port;
        try
        {
           port = Integer.parseInt(conf.getProperty("Wave_Tank_Port", "2055"));
        }
        catch (NumberFormatException ex)
        {
            this.logger.error("The Wave Tank port number is not valid, failing Wave Tank initialisation.");
            return false;
        }
        
        String username = conf.getProperty("Wave_Tank_Username");
        if (username == null)
        {
            this.logger.error("The Wave Tank username has not been configured, failing Wave Tank initialisation.");
            return false;
        }
        
        String password = conf.getProperty("Wave_Tank_Password");
        if (password == null)
        {
            this.logger.error("The Wave Tank password has not been configured, failing Wave Tank initialisation.");
            return false;
        }
     
        this.crio = new CRIOTcp(ip, port);
        try
        {
            /* Connect and login to the cRIO. */
            if (!this.crio.connect())
            {
                this.logger.warn("Failed to connect to the cRIO server at " + ip + ':' + port + ", failing Wave Tank initialisation.");
            }
            
            if (!this.crio.login(username, password))
            {
                this.logger.warn("Failed to login to the cRIO server, failing Wave Tank initialisation.");
            }
            
            /* The cRIO expects regular communication from a client. Failure to 
             * communicate within 5 seconds will cause the cRIO to disconnect and
             * close the TCP stream. To stop this we are using a thread to pull
             * data every 500 milliseconds. */
            this.crioThread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        while (!Thread.interrupted())
                        {   
                            WaveTankController.this.crio.bufferData();
                            Thread.sleep(500);
                        }
                    }
                    catch (IOException e)
                    {
                        WaveTankController.this.logger.warn("Failed communicating with Wave Tank, exception: " + 
                                e.getClass().getSimpleName() + ", message: " + e.getMessage());
                    }
                    catch (InterruptedException e)
                    {
                        /* Shutting down. */
                    }
                }
            });
            this.crioThread.start();
            
            /* Zero all analogue and digital outputs. */
            for (int c = 0; c < ANALOG_OUTPUT_CHANS; c++) this.crio.setAnalogOutput(c, this.analogOutputs[c] = 0);
            for (int c = 0; c < DIGITAL_OUTPUT_CHANS; c++) this.crio.setDigitalOutput(c, this.digitalOutputs[c] = false);
        }
        catch (IOException e)
        {
            WaveTankController.this.logger.warn("Failed communicating with Wave Tank, exception: " + 
                    e.getClass().getSimpleName() + ", message: " + e.getMessage());
            return false;
        }
        
        return true;
    }
    
    @Override
    public boolean preRoute()
    {
        /* Update tickle to stop the user being removed from non-activity. */
        TickleActivityDetector.tickle();
        
        return this.crio.isConnected();
    }
    
    /**
     * Obtains data about the wave tank. No parameters are required.
     * 
     * @param request 
     * @return response
     */
    public PrimitiveResponse dataAction(PrimitiveRequest request)
    {
        PrimitiveResponse response = new PrimitiveResponse();
        response.setSuccessful(true);
        
        response.addResult("pump", String.valueOf(this.pump));
        response.addResult("inverter", String.valueOf(this.inverter));
        response.addResult("paddle", String.valueOf(this.paddle));
        
        /* Inputs. */
        response.addResult("din", Arrays.toString(this.crio.getDigitalInputs()));
        response.addResult("ain", Arrays.toString(this.crio.getAnalogueInputs()));
        
        /* Outputs. */
        response.addResult("dout", Arrays.toString(this.digitalOutputs));
        response.addResult("aout", Arrays.toString(this.analogOutputs));
        
        return response;
    }
    
    /**
     * Sets the pump on or off. The parameter 'on' is required and must have
     * a value of 'true' or 'false'.
     * 
     * @param request
     * @return response
     * @throws IOException 
     */
    public PrimitiveResponse setPumpAction(PrimitiveRequest request) throws IOException
    {
        this.crio.enablePump(this.pump = "true".equals(request.getParameters().get("on")));
        return this.dataAction(request);
    }
    
    /**
     * Sets the inverter on or off. The parameter 'on' is required and must have
     * a value of 'true' or 'false'.
     * 
     * @param request
     * @return response
     * @throws IOException
     */
    public PrimitiveResponse setInverterAction(PrimitiveRequest request) throws IOException
    {
        this.crio.enableInverter(this.inverter = "true".equals(request.getParameters().get("on")));
        return this.dataAction(request);
    }
    
    /**
     * Sets the paddle speed in Hertz. The parameter 'speed' is required and 
     * must have a double value.
     * 
     * @param request
     * @return response
     * @throws IOException
     */
    public PrimitiveResponse setPaddleAction(PrimitiveRequest request) throws IOException
    {
        this.crio.setSpeed(this.paddle = Double.parseDouble(request.getParameters().get("speed")));
        return this.dataAction(request);
    }
    
    /**
     * Sets a digital output channel. The parameters 'chan' specifying channel  
     * number and 'val' specifying 'true' or 'false'.
     * 
     * @param request
     * @return response
     * @throws IOException
     */
    public PrimitiveResponse setDigitalOutputAction(PrimitiveRequest request) throws IOException
    {
        int chan = Integer.parseInt(request.getParameters().get("chan"));
        boolean on = "true".equals(request.getParameters().get("val"));
        
        if (chan < DIGITAL_OUTPUT_CHANS) 
        {
            this.crio.setDigitalOutput(chan, on);
            this.digitalOutputs[chan] = on;
        }
            
        return this.dataAction(request);
    }
    
    /**
     * Sets an analogue output channel. The parameters 'chan' specifying channel 
     * number and 'val' specifying the analogue value.
     * 
     * @param request
     * @return response
     * @throws IOException
     */
    public PrimitiveResponse setAnalogOutputAction(PrimitiveRequest request) throws IOException
    {
        int chan = Integer.parseInt(request.getParameters().get("chan"));
        double val = Double.parseDouble(request.getParameters().get("val"));
                
        if (chan < ANALOG_OUTPUT_CHANS)
        {
            this.crio.setAnalogOutput(chan, val);
            this.analogOutputs[chan] = val;
        }
        
        return this.dataAction(request);
    }

    @Override
    public boolean postRoute()
    {
        return true;
    }

    @Override
    public void cleanup()
    {
        /* Stops the data buffering. */
        this.crioThread.interrupt();
        
        try
        {
            /* Clean up experiment. */
            this.crio.setSpeed(0);
            this.crio.enableInverter(false);
            this.crio.enablePump(false);
            
            for (int c = 0; c < DIGITAL_OUTPUT_CHANS; c++) this.crio.setDigitalOutput(c, false);
            for (int c = 0; c < ANALOG_OUTPUT_CHANS; c++) this.crio.setAnalogOutput(c, 0);
            
            /* Disconnect from cRIO. */
            this.crio.disconnect();
        }
        catch (IOException e)
        {
           this.logger.warn("Failed disconnecting from Wave Tank, exception: " + e.getClass().getSimpleName() + 
                   ", message: " + e.getMessage());
        }
    }
}
