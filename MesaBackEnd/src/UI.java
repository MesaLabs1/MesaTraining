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
import java.awt.Font;

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

		//setType(Type.UTILITY);
		
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
		pnlConnection.setBounds(6, 6, 138, 96);
		pnlHolder.add(pnlConnection);
		pnlConnection.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		pnlConnection.setLayout(null);

		JLabel lblConnStatusH = new JLabel("Connection Status");
		lblConnStatusH.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblConnStatusH.setBounds(6, 3, 119, 16);
		pnlConnection.add(lblConnStatusH);

		JLabel lblClientsH = new JLabel("Clients:");
		lblClientsH.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblClientsH.setBounds(16, 21, 57, 16);
		pnlConnection.add(lblClientsH);

		JLabel lblOverheadH = new JLabel("Overhead:");
		lblOverheadH.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblOverheadH.setBounds(16, 38, 71, 16);
		pnlConnection.add(lblOverheadH);

		JLabel lblOverallH = new JLabel("Overall:");
		lblOverallH.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblOverallH.setBounds(16, 56, 48, 16);
		pnlConnection.add(lblOverallH);

		lblClients = new JLabel("XXX");
		lblClients.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblClients.setBounds(73, 21, 29, 16);
		pnlConnection.add(lblClients);

		lblOverhead = new JLabel("XXXXXX");
		lblOverhead.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblOverhead.setBounds(73, 38, 57, 16);
		pnlConnection.add(lblOverhead);

		lblOverall = new JLabel("XXXXXXXXX");
		lblOverall.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblOverall.setBounds(73, 56, 112, 16);
		pnlConnection.add(lblOverall);
		
				lblErrors = new JLabel("XXX");
				lblErrors.setBounds(73, 74, 30, 16);
				pnlConnection.add(lblErrors);
				lblErrors.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
				
						JLabel lblErrorsH = new JLabel("Errors:");
						lblErrorsH.setBounds(16, 74, 35, 16);
						pnlConnection.add(lblErrorsH);
						lblErrorsH.setFont(new Font("Lucida Grande", Font.PLAIN, 10));

		JPanel pnlOutput = new JPanel();
		pnlOutput.setBounds(6, 108, 628, 322);
		pnlHolder.add(pnlOutput);
		pnlOutput.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		pnlOutput.setLayout(null);

		JLabel lblOutputH = new JLabel("Output");
		lblOutputH.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		lblOutputH.setBounds(6, 6, 61, 16);
		pnlOutput.add(lblOutputH);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 30, 616, 266);
		pnlOutput.add(scrollPane);

		listModel = new DefaultListModel();
		consoleList = new JList(listModel);
		consoleList.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		consoleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		scrollPane.setViewportView(consoleList);

		pnlRed = new JPanel();
		pnlRed.setBackground(Color.RED);
		pnlRed.setBorder(new LineBorder(new Color(0, 0, 0)));
		pnlRed.setBounds(606, 300, 16, 16);
		pnlOutput.add(pnlRed);

		pnlYellow = new JPanel();
		pnlYellow.setBackground(Color.YELLOW);
		pnlYellow.setBorder(new LineBorder(new Color(0, 0, 0)));
		pnlYellow.setBounds(606, 300, 16, 16);
		pnlOutput.add(pnlYellow);

		pnlGreen = new JPanel();
		pnlGreen.setBackground(Color.GREEN);
		pnlGreen.setBorder(new LineBorder(new Color(0, 0, 0)));
		pnlGreen.setBounds(606, 300, 16, 16);
		pnlOutput.add(pnlGreen);

		JButton btnClear = new JButton("Clear");
		btnClear.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		btnClear.setMnemonic('c');
		btnClear.setBounds(561, 5, 61, 21);
		btnClear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				util.logs = new ArrayList();
				listModel.clear();
			}
		});
		pnlOutput.add(btnClear);

		progressBar = new JProgressBar();
		progressBar.setBounds(6, 302, 220, 14);
		pnlOutput.add(progressBar);

		lblRunning = new JLabel("Running... (XXX%)");
		lblRunning.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblRunning.setBounds(238, 302, 359, 16);
		pnlOutput.add(lblRunning);
		
				JLabel lblGCH = new JLabel("Garbage Collections:");
				lblGCH.setBounds(155, 6, 111, 16);
				pnlOutput.add(lblGCH);
				lblGCH.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
				
						lblGC = new JLabel("XXXXXXX");
						lblGC.setBounds(262, 6, 111, 16);
						pnlOutput.add(lblGC);
						lblGC.setFont(new Font("Lucida Grande", Font.PLAIN, 10));

		JPanel pnlSystem = new JPanel();
		pnlSystem.setBounds(150, 6, 223, 96);
		pnlHolder.add(pnlSystem);
		pnlSystem.setBorder(new LineBorder(new Color(0, 0, 0)));
		pnlSystem.setLayout(null);

		JLabel lblPeerInformationH = new JLabel("System Information");
		lblPeerInformationH.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblPeerInformationH.setBounds(6, 3, 132, 16);
		pnlSystem.add(lblPeerInformationH);

		JLabel lblThreadsH = new JLabel("Threads:");
		lblThreadsH.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblThreadsH.setBounds(16, 21, 45, 16);
		pnlSystem.add(lblThreadsH);

		JLabel lblHandlesH = new JLabel("Handles:");
		lblHandlesH.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblHandlesH.setBounds(16, 38, 45, 16);
		pnlSystem.add(lblHandlesH);

		JLabel lblMemoryUsageH = new JLabel("Memory Usage:");
		lblMemoryUsageH.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblMemoryUsageH.setBounds(16, 56, 82, 16);
		pnlSystem.add(lblMemoryUsageH);

		JLabel lblAccessCountH = new JLabel("Access Count:");
		lblAccessCountH.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblAccessCountH.setBounds(16, 74, 82, 16);
		pnlSystem.add(lblAccessCountH);

		lblThreads = new JLabel("XXX");
		lblThreads.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblThreads.setBounds(121, 21, 31, 16);
		pnlSystem.add(lblThreads);

		lblHandles = new JLabel("XXXXX");
		lblHandles.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblHandles.setBounds(121, 38, 45, 16);
		pnlSystem.add(lblHandles);

		lblMemUsage = new JLabel("XXXXXXXXXXXX K");
		lblMemUsage.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblMemUsage.setBounds(121, 55, 92, 16);
		pnlSystem.add(lblMemUsage);

		lblAccessCount = new JLabel("XXXXXXXXXXXXXX");
		lblAccessCount.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblAccessCount.setBounds(121, 74, 92, 16);
		pnlSystem.add(lblAccessCount);
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBounds(379, 6, 255, 96);
		pnlHolder.add(panel);
				panel.setLayout(null);
		
				lblUptime = new JLabel("Uptime: XXX:XXX:XXX");
				lblUptime.setBounds(6, 6, 101, 13);
				panel.add(lblUptime);
				lblUptime.setFont(new Font("Lucida Grande", Font.PLAIN, 10));

		menuBar = new JMenuBar();
		menuBar.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		menuBar.setBackground(Color.LIGHT_GRAY);
		setJMenuBar(menuBar);

		mnConnection = new JMenu("Connection");
		mnConnection.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		menuBar.add(mnConnection);

		mnListener = new JMenu("Listener");
		mnListener.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		mnConnection.add(mnListener);

		mntmActivate = new JMenuItem("Activate");
		mntmActivate.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		mnListener.add(mntmActivate);

		mntmDeactivate = new JMenuItem("Deactivate");
		mntmDeactivate.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		mnListener.add(mntmDeactivate);

		mnSystem = new JMenu("System");
		mnSystem.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		menuBar.add(mnSystem);

		mntmClearStatistics = new JMenuItem("Clear Statistics");
		mntmClearStatistics.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		mnSystem.add(mntmClearStatistics);

		mnHelp = new JMenu("Help");
		mnHelp.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		menuBar.add(mnHelp);

		menuAbout = new JMenuItem("About");
		menuAbout.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		mnHelp.add(menuAbout);

		mntmHelp = new JMenuItem("Help");
		mntmHelp.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
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
