import java.io.*;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;

public class Init {
	DocumentBuilderFactory dbFactory;
	File dbFile;
	DocumentBuilder dbBuilder;
	Document doc;

	PropertyMaster propMaster;
	Backend backend;

	static String[] arguments;

	public static void main (String[] args) {
		arguments = args;

		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			UIManager.getDefaults().put("Button.showMnemonics", Boolean.TRUE);
		}catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}catch (InstantiationException e) {
			e.printStackTrace();
		}catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		new Init();
	}

	public Init() {
		propMaster = new PropertyMaster();
		propMaster.util.Log("Mesa WebConnect Backend v." + propMaster.BACKEND_VERSION + " initializing...");

		LoadConfigFile();
		ParseConfigFile();

		if (CheckForUpdate()) {
			DoUpdate();
		}else {
			propMaster.util.Log("No update reported from the Master Server. Proceeding.");
		}

		if (!CheckForArgument("nogui")) {
			propMaster.ui = new UI(propMaster);
		}	
		backend = new Backend(propMaster);
	}

	public boolean CheckForUpdate() {
		propMaster.util.Log("Querying master server for update data...");
		String update = GetAttributeByNode("INIT", "Update");
		if (update.length() == 0) {
			return false;
		}else {
			return true;
		}
	}

	public void DoUpdate() {
		propMaster.util.Log("Preparing to update this backend via a remote source...");

		String updateLocation = GetAttributeByNode("INIT", "Update");
		File updateZip = new File(updateLocation);

		if (updateZip.exists()) {
			try {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				Document doc = docBuilder.newDocument();
				
				
				Element rootElement = doc.createElement("Update");
				doc.appendChild(rootElement);

				rootElement.setAttribute("Path", updateLocation);

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File("update.xml"));
				
				transformer.transform(source, result);

			} catch (ParserConfigurationException pce) {
				pce.printStackTrace();
			} catch (TransformerException tfe) {
				tfe.printStackTrace();
			}
		}

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

	public void ParseConfigFile() {
		propMaster.util.Log("Load complete! Parsing entries into master object...");
		doc.getDocumentElement().normalize();

		propMaster.NETWORK_MAX_CONNECTIONS = Integer.parseInt(GetAttributeByNode("Network", "MaxConnections"));
		propMaster.NETWORK_MAX_OVERHEAD = Integer.parseInt(GetAttributeByNode("Network", "MaxOverhead"));
		propMaster.NETWORK_PORT = Integer.parseInt(GetAttributeByNode("Network", "Port"));

		propMaster.BACKEND_VERSION = Integer.parseInt(GetAttributeByNode("INIT", "Version"));
	}


	public void LoadConfigFile() {		
		dbFile = new File("config.xml");
		dbFactory = DocumentBuilderFactory.newInstance();
		dbBuilder = null;
		try {
			dbBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		doc = null;
		try {
			doc = dbBuilder.parse(dbFile);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		doc.getDocumentElement().normalize();
	}

	public void Save() {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = transformerFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(dbFile);
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		LoadConfigFile();
	}



	public String GetAttributeByNode(String node, String attribute) {
		if (doc != null) {
			try {
				NodeList nList = doc.getElementsByTagName(node);
				Node nNode = nList.item(0);
				Element eElement = (Element) nNode;
				return eElement.getAttribute(attribute);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			propMaster.util.Log("Premature call to GetAttributeByNode! You must first initialize the Document with LoadConfigFile(). Ignoring.");
			return null;
		}
		return null;
	}


	public boolean CheckForArgument(String arg) {
		boolean found = false;
		propMaster.util.Log("Argument! Found " + arg + " in args[].");
		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i].toLowerCase().equals(arg.toLowerCase())) {
				found = true;
				break;
			}
		}
		return found;
	}


	public class PropertyMaster {
		Utils util = new Utils();
		UI ui;
		int BACKEND_VERSION = 1;

		final String newline = System.getProperty("line.separator");

		int NETWORK_PORT = 1337;
		int NETWORK_MAX_CONNECTIONS = 12;
		int NETWORK_MAX_OVERHEAD = 0;
	}
}
