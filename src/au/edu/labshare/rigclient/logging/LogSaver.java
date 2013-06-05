/**
 * Rig Client Data Logging 
 *
 * @author Dominic Argente (dargente)
 * @date 14th May 2013
 */
package au.edu.labshare.rigclient.logging;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


import au.edu.uts.eng.remotelabs.rigclient.util.ConfigFactory;
import au.edu.uts.eng.remotelabs.rigclient.util.IConfig;
import au.edu.uts.eng.remotelabs.rigclient.util.ILogger;
import au.edu.uts.eng.remotelabs.rigclient.util.LoggerFactory;

/** 
 * Handles the file saving options for the LogWriter.
 * This implementation will create a new logfile.
 */
public class LogSaver {
	
	private String fileAddress;
	private String logName;
	private int i;

	
	/** Logger **/
    private ILogger logger;
    
	public LogSaver()
	{
        this.logger = LoggerFactory.getLoggerInstance();
        IConfig conf = ConfigFactory.getInstance();
        
        fileAddress = conf.getProperty("File_Address");
        logName = conf.getProperty("Log_Name");
	}
	
	/**
	 * Takes the temporary log file from LogWriter and writes it to FILE_ADDRESS
	 * with name LOG_NAME.
	 * @return 
	 */
	public boolean saveFile(File logFile)
	{
		File saveFile = new File(logName + i + ".log");
		boolean doesExist = false;
		for(i = 0; !doesExist; i++)
		{
			doesExist = logFile.renameTo(saveFile = new File(fileAddress + logName + i + ".log"));
		}
		
		if(!saveFile.canWrite())
		{
			this.logger.warn("No write permission for data logger file at " + saveFile.getAbsolutePath());
			return false;
		}
		//this.zipFile(saveFile);
		
		this.logger.info("Logfile saved at " + saveFile.getAbsolutePath());
		
		return true;
	}
	
	/**
	 * Takes the saved file and places it into a zip archive then deletes
	 * the initial saved file. For single files.
	 *  
	 */
	private void zipFile(File address)
	{	
		try
		{
		BufferedInputStream buff = new BufferedInputStream(new FileInputStream(address));
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(logName + i + ".zip"));
		out.setLevel(9);
		
		out.putNextEntry(new ZipEntry(logName + i + ".log"));
		
			byte[] b = new byte[2048];
			
		int count;
		
		while ((count = buff.read(b)) > 0) 
		{
			out.write(b, 0, count);
		}
		out.close();
		buff.close();
		address.delete();
		}
		catch (Throwable e)
		{
			this.logger.error(e.getClass().getSimpleName() + "exception class was thrown."
					+ e.getMessage() + "during zipFile.");
		}

	}

}
