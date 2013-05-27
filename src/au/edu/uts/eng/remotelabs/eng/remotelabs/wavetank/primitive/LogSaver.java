/**
 * Rig Client Data Logging 
 *
 * @author Dominic Argente (dargente)
 * @date 14th May 2013
 */
package au.edu.uts.eng.remotelabs.eng.remotelabs.wavetank.primitive;

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
	
	private static final String FILE_ADDRESS = "C:\\temp\\";
	private static final String LOG_NAME = "WaveTankLog";
	private static final String ZIP_NAME = "WaveTankLog";
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
		boolean exists = false;
		for(i = 0; !exists; i++)
		{
			exists = logFile.renameTo(new File(FILE_ADDRESS + LOG_NAME + i + ".log"));
		}
		return true;
	}
	
	/**
	 * Takes the saved file and places it into a zip archive then deletes
	 * the initial saved file. For single files.
	 *  
	 */
	private void zipFile()
	{	
			try
			{
				FileOutputStream fos = new FileOutputStream(FILE_ADDRESS + ZIP_NAME + ".zip");
				ZipOutputStream zos = new ZipOutputStream(fos);
				FileInputStream fin = new FileInputStream(FILE_ADDRESS + LOG_NAME + i + ".log");
				
				int count;
				byte[] buffer = new byte[1024];
				
				while ((count = fin.read(buffer)) > 0) {
					zos.write(buffer, 0, count);
				}
				
				zos.putNextEntry(new ZipEntry(FILE_ADDRESS + LOG_NAME + i + ".log"));
				zos.close();
				fin.close();
				File file = new File(FILE_ADDRESS + LOG_NAME + i + ".log");
				file.delete();
			} 
			catch (FileNotFoundException e)
			{
				this.logger.error("File not found by zipFile.");
				return;
			}
			catch (IOException e)
			{
				this.logger.error("IOException in zipFile.");
			}

	}
	
}
