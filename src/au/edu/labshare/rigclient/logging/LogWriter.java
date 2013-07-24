package au.edu.labshare.rigclient.logging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import au.edu.uts.eng.remotelabs.rigclient.util.ConfigFactory;
import au.edu.uts.eng.remotelabs.rigclient.util.IConfig;
import au.edu.uts.eng.remotelabs.rigclient.util.ILogger;
import au.edu.uts.eng.remotelabs.rigclient.util.LoggerFactory;

/**
 * Handles the writing of log files by running a thread
 * to retrieve data periodically.
 */
public class LogWriter implements Runnable {
	
	/** Scheduled Executor Parameters **/
	private static final long INITIAL_DELAY = 0;
	private static final long LOG_PERIOD = 100;
	private static final TimeUnit TIME_UNITS = TimeUnit.MILLISECONDS;
	
    /** Thread Scheduler **/
    private ScheduledExecutorService scheduler;

	/** Control flags **/
	private boolean pause;
	private boolean stop;
    
    /** Temporary Log File **/
    private File logFile;
    private File tempAddress;
    private Boolean toLock;
    
    private OutputStreamWriter out;
    private FileOutputStream fOut;
    
    private IDataGrabber dataGrabber;
    
    
    /** Time Stamp Calculation */
    private double entryCount;
	
	/** Logger **/
    private ILogger logger;
	
    /** Constructor **/
	public LogWriter(Class<? extends IDataGrabber> dataGrabberClass)
	{
        this.logger = LoggerFactory.getLoggerInstance();
		this.pause = false;
		this.stop = false;
		
		/* Load config details */
		IConfig conf = ConfigFactory.getInstance();
		tempAddress = new File(conf.getProperty("Log_Temp_Address"));
		
		if(tempAddress == null)
		{
			this.logger.warn("The Temp Log Address has not been configured. Using the default directory.");
		}
		
		toLock = Boolean.parseBoolean(conf.getProperty("Temp_File_Lock"));
		
		if(toLock == null)
		{
			this.logger.warn("The Temp Log Address has not been configured. Using the default directory.");
			toLock = true;
		}
		
		
		/* Create single thread "pool" */
	 	scheduler = Executors.newSingleThreadScheduledExecutor();
		
		/* Setup output stream */
	 	try
	 	{
	 		logFile = File.createTempFile("datalog", ".tmp", tempAddress);
	 		
	 		/* Check if write permission */
	 		if(!logFile.canWrite())
	 		{
	 			this.logger.error("Rig Client does not have permission to write to the temp directory.");
	 		}
	 		
	 		fOut = new FileOutputStream(logFile);
	 		if(toLock)
	 		{
	 			FileChannel fileChannel;
	 			fileChannel = fOut.getChannel();
	 			fileChannel.lock();
	 		}
	 		out = new OutputStreamWriter(fOut);
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

	/** 
	 * Retrieves and writes a log entry 
	 * @return true if no exception was thrown
	 **/
	private boolean writeEntry()
	{
		try
		{	
			/* Write timestamp */
			double timeStamp = ((LOG_PERIOD / 1000.0) * entryCount);
			out.write(timeStamp + ",");
			out.write(dataGrabber.getLine() + "\r\n");
			entryCount++;
		} 
		catch (IOException e)
		{
			this.logger.error(e.getClass().getSimpleName() + " when attempting to write new entry to log file.");
			entryCount++;
			return false;
		}
		return true;
	}
	
	/** 
	 * Writes the header to the log file
	 * @return true if no exception was thrown 
	 **/
	private boolean writeHeader()
	{
		try
		{
			out.write("Time" + ",");
			out.write(dataGrabber.getHeading() + "\r\n");
		} 
		catch (IOException e)
		{
			this.logger.error(e.getClass().getSimpleName() + " when attempting to write header to log file.");
			return false;
		}
		return true;
	}
	
	/** Shuts down the logging thread and starts file saving **/
	public boolean shutdown()
	{
		this.stop = true;
		
		try
		{
			out.close();
		}
		catch(IOException e)
		{
			this.logger.warn(e.getClass().getSimpleName() + " while attempting to close OutputStreamWriter.");
			return false;
		}
		
		if(!scheduler.isShutdown())
		{
			try 
			{
				/* Wait for the last log entry to bit written */
				this.scheduler.awaitTermination(500, TimeUnit.MILLISECONDS);
			} 
			catch (InterruptedException e) 
			{
				this.logger.error("LogWriter thread was interrupted before shutdown.");
				return false;
			}
		}
		LogSaver saver = new LogSaver();
		saver.saveFile(logFile);
		
		return true;
	}
	

	/**
	 * A thread to grab log data periodically, unless paused or stopped.
	 */
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
	
	
	/** 
	 * Creates the logging thread that runs periodically according to LOG_PERIOD
	 * and TIME_UNITs.
	 **/
	public synchronized boolean startLog()
	{
		scheduler.scheduleWithFixedDelay(this, INITIAL_DELAY, LOG_PERIOD, TIME_UNITS);
		entryCount = 0;
		return true;
	}
	
	
	/** 
	 * Sets a flag to resume the log writing 
	 **/
	public synchronized boolean resumeLog()
	{
		this.pause = false;
		return true;
	}
	
	
	/** 
	 * Sets a flag to pause the log writing 
	 **/
	public synchronized boolean pauseLog()
	{
		this.pause = true;
		return true;
	}
}
