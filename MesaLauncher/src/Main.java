import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.Font;
import javax.swing.border.LineBorder;
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

import java.awt.Color;
import net.miginfocom.swing.MigLayout;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Component;
import javax.swing.Box;

public class Main extends JFrame {
	DocumentBuilderFactory dbFactory;
	File dbFile;
	DocumentBuilder dbBuilder;
	Document doc;

	int CURRENT_VERSION = -1;

	JLabel lblUpdate;

	AsyncUpdate adoUpdate;
	private JButton btnExit;


	public static void main (String[] args) {
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

		new Main();
	}

	public Main() {
		adoUpdate = new AsyncUpdate();
		adoUpdate.downloadFile("https://dl.dropboxusercontent.com/s/saq1osgfldukj21/status.xml?dl=0", "status.xml");

		File ver = new File("version");
		if (ver.exists()) {
			BufferedReader reader;
			try {
				reader = new BufferedReader(new FileReader(ver));
				String version = reader.readLine();
				CURRENT_VERSION = Integer.parseInt(version.substring("version=".length(), version.length()));
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			BufferedWriter writer;
			try {
				writer = new BufferedWriter(new FileWriter(ver));
				writer.write("version=-1");
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		loadConfigFile();
		parseConfigFile();

		this.setUndecorated(true);	

		int width = 630;
		int height = 256;
		int monWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		int monHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

		this.setSize(new Dimension(width, height));
		this.setLocation(monWidth / 2 - (width / 2), monHeight / 2 - (height / 2));

		this.setAlwaysOnTop(true);

		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		getContentPane().add(panel, BorderLayout.NORTH);

		JLabel lblWelcomeToMesa = new JLabel("Welcome to Mesa Labs");
		lblWelcomeToMesa.setFont(new Font("Consolas", Font.PLAIN, 20));
		panel.add(lblWelcomeToMesa);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
		getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new MigLayout("", "[24px:n:24px][grow][grow][grow][24px:n:24px]", "[][24px:n][grow][12px:n:12px][12px:n:12px]"));

		JLabel lblWhereWouldYou = new JLabel("What would you like to do?");
		panel_1.add(lblWhereWouldYou, "cell 0 0 4 1,growx,aligny top");

		lblUpdate = new JLabel("Updates may be required! Check for updates.");
		lblUpdate.setForeground(Color.RED);
		panel_1.add(lblUpdate, "cell 1 1 3 1,alignx right");

		JButton btnCreateServer = new JButton("Create a Server");
		btnCreateServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (btnCreateServer.isEnabled()) {
					executeJar("backend.jar");
					System.exit(0);
				}
			}
		});
		btnCreateServer.setIcon(new ImageIcon("create.png"));
		panel_1.add(btnCreateServer, "cell 1 2,grow");

		JButton btnLaunchServer = new JButton("Connect to a Server");
		btnLaunchServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (btnLaunchServer.isEnabled()) {
					executeJar("frontend.jar");
					System.exit(0);
				}
			}
		});
		btnLaunchServer.setIcon(new ImageIcon("connect.png"));
		panel_1.add(btnLaunchServer, "cell 2 2,grow");

		JButton btnCheckforUpdates = new JButton("Check for Updates");
		btnCheckforUpdates.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (btnCheckforUpdates.isEnabled()) {
					if (btnCheckforUpdates.getActionCommand().toLowerCase().startsWith("check")) {
						btnCheckforUpdates.setEnabled(false);
						if (checkForUpdate()) {
							btnCheckforUpdates.setText("Install Updates!");
						}else {
							btnCheckforUpdates.setText("Check for Updates");
						}
						btnCheckforUpdates.setEnabled(true);
					}else if (btnCheckforUpdates.getActionCommand().toLowerCase().startsWith("install")) {
						btnCheckforUpdates.setEnabled(false);
						Thread process = new Thread(adoUpdate);
						process.start();
						restartApplication();
					}
				}
			}
		});
		btnCheckforUpdates.setIcon(new ImageIcon("update.png"));
		panel_1.add(btnCheckforUpdates, "cell 3 2,grow");

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		this.setVisible(true);

		lblUpdate.setText("Updates may be required! Check for updates.");
		
				JLabel lblNewLabel_1 = new JLabel("Created by Jad Aboulhosn for Mesa Labs");
				panel_1.add(lblNewLabel_1, "cell 0 3 3 2,alignx left,aligny bottom");
		
		btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		btnExit.setFont(new Font("Consolas", Font.BOLD, 12));
		panel_1.add(btnExit, "flowx,cell 3 4,alignx right");
		lblUpdate.setVisible(false);

		if (!new File("backend.jar").exists()) {
			btnCreateServer.setEnabled(false);
			lblUpdate.setVisible(true);
			lblUpdate.setText("Malformed (incomplete) installation detected! Run 'Check for Updates'.");
		}

		if (!new File("frontend.jar").exists()) {
			btnLaunchServer.setEnabled(false);
			lblUpdate.setVisible(true);
			lblUpdate.setText("Malformed (incomplete) installation detected! Run 'Check for Updates'.");
		}
	}

	public boolean checkForUpdate() {
		String update = getAttributeByNode("Update", "Version");
		if (Integer.parseInt(update) > CURRENT_VERSION) {
			lblUpdate.setVisible(true);
			lblUpdate.setText("There is an update. Click 'Install Updates' to proceed.");
			lblUpdate.setForeground(Color.RED);
			return true;
		}else {
			lblUpdate.setVisible(true);
			lblUpdate.setText("You are up to date!");
			lblUpdate.setForeground(Color.GREEN);
			return false;
		}
	}

	public class AsyncUpdate implements Runnable{

		@Override
		public void run() {
			doUpdate();
		}

		public void doUpdate() {
			downloadFile(getAttributeByNode("Update", "Backend"), "backend.jar");
			downloadFile(getAttributeByNode("Update", "Frontend"), "frontend.jar");
			downloadFile(getAttributeByNode("Update", "Resources"), "resources.zip");

			unzip("resources.zip", System.getProperty("user.dir"));

			lblUpdate.setText("Update complete! Restarting...");

			
			BufferedWriter writer;
			try {
				writer = new BufferedWriter(new FileWriter(new File("version")));
				writer.write("version=" + getAttributeByNode("Update", "Version"));
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			restartApplication();
		}


		public void unzip(String f, String path) {
			lblUpdate.setText("Unpacking " + f + "...");
			try {
				ZipFile zipFile = new ZipFile(f);
				Enumeration<?> enu = zipFile.entries();
				while (enu.hasMoreElements()) {
					ZipEntry zipEntry = (ZipEntry) enu.nextElement();
					
					String name = path + File.separator + zipEntry.getName();
					long size = zipEntry.getSize();
					long compressedSize = zipEntry.getCompressedSize();
					System.out.printf("name: %-20s | size: %6d | compressed size: %6d\n", name, size, compressedSize);

					File file = new File(name);
					if (name.endsWith("/")) {
						System.out.println("Creating directory '" + name + "'.");
						file.mkdir();
						continue;
					}

					File parent = file.getParentFile();
					if (parent != null) {
						parent.mkdirs();
					}

					InputStream is = zipFile.getInputStream(zipEntry);
					lblUpdate.setText("Decompressing " + file + "...");
					FileOutputStream fos = new FileOutputStream(file);
					byte[] bytes = new byte[1024];
					int length;
					while ((length = is.read(bytes)) >= 0) {
						fos.write(bytes, 0, length);
					}
					is.close();
					fos.close();

				}
				zipFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


		public void downloadFile(String uri, String path) {
			if (lblUpdate != null) {
				lblUpdate.setText("Downloading " + uri + "...");
			}
			URL target = null;
			try {
				target = new URL(uri);
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			ReadableByteChannel rbc = null;
			try {
				rbc = Channels.newChannel(target.openStream());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(path);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}


	public void parseConfigFile() {
		doc.getDocumentElement().normalize();

	}

	public void restartApplication() {
		final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
		File currentJar = null;
		try {
			currentJar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		if(!currentJar.getName().endsWith(".jar"))
			return;

		final ArrayList<String> command = new ArrayList<String>();
		command.add(javaBin);
		command.add("-jar");
		command.add(currentJar.getPath());

		final ProcessBuilder builder = new ProcessBuilder(command);
		try {
			builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	public void executeJar(String file) {
		final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
		File currentJar = new File(file);
		if(!currentJar.getName().endsWith(".jar"))
			return;

		final ArrayList<String> command = new ArrayList<String>();
		command.add(javaBin);
		command.add("-jar");
		command.add(currentJar.getPath());

		final ProcessBuilder builder = new ProcessBuilder(command);
		try {
			builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	public void loadConfigFile() {		
		dbFile = new File("status.xml");
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

	public void save() {
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
		loadConfigFile();
	}

	public String getAttributeByNode(String node, String attribute) {
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
			return null;
		}
		return null;
	}
}
