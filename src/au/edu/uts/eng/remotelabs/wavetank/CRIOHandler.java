/**
 * UTS Remote Labs Wave Tank Rig Client. 
 *
 * @author Dominic Argente (dargente)
 * @date 30th April 2013
 */
package au.edu.uts.eng.remotelabs.wavetank;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import au.edu.uts.eng.remotelabs.rigclient.rig.IRigControl.PrimitiveRequest;
import au.edu.uts.eng.remotelabs.rigclient.rig.IRigControl.PrimitiveResponse;
import au.edu.uts.eng.remotelabs.rigclient.util.ConfigFactory;
import au.edu.uts.eng.remotelabs.rigclient.util.IConfig;
import au.edu.uts.eng.remotelabs.rigclient.util.ILogger;
import au.edu.uts.eng.remotelabs.rigclient.util.LoggerFactory;
import au.edu.uts.eng.remotelabs.wavetank.primitive.WaveTankController;

/**
 * Handles the communication requests between the CRIO and the
 * Rig Client. Implements a singleton pattern.
 */

public class CRIOHandler implements Runnable
{

	// Singleton Instance
	private static CRIOHandler instance = null;
	public CRIOTcp crioTCP;
	
	private static int acquireCount = 0;
	private static int leaseCount = 0;
	
    /** Number of output channels for digital. */
    private static final int DIGITAL_OUTPUT_CHANS = 8;
    
    /** Number of output channels for analog. */
    private static final int ANALOG_OUTPUT_CHANS = 8;
	
	private ILogger logger;
	
	private Thread crioThread;
	
	/* Private Constructor for Singleton */
	private CRIOHandler() {
		
	}
	
	private boolean init() {
		/* 1. Load Config file
		 * 2. create new CRIOTcp
		 * 3. Run 500 millisecond thread
		 * 4. Zero the analogue/digital inputs
		 */
		
		this.logger = LoggerFactory.getLoggerInstance();
		
		/* Load config details */
		IConfig conf = ConfigFactory.getInstance();
		String ip = conf.getProperty("Wave_Tank_IP");
		
		if(ip == null)
		{
			this.logger.error("The Wave Tank IP address has not been configured, failing Wave Tank initialisation.");
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
		
		/* Create connection with CRIO */
		this.crioTCP = new CRIOTcp(ip, port);
		
		try
		{
			if(!this.crioTCP.connect())
			{
				this.logger.warn("Failed to connect to the cRIO server at " + ip + ':' + port + ", failing Wave Tank initialisation.");
			}

			/* Run Thread */
			this.crioThread = new Thread(new CRIOHandler());
			this.crioThread.start();
			
			/* Zero Outputs */
			for (int c = 0; c <= ANALOG_OUTPUT_CHANS; c++) this.crioTCP.setAnalogOutput(c, 0);
			for (int c = 0; c <= DIGITAL_OUTPUT_CHANS; c++) this.crioTCP.setDigitalOutput(c, false);
		}
		catch(IOException e)
		{
			this.logger.warn("Failed communicating with Wave Tank, exception: " +
						e.getClass().getSimpleName() + ", message: " + e.getMessage());
			return false;
		}
		
		return true;
	}
	
    /* The cRIO expects regular communication from a client. Failure to 
     * communicate within 5 seconds will cause the cRIO to disconnect and
     * close the TCP stream. To stop this we are using a thread to pull
     * data every 500 milliseconds. */
	@Override
	public void run() 
	{

		try
		{
			while (!Thread.interrupted())
			{
				this.crioTCP.bufferData();
                Thread.sleep(500);
            }
        }
        catch (IOException e)
        {
        	this.logger.warn("Failed communicating with Wave Tank, exception: " + 
                             e.getClass().getSimpleName() + ", message: " + e.getMessage());
        }
        catch (InterruptedException e)
        {
                      /* Shutting down. */         
        }
		this.crioThread.start();
	}
	
	/**
	 *  Grabs data that has been fed to the CRIO. Returns it in a hashmap. 
	 *  
	 *  @return data
	 *  */
	public Map<String, String> getData()
	{
		Map<String, String> data = new HashMap<String, String>();
		
		/* Insert data into map */
		data.put("pump", String.valueOf(this.crioTCP.getPump()));
		data.put("inverter", String.valueOf(this.crioTCP.getInverter()));
		data.put("paddle", String.valueOf(this.crioTCP.getPaddle()));
		data.put("speed", String.valueOf(this.crioTCP.getSpeed()));
		data.put("ain", Arrays.toString(this.crioTCP.getAnalogInputs()));
		data.put("din", Arrays.toString(this.crioTCP.getDigitalInputs()));
		data.put("aout", Arrays.toString(this.crioTCP.getAnalogOutputs()));
		data.put("dout",  Arrays.toString(this.crioTCP.getDigitalOutputs()));
		
		return data;
	}
	
	public boolean isConnected()
	{
		return crioTCP.isConnected();
	}
	
	
	/* For the singleton pattern. Returns the instance of CRIOHandler.
	 * Will create the instance if it has not been created already.
	 * Double checked. */
	public static CRIOHandler getInstance() 
	{
		CRIOHandler r = instance;
		if (r == null)
		{
			synchronized(CRIOHandler.class)
			{
				r = instance;
				if(r == null)
				{
					r = new CRIOHandler();
					r.init();
					instance = r;
				}
			}
		}
		 return r;
	 }
}
