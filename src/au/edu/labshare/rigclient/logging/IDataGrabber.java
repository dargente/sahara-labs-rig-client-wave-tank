package au.edu.labshare.rigclient.logging;


/**
 * Interface for a rig specific logging. The implementation of this interface
 * should retrieve the information from the rig and format it for the logfile.
 */
public interface IDataGrabber
{

    /**
     * Retrieves a dataset from the rig and formats it for the logfile.
     * 
     * @return String with formatted dataset
     */
	public String getLine();
	
	
	/**
	 * Formats the heading line for the logfile.
	 * @return String with the headings
	 */
	public String getHeading();
	
}
