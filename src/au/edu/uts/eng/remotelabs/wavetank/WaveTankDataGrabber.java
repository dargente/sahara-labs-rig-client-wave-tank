package au.edu.uts.eng.remotelabs.wavetank;

import java.util.Arrays;

import au.edu.uts.eng.remotelabs.eng.remotelabs.wavetank.primitive.IDataGrabber;
import au.edu.uts.eng.remotelabs.rigclient.util.ILogger;
import au.edu.uts.eng.remotelabs.rigclient.util.LoggerFactory;

/*
 * Retrieves data from the Wave Tank CRIO and formats it for
 * data logs.
 */
public class WaveTankDataGrabber implements IDataGrabber
{	
	CRIOTcp crioTCP;
	
	/** Logger **/
    private ILogger logger;
	
    /** Constructor **/
	public WaveTankDataGrabber()
	{
        this.logger = LoggerFactory.getLoggerInstance();
		crioTCP = CRIOHandler.acquire();
        
    	if(crioTCP == null)
    	{
    		this.logger.warn("Could not retrieve CRIOHandler instance. Failing Wave Tank controller initialisation.");
    	}
	}
	
	/**
	 * Retrieves a dataset from the CRIO of the Wave Tank
	 * @return String formatted with dataset
	 */
	@Override
	public String getLine()
	{
		
		String logData = String.valueOf(this.crioTCP.getPump()) + "\t"
				+ String.valueOf(this.crioTCP.getInverter()) + "\t"
				+ String.valueOf(this.crioTCP.getSpeed()) + "\t"
				+ Arrays.toString(this.crioTCP.getAnalogInputs()) + "\t"
				+ Arrays.toString(this.crioTCP.getDigitalInputs()) + "\t"
				+ Arrays.toString(this.crioTCP.getAnalogOutputs()) + "\t"
				+ Arrays.toString(this.crioTCP.getDigitalOutputs());

		return logData;
	}
	
	/**
	 * Returns the headings used for the log files.
	 * @return String with headings
	 */
	@Override
	public String getHeading()
	{
		String headings = "Pump" + "\t"
						+ "Inverter" + "\t"
						+ "Paddle Speed" + "\t"
						+ "Analog Inputs" + "\t"
						+ "Digital Inputs" + "\t"
						+ "Analog Outputs" + "\t"
						+ "Digital Outputs";
		return headings;
	}
	

}
