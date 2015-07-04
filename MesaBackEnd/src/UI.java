import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.border.LineBorder;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.Timer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.ListSelectionModel;

import java.awt.Font;

import javax.swing.border.BevelBorder;

/**
 * This class is a simple GUI for this backend. You can disable it from appearing by adding the tag -nogui to
 * the launch parameters.
 * @author hackjunky, jacrin
 *
 */
public class UI extends JFrame{
	private static final long serialVersionUID = 3855917297699177329L;

	//Passed-in Objects
	Utils util;
 
	//GUI Controls
	JLabel lblUptime;
	JLabel lblClients;
	JLabel lblOverhead;
	JLabel lblOverall;
	JLabel lblThreads;
	JLabel lblHandles;
	JLabel lblErrors;
	JLabel lblMemUsage;
	JLabel lblAccessCount;
	JLabel lblVersion;

	JLabel lblRunning;

	JPanel pnlRed;
	JPanel pnlYellow;
	JPanel pnlGreen;

	JScrollPane scrollPane;
	JList<String> consoleList;

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
	
	/*
	 * Async Flags
	 * 
	 * What's the purpose of an Async flag? Since this thread runs in a separate thread, we cannot hook functions
	 * in the main loop in the server. Therefore, we set these flags and expect the server thread to read these
	 * flags for modifications to the thread. 
	 * 
	 * Example: Setting activate=true will make the main thread read it and set it to false when done.
	 */
	boolean activate = false;
	boolean deactivate = false;

	//Indicator
	enum ServerStatus {
		Active, Inactive, Busy
	}

	ServerStatus status;
	
	ActivityMonitor mon0;

	/*
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

	/*
	 * This is a Visual Interface. Since this is the case, we use a JavaX Timer to regulate the updating of controls. See the class below.
	 * 
	 * We initialize the timer in the constructor. Then, we start it. The timer hooks the ActionEvent of the EventHandler class, and calls
	 * the actionPerformed method in the EventHandler class.
	 */

	public UI(Utils u) {
		util = u;

		//setType(Type.UTILITY);

		setAlwaysOnTop(true);

		setTitle("MESA Backend - User Interface");
		//We chose 640x480 because this is the default minimum resolution support ratio for non-graphically enabled systems.
		this.setSize(new Dimension(640, 480));

		//Make sure that when the user terminates the UI, we don't leave our socket(s) running in the background.
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
		pnlOutput.setBounds(6, 108, 622, 309);
		pnlHolder.add(pnlOutput);
		pnlOutput.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		pnlOutput.setLayout(null);

		JLabel lblOutputH = new JLabel("Output");
		lblOutputH.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		lblOutputH.setBounds(6, 6, 61, 16);
		pnlOutput.add(lblOutputH);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 30, 610, 254);
		pnlOutput.add(scrollPane);
		
		consoleList = new JList<String>(util.logs);
		consoleList.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		consoleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		scrollPane.setViewportView(consoleList);

		pnlRed = new JPanel();
		pnlRed.setBackground(Color.RED);
		pnlRed.setBorder(new LineBorder(new Color(0, 0, 0)));
		pnlRed.setBounds(600, 288, 16, 16);
		pnlOutput.add(pnlRed);

		pnlYellow = new JPanel();
		pnlYellow.setBackground(Color.YELLOW);
		pnlYellow.setBorder(new LineBorder(new Color(0, 0, 0)));
		pnlYellow.setBounds(600, 288, 16, 16);
		pnlOutput.add(pnlYellow);

		pnlGreen = new JPanel();
		pnlGreen.setBackground(Color.GREEN);
		pnlGreen.setBorder(new LineBorder(new Color(0, 0, 0)));
		pnlGreen.setBounds(600, 288, 16, 16);
		pnlOutput.add(pnlGreen);

		JButton btnClear = new JButton("Clear");
		btnClear.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		btnClear.setMnemonic('c');
		btnClear.setBounds(555, 5, 61, 21);
		btnClear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				util.logs.clear();
			}
		});
		pnlOutput.add(btnClear);

		progressBar = new JProgressBar();
		progressBar.setBounds(6, 287, 220, 14);
		pnlOutput.add(progressBar);

		lblRunning = new JLabel("Running... (XXX%)");
		lblRunning.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		lblRunning.setBounds(238, 287, 252, 16);
		pnlOutput.add(lblRunning);
		
				lblUptime = new JLabel("Uptime: XXX:XXX:XXX");
				lblUptime.setBounds(495, 290, 101, 13);
				pnlOutput.add(lblUptime);
				lblUptime.setFont(new Font("Lucida Grande", Font.PLAIN, 10));

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
		panel.setBounds(379, 6, 249, 96);
		pnlHolder.add(panel);
		panel.setLayout(null);

		JLabel lblByMOI = new JLabel("By Jad Aboulhosn and Jacqueline Clow");
		lblByMOI.setBounds(460, 416, 184, 16);
		pnlHolder.add(lblByMOI);
		lblByMOI.setFont(new Font("Lucida Grande", Font.PLAIN, 9));

		JLabel lblVersionH = new JLabel("You are running Mesa Server version");
		lblVersionH.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
		lblVersionH.setBounds(6, 416, 169, 16);
		pnlHolder.add(lblVersionH);

		lblVersion = new JLabel("XX");
		lblVersion.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
		lblVersion.setBounds(178, 416, 169, 16);
		pnlHolder.add(lblVersion);

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
		mntmActivate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				activate = true;
			}
		});

		mntmDeactivate = new JMenuItem("Deactivate");
		mntmDeactivate.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		mnListener.add(mntmDeactivate);
		mntmDeactivate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deactivate = true;
			}
		});

		mnSystem = new JMenu("System");
		mnSystem.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		menuBar.add(mnSystem);

		mntmClearStatistics = new JMenuItem("Clear Statistics");
		mntmClearStatistics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				numClients = 0;
				numOverhead = 0;
				numOverall = 0;
				numThreads = 0;
				numHandles = 0;
				numGCs = 0;
				numErrors = 0;
				memUsage = 0;
				accessCount = 0;
				util.Log("Statistics reset!");
			}
		});
		mntmClearStatistics.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		mnSystem.add(mntmClearStatistics);

		mnHelp = new JMenu("Help");
		mnHelp.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		menuBar.add(mnHelp);

		menuAbout = new JMenuItem("About");
		menuAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(UI.this.getContentPane(), "WebConnect Server Backend designed by Jad Aboulhosn and Jacqueline Clow. 2015.");
			}
		});
		menuAbout.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		mnHelp.add(menuAbout);

		mntmHelp = new JMenuItem("Help");
		mntmHelp.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		mnHelp.add(mntmHelp);
		
		this.setResizable(false);

		SetUpInterface();	//Anything that needs to be done, should get done here.

		this.setVisible(true);
		
		mon0 = new ActivityMonitor(panel);
		
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
			ui.lblHandles.setText("" + ui.numHandles);
			ui.lblOverall.setText("" + ui.numOverall);
			ui.lblThreads.setText("" + ui.numThreads);
			ui.lblOverhead.setText("" + ui.numOverhead);
			ui.lblVersion.setText("" + Init.PropertyMaster.BACKEND_VERSION);

			//Special variables
			ui.lblUptime.setText(convertUpTime());
			ui.lblMemUsage.setText(getMemoryStatistics());

			if (internalTicks == 10) {	//1 second has passed
				ui.uptime++;
				internalTicks = 0;
			}
			
			consoleList.ensureIndexIsVisible(util.logs.size() - 1);

			if (status.equals(ServerStatus.Inactive)) {
				lblRunning.setText("Inactive... (" + progressBar.getValue() + "%)");
				pnlRed.setVisible(true);
				pnlYellow.setVisible(false);
				pnlGreen.setVisible(false);
				
				mntmActivate.setEnabled(true);
				mntmDeactivate.setEnabled(false);
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
				
				mntmActivate.setEnabled(false);
				mntmDeactivate.setEnabled(true);
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
				//memUsage = (int)temp;
			}else {
				if (mBytes > 0) {
					double temp = bytes / 1024 / 1024;
					temp = Double.valueOf(df.format(temp));
					memory = temp + " MB";
					memUsage = (int)temp;
				}else {
					if (kBytes > 0) {
						double temp = bytes / 1024;
						temp = Double.valueOf(df.format(temp));
						memory = temp + " KB";
						//memUsage = (int)temp;
					}else {
						memory = bytes + " B";
						//memUsage = (int)bytes;
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
				seconds = (uptime - (div * 60));
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
	
	/**
	 * This class converts any JPanel into an activity monitor for this application.
	 * author hackjunky
	 *
	 */
	public class ActivityMonitor implements ActionListener{
		JPanel target;
		
		Timer timer;
		Graphics g;
		Graphics2D g2d;
		FontMetrics fm;
		
		Rectangle mon0Area;
		Rectangle mon1Area;
		
		//Monitor vars
		int mon0YCap = 1;
		int mon1YCap = 1;
		
		int incrementMon0 = 0;
		int incrementMon1 = 0;
		
		int mon0XValue = 2;
		int mon1XValue = 2;
		
		boolean clearMon0 = true;
		boolean clearMon1 = true;
		
		Point mon0temp = new Point(0, 0);
		Point mon1temp = new Point(0, 0);
		
		public ActivityMonitor (JPanel t) {
			target = t;
			timer = new Timer(100, this);
			g = target.getGraphics();
			g2d = (Graphics2D) g;
			fm = g2d.getFontMetrics();
			
			g2d.setFont(target.getFont().deriveFont(10.0f));
			
			Activate();
		}
		
		public void Activate() {
			timer.start();
		}
		
		public void Deactivate() {
			timer.stop();
		}
		
		private void DrawRect(Color color, Rectangle r) {
			Color c = g2d.getColor();
			
			g2d.setColor(Color.GRAY);
			g2d.fillRect(r.x, r.y, r.width, r.height);
			
			DrawGrid(true, Color.GREEN, mon0Area, 10, 2);
			DrawGrid(false, Color.RED, mon1Area, 5, 2);
			
			g2d.setColor(color);
			g2d.drawRect(r.x, r.y, r.width, r.height);
			
			g2d.setColor(c);
		}
		
		private void DrawGrid(boolean toggle, Color color, Rectangle r, int rowX, int rowY) {
			Color c = g2d.getColor();
			g2d.setColor(color);
			//Draw the vertical lines
			int xGap = r.width / rowX;
			for (int i = r.x + xGap; i < r.width; i += xGap) {
				g2d.drawLine(i, r.y, i, r.height + r.y);
			}
			
			if (toggle) {
				incrementMon0 = xGap;
			}else {
				incrementMon1 = xGap;
			}
			
			//Draw the horizontal lines
			int yGap = r.height / rowY;
			for (int i = r.y + yGap; i < r.height; i += yGap) {
				g2d.drawLine(r.x, i, r.width + r.x, i);
			}
			g2d.setColor(c);
		}
		
		private void UpdateStatsCap() {
//			if (numOverhead > mon0YCap) {
//				mon0YCap = numOverhead;
//			}
//			if (memUsage > mon1YCap) {
//				mon1YCap = memUsage;
//			}
			//Network Overhead Cap
			mon0YCap = 50;
			
			//Max System Memory
			long allocatedMemory = (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory());
			long presumableFreeMemory = Runtime.getRuntime().maxMemory() - allocatedMemory;
			double temp = presumableFreeMemory / 1024 / 1024;
			mon1YCap = (int)temp;
		}
		
		private void DrawValues() {
			//Mon0
			g2d.setFont(target.getFont().deriveFont(10.0f));
			double height = ((double)numOverhead / (double)mon0YCap) * mon0Area.height;
			Point p = new Point(mon0XValue, (mon0Area.y + mon0Area.height - 4 - (int)height));
			g2d.fillRect(p.x, p.y, 3, 3);
			String overhead = String.valueOf(numOverhead);
			g2d.drawString(overhead, p.x - (fm.stringWidth(overhead) / 2), p.y - 2);
			mon0XValue += incrementMon0;
			if (mon0XValue > incrementMon0) {
				g2d.drawLine(mon0temp.x, mon0temp.y + 1, p.x, p.y + 1);
			}
			mon0temp = p;
			if (mon0XValue >= mon0Area.width) {
				mon0XValue = incrementMon0;
				mon0temp = new Point(2, p.y);
				clearMon0 = true;
			}
			
			//Mon1
			g2d.setFont(target.getFont().deriveFont(10.0f));
			height = ((double)memUsage / (double)mon1YCap) * mon1Area.height;
			p = new Point(mon1XValue, (mon1Area.y + mon1Area.height - 4 - (int)height));
			g2d.fillRect(p.x, p.y, 3, 3);
			String memory = lblMemUsage.getText();
			g2d.drawString(memory, p.x - (fm.stringWidth(memory) / 2) + 2, p.y - 2);
			mon1XValue += incrementMon1;
			if (mon1XValue > incrementMon1) {
				g2d.drawLine(mon1temp.x, mon1temp.y + 1, p.x, p.y + 1);
			}
			mon1temp = p;
			if (mon1XValue >= mon1Area.width) {
				mon1XValue = incrementMon1;
				mon1temp = new Point(2, p.y);
				clearMon1 = true;
			}
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			g2d.drawString("Network Activity", 4, fm.getAscent());
			g2d.drawString("Disk Activity", 4, target.getHeight() / 2 + fm.getAscent() - 4);
			
			mon0Area = new Rectangle(4, fm.getHeight() + 1, target.getWidth() - 8, target.getHeight() / 2 - fm.getHeight() - 4);
			mon1Area = new Rectangle(4, target.getHeight() / 2 + fm.getHeight() - 4, target.getWidth() - 8, target.getHeight() / 2 - fm.getHeight() - 1);
		
			if (clearMon0) {
				DrawRect(Color.BLACK, mon0Area);
				clearMon0 = false;
			}
			
			if (clearMon1) {
				DrawRect(Color.BLACK, mon1Area);
				clearMon1 = false;
			}
			
			UpdateStatsCap();
			DrawValues();
		}
	}
}
