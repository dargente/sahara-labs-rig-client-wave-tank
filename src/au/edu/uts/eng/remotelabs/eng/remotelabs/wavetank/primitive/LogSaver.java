/**
 * Rig Client Data Logging 
 *
 * @author Dominic Argente (dargente)
 * @date 14th May 2013
 */
package au.edu.uts.eng.remotelabs.eng.remotelabs.wavetank.primitive;

import java.io.File;


import au.edu.uts.eng.remotelabs.rigclient.util.ILogger;
import au.edu.uts.eng.remotelabs.rigclient.util.LoggerFactory;

/** 
 * Handles the file saving options for the LogWriter.
 * This implementation will create a new logfile.
 */
public class LogSaver {
	
	private static final String FILE_ADDRESS = "";
	private static final String LOG_NAME = "WaveTankLog";

	
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
		int i;
		boolean doesExist = false;
		for(i = 0; !doesExist; i++)
		{
			doesExist = logFile.renameTo(new File(FILE_ADDRESS + LOG_NAME + i + ".log"));
		}
		
		return true;
	}

}
