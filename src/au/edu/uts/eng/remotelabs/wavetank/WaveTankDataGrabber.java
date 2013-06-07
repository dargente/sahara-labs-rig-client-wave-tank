package au.edu.uts.eng.remotelabs.wavetank;

import au.edu.labshare.rigclient.logging.IDataGrabber;
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
		StringBuilder logData = new StringBuilder();
		
		logData.append(this.crioTCP.getPump() + ",");
		logData.append(this.crioTCP.getInverter() + ",");
		logData.append(this.crioTCP.getSpeed() + ",");
		
		
		for(int c = 0; c < 8; c++)
		{
			logData.append(this.crioTCP.getAnalogInput(c) + ",");
		}
		for(int c = 0; c < 8; c++)
		{
			logData.append((this.crioTCP.getDigitalInput(c) ? 1 : 0) + ",");
		}
		for(int c = 0; c < 8; c++)
		{
			logData.append(this.crioTCP.getAnalogOutput(c) + ",");
		}
		for(int c = 0; c < 8; c++)
		{
			logData.append((this.crioTCP.getDigitalOutput(c) ? 1 : 0) + ",");
		}
		return logData.toString();
	}
	
	/**
	 * Returns the headings used for the log files.
	 * @return String with headings
	 */
	@Override
	public String getHeading()
	{
		String headings = "Pump" + ","
						+ "Inverter" + ","
						+ "Paddle Speed" + ","
						+ "Analog Inputs" + ","
						+ "Digital Inputs" + ","
						+ "Analog Outputs" + ","
						+ "Digital Outputs";
		return headings;
	}
	

}
