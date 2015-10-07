import java.io.File;
import java.io.IOException;
import java.io.Serializable;
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


public class DatabaseManager {
	//The lockfile is created by the system to tell other areas of code to not access/modify this DB.
	File lockFile = new File ("lock.d");

	//If this file exists, this is a first-run. We delete it, if it does.
	File fRunFile = new File("firstrun");

	Utils util;
	UI ui;
	Payload payload;

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

	public DatabaseManager(Utils ut, UI u, Payload p) {
		util = ut;
		payload = p;
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

	
	public boolean CheckFS() {
		util.Log("Checking FS...");
		return new File("db.xml").exists();
	}

	
	public boolean MakeFS() {
		util.Log("Making the filesystem...");
		File dbtemp = new File("db_template.xml");
		if (!new File("db.xml").exists()) {
			try {
				Files.copy(new File("dbtemp.xml").toPath(), new File("db.xml").toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				System.out.println("Failed to create proper FS. Unexpected behavior could occur.");
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	
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

	synchronized public boolean ChangePassword(String username, String password, String newpass) {
		NodeList nList = doc.getElementsByTagName(username);
		util.Log("Checking " + username + " against " + nList.getLength() + " users.");
		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				if (nNode.getNodeName().toLowerCase().equals(username.toLowerCase())) {
					String localPass = eElement.getAttribute("Password");
					if (password.equals(localPass)) {
						eElement.setAttribute("Password", newpass);
						Save();

						return true;
					}
				}
			}
		}
		ui.accessCount++;
		return false;
	}

	synchronized public String GetPassword(String username) {
		NodeList nList = doc.getElementsByTagName(username);
		util.Log("Checking " + username + " against " + nList.getLength() + " users.");
		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				if (nNode.getNodeName().toLowerCase().equals(username.toLowerCase())) {
					String localPass = eElement.getAttribute("Password");
					return localPass;
				}
			}
		}
		ui.accessCount++;
		return "";
	}

	synchronized boolean CheckUser(String username) {
		NodeList nList = doc.getElementsByTagName(username);
		util.Log("Checking " + username + " against " + nList.getLength() + " users.");
		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				if (nNode.getNodeName().toLowerCase().equals(username.toLowerCase())) {
					return true;
				}
			}
		}
		ui.accessCount++;
		return false;
	}

	synchronized boolean DeleteUser(String username) {
		ui.accessCount++;
		NodeList nList = doc.getElementsByTagName(username);
		util.Log("Checking " + username + " against " + nList.getLength() + " users.");
		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				if (username.contains("*")) {
					if (nNode.getNodeName().toLowerCase().equals(username.toLowerCase())) {
						eElement.getParentNode().removeChild(eElement);				
					}
				}else {
					if (eElement.getTagName().equals(username)) {
						if (nNode.getNodeName().toLowerCase().equals(username.toLowerCase())) {
							eElement.getParentNode().removeChild(eElement);

							Save();
							return true;
						}
					}
				}
			}
		}
		if (nList.getLength() == 0) {
			return false;
		}
		Save();
		return true;
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

	public void SendPayloadStacks() {
		if (payload != null) {
			Payload.Entry[] entries = ConvertXMLToPayload();

			if (entries != null) {
				payload.ClearEntries();
				for (int i = 0; i < entries.length; i++) {
					payload.AddEntry(entries[i]);
				}
			}
		}
	}

	synchronized public Payload.Entry[] ConvertXMLToPayload() {
		try {
			if (doc != null) {
				ArrayList<Payload.Entry> entries = new ArrayList<Payload.Entry>();

				NodeList nList = doc.getElementsByTagName("Data");
				Node nNode = nList.item(0);
				for (int i = 0; i < nNode.getChildNodes().getLength(); i++) {
					Node subNode = nNode.getChildNodes().item(i);
					if (subNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) subNode;
						String date = eElement.getAttribute("Date");
						String aircraft = eElement.getAttribute("Aircraft");
						String notes = eElement.getAttribute("Notes");
						String pilot = eElement.getAttribute("Pilot");

						Payload.Entry entry = payload.CreateBlankEntry(pilot, aircraft, date);

						if (notes.startsWith("m_")) {
							entry.setRepairData(notes.substring(2, notes.length()));
							entries.add(entry);
						}else if (notes.startsWith("t_")) {
							entry.setTrainingData(notes.substring(2, notes.length()));
							entries.add(entry);
						}else if (notes.startsWith("f_")) {
							entry.setFlightData(notes.substring(2, notes.length()));
							entries.add(entry);
						}
					}
				}
				ui.accessCount++;

				Payload.Entry[] entriesArray = new Payload.Entry[entries.size()];
				for (int i = 0; i < entries.size(); i++) {
					entriesArray[i] = entries.get(i);
				}

				return entriesArray;
			}
			return null;
		}catch (Exception e) {
			//e.printStackTrace();
			//NOTE: This error seems to occur whenever a client terminates connection due to premature thread termination in an ADO.
			return null;
		}
	}

	synchronized public String[] GetAllOfType(FieldType type) {
		try {
			if (doc != null) {
				ArrayList<String> results = new ArrayList<String>();
				String[] resultsArray;

				NodeList nList = doc.getElementsByTagName("Data");
				Node nNode = nList.item(0);
				for (int i = 0; i < nNode.getChildNodes().getLength(); i++) {
					Node subNode = nNode.getChildNodes().item(i);
					if (subNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) subNode;
						if (type.equals(FieldType.DATES)) {
							String date = eElement.getAttribute("Date");
							results.add(date);
						}else if (type.equals(FieldType.AIRCRAFTS)) {
							String aircraft = eElement.getAttribute("Aircraft");
							results.add(aircraft);
						}else if (type.equals(FieldType.PILOTS)) {
							String pilot = eElement.getAttribute("Pilot");
							results.add(pilot);
						}
					}
				}

				resultsArray = new String[results.size()];
				for (int i = 0; i < results.size(); i++) {
					resultsArray[i] = results.get(i);
				}
				return resultsArray;
			}
			return null;
		}catch (Exception e) {
			//e.printStackTrace();
			return null;
		}
	}

	synchronized public String CreateEntry(String caller, Payload.Entry entry) {
		try {
			NodeList nList = doc.getElementsByTagName("Data");
			Element dataPoint = (Element)nList.item(0);
			Element element = doc.createElement("Entry" + (dataPoint.getChildNodes().getLength()));

			dataPoint.appendChild(element);
			element.setAttribute("Date", entry.getRawDate());
			element.setAttribute("Aircraft", entry.getAircraft());
			element.setAttribute("Pilot", entry.getPilot());

			if (entry.getFlightData().length() > 0) {
				element.setAttribute("Notes", "f_" + entry.getFlightData());
			}else if (entry.getRepairData().length() > 0) {
				element.setAttribute("Notes", "m_" + entry.getRepairData());
			}else if (entry.getTrainingData().length() > 0) {
				element.setAttribute("Notes", "t_" + entry.getTrainingData());
			}

			ui.accessCount++;

			util.Log("Reloading the DOM XML System...");

			Save();
			return "";
		}catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	synchronized public String RemoveEntryByType(String caller, String type, String search) {
		try {
			NodeList nList = doc.getElementsByTagName("Data");
			Node nNode = nList.item(0);
			for (int i = 0; i < nNode.getChildNodes().getLength(); i++) {
				Node subNode = nNode.getChildNodes().item(i);
				if (subNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) subNode;
					if (type.equals("date")) {
						if (eElement.getAttribute("Date").startsWith(search)) {
							nNode.removeChild(subNode);
							util.Log("The user " + caller + " is deleting '" + subNode.getNodeName() + "' via a " + type + " search for '" + search + "'...");
						}
					}else if (type.equals("pilot")) {
						if (eElement.getAttribute("Pilot").toLowerCase().equals(search)) {
							nNode.removeChild(subNode);
							util.Log("The user " + caller + " is deleting '" + subNode.getNodeName()  + "' via a " + type + " search for '" + search + "'...");
						}
					}else if (type.equals("aircraft")) {
						if (eElement.getAttribute("Aircraft").toLowerCase().equals(search)) {
							nNode.removeChild(subNode);
							util.Log("The user " + caller + " is deleting '" + subNode.getNodeName()  + "' via a " + type + " search for '" + search + "'...");
						}
					}
				}
			}
			
			Save();
			return "";
		}catch (Exception e) {
			//e.printStackTrace();
			return e.getMessage();
		}
	}
	
	synchronized public String AddLog(String caller, Payload.Entry entry) {
		try {
			util.Log("The user " + caller + " is creating a synthetic operational log for 'REGEX_" + "'.");
			
			return null;
		}catch (Exception e) {
			return e.getMessage();
		}
	}

	synchronized public String CreateUser(String caller, String username, String password, String permissions) { 
		try {
			util.Log("Creating userspace for " + username  + " with permissions " + permissions + ".");

			if (!CheckUser(username)) {
				NodeList nList = doc.getElementsByTagName("Users");
				Element eElement = (Element) nList.item(0);

				//Create a template entry.
				Element node = doc.createElement(username.toLowerCase());
				eElement.appendChild(node);

				Attr rank = doc.createAttribute("Rank");
				rank.setValue(permissions);

				Attr pass = doc.createAttribute("Password");
				pass.setValue(password);

				Attr lastlog = doc.createAttribute("LastLogin");

				node.setAttributeNode(rank);
				node.setAttributeNode(pass);
				node.setAttributeNode(lastlog);

				Calendar cal = Calendar.getInstance();
				Comment comm = doc.createComment("Remotely-generated user. Created by " + caller + " on " + cal.getTime());

				node.appendChild(comm);

				ui.accessCount++;

				Save();
				return null;
			}else {
				return "A user with this name already exists.";
			}
		}catch (Exception e) {
			e.printStackTrace();
			return e.getLocalizedMessage();
		}
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
