package au.edu.uts.eng.remotelabs.wavetank.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.createMockBuilder;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import au.edu.uts.eng.remotelabs.wavetank.CRIOTcp;
import au.edu.uts.eng.remotelabs.wavetank.WaveTankDataGrabber;

public class WaveTankDataGrabberTest
{	
	private static CRIOTcp crioTCP;
	private WaveTankDataGrabber dataGrabber;
	
	static double analogInputs[] = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 };
	static boolean digitalInputs[] = new boolean[] { false, true, false, true, false, true, false, true };
	static double analogOutputs[] = new double[] { 7, 6, 5, 4, 3, 2, 1, 0 };
	static boolean digitalOutputs[] = new boolean[] { true, false, true, false, true, false, true, false };
	

    @Before
	public void setUp()
	{
	
    	crioTCP = createMockBuilder(CRIOTcp.class)
				.addMockedMethod("getPump")
				.addMockedMethod("getInverter")
				.addMockedMethod("getSpeed")
				.addMockedMethod("getAnalogInputs")
				.addMockedMethod("getAnalogOutputs")
				.addMockedMethod("getDigitalOutputs")
				.addMockedMethod("getDigitalInputs")
				.createMock();
    	
		expect(crioTCP.getPump()).andReturn(true);
		expect(crioTCP.getInverter()).andReturn(false);
		expect(crioTCP.getSpeed()).andReturn(2.2);
		expect(crioTCP.getAnalogInputs()).andReturn(analogInputs);
		expect(crioTCP.getDigitalInputs()).andReturn(digitalInputs);
		expect(crioTCP.getAnalogOutputs()).andReturn(analogOutputs);
		expect(crioTCP.getDigitalOutputs()).andReturn(digitalOutputs);
		
		replay(crioTCP);
		
	}

	@Test
	public void testGetHeading()
	{
		dataGrabber = new WaveTankDataGrabber();
		String test = "Pump\tInverter\tPaddle Speed\tAnalog Inputs\t" +
				"Digital Inputs\tAnalog Outputs\tDigital Outputs";
		System.out.println(test);
		assertEquals(test, this.dataGrabber.getHeading());
	}
	
	@Test
	public void testGetLine()
	{
		dataGrabber = new WaveTankDataGrabber();
		
		
		assertEquals("false	false	0.0	[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]	[false, false, false, false, false, false, false, false]	null	null",
				this.dataGrabber.getLine());

		verify(crioTCP);
	}

}
