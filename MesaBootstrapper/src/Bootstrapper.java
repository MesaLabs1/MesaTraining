import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class Bootstrapper {
	DocumentBuilderFactory dbFactory;
	File dbFile;
	DocumentBuilder dbBuilder;
	Document doc;

	static String LOG_NAME = "bootstrapper.log";
	BufferedWriter logWriter;

	public static void main (String[] args) {
		new Bootstrapper();
	}

	public Bootstrapper() {
		Log("Bootstrapper initializing...");
		
		if (new File("update.xml").exists()) {
			LoadConfigFile("update.xml");
			String updatePath = GetAttributeByNode("Update", "Path");
			File update = new File(updatePath);
			if (update.exists()) {
				if (updatePath.endsWith("zip")) {
					Log("Preparing to unzip dynamic update...");
					Unzip(updatePath, "update");
					if (new File("update").exists()) {
						File directive = new File ("update/directive.xml");
						if (directive.exists()) {
							LoadConfigFile("update/directive.xml");
							Log("Accessing update directive and applying updates...");
							
						}else {
							Log("Does not contain a directive to update with! Update is aborted, no changes have been made.");
						}
					}else {
						Log("Cannot locate 'update' directory! Update is aborted, no changes have been made.");
					}
				}else {
					Log("The target file is not ZIP. Update is aborted, no changes have been made.");
				}
			}else {
				Log("The target file is missing. Update is aborted, no changes have been made.");
			}
		}else {
			Log("The update file is missing. Update is aborted, no changes have been made.");
		}
	}

	public void Unzip(String zipFile, String outputFolder) {
		byte[] buffer = new byte[1024];
		try {
			File folder = new File(outputFolder);
			if(!folder.exists()){
				folder.mkdir();
			}

			ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
			ZipEntry ze = zis.getNextEntry();

			while (ze != null) {
				String fileName = ze.getName();
				File newFile = new File(outputFolder + File.separator + fileName);

				Log("Unpacking: "+ newFile.getAbsoluteFile());
				
				new File(newFile.getParent()).mkdirs();
				
				FileOutputStream fos = new FileOutputStream(newFile);             

				int len;
				
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}

				fos.close();   
				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();

			Log("Unpack complete. Proceeding...");

		}catch(IOException ex){
			ex.printStackTrace(); 
		}
	}

	public void LoadConfigFile(String target) {		
		dbFile = new File(target);
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

	public String GetAttributeByNode(String node, String attribute) {
		if (doc != null) {
			try {
				Log("Accessing '" + node + "' for an attribute called '" + attribute + "'.");
				NodeList nList = doc.getElementsByTagName(node);
				Node nNode = nList.item(0);
				Element eElement = (Element) nNode;
				return eElement.getAttribute(attribute);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			Log("Premature call to GetAttributeByNode! You must first initialize the Document with LoadConfigFile(). Ignoring.");
			return null;
		}
		return null;
	}


	private static final int CLIENT_CODE_STACK_INDEX;
	static {
		int i = 0;
		for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
			i++;
			if (ste.getClassName().equals(Bootstrapper.class.getName())) {
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
		
		System.out.println(log);
		
		try {
			logWriter.write(log);
			logWriter.newLine();
			logWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
