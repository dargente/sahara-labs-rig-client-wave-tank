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
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
	}

	@Test
	public void acquireTest()
	{
		crioTCP = CRIOHandler.acquire();
		crioTCP = CRIOHandler.acquire();
		
		

	}

}
