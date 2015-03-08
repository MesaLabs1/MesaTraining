import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.border.LineBorder;

import java.awt.Color;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.Timer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.ListSelectionModel;

/**
 * This class is a simple GUI for this backend. You can disable it from appearing by adding the tag -nogui to
 * the launch parameters.
 * @author hackjunky, jacrin
 *
 */
public class UI extends JFrame{
	//Passed-in Objects
	Utils util;

	//GUI Controls
	JLabel lblUptime;
	JLabel lblClients;
	JLabel lblOverhead;
	JLabel lblOverall;
	JLabel lblThreads;
	JLabel lblHandles;
	JLabel lblGC;
	JLabel lblErrors;
	JLabel lblMemUsage;
	JLabel lblAccessCount;
	
	JLabel lblRunning;

	JPanel pnlRed;
	JPanel pnlYellow;
	JPanel pnlGreen;

	JScrollPane scrollPane;
	JList consoleList;
	DefaultListModel listModel;

	JProgressBar progressBar;

	JMenuBar menuBar;
	JMenu mnConnection;
	JMenu mnListener;
	JMenu mnSystem;
	JMenu mnHelp;
	JMenuItem mntmActivate;
	JMenuItem mntmDeactivate;
	JMenuItem mntmClearStatistics;
	JMenuItem menuAbout;
	JMenuItem mntmHelp;

	//Timer
	Timer eventTicker;
	EventHandler eventHandler;

	//Status Variables
	int uptime;
	int numClients;
	int numOverhead;
	int numOverall;
	int numThreads;
	int numHandles;
	int numGCs;
	int numErrors;
	int memUsage;
	int accessCount;

	ArrayList logs;
	
	//Indicator
	enum ServerStatus {
		Active, Inactive, Busy
	}
	
	ServerStatus status;

	/**
	 * The constructor here will simply define the GUI components, make sure the style is visually neutral (to avoid random UI inconsistencies),
	 * and set the font package of the target system.
	 * 
	 * IT is highly recommended that you install WindowBuilder (native to Luna Eclipse) in order to modify
	 * the User Interface, as hard-coding interfaces is laborious and pretty freaking boring.
	 * 
	 * Note: Notice below, that we used Xs to placehold characters for their size. Why did we use Xs? The character
	 * X takes up the most possible space when capitalized, as opposed to a character like ?, where it does not take
	 * up as much space as as number. Always use a capital letter to placehold label sizes.
	 */

	/**
	 * This is a Visual Interface. Since this is the case, we use a JavaX Timer to regulate the updating of controls. See the class below.
	 * 
	 * We initialize the timer in the constructor. Then, we start it. The timer hooks the ActionEvent of the EventHandler class, and calls
	 * the actionPerformed method in the EventHandler class.
	 */

	public UI(Utils u) {
		util = u;
		logs = new ArrayList<String>();

		setType(Type.UTILITY);
		
		setAlwaysOnTop(true);

		setTitle("MESA Backend - User Interface");
		//We chose 640x480 because this is the default minimum resolution support ratio for non-graphically enabled systems.
		this.setSize(new Dimension(640, 480));

		//Make sure that when the user terminates the UI, we don't leave our socket running in the background.
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel pnlHolder = new JPanel();
		getContentPane().add(pnlHolder);
		pnlHolder.setLayout(null);

		JPanel pnlConnection = new JPanel();
		pnlConnection.setBounds(6, 6, 263, 96);
		pnlHolder.add(pnlConnection);
		pnlConnection.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		pnlConnection.setLayout(null);

		JLabel lblConnStatusH = new JLabel("Connection Status");
		lblConnStatusH.setBounds(6, 6, 119, 16);
		pnlConnection.add(lblConnStatusH);

		JLabel lblClientsH = new JLabel("Clients:");
		lblClientsH.setBounds(16, 26, 57, 16);
		pnlConnection.add(lblClientsH);

		JLabel lblOverheadH = new JLabel("Overhead:");
		lblOverheadH.setBounds(16, 45, 71, 16);
		pnlConnection.add(lblOverheadH);

		lblUptime = new JLabel("XXX:XXX:XXX");
		lblUptime.setBounds(161, 6, 92, 16);
		pnlConnection.add(lblUptime);

		JLabel lblOverallH = new JLabel("Overall:");
		lblOverallH.setBounds(16, 66, 48, 16);
		pnlConnection.add(lblOverallH);

		lblClients = new JLabel("XXX");
		lblClients.setBounds(85, 26, 29, 16);
		pnlConnection.add(lblClients);

		lblOverhead = new JLabel("XXXXXX");
		lblOverhead.setBounds(85, 45, 57, 16);
		pnlConnection.add(lblOverhead);

		lblOverall = new JLabel("XXXXXXXXXXXXX");
		lblOverall.setBounds(85, 66, 112, 16);
		pnlConnection.add(lblOverall);

		JPanel pnlOutput = new JPanel();
		pnlOutput.setBounds(6, 108, 611, 305);
		pnlHolder.add(pnlOutput);
		pnlOutput.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		pnlOutput.setLayout(null);

		JLabel lblOutputH = new JLabel("Output");
		lblOutputH.setBounds(6, 6, 61, 16);
		pnlOutput.add(lblOutputH);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 25, 597, 244);
		pnlOutput.add(scrollPane);

		listModel = new DefaultListModel();
		consoleList = new JList(listModel);
		consoleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		scrollPane.setViewportView(consoleList);

		pnlRed = new JPanel();
		pnlRed.setBackground(Color.RED);
		pnlRed.setBorder(new LineBorder(new Color(0, 0, 0)));
		pnlRed.setBounds(585, 280, 16, 16);
		pnlOutput.add(pnlRed);

		pnlYellow = new JPanel();
		pnlYellow.setBackground(Color.YELLOW);
		pnlYellow.setBorder(new LineBorder(new Color(0, 0, 0)));
		pnlYellow.setBounds(585, 280, 16, 16);
		pnlOutput.add(pnlYellow);

		pnlGreen = new JPanel();
		pnlGreen.setBackground(Color.GREEN);
		pnlGreen.setBorder(new LineBorder(new Color(0, 0, 0)));
		pnlGreen.setBounds(585, 280, 16, 16);
		pnlOutput.add(pnlGreen);

		JButton btnClear = new JButton("Clear");
		btnClear.setMnemonic('c');
		btnClear.setBounds(540, 6, 61, 16);
		btnClear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				util.logs = new ArrayList();
				listModel.clear();
			}
		});
		pnlOutput.add(btnClear);

		progressBar = new JProgressBar();
		progressBar.setBounds(6, 280, 183, 14);
		pnlOutput.add(progressBar);

		lblRunning = new JLabel("Running... (XXX%)");
		lblRunning.setBounds(199, 278, 117, 16);
		pnlOutput.add(lblRunning);

		JPanel pnlSystem = new JPanel();
		pnlSystem.setBounds(274, 6, 343, 96);
		pnlHolder.add(pnlSystem);
		pnlSystem.setBorder(new LineBorder(new Color(0, 0, 0)));
		pnlSystem.setLayout(null);

		JLabel lblPeerInformationH = new JLabel("System Information");
		lblPeerInformationH.setBounds(6, 6, 132, 16);
		pnlSystem.add(lblPeerInformationH);

		JLabel lblThreadsH = new JLabel("Threads:");
		lblThreadsH.setBounds(16, 25, 61, 16);
		pnlSystem.add(lblThreadsH);

		JLabel lblHandlesH = new JLabel("Handles:");
		lblHandlesH.setBounds(16, 43, 61, 16);
		pnlSystem.add(lblHandlesH);

		JLabel lblGCH = new JLabel("Garbage Collections:");
		lblGCH.setBounds(16, 74, 137, 16);
		pnlSystem.add(lblGCH);

		JLabel lblErrorsH = new JLabel("Errors:");
		lblErrorsH.setBounds(255, 74, 35, 16);
		pnlSystem.add(lblErrorsH);

		JLabel lblMemoryUsageH = new JLabel("Memory Usage:");
		lblMemoryUsageH.setBounds(134, 25, 102, 16);
		pnlSystem.add(lblMemoryUsageH);

		JLabel lblAccessCountH = new JLabel("Access Count:");
		lblAccessCountH.setBounds(134, 43, 102, 16);
		pnlSystem.add(lblAccessCountH);

		lblThreads = new JLabel("XXX");
		lblThreads.setBounds(77, 25, 31, 16);
		pnlSystem.add(lblThreads);

		lblHandles = new JLabel("XXXXX");
		lblHandles.setBounds(77, 43, 45, 16);
		pnlSystem.add(lblHandles);

		lblGC = new JLabel("XXXXXXXXXXXXX");
		lblGC.setBounds(125, 74, 111, 16);
		pnlSystem.add(lblGC);

		lblErrors = new JLabel("XXX");
		lblErrors.setBounds(300, 74, 30, 16);
		pnlSystem.add(lblErrors);

		lblMemUsage = new JLabel("XXXXXXXXXXXX K");
		lblMemUsage.setBounds(235, 25, 117, 16);
		pnlSystem.add(lblMemUsage);

		lblAccessCount = new JLabel("XXXXXXXXXXXXXX");
		lblAccessCount.setBounds(235, 43, 117, 16);
		pnlSystem.add(lblAccessCount);

		menuBar = new JMenuBar();
		menuBar.setBackground(Color.LIGHT_GRAY);
		setJMenuBar(menuBar);

		mnConnection = new JMenu("Connection");
		menuBar.add(mnConnection);

		mnListener = new JMenu("Listener");
		mnConnection.add(mnListener);

		mntmActivate = new JMenuItem("Activate");
		mnListener.add(mntmActivate);

		mntmDeactivate = new JMenuItem("Deactivate");
		mnListener.add(mntmDeactivate);

		mnSystem = new JMenu("System");
		menuBar.add(mnSystem);

		mntmClearStatistics = new JMenuItem("Clear Statistics");
		mnSystem.add(mntmClearStatistics);

		mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		menuAbout = new JMenuItem("About");
		mnHelp.add(menuAbout);

		mntmHelp = new JMenuItem("Help");
		mnHelp.add(mntmHelp);

		SetUpInterface();	//Anything that needs to be done, should get done here.

		this.setVisible(true);
		
		util.Log("User Interface successfully initialized. Standby for Network Layer...");
	}

	/**
	 * All things that need to get done before the interface is shown, it needs to be added here.
	 */
	public void SetUpInterface() {
		status = ServerStatus.Inactive;
		
		eventHandler = new EventHandler();
		eventTicker = new Timer(100, eventHandler);		//This timer will execute once every 100 milliseconds (10 times a second).
		eventTicker.start();
	}
	
	/**
	 * Set the UI status.
	 * @param status The status to set the UI to.
	 */
	public void SetStatus(ServerStatus status) {
		this.status = status;
	}

	public class EventHandler implements ActionListener {
		UI ui;
		int internalTicks;

		public EventHandler() {
			ui = UI.this;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			//Set indicator controls to representative variable
			ui.lblAccessCount.setText("" + ui.accessCount);
			ui.lblClients.setText("" + ui.numClients);
			ui.lblErrors.setText("" + ui.numErrors);
			ui.lblGC.setText("" + ui.numGCs);
			ui.lblHandles.setText("" + ui.numHandles);
			ui.lblOverall.setText("" + ui.numOverall);
			ui.lblThreads.setText("" + ui.numThreads);
			ui.lblOverhead.setText("" + ui.numOverhead);

			//Special variables
			ui.lblUptime.setText(convertUpTime());
			ui.lblMemUsage.setText(getMemoryStatistics());

			//Update console
			int currSize = consoleList.getModel().getSize();
			int totalSize = util.logs.size();
			if (currSize != totalSize) {
				int net = totalSize - currSize;
				if (net > 0) {
					for (int i = totalSize - net; i < totalSize; i++) {
						listModel.addElement(util.logs.get(i));
					}
				}
			}

			if (internalTicks == 10) {	//1 second has passed
				ui.uptime++;
				internalTicks = 0;
			}
			
			if (status.equals(ServerStatus.Inactive)) {
				lblRunning.setText("Inactive... (" + progressBar.getValue() + "%)");
				pnlRed.setVisible(true);
				pnlYellow.setVisible(false);
				pnlGreen.setVisible(false);
			}else if (status.equals(ServerStatus.Busy)) {
				lblRunning.setText("Busy... (" + progressBar.getValue() + "%)");
				pnlRed.setVisible(false);
				pnlYellow.setVisible(true);
				pnlGreen.setVisible(false);
			}else if (status.equals(ServerStatus.Active)) {
				lblRunning.setText("Running.. (" + progressBar.getValue() + "%)");
				pnlRed.setVisible(false);
				pnlYellow.setVisible(false);
				pnlGreen.setVisible(true);
			}
			
			internalTicks++;
			
			consoleList.setSelectedIndex(consoleList.getModel().getSize() - 1);
		}

		/**
		 * Converts the memory usage from bytes to a suitable unit, and then puts it in String format.
		 * @return The String format of the memory usage as XXXXXX KB/MB/GB
		 */
		public String getMemoryStatistics() {
			String memory;

			Runtime runtime = Runtime.getRuntime();
			long bytes = runtime.totalMemory() - runtime.freeMemory();
			int kBytes = (int) (bytes / 1024);
			int mBytes = kBytes / 1024;
			int gBytes = mBytes / 1024;

			DecimalFormat df = new DecimalFormat("0.##");
			df.setRoundingMode(RoundingMode.DOWN);

			if (gBytes > 0) {
				double temp = bytes / 1024 / 1024 / 1024;
				temp = Double.valueOf(df.format(temp));
				memory = temp + " GB";
			}else {
				if (mBytes > 0) {
					double temp = bytes / 1024 / 1024;
					temp = Double.valueOf(df.format(temp));
					memory = temp + " MB";
				}else {
					if (kBytes > 0) {
						double temp = bytes / 1024;
						temp = Double.valueOf(df.format(temp));
						memory = temp + " KB";
					}else {
						memory = bytes + " B";
					}
				}
			}

			return memory;
		}

		/**
		 * Converts the uptime, in the format of an integer, to a String of HHH:MM:SS
		 * @return The String time in HHH:MM:SS
		 */
		public String convertUpTime() {
			String sUptime = "";
			int hours = 0;
			int minutes = 0;
			int seconds = 0;

			int uptime = ui.uptime;
			int div = uptime / 60;

			if (div > 0) {
				seconds = (uptime - (seconds * div));
				if (div > 60) {
					hours = (div / 60);
					minutes = (div - (hours * 60));
				}else {
					minutes = div;
				}
			}else {
				seconds = uptime;
			}

			String sHours = "";
			String sMinutes = "";
			String sSeconds = "";

			if (seconds < 10) {
				sSeconds = "0" + seconds;
			}else {
				sSeconds = "" + seconds;
			}

			if (minutes < 10) {
				sMinutes = "0" + minutes;
			}else {
				sMinutes = "" + minutes;
			}

			if (hours < 100) {
				sHours = "00" + hours;
			}else {
				sHours = "" + hours;
			}
			
			sUptime = "Uptime: " + sHours + ":" + sMinutes + ":" + sSeconds;

			return sUptime;
		}
	}
}
