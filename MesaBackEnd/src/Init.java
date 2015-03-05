import java.io.*;

/** This class bootstraps the BackEnd. It should only contain configuration parameters, handle the loading from a configuration file, and pass parameters to the main
 * class. See documentation for the API.
 * 
 * 
 * @author hackjunky, jacrin
 * hi
 *
 */

public class Init {

	public static void main (String[] args) {
		new Init();
	}

	public Init() {
		Utils.Log("Mesa BackEnd initializing...");
	}

	public boolean LoadConfigFile(String path, String name) {
		boolean readSuccessful = true;

		//Check for path validity before using it.
		boolean pathIsValid = false;
		if (path.endsWith("/") || path.endsWith("/")) {
			pathIsValid = true;
		}
		
		if (!pathIsValid) {
			
		}

		if (pathIsValid) {
			try {
				BufferedReader in;
				try {
					in = new BufferedReader(new FileReader(path + name));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				
			} catch (Exception e2) {
				readSuccessful = false;
			}
		}
		return readSuccessful;
	}

	public void ParseConfigFile() {

	}

	/** Container for the Configuration Data that is parsed at runtime.
	 * 
	 */
	public class PropertyMaster {
		//The newline character must be used when formatting a new line. Using \n or \r will NOT work cross-system.
		final String newline = System.getProperty("line.separator");

	}
}
