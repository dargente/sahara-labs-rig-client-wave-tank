package au.edu.labshare.rigclient.logging;

import au.edu.uts.eng.remotelabs.rigclient.rig.IRigControl.PrimitiveRequest;
import au.edu.uts.eng.remotelabs.rigclient.rig.IRigControl.PrimitiveResponse;
import au.edu.uts.eng.remotelabs.rigclient.rig.primitive.IPrimitiveController;
import au.edu.uts.eng.remotelabs.rigclient.util.ILogger;
import au.edu.uts.eng.remotelabs.rigclient.util.LoggerFactory;
import au.edu.uts.eng.remotelabs.wavetank.CRIOHandler;
import au.edu.uts.eng.remotelabs.wavetank.CRIOTcp;
import au.edu.uts.eng.remotelabs.wavetank.WaveTankDataGrabber;

public class LogController implements IPrimitiveController 
{
    /** Interface to cRIO. */
    private CRIOTcp crioTCP;

    /** LogWriter Instance **/
    private LogWriter logWriter;
    
    /** Logger. */
    private ILogger logger;
 
    private boolean logState;
    
    /**
     * Starts the data log capture.
     * @param request
     * @return response
     */
	public PrimitiveResponse startLogAction(PrimitiveRequest request)
	{
		PrimitiveResponse response = new PrimitiveResponse();
    	logWriter = new LogWriter(WaveTankDataGrabber.class);
		this.logWriter.startLog();
		response.setSuccessful(true);
		logState = true;
		response.addResult("Log State", Boolean.toString(logState));
		return response;
	}
	
	/**
	 * Pauses the data log capture.
	 * @param request
	 * @return response
	 */
	public PrimitiveResponse pauseLogAction(PrimitiveRequest request)
	{
		PrimitiveResponse response = new PrimitiveResponse();
		this.logWriter.pauseLog();
		response.setSuccessful(true);
		logState = false;
		response.addResult("Log State", Boolean.toString(logState));
		return response;
	}
	
	/**
	 * Resumes the data log capture.
	 * @param request
	 * @return response
	 */
	
	public PrimitiveResponse resumeLogAction(PrimitiveRequest request)
	{
		PrimitiveResponse response = new PrimitiveResponse();
		this.logWriter.resumeLog();
		response.setSuccessful(true);
		logState = true;
		response.addResult("Log State", Boolean.toString(logState));
		return response;
	}
	
	/**
	 * Signals the LogWriter to end the log and initiate the
	 * saving process.
	 * @return true, if successful
	 */
	public PrimitiveResponse endLogAction(PrimitiveRequest request)
	{
		PrimitiveResponse response = new PrimitiveResponse();
		
		if(!this.logWriter.shutdown())
		{
			this.logger.warn("End Log failed.");
			response.setSuccessful(false);
			response.setErrorCode(-7);
			response.setErrorReason("Exception thrown during log saving.");
			return response;
		}
		
		response.setSuccessful(true);
		return response;
	}
    
    @Override
	public boolean initController() 
    {	
        this.logger = LoggerFactory.getLoggerInstance();
    	
        /* Get instance of CRIO */
    	crioTCP = CRIOHandler.acquire();
    	if(crioTCP == null)
    	{
    		this.logger.warn("Could not retrieve CRIOHandler instance. Failing Wave Tank controller initialisation.");
    		return false;
    	}
    	
    	/* Create instance of LogWriter  */
    	return true;
	}

	@Override
	public boolean postRoute() 
	{
		return true;
	}

	@Override
	public boolean preRoute() 
	{
		if(!this.crioTCP.isConnected())
		{
			this.logger.warn("Connection to CRIO failed. Failing action method.");
			return false;
		}
		return true;
	}
	
	@Override
	public void cleanup() 
	{
		this.logWriter.shutdown();
		CRIOHandler.lease();
	}


}
