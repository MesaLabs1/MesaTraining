import java.util.Calendar;

/**
 * Utils contains static functions that are specific to the back-end. This entails methods such as logging, debugging, etc.
 * @author hackjunky, jacrin
 *
 */
public class Utils {
	
	/**
	 * The following block of code allows the Utils class to fetch the calling class' data
	 * without placing any additional resource load on Thread than is necessary.
	 * 
	 * TODO: Investigate possibility that Linux system doesn't multithread.
	 */
	private static final int CLIENT_CODE_STACK_INDEX;
    static {
        int i = 0;
        for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
            i++;
            if (ste.getClassName().equals(Utils.class.getName())) {
                break;
            }
        }
        CLIENT_CODE_STACK_INDEX = i;
    }
    
	static void Log(String message) {
		Calendar cal = Calendar.getInstance();
		String sender = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getMethodName();
		String time = cal.HOUR_OF_DAY + ":" + cal.MINUTE + ":" + cal.SECOND;
		
		System.out.println("[" + sender + "@" + time +"]: " + message);
	}
}
