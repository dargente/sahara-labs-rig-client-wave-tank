package au.edu.labshare.rigclient.logging.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;

import au.edu.labshare.rigclient.logging.LogController;
import au.edu.labshare.rigclient.logging.LogWriter;
import au.edu.uts.eng.remotelabs.rigclient.rig.IRigControl.PrimitiveRequest;
import au.edu.uts.eng.remotelabs.rigclient.rig.IRigControl.PrimitiveResponse;

public class LogControllerTest
{

	PrimitiveResponse testResponse;
	LogWriter logWriter;
	LogController logController;
	PrimitiveRequest testRequest;
	
	@Before
	public void setUp() throws Exception
	{
		testResponse = new PrimitiveResponse();
		testResponse.setSuccessful(true);
	}

	@Test
	public void StartLogtest()
	{
		this.logWriter = createMockBuilder(LogWriter.class)
				.addMockedMethod("startLog")
				.createMock();
		
		expect(this.logWriter.startLog()).andReturn(true);
		replay(this.logWriter);
		
		this.logController = new LogController();
		
		assertEquals(testResponse, logController.startLogAction(testRequest));
		verify(this.logWriter);
	
	}

}
