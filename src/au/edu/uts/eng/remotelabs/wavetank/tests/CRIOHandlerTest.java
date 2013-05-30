package au.edu.uts.eng.remotelabs.wavetank.tests;

import static org.easymock.EasyMock.createMockBuilder;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.IOException;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;



import au.edu.uts.eng.remotelabs.wavetank.CRIOHandler;
import au.edu.uts.eng.remotelabs.wavetank.CRIOTcp;

public class CRIOHandlerTest
{

	CRIOHandler crioHandler;
	
	CRIOTcp crioTCP;
	CRIOTcp crioTCP2;
	
    @Before
	public void setUp()
    {
    	crioTCP = createMockBuilder(CRIOTcp.class)
    			.addMockedMethod("connect")
    			.addMockedMethod("bufferData")
    			.createNiceMock();
    	
    	try
		{
			expect(crioTCP.connect()).andReturn(true).atLeastOnce();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
    	
    	try
		{
			crioTCP.bufferData();
	    	expectLastCall().anyTimes();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		replay(crioTCP);
    }

	
	public void threadTest()
	{
		//crioTCP2 = CRIOHandler.acquire(crioTCP);
		assertEquals(crioTCP, crioTCP2);
		try
		{
			Thread.sleep(10000);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		CRIOHandler.lease();
		try
		{
			Thread.sleep(5000);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		verify(crioTCP);
	}
	
	@Test
	public void multiThreadTest()
	{
		//crioTCP2 = CRIOHandler.acquire(crioTCP);
		System.out.println("Acquire 1.");
		//crioTCP2 = CRIOHandler.acquire(crioTCP);
		System.out.println("Acquire 2.");
		try
		{
			Thread.sleep(10000);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		CRIOHandler.lease();
		System.out.println("Lease 1.");
		try
		{
			Thread.sleep(10000);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		CRIOHandler.lease();
		System.out.println("Lease 2.");
		try
		{
			Thread.sleep(10000);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		CRIOHandler.lease();
		System.out.println("Lease 3.");
		CRIOHandler.lease();
		CRIOHandler.lease();
		
		//crioTCP2 = CRIOHandler.acquire();
		System.out.println("Acquire again.");
		try
		{
			Thread.sleep(10000);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		verify(crioTCP);
	}


}
