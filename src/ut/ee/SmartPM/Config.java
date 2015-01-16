/**
 * @author Pätris Halapuu 2014
 */

package ut.ee.SmartPM;

public interface Config {

	
	// CONSTANTS
	// Server of Pätris
//	static final String YOUR_SERVER_URL =  "http://smartpm.cloudapp.net/register.php";
	// DIAG server
	static final String YOUR_SERVER_URL =  "http://www.dis.uniroma1.it/~smartpm/webtool/register.php";

	// Google project id
    static final String GOOGLE_SENDER_ID = "64754581420";  // Google project id

    /**
     * Tag used on log messages.
     */
    static final String TAG = "SmartPM";

    static final String DISPLAY_MESSAGE_ACTION =
            "ut.ee.SmartPM.DISPLAY_MESSAGE";

    static final String EXTRA_MESSAGE = "message";
		
	
}
