package au.edu.labshare.rigclient.logging.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;

import au.edu.labshare.rigclient.logging.LogWriter;
import au.edu.uts.eng.remotelabs.wavetank.WaveTankDataGrabber;

public class LogWriterTest
{
	LogWriter logWriter;
	WaveTankDataGrabber dataGrabber;
	
	@Before
	public void setUp()
	{		
		dataGrabber = createMockBuilder(WaveTankDataGrabber.class)
					.addMockedMethod("getHeading")
					.addMockedMethod("getLine")
					.createMock();
		expect(dataGrabber.getHeading()).andReturn("Marco").anyTimes();
		expect(dataGrabber.getLine()).andReturn("Polo").anyTimes();
		
		
	}

	@Test
	public void startStoptest()
	{
		replay(dataGrabber);
		logWriter = new LogWriter(WaveTankDataGrabber.class);
		assertTrue(logWriter.startLog());
		try
		{
			Thread.sleep(5000);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(logWriter.shutdown());
	}

	

	public void Pausetest()
	{
		replay(dataGrabber);
		logWriter = new LogWriter(WaveTankDataGrabber.class);
		assertTrue(logWriter.startLog());
		try
		{
			Thread.sleep(5000);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(logWriter.pauseLog());
		try
		{
			Thread.sleep(10000);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(logWriter.shutdown());
	}
	

	public void PauseResumetest()
	{
		replay(dataGrabber);
		logWriter = new LogWriter(WaveTankDataGrabber.class);
		assertTrue(logWriter.startLog());
		try
		{
			Thread.sleep(10000);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertTrue(logWriter.pauseLog());
		try
		{
			Thread.sleep(10000);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertTrue(logWriter.resumeLog());
		try
		{
			Thread.sleep(10000);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertTrue(logWriter.shutdown());
	}
	
}
