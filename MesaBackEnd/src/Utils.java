import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javax.swing.DefaultListModel;

/**
 * Utils contains static functions that are specific to the back-end. This entails methods such as logging, debugging, etc.
 * @author hackjunky, jacrin
 *
 */
public class Utils {
	//The number of digits appended to the log name, to avoid overwriting.
	static final int LOG_NUM_IDENTIFIERS = 6;
	
	//The log name, assigned by the constructor.
	static String LOG_NAME = "";
	
	BufferedWriter logWriter;
	
	//The logs, posted to this array for usage by the UI.
	DefaultListModel<String> logs;
	
	/**
	 * The constructor of this class initializes a unique numeric identifier that attaches to the log output.
	 */
	public Utils() {
		logs = new DefaultListModel<String>();
		
		Random random = new Random();
		LOG_NAME = "output";
		for (int i = 0; i < LOG_NUM_IDENTIFIERS; i++) {
			LOG_NAME += new Random().nextInt(10);
		}
		LOG_NAME += ".log";
		
		try {
			logWriter = new BufferedWriter(new FileWriter(LOG_NAME, true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Log("Selected " + LOG_NAME + " as default logging location.");
	}
		
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

	//This variable belongs to the Log method, and is used to convert a System Time to a String.
	private static SimpleDateFormat timeFormatter= new SimpleDateFormat("hh:mm:ss a");

	/**
	 * Log is a method that posts the sender name, the time, and the message. It also writes to a log file.
	 * @param message The message to display in the system console.
	 * 
	 */
	void Log(String message) {
		Date date = new Date();
		String sender = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getMethodName();
		String time = timeFormatter.format(date);

		String log = "[" + sender + "@" + time +"]: " + message;
		
		//Print it
		System.out.println(log);
		
		//Log it
		try {
			logWriter.write(log);;
			logWriter.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Save it
		logs.addElement(log);
	}
}

