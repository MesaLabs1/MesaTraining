import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.xml.sax.SAXException;

/**
 * This class outlines the basic get/set methods of the SQL database along with BDO support for the
 * main thread and subsequent networked threads. 
 * 
 * We will need to queue database requests in a master list, and process them/send their callback info
 * as we process them. This system will be embedded in its own class for the purposes of ADO.
 * 
 * @author hackjunky, jacrin
 *
 */
public class DatabaseManager {
	//The lockfile is created by the system to tell other areas of code to not access/modify this DB.
	File lockFile = new File ("lock.d");

	//If this file exists, this is a first-run. We delete it, if it does.
	File fRunFile = new File("firstrun");

	Utils util;
	UI ui;

	//DB Stuff
	DocumentBuilderFactory dbFactory;
	File dbFile;
	DocumentBuilder dbBuilder;
	Document doc;

	public enum FieldType {
		PILOTS ("Pilot"), 
		AIRCRAFTS ("Aircraft"), 
		DATES ("Date"), 
		LOGS ("Logs");

		private final String name;       

		private FieldType(String s) {
			name = s;
		}

		public boolean equalsName(String otherName){
			return (otherName == null)? false:name.equals(otherName);
		}

		public String toString(){
			return name;
		}
	}

	public enum FieldSubType {
		NONE ("None"), 
		TRAINING ("Training"), 
		FLIGHT ("Flight"), 
		MAINTINENCE ("Maintinence");

		private final String name;       

		private FieldSubType(String s) {
			name = s;
		}

		public boolean equalsName(String otherName){
			return (otherName == null)? false:name.equals(otherName);
		}

		public String toString(){
			return name;
		}
	}

	public DatabaseManager(Utils ut, UI u) {
		util = ut;
		ui = u;

		util.Log("Initializing DBManager...");

		/*
		 * Let's begin by creating the lock.d file. If it exists, we might have a problem, or it might
		 * be a residual file from the previous run. 
		 */

		lockFile.deleteOnExit();

		if (!lockFile.exists()) {
			try {
				lockFile.createNewFile();
			} catch (IOException e) {
				util.Log("Failed to create the lock file. Expect the unexpected. All hope is lost.");
			}
		}else {
			util.Log("Lockfile exists but we are the super... let's pretend we didn't see that.");
		}

		if (fRunFile.exists()) {
			//This is our first run, let's make the DB.
			util.Log("Database is in First-Run mode. Initializing with a full sweep.");
			MakeFS();
			fRunFile.delete();
		}else {
			//This is not the first run. 
			if (!CheckFS()) {
				util.Log("Database filesystem is partially damaged?! Who would do such a thing.");
				FixFS();
			}else {
				util.Log("Database checks out okay. Continuing...");
			}
		}
		Refresh();

		//Delete the lockfile since we're done for now.
		lockFile.delete();
	}

	public void Refresh() {		
		dbFile = new File("db.xml");
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
		Refresh();
	}

	/**
	 * CheckFS checks for a valid database, and that all tables are present. If not, it will
	 * return false. In this case, you may want to call MakeFS() or FixFS().
	 * 
	 * @return True/False if the File System is correct.
	 */
	public boolean CheckFS() {
		util.Log("Checking FS...");
		return new File("db.xml").exists();
	}

	/**
	 * MakeFS creates an XML database based on a template provided to it initially in the FS. If the db is broken or MIA,
	 * it can repair it automatically.
	 * @return True/False depending on if the make is successful.
	 */
	public boolean MakeFS() {
		util.Log("Making the filesystem...");
		File dbtemp = new File("db_template.xml");
		if (!new File("db.xml").exists()) {
			try {
				Files.copy(new File("dbtemp.xml").toPath(), new File("db.xml").toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				System.out.println("Failed to create proper FS. Unexpected behavior could occur.");
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * FixFS should be called IFF CheckFS is false. Do not call this otherwise. This method
	 * does not return a boolean for validity.
	 */
	public void FixFS() {

	}

	synchronized public boolean ParseUser(String username, String password) {
		NodeList nList = doc.getElementsByTagName(username);
		util.Log("Checking " + username + " against " + nList.getLength() + " users.");
		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				if (nNode.getNodeName().toLowerCase().equals(username.toLowerCase())) {
					String localPass = eElement.getAttribute("Password");
					if (password.equals(localPass)) {
						//We can now set the last login time to the dateformat for this moment, just for tracking's sake.
						final String DATE_FORMAT_NOW = "ddMMyyyy;HHmmss";
						Calendar cal = Calendar.getInstance();
						SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
						eElement.setAttribute("LastLogin", sdf.format(cal.getTime()));

						Save();

						return true;
					}
				}
			}
		}
		ui.accessCount++;
		return false;
	}

	synchronized public int GetUserCount() {
		try {
			NodeList nList = doc.getElementsByTagName("Users");
			return nList.getLength();
		}catch (Exception e) {
			return -1;
		}
	}

	synchronized public String RequestRank(String username) {
		try {
			NodeList nList = doc.getElementsByTagName(username);
			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					if (nNode.getNodeName().toLowerCase().equals(username.toLowerCase())) {
						String rank = eElement.getAttribute("Rank");
						return rank;
					}
				}
			}
			ui.accessCount++;
		}catch (Exception e) {
			return null;
		}
		return null;
	}

	synchronized public String[] RequestRankList() {
		try {
			ArrayList<String> list = new ArrayList<String>();

			NodeList nList = doc.getElementsByTagName("Users");
			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);
				NodeList subList = nNode.getChildNodes();
				for (int j = 0; j < subList.getLength(); j++) {
					Node subNode = subList.item(j);
					if (subNode.getNodeType() == Node.ELEMENT_NODE) {
						Element subElement = (Element)subNode;
						if (subElement.hasAttributes()) {
							Element eElement = (Element) subNode;
							String username = subNode.getNodeName().toLowerCase();
							String rank = eElement.getAttribute("Rank");

							String entry = username + ";" + rank;
							list.add(entry);
							ui.accessCount++;
						}
					}
				}
			}
			String[] output = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				output[i] = list.get(i);
			}
			return output;
		}catch (Exception e) {
			return null;
		}
	}

	synchronized public String[] RequestField(FieldType type, FieldSubType subtype) {
		try {
			NodeList nList = null;
			if (type.equals(FieldType.LOGS)) {		//Let's access by the tag we want, not the superlevel.
				nList = doc.getElementsByTagName(subtype.toString());
			}else {
				nList = doc.getElementsByTagName(type.toString());
			}

			String values = "";
			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element)nNode;
					if (eElement.hasAttributes()) {
						NamedNodeMap map = eElement.getAttributes();
						for (int j = 0; j < map.getLength(); j++) {
							Node subNode = map.item(j);
							Attr attribute = (Attr)subNode;
							values += attribute.getValue() + "~";	
						}
					}else {
						//This could be the Logs entries, since they use Elements, then attributes
						NodeList subList = nNode.getChildNodes();
						for (int j = 0; j < subList.getLength(); j++) {
							Node subNode = subList.item(j);
							if (subNode.getNodeType() == Node.ELEMENT_NODE) {
								Element subElement = (Element)subNode;
								if (subElement.hasAttributes()) {
									NamedNodeMap map = subElement.getAttributes();
									for (int k = 0; k < map.getLength(); k++) {
										Node subSubNode = map.item(k);
										Attr attribute = (Attr)subSubNode;
										values += attribute.getValue() + "~";	
									}
								}
							}
						}
					}
				}
			}
			if (values.length() > 0) {
				values = values.substring(0, values.length() - 1);
			}
			ui.accessCount++;
			return values.split("~");
		}catch (Exception e) {
			//Return an empty array (this is a ghetto way of doing it), to keep the system up.
			//NOTE: This error seems to occur whenever a client terminates connection due to premature thread termination in an ADO.
			return "".split("");
		}
	}

	synchronized public void CreateUser(String caller, String username, String password, String permissions) {
		util.Log("Creating userspace for " + username  + " with permissions " + permissions + ".");
		NodeList nList = doc.getElementsByTagName("Users");
		Element eElement = (Element) nList;

		//Create a template entry.
		Element node = doc.createElement(username.toLowerCase());

		Attr rank = doc.createAttribute("Rank");
		Attr pass = doc.createAttribute("Password");
		Attr lastlog = doc.createAttribute("LastLogin");

		Calendar cal = Calendar.getInstance();
		Comment comm = doc.createComment("Auto-generated user. Created by " + caller + " on " + cal.getTime());

		node.appendChild(rank);
		node.appendChild(pass);
		node.appendChild(lastlog);

		eElement.appendChild(node);
		ui.accessCount++;

		Save();
	}

	synchronized public void PromoteUser(String username) {
		NodeList nList = doc.getElementsByTagName(username);
		util.Log("Promoting " + username + "...");
		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				if (nNode.getNodeName().toLowerCase().equals(username.toLowerCase())) {
					String rank = eElement.getAttribute("Rank");
					String newRank = rank;
					if (rank.equals("user")) {
						newRank = "admin";
					}else if (rank.equals("admin")) {
						newRank = "superadmin";
					}else if (rank.equals("superadmin")) {
						newRank = "superadmin";
					}
					eElement.setAttribute("Rank", newRank);
					break;
				}
			}
		}
		ui.accessCount++;

		Save();
	}

	synchronized public void DemoteUser(String username) {
		NodeList nList = doc.getElementsByTagName(username);
		util.Log("Demoting " + username + "...");
		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				if (nNode.getNodeName().toLowerCase().equals(username.toLowerCase())) {
					String rank = eElement.getAttribute("Rank");
					String newRank = rank;
					if (rank.equals("user")) {
						newRank = "user";
					}else if (rank.equals("admin")) {
						newRank = "user";
					}else if (rank.equals("superadmin")) {
						newRank = "admin";
					}
					eElement.setAttribute("Rank", newRank);
					break;
				}
			}
		}
		ui.accessCount++;

		Save();
	}
}
