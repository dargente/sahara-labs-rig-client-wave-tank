package au.edu.uts.eng.remotelabs.eng.remotelabs.wavetank.primitive;

import au.edu.uts.eng.remotelabs.rigclient.rig.IRigControl.PrimitiveRequest;
import au.edu.uts.eng.remotelabs.rigclient.rig.IRigControl.PrimitiveResponse;
import au.edu.uts.eng.remotelabs.rigclient.rig.primitive.IPrimitiveController;
import au.edu.uts.eng.remotelabs.rigclient.util.ILogger;
import au.edu.uts.eng.remotelabs.rigclient.util.LoggerFactory;
import au.edu.uts.eng.remotelabs.wavetank.CRIOHandler;
import au.edu.uts.eng.remotelabs.wavetank.WaveTankDataGrabber;

public class LogController implements IPrimitiveController 
{
    /** Interface to cRIO. */
    private CRIOHandler crioHandler;

    /** LogWriter Instance **/
    private LogWriter logWriter;
    
    /** Logger. */
    private ILogger logger;
    
 
	public PrimitiveResponse startLog(PrimitiveRequest request)
	{
    	PrimitiveResponse response = new PrimitiveResponse();
    	this.logWriter.startLog();
		response.setSuccessful(true);
		return response;
	}
	
	public PrimitiveResponse pauseLog(PrimitiveRequest request)
	{
		PrimitiveResponse response = new PrimitiveResponse();
		this.logWriter.pauseLog();
		response.setSuccessful(true);
		return response;
	}
	
	public PrimitiveResponse resumeLog(PrimitiveRequest request)
	{
		PrimitiveResponse response = new PrimitiveResponse();
		this.logWriter.resumeLog();
		response.setSuccessful(true);
		return response;
	}
	
	public boolean endLog()
	{
		return true;
	}
    
    @Override
	public boolean initController() 
    {	
        this.logger = LoggerFactory.getLoggerInstance();
    	
        /* Get instance of CRIO */
    	crioHandler = CRIOHandler.getInstance();
    	if(crioHandler == null)
    	{
    		this.logger.warn("Could not retrieve CRIOHandler instance. Failing Wave Tank controller initialisation.");
    		return false;
    	}
    	
    	/* Create instance of LogWriter  */
    	logWriter = new LogWriter(WaveTankDataGrabber.class);
    	return true;
	}

	@Override
	public boolean postRoute() 
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean preRoute() {
		if(!this.crioHandler.isConnected())
		{
			this.logger.warn("Connection to CRIO failed. Failing action method.");
			return false;
		}
		return true;
	}
	
	@Override
	public void cleanup() {
		this.endLog();
	}


}
