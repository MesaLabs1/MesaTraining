import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

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

	class DatabaseRunner implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
		}
	}
}
