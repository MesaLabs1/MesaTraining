import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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

		//Delete the lockfile since we're done for now.
		lockFile.delete();
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
	 * MakeFS creates a database w/ all tables and subsequent entries for the first-run. Do not
	 * call this more than once, as it may obfuscate the entry permissions.
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

	public boolean ParseUser(String username, String password) {
		NodeList nList = doc.getElementsByTagName(username);
		util.Log("Checking " + username + " against " + nList.getLength() + " users.");
		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				if (nNode.getNodeName().toLowerCase().equals(username.toLowerCase())) {
					String localPass = eElement.getAttribute("Password");
					if (password.equals(localPass)) {
						return true;
					}
				}
			}
		}
		ui.accessCount++;
		return false;
	}

	public String RequestRank(String username) {
		NodeList nList = doc.getElementsByTagName(username);
		//util.Log("Checking " + username + " against " + nList.getLength() + " users.");
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
		return null;
	}
	
	public String[] RequestRankList() {
		ArrayList<String> list = new ArrayList<String>();
		
		NodeList nList = doc.getElementsByTagName("Users");
		//util.Log("Checking " + username + " against " + nList.getLength() + " users.");
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
	}

	public String[] RequestField(String fieldname) {
		//PILOTS,AIRCRAFTS,DATES,TRAINING,FLIGHT,MAINTINENCE

		//Hotfix 1a for Capitalizations in XML issue
		String subfield = "";
		if (fieldname.equals("PILOTS")) {
			fieldname = "Pilot";
		}else if (fieldname.equals("AIRCRAFTS")) {
			fieldname = "Aircraft";
		}else if (fieldname.equals("DATES")) {
			fieldname = "Date";
		}else if (fieldname.equals("TRAINING")) {
			fieldname = "Logs";
			subfield = "Training";
		}else if (fieldname.equals("FLIGHT")) {
			fieldname = "Logs";
			subfield = "Flight";
		}else if (fieldname.equals("MAINTINENCE")) {
			fieldname = "Logs";
			subfield = "Maintinence";
		} 

		NodeList nList = null;
		if (subfield.length() > 0) {		//Let's access by the tag we want, not the superlevel.
			nList = doc.getElementsByTagName(subfield);
			//util.Log("Accessing logs for " + subfield);
		}else {
			nList = doc.getElementsByTagName(fieldname);
			//util.Log("Accessing logs for " + fieldname);
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
	}

	class DatabaseRunner implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

		}
	}
}
