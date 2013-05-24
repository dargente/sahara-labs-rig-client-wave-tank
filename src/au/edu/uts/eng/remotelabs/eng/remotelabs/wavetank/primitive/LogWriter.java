package au.edu.uts.eng.remotelabs.eng.remotelabs.wavetank.primitive;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
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
    
    /** Time Stamp Calculation */
    private long startTime;
    private Date date;
	
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
	 		logFile = File.createTempFile("datalog", ".tmp");
	 		out = new BufferedWriter(new FileWriter(logFile));
	 	}
	 	catch (IOException e)
	 	{
	 		this.logger.error(e.getClass().getSimpleName() + ". LogWriter cannot create a temporary log file.");
	 		return;
	 	}


			try
			{
				dataGrabber = dataGrabberClass.newInstance();
			} 
			catch (InstantiationException e)
			{
				logger.error(e.getClass().getSimpleName() + " was thrown. DataGrabber class cannot be instantiated.");
			} 
			catch (IllegalAccessException e)
			{
				logger.error(e.getClass().getSimpleName() + " was thrown. DataGrabber class unaccessible.");
			}
			
			this.writeHeader();

	}

	/** Retrieves and writes a log entry **/
	private boolean writeEntry()
	{
		try
		{
			out.newLine();
			
			/* Write timestamp */
			double logTime;
			date = new Date();
			logTime = ((date.getTime() - startTime)/ 1000.0);
			
			out.write(logTime + "\t");
			out.write(dataGrabber.getLine());
		} 
		catch (IOException e)
		{
			this.logger.error("IOException when attempting to write new entry to log file.");
			return false;
		}
		
		return true;

	}
	
	/** Writes the header to the log file **/
	private void writeHeader()
	{
		try
		{
			out.write("Time \t");
			out.write(dataGrabber.getHeading());
		} 
		catch (IOException e)
		{
			this.logger.error("IOException when attempting to write header to log file.");
		}

	}
	
	/** Shuts down the logging thread and starts file saving **/
	public void shutdown()
	{
		this.stop = true;
		
		try
		{
			out.close();
		}
		catch(IOException e)
		{
			this.logger.warn("IOException while attempting to close BufferedWriter.");
		}
		
		try 
		{
			/* Wait for the last log entry to bit written */
			this.scheduler.awaitTermination(500, TimeUnit.MILLISECONDS);
		} 
		catch (InterruptedException e) 
		{
			this.logger.error("LogWriter thread was interrupted before shutdown.");
		}
		LogSaver saver = new LogSaver();
		saver.saveFile(logFile);
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
	public synchronized boolean startLog()
	{
		scheduler.scheduleWithFixedDelay(this, 1, 1, TimeUnit.SECONDS);
		date = new Date();
		this.startTime = date.getTime();
		return true;
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
