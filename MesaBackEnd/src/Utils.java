import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javax.swing.DefaultListModel;


public class Utils {
	//The number of digits appended to the log name, to avoid overwriting.
	static final int LOG_NUM_IDENTIFIERS = 6;
	
	//The log name, assigned by the constructor.
	static String LOG_NAME = "";
	
	BufferedWriter logWriter;
	
	//The logs, posted to this array for usage by the UI.
	DefaultListModel<String> logs;
	
	
	public Utils() {
		logs = new DefaultListModel<String>();
		
		Random random = new Random();
		LOG_NAME = "output";
		for (int i = 0; i < LOG_NUM_IDENTIFIERS; i++) {
			LOG_NAME += new Random().nextInt(10);
		}
		LOG_NAME += ".log";
		
		Log("Selected " + LOG_NAME + " as default logging location.");
	}
		
	
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

	
	void Log(String message) {
		try {
			logWriter = new BufferedWriter(new FileWriter(LOG_NAME, true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Date date = new Date();
		String sender = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getMethodName();
		String time = timeFormatter.format(date);

		String log = "[" + sender + "@" + time +"]: " + message;
		
		//Print it
		System.out.println(log);
		
		//Log it
		try {
			logWriter.write(log);
			logWriter.newLine();
			logWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Save it
		logs.addElement(log);
	}
}

