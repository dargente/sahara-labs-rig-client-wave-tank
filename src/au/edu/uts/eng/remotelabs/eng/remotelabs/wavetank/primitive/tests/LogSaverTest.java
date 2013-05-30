package au.edu.uts.eng.remotelabs.eng.remotelabs.wavetank.primitive.tests;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import au.edu.uts.eng.remotelabs.eng.remotelabs.wavetank.primitive.LogSaver;

public class LogSaverTest
{
		private LogSaver logSave;
		private File logFile;
		private BufferedWriter out;
	    private Date date;
	    private long startTime;
	    private double logTime;
	    
	@Before
	public void setUp()
	{
		try
		{
		logFile = File.createTempFile("datalog", ".tmp");
		System.out.println("Temp file : " + logFile.getAbsolutePath());
 		out = new BufferedWriter(new FileWriter(logFile));
		date = new Date();
		this.startTime = date.getTime();
		}
		catch (Throwable e)
		{}
		
	}

	@Test(timeout=10000)
	public void test()
	{
		try
		{
			out.write("Test \t");

			logTime = ((date.getTime() - startTime)/ 1000.0);
			out.write(logTime + "\t");
			out.close();
			//Thread.sleep(10000);
		}
		catch (Throwable e)
		{
			System.out.println("EXCEPTION!");
		}
		
		logSave = new LogSaver();
		
		assertTrue(logSave.saveFile(logFile));
		
	}

}
