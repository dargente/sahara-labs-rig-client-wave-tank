/**
 * UTS Remote Labs Wave Tank Rig Client. 
 *
 * @author Michael Diponio (mdiponio)
 * @date 9th Janurary 2012
 */

package au.edu.uts.eng.remotelabs.wavetank.primitive;

import java.io.IOException;

import au.edu.uts.eng.remotelabs.rigclient.rig.IRigControl.PrimitiveRequest;
import au.edu.uts.eng.remotelabs.rigclient.rig.IRigControl.PrimitiveResponse;
import au.edu.uts.eng.remotelabs.rigclient.rig.primitive.IPrimitiveController;
import au.edu.uts.eng.remotelabs.rigclient.util.ILogger;
import au.edu.uts.eng.remotelabs.rigclient.util.LoggerFactory;
import au.edu.uts.eng.remotelabs.wavetank.CRIOHandler;

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
    private CRIOHandler crioHandler;

    /** Logger. */
    private final ILogger logger;
    
    public WaveTankController()
    {
        this.logger = LoggerFactory.getLoggerInstance();
    }

    @Override
    public boolean initController()
    {
    	/* Call getInstance */
    	crioHandler = CRIOHandler.getInstance();
    	if(crioHandler == null)
    	{
    		this.logger.warn("Could not retrieve CRIOHandler instance. Failing Wave Tank controller initialisation.");
    		return false;
    	}
    	return true;
    }
    
    @Override
    public boolean preRoute()
    {
    	if(!crioHandler.isConnected())
    	{
    		this.logger.warn("Connection to CRIO failed. Failing action method in preRoute.");
    		return false;
    	}
    	return true;
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
    	response.setResults(this.crioHandler.getData());
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
    	this.crioHandler.crioTCP.enablePump("true".equals(request.getParameters().get("on")));
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
    	this.crioHandler.crioTCP.enableInverter("true".equals(request.getParameters().get("on")));
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
    	this.crioHandler.crioTCP.setSpeed(Double.parseDouble(request.getParameters().get("speed")));
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
    		this.crioHandler.crioTCP.setDigitalOutput(chan, on);
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
    		this.crioHandler.crioTCP.setAnalogOutput(chan, val);
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
    	/* Call crio.lease() */
    }
}
