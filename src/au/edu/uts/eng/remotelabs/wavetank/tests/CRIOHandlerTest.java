package au.edu.uts.eng.remotelabs.wavetank.tests;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import au.edu.uts.eng.remotelabs.wavetank.CRIOHandler;
import au.edu.uts.eng.remotelabs.wavetank.CRIOTcp;

public class CRIOHandlerTest
{

	CRIOHandler crioHandler;
	
	CRIOTcp crioTCP;
	CRIOTcp crioTCP2;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
	}

	@Test
	public void acquireTest()
	{
		assertNotNull(crioTCP = CRIOHandler.acquire());
		assertSame(crioTCP, crioTCP2 = CRIOHandler.acquire());

		assertEquals("Acquire count should be 2.", 2, CRIOHandler.acquireCount);
		
		CRIOHandler.lease();
		
		assertEquals("Acquire count should be 1.", 1, CRIOHandler.acquireCount);
		
		CRIOHandler.lease();
		
		//assertEquals("Acquire count should be 0.", 0, CRIOHandler.acquireCount);
		
	}

}
