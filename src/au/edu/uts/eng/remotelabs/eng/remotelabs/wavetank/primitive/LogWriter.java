package au.edu.uts.eng.remotelabs.eng.remotelabs.wavetank.primitive;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import au.edu.uts.eng.remotelabs.rigclient.util.ILogger;
import au.edu.uts.eng.remotelabs.rigclient.util.LoggerFactory;

public class LogWriter implements Runnable {
	
    /** Thread Scheduler **/
    private ScheduledExecutorService scheduler;

	/** Control flags **/
	private boolean pause;
	private boolean stop;
    
    /** Temporary Log File **/
    private File logFile;
    
    private BufferedWriter out;
    
    private IDataGrabber dataGrabber;
	
	/** Logger **/
    private ILogger logger;
	
    /** Constructor **/
	public LogWriter(Class<? extends IDataGrabber> dataGrabberClass)
	{
        this.logger = LoggerFactory.getLoggerInstance();
		this.pause = false;
		this.stop = false;
		
		/* Create single thread "pool" */
	 	scheduler = Executors.newSingleThreadScheduledExecutor();
		
		/* Setup output stream */
	 	try
	 	{
	 		BufferedWriter out = new BufferedWriter(new FileWriter(logFile));
	 	}
	 	catch (IOException e)
	 	{
	 		this.logger.error("LogWriter cannot create a temporary log file.");
	 		return;
	 	}
		this.writeHeader();

			try
			{
				dataGrabber = dataGrabberClass.newInstance();
			} 
			catch (InstantiationException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

	/** Retrieves and writes a log entry **/
	private void writeEntry()
	{
		out.newLine();
		out.write(dataGrabber.getLine());
	}
	
	/** Writes the header to the log file **/
	private void writeHeader()
	{
		out.write(dataGrabber.getHeading());
	}
	
	/** Shuts down the logging thread and starts file saving **/
	public void shutdown()
	{
		this.stop = true;
		try 
		{
			this.scheduler.awaitTermination(2, TimeUnit.SECONDS);
		} 
		catch (InterruptedException e) 
		{
			this.logger.error("LogWriter thread did not terminate in time.");
			e.printStackTrace();
		}
	}
	

	@Override
	public void run() 
	{
		if(!pause)
		{
			this.writeEntry();
		}
		
		if(stop)
		{
			this.scheduler.shutdown();
		}
	}
	
	
	/** Sets up a ScheduledExecutorService thread to run the logs **/
	public synchronized void startLog()
	{
		scheduler.scheduleWithFixedDelay(this, 1, 1, TimeUnit.SECONDS);
	}
	
	
	/** Sets a flag to resume the log writing **/
	public synchronized void resumeLog()
	{
		this.pause = false;
	}
	
	
	/** Sets a flag to pause the log writing **/
	public synchronized void pauseLog()
	{
		this.pause = true;
	}
}
