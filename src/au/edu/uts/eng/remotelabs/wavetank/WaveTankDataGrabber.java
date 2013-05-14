package au.edu.uts.eng.remotelabs.wavetank;

import java.util.Map;

import au.edu.uts.eng.remotelabs.eng.remotelabs.wavetank.primitive.IDataGrabber;
import au.edu.uts.eng.remotelabs.rigclient.util.ILogger;
import au.edu.uts.eng.remotelabs.rigclient.util.LoggerFactory;

public class WaveTankDataGrabber implements IDataGrabber
{	
	CRIOHandler crioHandler;
	
	/** Logger **/
    private ILogger logger;
	
	public WaveTankDataGrabber()
	{
        this.logger = LoggerFactory.getLoggerInstance();
		crioHandler = CRIOHandler.getInstance();
    	if(crioHandler == null)
    	{
    		this.logger.warn("Could not retrieve CRIOHandler instance. Failing Wave Tank controller initialisation.");
    	}
	}
	
	@Override
	public String getLine()
	{
		String logData = null;
		Map<String, String> results = crioHandler.getData();
		
		logData = results.get("pump") + "\t"
				+ results.get("inverter") + "\t"
				+ results.get("speed") + "\t"
				+ results.get("ain") + "\t"
				+ results.get("din") + "\t"
				+ results.get("aout") + "\t"
				+ results.get("dout");
		
		return logData;
	}
	
	@Override
	public String getHeading()
	{
		String headings = "Pump" + "/t"
						+ "Inverter" + "/t"
						+ "Speed" + "/t"
						+ "Analog Inputs" + "/t"
						+ "Digital Inputs" + "/t"
						+ "Analog Outputs" + "/t"
						+ "Digital Outputs";
		return headings;
	}
	

}
