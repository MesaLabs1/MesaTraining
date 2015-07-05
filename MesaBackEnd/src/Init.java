import java.io.*;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;

/** 
 * This class bootstraps the BackEnd. It should only contain configuration parameters, handle the loading from a configuration file, and 
 * pass parameters to the main class. See documentation for the API.
 * 
 * @author hackjunky, jacrin
 *
 *
 */

public class Init {
	//See LoadConfigFile. This object will contain the normalized config file after the ParseConfigFile() call.
	Document doc;

	//See ParseConfigFile. This object will contain the nodeList based on the name of the Master Node.
	NodeList nodeList;

	//See PropertyMaster class. This class will contain all data and object references that need to be passed to the main program.
	PropertyMaster propMaster;

	//Command Line Arguments.
	static String[] arguments;

	Backend backend;

	public static void main (String[] args) {
		//Capture Command Line Arguments
		arguments = args;
		
		/*
		 * Set the visual style to be Linux, since we are developing this for a Linux target. BUT WAIT.
		 * Whoa, isn't Java supposed to be super cross compatible with everything forever? Yes. But heres the thing.
		 * UI Look and Feel, a UI Manager derivative, tells the system how buttons, controls, and interfaces Look.
		 * However, each systems visual interfaces take up marginally more or less pixels-per-control to display.
		 * By setting the L&F to Windows, the display editor will show us the interface as it will appear on the 
		 * host system, not on your specific OS.
		 */
		try {
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			/**
			 * A Button Mnemonic is the key a user has to press to activate that button automatically.
			 * Since we want the program to display them, when the user hits ALT, the mnemonic will display.
			 */
			UIManager.getDefaults().put("Button.showMnemonics", Boolean.TRUE);
		}catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}catch (ClassNotFoundException e) {
			
		}catch (InstantiationException e) {
			e.printStackTrace();
		}catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		new Init();
	}

	public Init() {
		//You must initialize the PropertyMaster before logging, since it contains the logger.
		propMaster = new PropertyMaster();

		propMaster.util.Log("Mesa BackEnd v." + propMaster.BACKEND_VERSION + " initializing...");

		//TODO: Don't worry about this until waaaaaay at the end. It's simply not worth our time.
		if (CheckForUpdate()) {
			DoUpdate();
		}else {
			propMaster.util.Log("No update reported from the Master Server. Proceeding.");
		}

		boolean configResult = LoadConfigFile("config.xml");
		if (configResult) {
			ParseConfigFile();

			/*
			 * Here, we check to see if the launch parameter wants a "silent" run (in the background), by using
			 * a tag we dubbed "nogui". By adding "-nogui" to the command line launch options for the Jar, you
			 * can disable the User Interface. It is not a necessary component to the server, used purely
			 * for debugging and visual effect purposes.
			 */

			if (!CheckForArgument("nogui")) {
				propMaster.ui = new UI(propMaster.util);
			}
			
			backend = new Backend(propMaster);
		}else {
			propMaster.util.Log("Received ABORT. Please send a copy of these logs to the System Administrator.");
		}
	}

	public boolean CheckForUpdate() {
		propMaster.util.Log("Querying master server for update data...");

		/*
		 * TODO: Bootstrap an update method here, where we check a specific remote source for a ZIP/TARBALL update.
		 * IF there is a remote update (in whatever medium we choose to package it in), we will need to download that file
		 * and extract it. Bear in mind that while Linux Shell allows us to send commands to console and do the extraction...
		 * 
		 * SECURITY NOTE: I don't advise it. I recommend using an internal class, to minimize cross-system issues, as well as the bug reported
		 * by CVE-2005-2475 (Security Hole). We're not worried about foreign attackers, but the problem can present for MANY reasons
		 * one of which is if you happened to package the update in a non-UNIX system, so no permission data is retained.
		 */
		return false;
	}

	public void DoUpdate() {
		propMaster.util.Log("Preparing to update this backend via a remote source...");

		/*
		 * How do I update this application when its running? Easy. We need to...
		 * TODO: Create another project that is a bootstrapping Java jar, and all it
		 * does is take a predesignated YOURFILE.ZIP from a config file we get in 
		 * the zip, and execute the extraction along with any commands in the config file.
		 * 
		 * This could be something like this...
		 * COPY: file1 TO bin/data/
		 * DELETE: oldfile1
		 * DELETE: oldfile2
		 * 
		 * These simple commands will allow you to create any changes necessary in the system
		 * with this patch mechanism.
		 * 
		 * 
		 * tl;dr TODO: Create bootstrapping class, along the parameters described above.
		 * TODO: Also, we need to keep a copy on the server of a master copy, that the
		 * bootstrapper can return to in the event of a broken update. 
		 */
	} 

	/**
	 * Loads a Configuration File to the backend memory space.
	 * @param path Path to the configuration file, along with the file name and extension.
	 * @return Success. False will cause this program to terminate.
	 */
	public boolean LoadConfigFile(String path) {
		boolean readSuccessful = true;

		propMaster.util.Log("Attempting to load " + path + "... ");

		File xmlInput = new File(path);

		/*
		 * What is DocumentBuilderFactory? This class specializes in accessing XML format files. As opposed to manually parsing the file,
		 * the Factory will generate an instance around the indicated file. This object must be passed to the ParseConfigFile method in 
		 * order to populate needed entries and package them into the PropertyMaster class.
		 */
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;

		try {

			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(xmlInput);
		} catch (ParserConfigurationException e) {
			readSuccessful = false;
			e.printStackTrace();

			propMaster.util.Log("ParserConfigurationException in Backend bootstrapper! Aborting.");
		} catch (SAXException e) {
			readSuccessful = false;
			e.printStackTrace();

			propMaster.util.Log("SAXException in Backend bootstrapper! Aborting.");
		} catch (IOException e) {
			readSuccessful = false;
			e.printStackTrace();

			propMaster.util.Log("IOException in Backend bootstrapper! Aborting.");

		}

		return readSuccessful;
	}

	public void ParseConfigFile() {
		propMaster.util.Log("Load complete! Parsing entries into master object...");

		doc.getDocumentElement().normalize();
		nodeList = doc.getElementsByTagName("INIT");

		/*
		 * This is where we make checks for crucial entry data, using the GetElementByNode helper method.
		 * If there are entries we ABSOLUTELY NEED in our lives at this point, now is an excellent time to
		 * find out if the configuration file has them.
		 */


	}


	/**
	 * Helper Method. Allows you to fetch an element's contents via a node tag, and an element's name.
	 * @param node The tag of the node you're attempting to access.
	 * @param element The element name.
	 * @return The element contents, or 'ERROR' if the node/element is MIA.
	 */
	public String GetElementByNode(String nodeName, String elementName) {
		String elementValue = "ERROR";
		if (doc != null) {
			propMaster.util.Log("FETCH_" + nodeName + "@" + elementName +"");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				System.out.println("SEEK_" + node.getNodeName());
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;

					/*
					 * Fun decision you (jacrin) get to make... XML. Do we use Attributes to store the data, or Elements?
					 * Attributes are single-value entries. Elements can contain multiple entries for the same element name.
					 * See the commented lines below to get an idea. Your call.
					 */
					//element.getAttribute(elementName);
					//element.getElementsByTagName(elementName).item(0).getTextContent();

					elementValue = element.getAttribute(elementName);	//Replace this line with your decision from above.
				}
			}

		}else {
			propMaster.util.Log("Premature call to GetElementByNode! You must first initialize the Document with LoadConfigFile(). Ignoring.");
		}

		return elementValue;
	}

	/**
	 * Searches the command line arguments given when launching the program for a specific argument.
	 * @param arg The name of the argument you're looking for.
	 * @return Returns true if the argument exists, false if not.
	 */
	public boolean CheckForArgument(String arg) {
		boolean found = false;
		propMaster.util.Log("SEARCH: " + arg + " IN arguments[].");
		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i].toLowerCase().equals(arg.toLowerCase())) {
				found = true;
				break;
			}
		}
		return found;
	}

	/** 
	 * Container for the Configuration Data that is parsed at runtime.
	 */
	public class PropertyMaster {
		//A reference to the utility class, to keep the output file name available and the same.
		Utils util = new Utils();
		
		//User Interface.
		UI ui;

		//The newline character must be used when formatting a new line. Using \n or \r will NOT work cross-system.
		final String newline = System.getProperty("line.separator");

		//Version of the program, for network compatibility issues
		static final int BACKEND_VERSION = 1;
		
		//List of Variables that can be set by config
		int NETWORK_PORT = 1337;				//Port for the Server
		int NETWORK_MAX_CONNECTIONS = 12;		//Maximum Concurrent Users
		int NETWORK_MAX_OVERHEAD = 0;			//Maximum Data to be Sent per Tick; 0 to send all
		
	}
}
