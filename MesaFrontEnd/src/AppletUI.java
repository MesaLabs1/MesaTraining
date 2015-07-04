import java.applet.Applet;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.border.LineBorder;

import java.awt.Color;

import javax.swing.border.EtchedBorder;
import javax.swing.JLabel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JList;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.border.BevelBorder;
import javax.swing.JProgressBar;
import javax.swing.BoxLayout;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;

import javax.swing.SwingConstants;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

/**
 * This is the Visual Applet that the web browser will display.
 * 
 * @author hackjunky, jacrin
 *
 */
public class AppletUI extends Applet{
	//UID Automatically Generated
	private static final long serialVersionUID = 6373615470012072107L;

	//Applet Info
	int SCREEN_SIZE_X;
	int SCREEN_SIZE_Y;

	//Applet Resources
	Image mesaIcon;

	ImageIcon tab1Icon;
	ImageIcon tab2Icon;
	ImageIcon tab3Icon;
	ImageIcon tab4Icon;

	Image notify1Icon;
	Image notify2Icon;
	Image notify3Icon;
	Image notifyPop;

	//Applet Data
	File appletRoot = new File(new File("").getAbsolutePath());
	File appletRes = new File(appletRoot.getPath() + "/resources/res/");

	//User Data
	String username = "UNDEF";
	int uptime = 0;

	Timer eventTicker;
	EventHandler eventHandler;
	Client client;

	//UI Stuffs
	JLabel lblUsername;
	JLabel lblTime;
	JLabel lblUserPermissions;
	JLabel lblMode;

	JLabel lblUsers;
	JLabel lblUsersOnline;
	JLabel lblNetworkOverhead;
	JLabel lblServerUptime;
	JLabel lblBufferSize;
	JLabel lblNetworkIP;
	JLabel lblMemoryUsage;

	JPanel pnlNotification1;
	JPanel pnlNotification2;
	JPanel pnlNotification3;
	JPanel pnlDate;
	JPanel pnlMesaIcon;


	JList<String> listData;
	JList<String> fLogsList;
	JList<String> tLogsList;
	JList<String> mLogsList;
	JList<String> userList;
	JList<String> rankList;

	//ArrayList of JObjects to be hidden/shown based on rank.
	ArrayList<Component> adminElements;
	ArrayList<Component> superadminElements;

	DefaultListModel<String> consoleModel;

	DATA_MODE dataMode;
	private JTextField inputField;

	//Enumerators
	enum DATA_MODE {
		MODE_DATE, MODE_PILOT, MODE_AIRCRAFT
	}

	public static void main(String[] args) {
		new AppletUI();
	}

	public void AllocateResources() {
		//Verify all appletXXX variables are defined
		boolean verified = true;
		if (!appletRoot.exists()) {
			client.Log("Failed to verify APPLET_ROOT@" + appletRoot.getPath());
			verified = false;
		}
		if (!appletRes.exists()) {
			client.Log("Failed to verify APPLET_RES@" + appletRes.getPath());
			verified = false;
		}

		if (verified) {
			//Let's allocate all the ImageIcons
			mesaIcon = Toolkit.getDefaultToolkit().getImage(appletRes.getPath() + "/mesa.png");
			tab1Icon = new ImageIcon(appletRes.getPath() + "/flight.png");
			tab2Icon = new ImageIcon(appletRes.getPath() + "/maintinence.png");
			tab3Icon = new ImageIcon(appletRes.getPath() + "/training2.png");
			tab4Icon = new ImageIcon(appletRes.getPath() + "/controlpanel.png");
			notify1Icon = Toolkit.getDefaultToolkit().getImage(appletRes.getPath() + "/notify.png");
			notify2Icon = Toolkit.getDefaultToolkit().getImage(appletRes.getPath() + "/quiz.png");
			notify3Icon = Toolkit.getDefaultToolkit().getImage(appletRes.getPath() + "/help.png");
			notifyPop = Toolkit.getDefaultToolkit().getImage(appletRes.getPath() + "/pop.png");
		}
	}

	public AppletUI() {
		/*
		 * Set the visual style to be Linux, since we are developing this for a Linux target. BUT WAIT.
		 * Whoa, isn't Java supposed to be super cross compatible with everything forever? Yes. But heres the thing.
		 * UI Look and Feel, a UI Manager derivative, tells the system how buttons, controls, and interfaces Look.
		 * However, each systems visual interfaces take up marginally more or less pixels-per-control to display.
		 * By setting the L&F to Windows, the display editor will show us the interface as it will appear on the 
		 * host system, not on your specific OS.
		 */
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");

			/**
			 * A Button Mnemonic is the key a user has to press to activate that button automatically.
			 * Since we want the program to display them, when the user hits ALT, the mnemonic will display.
			 */
			UIManager.getDefaults().put("Button.showMnemonics", Boolean.TRUE);
		}catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}catch (ClassNotFoundException e) {

		}catch (InstantiationException e) {
			e.printStackTrace();
		}catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		setBackground(Color.BLACK);
		//We auto-set the default data modality to date-sorted.
		dataMode = DATA_MODE.MODE_DATE;

		consoleModel = new DefaultListModel<String>();
		client = new Client(this);

		//Allocate all Resources, make sure they're there, etc etc
		AllocateResources();

		//Set the size of the Applet to half the screen's Width, and half the height.
		//this.setSize(new Dimension(SCREEN_SIZE_X / 2, SCREEN_SIZE_Y / 2));

		/*
		 * Since we used MIGLayout, which resizes controls automatically with screen size changes, we can design this interface as
		 * large as possible, as assume it can shrink it for us if necessary. In this case, I have chosen the size of my desktop's
		 * monitor in Eclipse (to avoid scrolling around). In the future, another developer could set it to something even larger if they wanted to.
		 */
		this.setMinimumSize(new Dimension(896, 606));
		this.setSize(896, 606);
		setLayout(new BorderLayout(0, 0));

		adminElements = new ArrayList<Component>();
		superadminElements = new ArrayList<Component>();

		Component horizontalStrut_1 = Box.createHorizontalStrut(8);
		horizontalStrut_1.setBackground(Color.BLACK);
		add(horizontalStrut_1, BorderLayout.WEST);

		JPanel pnlHeader = new JPanel();
		pnlHeader.setBackground(Color.DARK_GRAY);
		pnlHeader.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		add(pnlHeader, BorderLayout.NORTH);
		pnlHeader.setLayout(new MigLayout("", "[32px:n:32px,grow][][][][][][][][][][][][][][][grow][][grow][][32px:n:32px,grow][32px:n:32px,grow][32px:n:32px,grow][::8px][]", "[32px:n:32px,grow]"));

		pnlMesaIcon = new JPanel();
		pnlMesaIcon.setBackground(Color.DARK_GRAY);
		pnlHeader.add(pnlMesaIcon, "cell 0 0,grow");

		JLabel lblWelcomeToMesa = new JLabel("Welcome to Mesa Labs WebConnect, ");
		lblWelcomeToMesa.setForeground(Color.WHITE);
		pnlHeader.add(lblWelcomeToMesa, "cell 1 0");

		lblUsername = new JLabel("USERNAME (ID)");
		lblUsername.setForeground(Color.WHITE);
		pnlHeader.add(lblUsername, "cell 2 0,alignx left");

		pnlNotification1 = new JPanel();
		pnlNotification1.setBackground(Color.GRAY);
		pnlNotification1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				pnlNotification1.setBorder(BorderFactory.createRaisedBevelBorder());
			}
			@Override
			public void mouseExited(MouseEvent e) {
				pnlNotification1.setBorder(BorderFactory.createEtchedBorder());
			}
			@Override
			public void mousePressed(MouseEvent e) {
				pnlNotification1.setBorder(BorderFactory.createLoweredBevelBorder());
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				pnlNotification1.setBorder(BorderFactory.createEtchedBorder());
			}
		});

		JButton btnPilotMode = new JButton("Pilot Mode");
		btnPilotMode.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				dataMode = DATA_MODE.MODE_PILOT;
				client.Log("Switching to Pilot Mode...");
			}
		});
		btnPilotMode.setBackground(Color.GRAY);
		btnPilotMode.setForeground(Color.WHITE);
		pnlHeader.add(btnPilotMode, "cell 8 0");

		JButton btnAircraftMode = new JButton("Aircraft Mode");
		btnAircraftMode.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				dataMode = DATA_MODE.MODE_AIRCRAFT;
				client.Log("Switching to Aircraft Mode...");
			}
		});
		btnAircraftMode.setBackground(Color.GRAY);
		btnAircraftMode.setForeground(Color.WHITE);
		pnlHeader.add(btnAircraftMode, "cell 9 0");

		JButton btnDateMode = new JButton("Date Mode");
		btnDateMode.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				dataMode = DATA_MODE.MODE_DATE;
				client.Log("Switching to Date Mode...");
			}
		});
		btnDateMode.setBackground(Color.GRAY);
		btnDateMode.setForeground(Color.WHITE);
		pnlHeader.add(btnDateMode, "cell 10 0");

		lblTime = new JLabel("XX:XX:XX");
		lblTime.setForeground(Color.WHITE);
		pnlHeader.add(lblTime, "cell 18 0");
		pnlNotification1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnlHeader.add(pnlNotification1, "cell 19 0,grow");

		pnlNotification2 = new JPanel();
		pnlNotification2.setBackground(Color.GRAY);
		pnlNotification2.setForeground(Color.GRAY);
		pnlNotification2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				pnlNotification2.setBorder(BorderFactory.createRaisedBevelBorder());
			}
			@Override
			public void mouseExited(MouseEvent e) {
				pnlNotification2.setBorder(BorderFactory.createEtchedBorder());
			}
			@Override
			public void mousePressed(MouseEvent e) {
				pnlNotification2.setBorder(BorderFactory.createLoweredBevelBorder());
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				pnlNotification2.setBorder(BorderFactory.createEtchedBorder());
			}
		});
		pnlNotification2.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnlHeader.add(pnlNotification2, "cell 20 0,grow");

		pnlNotification3 = new JPanel();
		pnlNotification3.setBackground(Color.GRAY);
		pnlNotification3.setForeground(Color.GRAY);
		pnlNotification3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				pnlNotification3.setBorder(BorderFactory.createRaisedBevelBorder());
			}
			@Override
			public void mouseExited(MouseEvent e) {
				pnlNotification3.setBorder(BorderFactory.createEtchedBorder());
			}
			@Override
			public void mousePressed(MouseEvent e) {
				pnlNotification3.setBorder(BorderFactory.createLoweredBevelBorder());
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				pnlNotification3.setBorder(BorderFactory.createEtchedBorder());
			}
		});
		pnlNotification3.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnlHeader.add(pnlNotification3, "cell 21 0,grow");

		JButton btnLogout = new JButton("Logout");
		btnLogout.setForeground(Color.WHITE);
		btnLogout.setBackground(Color.GRAY);
		pnlHeader.add(btnLogout, "cell 23 0,aligny baseline");

		JPanel pnlFooter = new JPanel();
		add(pnlFooter, BorderLayout.SOUTH);
		pnlFooter.setBackground(Color.DARK_GRAY);
		pnlFooter.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		pnlFooter.setLayout(new MigLayout("", "[256px:n:256px][][][][grow][][][][][][][][grow][][][][]", "[]"));

		JProgressBar progressBar = new JProgressBar();
		progressBar.setForeground(Color.RED);
		progressBar.setBackground(Color.GRAY);
		progressBar.setStringPainted(true);
		pnlFooter.add(progressBar, "cell 0 0,growx");

		JLabel lblStatus = new JLabel("Idle.");
		lblStatus.setForeground(Color.WHITE);
		pnlFooter.add(lblStatus, "cell 1 0");

		JLabel lblUserStatus = new JLabel("User Status:");
		lblUserStatus.setForeground(Color.WHITE);
		pnlFooter.add(lblUserStatus, "cell 15 0");

		lblUserPermissions = new JLabel("XXXXXXXXXXXXX");
		lblUserPermissions.setForeground(Color.WHITE);
		pnlFooter.add(lblUserPermissions, "cell 16 0");

		JPanel pnlInternalPane = new JPanel();
		add(pnlInternalPane, BorderLayout.CENTER);
		pnlInternalPane.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		pnlInternalPane.setBackground(Color.BLACK);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBorder(null);
		tabbedPane.setBackground(Color.LIGHT_GRAY);
		tabbedPane.setSelectedIndex(-1);

		JPanel pnlTrainingLogsHolder = new JPanel();
		pnlTrainingLogsHolder.setBackground(Color.BLACK);
		tabbedPane.addTab("Training Logs", tab3Icon, pnlTrainingLogsHolder, null);

		JLabel lblTrainingLogs = new JLabel("Training Logs");
		lblTrainingLogs.setForeground(Color.WHITE);
		pnlTrainingLogsHolder.setLayout(new MigLayout("", "[63px,grow][78px][42px][::18px][53px]", "[::14px][grow][::26px]"));

		tLogsList = new JList<String>();
		tLogsList.setForeground(Color.GREEN);
		tLogsList.setBackground(Color.DARK_GRAY);
		tLogsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tLogsList.setLayoutOrientation(JList.VERTICAL_WRAP);
		tLogsList.setBorder(new LineBorder(Color.WHITE));
		pnlTrainingLogsHolder.add(tLogsList, "cell 0 1 5 1,grow");
		pnlTrainingLogsHolder.add(lblTrainingLogs, "cell 0 0,alignx left,aligny top");

		JPanel panel_2 = new JPanel();
		panel_2.setBackground(Color.BLACK);
		pnlTrainingLogsHolder.add(panel_2, "cell 0 2 5 1,grow");

		JButton btnAdd_2 = new JButton("Add");
		btnAdd_2.setForeground(Color.WHITE);
		btnAdd_2.setBackground(Color.GRAY);

		JButton btnChange_2 = new JButton("Change");
		btnChange_2.setForeground(Color.WHITE);
		btnChange_2.setBackground(Color.GRAY);

		JButton btnRemove_2 = new JButton("Remove");
		btnRemove_2.setForeground(Color.WHITE);
		btnRemove_2.setBackground(Color.GRAY);

		superadminElements.add(btnRemove_2);
		adminElements.add(btnChange_2);

		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
				gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
						.addComponent(btnAdd_2, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnChange_2)
						.addPreferredGap(ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
						.addComponent(btnRemove_2))
				);
		gl_panel_2.setVerticalGroup(
				gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
						.addGroup(gl_panel_2.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnAdd_2)
								.addComponent(btnChange_2)
								.addComponent(btnRemove_2))
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				);
		panel_2.setLayout(gl_panel_2);

		JPanel pnlFlightLogsHolder = new JPanel();
		pnlFlightLogsHolder.setForeground(Color.WHITE);
		pnlFlightLogsHolder.setBackground(Color.BLACK);
		tabbedPane.addTab("Flight Logs", tab1Icon, pnlFlightLogsHolder, null);
		pnlFlightLogsHolder.setLayout(new MigLayout("", "[grow][][][grow][][::18px][]", "[][grow][24px:n:24px]"));

		JLabel lblFlightLogs = new JLabel("Flight Logs");
		lblFlightLogs.setForeground(Color.WHITE);
		pnlFlightLogsHolder.add(lblFlightLogs, "cell 0 0");

		fLogsList = new JList<String>();
		fLogsList.setForeground(Color.GREEN);
		fLogsList.setBackground(Color.DARK_GRAY);
		fLogsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fLogsList.setLayoutOrientation(JList.VERTICAL_WRAP);
		fLogsList.setBorder(new LineBorder(Color.WHITE));
		pnlFlightLogsHolder.add(fLogsList, "cell 0 1 7 1,grow");

		JPanel panel = new JPanel();
		panel.setBackground(Color.BLACK);
		panel.setBorder(null);
		pnlFlightLogsHolder.add(panel, "cell 0 2 7 1,grow");

		JButton btnAdd = new JButton("Add");
		btnAdd.setForeground(Color.WHITE);
		btnAdd.setBackground(Color.GRAY);

		JButton btnChange = new JButton("Change");
		btnChange.setForeground(Color.WHITE);
		btnChange.setBackground(Color.GRAY);

		JButton btnRemove = new JButton("Remove");
		btnRemove.setForeground(Color.WHITE);
		btnRemove.setBackground(Color.GRAY);

		superadminElements.add(btnRemove);
		adminElements.add(btnChange);


		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
				gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
						.addGap(1)
						.addComponent(btnAdd, GroupLayout.PREFERRED_SIZE, 61, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnChange)
						.addPreferredGap(ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
						.addComponent(btnRemove))
				);
		gl_panel.setVerticalGroup(
				gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
						.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnAdd)
								.addComponent(btnChange)
								.addComponent(btnRemove))
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				);
		panel.setLayout(gl_panel);

		JPanel pnlMaintinenceLogsHolder = new JPanel();
		pnlMaintinenceLogsHolder.setBackground(Color.BLACK);
		tabbedPane.addTab("Maintinence Logs", tab2Icon, pnlMaintinenceLogsHolder, null);
		pnlMaintinenceLogsHolder.setLayout(new MigLayout("", "[grow][][][][][10px:n][]", "[][grow][24px:n:24px,grow]"));

		JLabel lblMaintinenceLogs = new JLabel("Maintinence Logs");
		lblMaintinenceLogs.setForeground(Color.WHITE);
		pnlMaintinenceLogsHolder.add(lblMaintinenceLogs, "cell 0 0");

		mLogsList = new JList<String>();
		mLogsList.setBackground(Color.DARK_GRAY);
		mLogsList.setForeground(Color.GREEN);
		mLogsList.setLayoutOrientation(JList.VERTICAL_WRAP);
		mLogsList.setBorder(new LineBorder(Color.WHITE));
		mLogsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		pnlMaintinenceLogsHolder.add(mLogsList, "cell 0 1 7 1,grow");

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.BLACK);
		pnlMaintinenceLogsHolder.add(panel_1, "cell 0 2 7 1,grow");

		JButton btnAdd_1 = new JButton("Add");
		btnAdd_1.setForeground(Color.WHITE);
		btnAdd_1.setBackground(Color.GRAY);

		JButton btnChange_1 = new JButton("Change");
		btnChange_1.setForeground(Color.WHITE);
		btnChange_1.setBackground(Color.GRAY);

		JButton btnRemove_1 = new JButton("Remove");
		btnRemove_1.setForeground(Color.WHITE);
		btnRemove_1.setBackground(Color.GRAY);

		superadminElements.add(btnRemove_1);
		adminElements.add(btnChange_1);

		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
				gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
						.addComponent(btnAdd_1, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnChange_1)
						.addPreferredGap(ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
						.addComponent(btnRemove_1))
				);
		gl_panel_1.setVerticalGroup(
				gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
						.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnAdd_1)
								.addComponent(btnChange_1)
								.addComponent(btnRemove_1))
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				);
		panel_1.setLayout(gl_panel_1);

		JPanel pnlAdministrationHolder = new JPanel();
		pnlAdministrationHolder.setBackground(Color.BLACK);
		tabbedPane.addTab("Administration", tab4Icon, pnlAdministrationHolder, null);

		JPanel pnlAdmin = new JPanel();
		pnlAdmin.setBackground(Color.DARK_GRAY);
		pnlAdmin.setBorder(new LineBorder(new Color(255, 255, 255)));

		JButton btnNew = new JButton("Create a New User");
		btnNew.setForeground(Color.WHITE);
		btnNew.setBackground(Color.GRAY);

		superadminElements.add(btnNew);

		JLabel lblAdministrativeTasks = new JLabel("Administrative Tasks");
		lblAdministrativeTasks.setForeground(Color.LIGHT_GRAY);

		JButton btnNewButton = new JButton("SERVER FACTORY RESET");
		btnNewButton.setBackground(Color.GRAY);
		btnNewButton.setForeground(Color.RED);

		superadminElements.add(btnNewButton);

		GroupLayout gl_pnlAdmin = new GroupLayout(pnlAdmin);
		gl_pnlAdmin.setHorizontalGroup(
				gl_pnlAdmin.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlAdmin.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_pnlAdmin.createParallelGroup(Alignment.LEADING)
								.addComponent(btnNewButton, GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
								.addComponent(lblAdministrativeTasks)
								.addComponent(btnNew, GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE))
								.addContainerGap())
				);
		gl_pnlAdmin.setVerticalGroup(
				gl_pnlAdmin.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlAdmin.createSequentialGroup()
						.addGap(7)
						.addComponent(lblAdministrativeTasks)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnNew)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnNewButton)
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				);
		pnlAdmin.setLayout(gl_pnlAdmin);
		pnlAdministrationHolder.setLayout(new MigLayout("", "[::212px,grow][252px:n:252px][grow]", "[100px:n:100px][100px:n:100px][grow]"));
		pnlAdministrationHolder.add(pnlAdmin, "cell 0 0,grow");

		JPanel pnlManagement = new JPanel();
		pnlManagement.setBackground(Color.DARK_GRAY);
		pnlManagement.setForeground(Color.DARK_GRAY);
		pnlManagement.setBorder(new LineBorder(Color.WHITE));

		JLabel lblUserManagement = new JLabel("User Management");
		lblUserManagement.setForeground(Color.LIGHT_GRAY);

		JButton btnPromote = new JButton("Promote");
		btnPromote.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				client.Log("Sending PROMOTE request for '" + userList.getSelectedValue() + "'...");
				client.instance.RemoteRequest("$PROMOTE " + userList.getSelectedValue());
			}
		});
		btnPromote.setForeground(Color.WHITE);
		btnPromote.setBackground(Color.GRAY);

		JButton btnDemote = new JButton("Demote");
		btnDemote.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				client.Log("Sending DEMOTE request for '" + userList.getSelectedValue() + "'...");
				client.instance.RemoteRequest("$DEMOTE " + userList.getSelectedValue());
			}
		});
		btnDemote.setForeground(Color.WHITE);
		btnDemote.setBackground(Color.GRAY);

		superadminElements.add(btnPromote);
		superadminElements.add(btnDemote);

		userList = new JList<String>();
		userList.setBackground(Color.GRAY);
		userList.setForeground(Color.BLUE);
		userList.setBorder(new LineBorder(Color.WHITE));

		JButton btnRecover = new JButton("Recover User");
		btnRecover.setForeground(Color.WHITE);
		btnRecover.setBackground(Color.GRAY);

		JButton btnRemoveUser = new JButton("Remove User");
		btnRemoveUser.setForeground(Color.WHITE);
		btnRemoveUser.setBackground(Color.GRAY);

		adminElements.add(btnRecover);
		superadminElements.add(btnRemoveUser);

		rankList = new JList<String>();
		rankList.setForeground(Color.BLUE);
		rankList.setBorder(new LineBorder(Color.WHITE));
		rankList.setBackground(Color.GRAY);

		JLabel lblUsername_1 = new JLabel("Username");
		lblUsername_1.setForeground(Color.WHITE);

		JLabel lblRank = new JLabel("Rank");
		lblRank.setForeground(Color.WHITE);
		pnlAdministrationHolder.add(pnlManagement, "cell 1 0 1 3,grow");
		pnlManagement.setLayout(new MigLayout("", "[121px][110px]", "[14px][14px][167px,grow][23px][23px]"));
		pnlManagement.add(lblUserManagement, "cell 0 0,alignx left,aligny top");
		pnlManagement.add(userList, "cell 0 2,grow");
		pnlManagement.add(btnRemoveUser, "cell 0 3,growx,aligny top");
		pnlManagement.add(btnDemote, "cell 0 4,growx,aligny top");
		pnlManagement.add(rankList, "cell 1 2,grow");
		pnlManagement.add(btnRecover, "cell 1 3,growx,aligny top");
		pnlManagement.add(btnPromote, "cell 1 4,growx,aligny top");
		pnlManagement.add(lblUsername_1, "cell 0 1,alignx center,aligny top");
		pnlManagement.add(lblRank, "cell 1 1,alignx center,aligny top");

		JPanel panel_5 = new JPanel();
		panel_5.setBorder(new LineBorder(Color.WHITE));
		panel_5.setBackground(Color.DARK_GRAY);
		pnlAdministrationHolder.add(panel_5, "cell 2 0 1 3,grow");
		panel_5.setLayout(new MigLayout("", "[grow]", "[][grow][]"));

		JLabel lblConsole = new JLabel("Console");
		lblConsole.setForeground(Color.LIGHT_GRAY);
		panel_5.add(lblConsole, "cell 0 0");

		JList<String> listConsole = new JList<String>();
		listConsole.setFont(new Font("Consolas", Font.PLAIN, 11));
		listConsole.setForeground(Color.GREEN);
		listConsole.setBackground(Color.GRAY);
		panel_5.add(listConsole, "cell 0 1,grow");

		listConsole.setModel(consoleModel);

		inputField = new JTextField();
		inputField.setBackground(Color.GRAY);
		inputField.setForeground(Color.WHITE);
		panel_5.add(inputField, "flowx,cell 0 2,growx");
		inputField.setColumns(10);

		JButton btnExecute = new JButton("Execute");
		btnExecute.setForeground(Color.WHITE);
		btnExecute.setBackground(Color.GRAY);
		panel_5.add(btnExecute, "cell 0 2");

		adminElements.add(btnExecute);

		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new LineBorder(Color.WHITE));
		panel_3.setBackground(Color.DARK_GRAY);
		pnlAdministrationHolder.add(panel_3, "cell 0 1,grow");

		JLabel lblDataTasks = new JLabel("Data Tasks");
		lblDataTasks.setForeground(Color.LIGHT_GRAY);

		JButton btnDeleteEntries = new JButton("DELETE DATA ENTRY TREE");
		btnDeleteEntries.setBackground(Color.GRAY);
		btnDeleteEntries.setForeground(Color.RED);

		superadminElements.add(btnDeleteEntries);

		JButton btnCreateEntry = new JButton("Create New Entry");
		btnCreateEntry.setBackground(Color.GRAY);
		btnCreateEntry.setForeground(Color.WHITE);

		adminElements.add(btnCreateEntry);

		GroupLayout gl_panel_3 = new GroupLayout(panel_3);
		gl_panel_3.setHorizontalGroup(
				gl_panel_3.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_3.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_panel_3.createParallelGroup(Alignment.LEADING)
								.addComponent(lblDataTasks)
								.addComponent(btnCreateEntry, GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
								.addComponent(btnDeleteEntries, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE))
								.addContainerGap())
				);
		gl_panel_3.setVerticalGroup(
				gl_panel_3.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_3.createSequentialGroup()
						.addContainerGap()
						.addComponent(lblDataTasks)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnCreateEntry)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnDeleteEntries)
						.addContainerGap(43, Short.MAX_VALUE))
				);
		panel_3.setLayout(gl_panel_3);

		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new LineBorder(Color.WHITE));
		panel_4.setBackground(Color.DARK_GRAY);
		pnlAdministrationHolder.add(panel_4, "cell 0 2,growx,aligny top");

		JLabel lblStatistics = new JLabel("Statistics");
		lblStatistics.setForeground(Color.LIGHT_GRAY);

		JLabel lblUsersHead = new JLabel("Users:");
		lblUsersHead.setForeground(Color.WHITE);

		JLabel lblUsersOnlineHead = new JLabel("Users Online:");
		lblUsersOnlineHead.setForeground(Color.WHITE);

		JLabel lblLocalSize = new JLabel("Local Buffer Size:");
		lblLocalSize.setForeground(Color.WHITE);

		JLabel lblServerUptimeHead = new JLabel("Server Uptime:");
		lblServerUptimeHead.setForeground(Color.WHITE);

		JLabel lblTechnicalStatistics = new JLabel("Technical Statistics");
		lblTechnicalStatistics.setForeground(Color.LIGHT_GRAY);

		JLabel lblMemoryUsageHead = new JLabel("Memory Usage:");
		lblMemoryUsageHead.setForeground(Color.WHITE);

		JLabel lblNetworkOverheadHead = new JLabel("Ops/second:");
		lblNetworkOverheadHead.setForeground(Color.WHITE);

		JLabel lblNetworkIpHead = new JLabel("Network IP:");
		lblNetworkIpHead.setForeground(Color.WHITE);

		lblUsers = new JLabel("XXX");
		lblUsers.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblUsers.setForeground(Color.WHITE);

		lblUsersOnline = new JLabel("XXX");
		lblUsersOnline.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblUsersOnline.setForeground(Color.WHITE);

		lblBufferSize = new JLabel("XXX");
		lblBufferSize.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblBufferSize.setForeground(Color.WHITE);

		lblServerUptime = new JLabel("XXX");
		lblServerUptime.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblServerUptime.setForeground(Color.WHITE);

		lblMemoryUsage = new JLabel("XXX");
		lblMemoryUsage.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblMemoryUsage.setForeground(Color.WHITE);

		lblNetworkOverhead = new JLabel("XXX");
		lblNetworkOverhead.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNetworkOverhead.setForeground(Color.WHITE);

		lblNetworkIP = new JLabel("XXX");
		lblNetworkIP.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNetworkIP.setForeground(Color.WHITE);
		GroupLayout gl_panel_4 = new GroupLayout(panel_4);
		gl_panel_4.setHorizontalGroup(
				gl_panel_4.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_4.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_panel_4.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel_4.createSequentialGroup()
										.addGap(10)
										.addGroup(gl_panel_4.createParallelGroup(Alignment.LEADING)
												.addComponent(lblUsersHead)
												.addComponent(lblUsersOnlineHead)
												.addComponent(lblLocalSize)
												.addComponent(lblServerUptimeHead)))
												.addComponent(lblStatistics)
												.addComponent(lblTechnicalStatistics)
												.addGroup(gl_panel_4.createSequentialGroup()
														.addGap(10)
														.addGroup(gl_panel_4.createParallelGroup(Alignment.LEADING)
																.addComponent(lblNetworkOverheadHead)
																.addComponent(lblMemoryUsageHead)
																.addComponent(lblNetworkIpHead))))
																.addPreferredGap(ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
																.addGroup(gl_panel_4.createParallelGroup(Alignment.LEADING, false)
																		.addComponent(lblUsers, GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
																		.addComponent(lblUsersOnline, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																		.addComponent(lblBufferSize, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																		.addComponent(lblServerUptime, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																		.addComponent(lblMemoryUsage, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																		.addComponent(lblNetworkOverhead, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																		.addComponent(lblNetworkIP, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
																		.addContainerGap(28, Short.MAX_VALUE))
				);
		gl_panel_4.setVerticalGroup(
				gl_panel_4.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_4.createSequentialGroup()
						.addContainerGap()
						.addComponent(lblStatistics)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(gl_panel_4.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblUsersHead)
								.addComponent(lblUsers))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(gl_panel_4.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblUsersOnlineHead)
										.addComponent(lblUsersOnline))
										.addPreferredGap(ComponentPlacement.RELATED)
										.addGroup(gl_panel_4.createParallelGroup(Alignment.BASELINE)
												.addComponent(lblLocalSize)
												.addComponent(lblBufferSize))
												.addPreferredGap(ComponentPlacement.RELATED)
												.addGroup(gl_panel_4.createParallelGroup(Alignment.BASELINE)
														.addComponent(lblServerUptimeHead)
														.addComponent(lblServerUptime))
														.addGap(31)
														.addComponent(lblTechnicalStatistics)
														.addPreferredGap(ComponentPlacement.RELATED)
														.addGroup(gl_panel_4.createParallelGroup(Alignment.BASELINE)
																.addComponent(lblMemoryUsageHead)
																.addComponent(lblMemoryUsage))
																.addPreferredGap(ComponentPlacement.RELATED)
																.addGroup(gl_panel_4.createParallelGroup(Alignment.BASELINE)
																		.addComponent(lblNetworkOverheadHead)
																		.addComponent(lblNetworkOverhead))
																		.addPreferredGap(ComponentPlacement.RELATED)
																		.addGroup(gl_panel_4.createParallelGroup(Alignment.BASELINE)
																				.addComponent(lblNetworkIpHead)
																				.addComponent(lblNetworkIP))
																				.addContainerGap(39, Short.MAX_VALUE))
				);
		panel_4.setLayout(gl_panel_4);
		pnlInternalPane.setLayout(new BorderLayout(0, 0));

		JPanel pnlDateSuper = new JPanel();
		pnlDateSuper.setBackground(Color.BLACK);
		pnlInternalPane.add(pnlDateSuper, BorderLayout.WEST);
		pnlDateSuper.setLayout(new BoxLayout(pnlDateSuper, BoxLayout.X_AXIS));

		JPanel pnlDateHolder = new JPanel();
		pnlDateSuper.add(pnlDateHolder);
		pnlDateHolder.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		pnlDateHolder.setLayout(new BorderLayout(0, 0));

		pnlDate = new JPanel();
		pnlDate.setForeground(Color.DARK_GRAY);
		pnlDate.setBackground(Color.GRAY);
		pnlDate.setBorder(new LineBorder(new Color(0, 0, 0)));
		pnlDateHolder.add(pnlDate, BorderLayout.NORTH);
		pnlDate.setLayout(new MigLayout("", "[][grow][][grow][]", "[14px]"));

		lblMode = new JLabel("DATA_MODE");
		lblMode.setHorizontalAlignment(SwingConstants.CENTER);
		lblMode.setForeground(Color.ORANGE);
		pnlDate.add(lblMode, "cell 2 0,alignx left,aligny top");

		listData = new JList<String>();
		listData.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listData.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				if (client.payload != null && listData.getSelectedValue() != null) {
					String selection = listData.getSelectedValue();
					ArrayList<Payload.Entry> entries = null;
					if (dataMode.equals(DATA_MODE.MODE_AIRCRAFT)) {
						entries = client.payload.getDataByAircraft(selection);
					}else if (dataMode.equals(DATA_MODE.MODE_PILOT)) {

					}else if (dataMode.equals(DATA_MODE.MODE_AIRCRAFT)) {

					} 
					if (entries != null) {
						ArrayList<String> training = new ArrayList<String>();
						ArrayList<String> flight = new ArrayList<String>();
						ArrayList<String> maintinence = new ArrayList<String>();
						for (Payload.Entry entry : entries) {
							for (String s : entry.getTrainingData()) {
								training.add(s);
							}
							for (String s : entry.getFlightData()) {
								flight.add(s);
							}
							for (String s : entry.getMaintinenceData()) {
								maintinence.add(s);
							}
						}
						tLogsList.setModel(ConvertArrayToModel(training));
						fLogsList.setModel(ConvertArrayToModel(flight));
						mLogsList.setModel(ConvertArrayToModel(maintinence));
					}
				}
			}
		});
		listData.setBackground(Color.DARK_GRAY);
		listData.setForeground(Color.GREEN);
		listData.setLayoutOrientation(JList.VERTICAL_WRAP);
		pnlDateHolder.add(listData, BorderLayout.CENTER);

		Component horizontalStrut_2 = Box.createHorizontalStrut(8);
		pnlDateSuper.add(horizontalStrut_2);
		pnlInternalPane.add(tabbedPane);

		Component verticalStrut = Box.createVerticalStrut(8);
		pnlInternalPane.add(verticalStrut, BorderLayout.NORTH);

		Component verticalStrut_1 = Box.createVerticalStrut(8);
		pnlInternalPane.add(verticalStrut_1, BorderLayout.SOUTH);

		Component horizontalStrut = Box.createHorizontalStrut(8);
		pnlInternalPane.add(horizontalStrut, BorderLayout.EAST);

		hideControls();

		eventHandler = new EventHandler();
		eventTicker = new Timer(1, eventHandler);

		this.addMouseListener(eventHandler);

		JLoginDialog dialog = new JLoginDialog(client);

		eventTicker.start();
	}
	
	public DefaultListModel<String> ConvertArrayToModel(ArrayList<String> s) {
		DefaultListModel<String> out = new DefaultListModel<String>();
		for (String a : s) {
			out.addElement(a);
		}
		return out;
	}


	public void hideControls() {
		for (Component c : this.getComponents()) {
			c.setVisible(false);
		}
	}

	public void showControls() {
		for (Component c : this.getComponents()) {
			c.setVisible(true);
		}
	}

	public class EventHandler implements ActionListener, MouseListener {
		int secondsTicker = 0;
		AppletUI superInstance;

		public EventHandler() {
			superInstance = AppletUI.this;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			lblUsername.setText(username + "!");

			secondsTicker++;
			if (secondsTicker == 1000) {
				secondsTicker = 0;
				uptime++;
			}

			//Mesa Icon
			pnlMesaIcon.getGraphics().drawImage(mesaIcon, 1, 1, 30, 30, superInstance);
			lblTime.setText(convertUptime());

			if (client.payload != null && client.authenticated == true) {
				try {
					if (!userList.getModel().equals(client.payload.getUserModel())) {
						userList.setModel(client.payload.getUserModel());
					}
					if (!rankList.getModel().equals(client.payload.getRankModel())) {
						rankList.setModel(client.payload.getRankModel());
					}
					//Set the JList modality based on the Mode, and the JLabel
					if (dataMode.equals(DATA_MODE.MODE_AIRCRAFT)) {
						if (client.payload.getNameModel().getSize() > 0 && !client.payload.getNameModel().elementAt(0).equals("")) {
							lblMode.setText("Aircraft");
							listData.setModel(client.payload.getNameModel());
						}
					}else if (dataMode.equals(DATA_MODE.MODE_DATE)) {
						if (client.payload.getDateModel().getSize() > 0 && !client.payload.getDateModel().elementAt(0).equals("")) {
							lblMode.setText("Date");
							listData.setModel(client.payload.getDateModel());
						}
					}else if (dataMode.equals(DATA_MODE.MODE_PILOT)) {
						if (client.payload.getPilotModel().getSize() > 0 && !client.payload.getPilotModel().elementAt(0).equals("")) {
							lblMode.setText("Pilot");
							listData.setModel(client.payload.getPilotModel());
						}
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}

			//Show/Hide controls based on rank
			if (client != null && client.instance != null) {
				String rank = client.instance.GetRank();
				lblUserPermissions.setText(rank);
				if (rank.equals("user")) {
					for (Component o : adminElements) {
						o.setEnabled(false);
					}
					for (Component o : superadminElements) {
						o.setEnabled(false);
					}
				}else if (client.instance.GetRank().equals("admin")) {
					for (Component o : adminElements) {
						o.setEnabled(true);
					}
					for (Component o : superadminElements) {
						o.setEnabled(false);
					}
				}else if (client.instance.GetRank().equals("superadmin")) {
					for (Component o : adminElements) {
						o.setEnabled(true);
					}
					for (Component o : superadminElements) {
						o.setEnabled(true);
					}
				}
			}
		}

		public String convertUptime() {
			String sUptime = "";
			int hours = 0;
			int minutes = 0;
			int seconds = 0;

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

			if (hours < 10) {
				sHours = "0" + hours;
			}else {
				sHours = "" + hours;
			}

			sUptime = sHours + ":" + sMinutes + ":" + sSeconds;
			return sUptime;
		}

		@Override
		public void mouseClicked(MouseEvent e) {

		}

		@Override
		public void mouseEntered(MouseEvent e) {

		}

		@Override
		public void mouseExited(MouseEvent e) {

		}

		@Override
		public void mousePressed(MouseEvent e) {

		}

		@Override
		public void mouseReleased(MouseEvent e) {

		}
	}
}	
