/**
 * UTS Remote Labs Wave Tank Rig Client. 
 *
 * @author Dominic Argente (dargente)
 * @date 30th April 2013
 */
package au.edu.uts.eng.remotelabs.wavetank;

import java.io.IOException;

import au.edu.uts.eng.remotelabs.wavetank.primitive.WaveTankController;

/**
 * Handles the communication requests between the CRIO and the
 * Rig Client. Implements a singleton pattern.
 */

public class CRIOHandler implements Runnable
{

	// Singleton Instance
	private static CRIOHandler instance = null;
	
	private int acquireCount = 0;
	private int leaseCount = 0;
	
	/* Private Constructor for Singleton */
	private CRIOHandler() {
		
		/* 1. Load Config file
		 * 2. create new CRIOTcp
		 * 3. Run 500 millisecond thread
		 * 4. Zero the analogue/digital inputs
		 */
	}
	
    /* The cRIO expects regular communication from a client. Failure to 
     * communicate within 5 seconds will cause the cRIO to disconnect and
     * close the TCP stream. To stop this we are using a thread to pull
     * data every 500 milliseconds. */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		/* 
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
                      Shutting down.
                 }
	
             }
         });
         this.crioThread.start();
		*/
		
	}
	
	/* For the singleton pattern. Returns the instance of CRIOHandler.
	 * Will create the instance if it has not been created already. */
	public static synchronized CRIOHandler getInstance() 
	{
		if(instance == null)
		{
			instance = new CRIOHandler();
		}
		
	}
	
	
}
	
	
}
