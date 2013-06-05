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
    
    private enum LogState {
    	ON, PAUSE
    }
 
    private LogState logState;
    
    /**
     * Starts the data log capture.
     * @param request
     * @return response
     */
	public PrimitiveResponse startLog(PrimitiveRequest request)
	{
    	PrimitiveResponse response = new PrimitiveResponse();
    	this.logWriter.startLog();
		response.setSuccessful(true);
		logState = LogState.ON;
		response.addResult("Log State", logState.toString());
		return response;
	}
	
	/**
	 * Pauses the data log capture.
	 * @param request
	 * @return response
	 */
	public PrimitiveResponse pauseLog(PrimitiveRequest request)
	{
		PrimitiveResponse response = new PrimitiveResponse();
		this.logWriter.pauseLog();
		response.setSuccessful(true);
		logState = LogState.PAUSE;
		response.addResult("Log State", logState.toString());
		return response;
	}
	
	/**
	 * Resumes the data log capture.
	 * @param request
	 * @return response
	 */
	
	public PrimitiveResponse resumeLog(PrimitiveRequest request)
	{
		PrimitiveResponse response = new PrimitiveResponse();
		this.logWriter.resumeLog();
		response.setSuccessful(true);
		logState = LogState.ON;
		response.addResult("Log State", logState.toString());
		return response;
	}
	
	/**
	 * Signals the LogWriter to end the log and initiate the
	 * saving process.
	 * @return true, if successful
	 */
	public boolean endLog()
	{
		this.logWriter.shutdown();
		return true;
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
    	logWriter = new LogWriter(WaveTankDataGrabber.class);
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
		this.endLog();
	}


}
