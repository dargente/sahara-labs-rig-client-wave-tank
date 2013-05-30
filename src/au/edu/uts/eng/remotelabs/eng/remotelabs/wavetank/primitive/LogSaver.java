/**
 * Rig Client Data Logging 
 *
 * @author Dominic Argente (dargente)
 * @date 14th May 2013
 */
package au.edu.uts.eng.remotelabs.eng.remotelabs.wavetank.primitive;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


import au.edu.uts.eng.remotelabs.rigclient.util.ILogger;
import au.edu.uts.eng.remotelabs.rigclient.util.LoggerFactory;

/** 
 * Handles the file saving options for the LogWriter.
 * This implementation will create a new logfile.
 */
public class LogSaver {
	
	private static final String FILE_ADDRESS = "";
	private static final String LOG_NAME = "WaveTankLog";
	private static final int MAX_NUM_FILES = 10;
	private int i;

	
	/** Logger **/
    private ILogger logger;
    
	public LogSaver()
	{
        this.logger = LoggerFactory.getLoggerInstance();
	}
	
	/**
	 * Takes the temporary log file from LogWriter and writes it to FILE_ADDRESS
	 * with name LOG_NAME.
	 * @return 
	 */
	public boolean saveFile(File logFile)
	{
		File saveFile = new File(LOG_NAME + i + ".log");
		boolean doesExist = false;
		for(i = 0; !doesExist; i++)
		{
			doesExist = logFile.renameTo(saveFile = new File(FILE_ADDRESS + LOG_NAME + i + ".log"));
		}
		
		this.zipFile(saveFile.getAbsolutePath());
		
		this.logger.info("Logfile saved at " + saveFile.getAbsolutePath());
		
		
		return true;
	}
	
	/**
	 * Takes the saved file and places it into a zip archive then deletes
	 * the initial saved file. For single files.
	 *  
	 */
	private void zipFile(String address)
	{	
		try
		{
		BufferedInputStream buff = new BufferedInputStream(new FileInputStream(address));
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(LOG_NAME + i + ".zip"));
		out.setLevel(9);
		
		out.putNextEntry(new ZipEntry(LOG_NAME + i + ".txt"));
		
			byte[] b = new byte[2048];
			
		int count;
		
		while ((count = buff.read(b)) > 0) 
		{
			out.write(b, 0, count);
		}
		out.close();
		buff.close();
		}
		catch (Throwable e)
		{
			this.logger.error(e.getClass().getSimpleName() + "exception class was thrown."
					+ e.getMessage() + "during zipFile.");
		}

	}

}
