/**
 * UTS Remote Labs Wave Tank Rig Client. 
 *
 * @author Dominic Argente (dargente)
 * @date 30th April 2013
 */
package au.edu.uts.eng.remotelabs.wavetank;

import java.io.IOException;

import au.edu.uts.eng.remotelabs.rigclient.util.ConfigFactory;
import au.edu.uts.eng.remotelabs.rigclient.util.IConfig;
import au.edu.uts.eng.remotelabs.rigclient.util.ILogger;
import au.edu.uts.eng.remotelabs.rigclient.util.LoggerFactory;

/**
 * Handles the communication requests between the CRIO and the
 * Rig Client. Implements a singleton pattern.
 */

public class CRIOHandler implements Runnable
{

	// Singleton Instance
	private static CRIOHandler instance = null;
	private static CRIOTcp crioTCP;
	
	public static int acquireCount = 0;
	
	private ILogger logger;
	
	private static Thread crioThread;
	
	/* Private Constructor for Singleton */
	private CRIOHandler() {
		
	}
	
	/**
	 * Initialisation of the CRIOHandler thread.
	 * Loads config files, opens comms with the CRIO, begins the CRIOHandler thread and
	 * zeros the outputs to the CRIO. 
	 * @return
	 */
	private boolean init() {
		
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
		crioTCP = new CRIOTcp(ip, port);
		
		try
		{
			if(!crioTCP.connect())
			{
				this.logger.warn("Failed to connect to the cRIO server at " + ip + ':' + port + ", failing Wave Tank initialisation.");
				return false;
			}
			
			if(!crioTCP.login(username, password))
			{
				this.logger.warn("Failed to login to the cRIO server using " + username + ' ' + password + ", failing Wave Tank initialisation.");
				return false;
			}

			/* Run Thread */
			crioThread = new Thread(this);
			crioThread.start();
		}
		
		catch(IOException e)
		{
			this.logger.warn("Failed communicating with Wave Tank, exception: " +
						e.getClass().getSimpleName() + ", message: " + e.getMessage());
			return false;
		}
		
		return true;
	}
	
    /** The cRIO expects regular communication from a client. Failure to 
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
				crioTCP.bufferData();
                Thread.sleep(100);
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
	}
	
	/**
	 * When acquiring an instance of CRIOTcp, ensure that the CRIOHandler thread is active.
	 * @return crioTCP
	 */
	public static synchronized CRIOTcp acquire()
	{
		if(acquireCount == 0)
		{
			instance = new CRIOHandler();
			instance.init();
		}
		acquireCount++;
		return crioTCP;
	}
	
	/**
	 * When all controllers are finished using the CRIO, shutdown the CRIOhandler thread.
	 */
	public static synchronized void lease()
	{
		if(acquireCount > 0)
		{
			acquireCount--;
			
			if(acquireCount == 0)
			{
				crioThread.interrupt();
			}
		}

	}
	
}
