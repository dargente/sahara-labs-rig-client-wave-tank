package au.edu.uts.eng.remotelabs.wavetank.tests;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.easymock.EasyMock.*;

import au.edu.uts.eng.remotelabs.wavetank.CRIOTcp;
import au.edu.uts.eng.remotelabs.wavetank.WaveTankDataGrabber;

public class WaveTankDataGrabberTest
{	
	private CRIOTcp crioTest;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{

	}

	@Test
	public void testGetHeading()
	{
		String test = "Pump\tInverter\tPaddle Speed\tAnalog Inputs\t" +
				"Digital Inputs\tAnalog Outputs\tDigital Outputs";
		
		WaveTankDataGrabber dataGrabber = new WaveTankDataGrabber();
		assertEquals(test, dataGrabber.getHeading());
	}
	
	public void testGetLine()
	{
		double analogInputs[] = { 1, 2, 3, 4, 5};
		double analogOutputs[];
		boolean digitalOutputs[] = { true, false, true, false, true, false, true, false };
		boolean digitalInputs[] = { false, true, false, true, false, true, false, true };
		
		crioTest = createMock(CRIOTcp.class);
		expect(crioTest.getPump()).andReturn(true);
		expect(crioTest.getInverter()).andReturn(true);
		expect(crioTest.getSpeed()).andReturn((double) 22);
		expect(crioTest.getAnalogInputs()).andReturn();
		expect(crioTest.getDigitalInputs()).andReturn();
		expect(crioTest.getAnalogOutputs()).andReturn();
		expect(crioTest.getDigitalOutputs()).andReturn();
		
		
		
	}

}
