import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
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
import java.awt.event.KeyListener;
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
import javax.swing.JScrollPane;

import java.awt.event.KeyEvent;


public class UI extends JFrame{
	//UID Automatically Generated
	private static final long serialVersionUID = 6373615470012072107L;

	//Application Info
	int SCREEN_SIZE_X;
	int SCREEN_SIZE_Y;

	//Application Resources
	Image mesaIcon;

	ImageIcon tab1Icon;
	ImageIcon tab2Icon;
	ImageIcon tab3Icon;
	ImageIcon tab4Icon;

	Image notify1Icon;
	Image notify2Icon;
	Image notify3Icon;
	Image notifyPop;

	//Application Data
	File applicationRoot = new File(new File("").getAbsolutePath());
	File applicationRes = new File(applicationRoot.getPath() + "/resources/res/");

	//User Data
	String username = "UNDEF";
	int uptime = 0;

	Timer eventTicker;
	EventHandler eventHandler;
	Client client;

	//UI Stuffs
	QuizUI quizUI;
	CBuilder builderUI;
	JPanel pnlInternalPane;
	boolean quizVisible = false;
	boolean builderVisible = false;

	JScrollPane scrollPane;

	JButton btnLogout;

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
	JList<String> listConsole;

	//ArrayList of JObjects to be hidden/shown based on rank.
	ArrayList<Component> adminElements;
	ArrayList<Component> superadminElements;

	DefaultListModel<String> consoleModel;

	ArrayList<String> consoleCommands;
	int cmdindex = 0;

	DATA_MODE dataMode;
	private JTextField inputField;

	//Enumerators
	enum DATA_MODE {
		MODE_DATE, MODE_PILOT, MODE_AIRCRAFT
	}

	public static void main(String[] args) {
		new UI();
	}

	public void AllocateResources() {
		//Verify all applicationXXX variables are defined
		boolean verified = true;
		if (!applicationRoot.exists()) {
			client.Log("Failed to verify APPLICATION_ROOT@" + applicationRoot.getPath());
			verified = false;
		}
		if (!applicationRes.exists()) {
			client.Log("Failed to verify APPLICATION_RES@" + applicationRes.getPath());
			verified = false;
		}

		consoleCommands = new ArrayList<String>();

		if (verified) {
			//Let's allocate all the ImageIcons
			mesaIcon = Toolkit.getDefaultToolkit().getImage(applicationRes.getPath() + "/mesa.png");
			tab1Icon = new ImageIcon(applicationRes.getPath() + "/flight.png");
			tab2Icon = new ImageIcon(applicationRes.getPath() + "/repair.png");
			tab3Icon = new ImageIcon(applicationRes.getPath() + "/training2.png");
			tab4Icon = new ImageIcon(applicationRes.getPath() + "/controlpanel.png");
			notify1Icon = Toolkit.getDefaultToolkit().getImage(applicationRes.getPath() + "/quiz.png");
			notify2Icon = Toolkit.getDefaultToolkit().getImage(applicationRes.getPath() + "/training.png");
			notify3Icon = Toolkit.getDefaultToolkit().getImage(applicationRes.getPath() + "/help.png");
			notifyPop = Toolkit.getDefaultToolkit().getImage(applicationRes.getPath() + "/pop.png");
		}
	}

	public UI() {
		quizUI = new QuizUI();
		builderUI = new CBuilder();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setBackground(Color.BLACK);
		try {
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
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

		setBackground(Color.BLACK);
		//We auto-set the default data modality to date-sorted.
		dataMode = DATA_MODE.MODE_DATE;

		consoleModel = new DefaultListModel<String>();
		client = new Client(this);

		//Allocate all Resources, make sure they're there, etc etc
		AllocateResources();

		/*
		 * Since we used MIGLayout, which resizes controls automatically with screen size changes, we can design this interface as
		 * large as possible, as assume it can shrink it for us if necessary. In this case, I have chosen the size of my desktop's
		 * monitor in Eclipse (to avoid scrolling around). In the future, another developer could set it to something even larger if they wanted to.
		 */
		this.setMinimumSize(new Dimension(1024, 606));
		this.setSize(896, 606);
		getContentPane().setLayout(new BorderLayout(0, 0));

		adminElements = new ArrayList<Component>();
		superadminElements = new ArrayList<Component>();

		Component horizontalStrut_1 = Box.createHorizontalStrut(8);
		horizontalStrut_1.setBackground(Color.BLACK);
		getContentPane().add(horizontalStrut_1, BorderLayout.WEST);

		JPanel pnlHeader = new JPanel();
		pnlHeader.setBackground(Color.DARK_GRAY);
		pnlHeader.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		getContentPane().add(pnlHeader, BorderLayout.NORTH);
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
				if (!quizVisible) {
					pnlNotification1.setBorder(BorderFactory.createRaisedBevelBorder());
				}
			}
			@Override
			public void mouseExited(MouseEvent e) {
				if (!quizVisible) {
					pnlNotification1.setBorder(BorderFactory.createEtchedBorder());
				}
			}
			@Override
			public void mousePressed(MouseEvent e) {
				pnlNotification1.setBorder(BorderFactory.createLoweredBevelBorder());
				UI.this.buttonOne();
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				if (!quizVisible) {
					pnlNotification1.setBorder(BorderFactory.createEtchedBorder());
				}
			}
		});

		JButton btnPilotMode = new JButton("Pilot Mode");
		btnPilotMode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dataMode = DATA_MODE.MODE_PILOT;
				client.Log("Switching to Pilot Mode...");
			}
		});
		btnPilotMode.setBackground(Color.GRAY);
		btnPilotMode.setForeground(Color.WHITE);
		pnlHeader.add(btnPilotMode, "cell 8 0");

		JButton btnAircraftMode = new JButton("Aircraft Mode");
		btnAircraftMode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dataMode = DATA_MODE.MODE_AIRCRAFT;
				client.Log("Switching to Aircraft Mode...");
			}
		});
		btnAircraftMode.setBackground(Color.GRAY);
		btnAircraftMode.setForeground(Color.WHITE);
		pnlHeader.add(btnAircraftMode, "cell 9 0");

		JButton btnDateMode = new JButton("Date Mode");
		btnDateMode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
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
				if (!builderVisible) {
					pnlNotification2.setBorder(BorderFactory.createRaisedBevelBorder()); 
				}
			}
			@Override
			public void mouseExited(MouseEvent e) {
				if (!builderVisible) {
					pnlNotification2.setBorder(BorderFactory.createEtchedBorder());
				}
			}
			@Override
			public void mousePressed(MouseEvent e) {
				pnlNotification2.setBorder(BorderFactory.createLoweredBevelBorder());
				buttonTwo();
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				if (!builderVisible) {
					pnlNotification2.setBorder(BorderFactory.createEtchedBorder());
				}
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
				buttonThree();
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				pnlNotification3.setBorder(BorderFactory.createEtchedBorder());
			}
		});
		pnlNotification3.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnlHeader.add(pnlNotification3, "cell 21 0,grow");

		btnLogout = new JButton("Logout");
		btnLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (btnLogout.isEnabled()) {
					System.exit(0);
				}
			}
		});
		btnLogout.setForeground(Color.WHITE);
		btnLogout.setBackground(Color.GRAY);
		pnlHeader.add(btnLogout, "cell 23 0,aligny baseline");

		JPanel pnlFooter = new JPanel();
		getContentPane().add(pnlFooter, BorderLayout.SOUTH);
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

		pnlInternalPane = new JPanel();
		getContentPane().add(pnlInternalPane, BorderLayout.CENTER);
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
		btnAdd_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Payload.Entry entry = null;
				if (dataMode.equals(DATA_MODE.MODE_AIRCRAFT)) {
					entry = client.payload.CreateBlankEntry(null, listData.getSelectedValue(), null);
				}else if (dataMode.equals(DATA_MODE.MODE_DATE)) {
					entry = client.payload.CreateBlankEntry(null, null, listData.getSelectedValue());
				}else if (dataMode.equals(DATA_MODE.MODE_PILOT)) {
					entry = client.payload.CreateBlankEntry(listData.getSelectedValue(), null, null);
				}
				new NewEntry(client, NewEntry.ReportType.TrainingOnly, entry);
			}
		});
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
		pnlFlightLogsHolder.setLayout(new MigLayout("", "[63px,grow][78px][42px][::18px][53px]", "[::14px][grow][::26px]"));

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
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Payload.Entry entry = null;
				if (dataMode.equals(DATA_MODE.MODE_AIRCRAFT)) {
					entry = client.payload.CreateBlankEntry(null, listData.getSelectedValue(), null);
				}else if (dataMode.equals(DATA_MODE.MODE_DATE)) {
					entry = client.payload.CreateBlankEntry(null, null, listData.getSelectedValue());
				}else if (dataMode.equals(DATA_MODE.MODE_PILOT)) {
					entry = client.payload.CreateBlankEntry(listData.getSelectedValue(), null, null);
				}
				new NewEntry(client, NewEntry.ReportType.FlightOnly, entry);
			}
		});
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

		JPanel pnlRepairLogsHolder = new JPanel();
		pnlRepairLogsHolder.setBackground(Color.BLACK);
		tabbedPane.addTab("Repair Logs", tab2Icon, pnlRepairLogsHolder, null);
		pnlRepairLogsHolder.setLayout(new MigLayout("", "[63px,grow][78px][42px][::18px][53px]", "[::14px][grow][::26px]"));

		JLabel lblRepairLogs = new JLabel("Repair Logs");
		lblRepairLogs.setForeground(Color.WHITE);
		pnlRepairLogsHolder.add(lblRepairLogs, "cell 0 0");

		mLogsList = new JList<String>();
		mLogsList.setBackground(Color.DARK_GRAY);
		mLogsList.setForeground(Color.GREEN);
		mLogsList.setLayoutOrientation(JList.VERTICAL_WRAP);
		mLogsList.setBorder(new LineBorder(Color.WHITE));
		mLogsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		pnlRepairLogsHolder.add(mLogsList, "cell 0 1 7 1,grow");

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.BLACK);
		pnlRepairLogsHolder.add(panel_1, "cell 0 2 7 1,grow");

		JButton btnAdd_1 = new JButton("Add");
		btnAdd_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Payload.Entry entry = null;
				if (dataMode.equals(DATA_MODE.MODE_AIRCRAFT)) {
					entry = client.payload.CreateBlankEntry(null, listData.getSelectedValue(), null);
				}else if (dataMode.equals(DATA_MODE.MODE_DATE)) {
					entry = client.payload.CreateBlankEntry(null, null, listData.getSelectedValue());
				}else if (dataMode.equals(DATA_MODE.MODE_PILOT)) {
					entry = client.payload.CreateBlankEntry(listData.getSelectedValue(), null, null);
				}
				new NewEntry(client, NewEntry.ReportType.RepairOnly, entry);
			}
		});
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

		JLabel lblAdministrativeTasks = new JLabel("Administrative Tasks");
		lblAdministrativeTasks.setForeground(Color.LIGHT_GRAY);

		JButton btnFactoryReset = new JButton("Factory Reset");
		btnFactoryReset.setBackground(Color.GRAY);
		btnFactoryReset.setForeground(Color.WHITE);

		superadminElements.add(btnFactoryReset);

		JButton btnCreateEntry = new JButton("Create New Entry");
		btnCreateEntry.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new NewEntry(client, NewEntry.ReportType.FullReport, null);
			}
		});
		btnCreateEntry.setBackground(Color.GRAY);
		btnCreateEntry.setForeground(Color.WHITE);

		adminElements.add(btnCreateEntry);

		JButton btnDeleteEntries = new JButton("Delete Entry");
		btnDeleteEntries.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (listData.getSelectedValue() != null) {
					String type = "";
					String search = listData.getSelectedValue();
					if (dataMode.equals(DATA_MODE.MODE_AIRCRAFT)) {
						type = "aircraft";
					}else if (dataMode.equals(DATA_MODE.MODE_DATE)) {
						type = "date";
						search = search.substring(3, 5) + search.substring(0, 2) + search.substring(6, 10);
					}else if (dataMode.equals(DATA_MODE.MODE_PILOT)) {
						type = "pilot";
					}
					client.instance.remoteRequest("$REMOVE ENTRY " + type + " " + search);
				}
			}
		});
		btnDeleteEntries.setBackground(Color.GRAY);
		btnDeleteEntries.setForeground(Color.WHITE);

		superadminElements.add(btnDeleteEntries);

		GroupLayout gl_pnlAdmin = new GroupLayout(pnlAdmin);
		gl_pnlAdmin.setHorizontalGroup(
				gl_pnlAdmin.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlAdmin.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_pnlAdmin.createParallelGroup(Alignment.LEADING)
								.addComponent(btnDeleteEntries, GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
								.addGroup(gl_pnlAdmin.createParallelGroup(Alignment.TRAILING, false)
										.addComponent(btnFactoryReset, Alignment.LEADING, 0, 0, Short.MAX_VALUE)
										.addComponent(btnCreateEntry, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE))
										.addComponent(lblAdministrativeTasks))
										.addContainerGap())
				);
		gl_pnlAdmin.setVerticalGroup(
				gl_pnlAdmin.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlAdmin.createSequentialGroup()
						.addGap(7)
						.addComponent(lblAdministrativeTasks)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnFactoryReset)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnCreateEntry)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnDeleteEntries)
						.addContainerGap())
				);
		pnlAdmin.setLayout(gl_pnlAdmin);
		pnlAdministrationHolder.setLayout(new MigLayout("", "[::212px,grow][2px:n:2px][252px:n:252px][2px:n:2px][grow]", "[132px:n:132px][growprio 99,grow]"));
		pnlAdministrationHolder.add(pnlAdmin, "cell 0 0,grow");

		JPanel pnlManagement = new JPanel();
		pnlManagement.setBackground(Color.DARK_GRAY);
		pnlManagement.setForeground(Color.DARK_GRAY);
		pnlManagement.setBorder(new LineBorder(Color.WHITE));

		JLabel lblUserManagement = new JLabel("User Management");
		lblUserManagement.setForeground(Color.LIGHT_GRAY);

		JButton btnPromote = new JButton("Promote");
		btnPromote.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				client.Log("Sending PROMOTE request for '" + userList.getSelectedValue() + "'...");
				client.instance.remoteRequest("$PROMOTE " + userList.getSelectedValue());
			}
		});
		btnPromote.setForeground(Color.WHITE);
		btnPromote.setBackground(Color.GRAY);

		JButton btnDemote = new JButton("Demote");
		btnDemote.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				client.Log("Sending DEMOTE request for '" + userList.getSelectedValue() + "'...");
				client.instance.remoteRequest("$DEMOTE " + userList.getSelectedValue());
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
		btnRecover.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				client.instance.remoteRequest("$RECOVER " + userList.getSelectedValue());
			}
		});
		btnRecover.setForeground(Color.WHITE);
		btnRecover.setBackground(Color.GRAY);

		JButton btnRemoveUser = new JButton("Remove User");
		btnRemoveUser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				client.instance.remoteRequest("$REMOVE USER " + userList.getSelectedValue());
			}
		});
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
		pnlAdministrationHolder.add(pnlManagement, "cell 2 0 1 2,grow");
		pnlManagement.setLayout(new MigLayout("", "[121px][110px]", "[14px][14px][167px,grow][23px:n:23px][23px][23px]"));
		pnlManagement.add(lblUserManagement, "cell 0 0,alignx left,aligny top");
		pnlManagement.add(userList, "cell 0 2,grow");

		JButton btnCreateUser = new JButton("Create User");
		btnCreateUser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new NewUserDialog(client);
			}
		});
		btnCreateUser.setBackground(Color.GRAY);
		btnCreateUser.setForeground(Color.WHITE);
		pnlManagement.add(btnCreateUser, "cell 0 3,growx");

		superadminElements.add(btnCreateUser);

		JButton btnChangePassword = new JButton("Change PWD");
		btnChangePassword.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new PasswordDialog(client);
			}
		});
		btnChangePassword.setBackground(Color.GRAY);
		btnChangePassword.setForeground(Color.WHITE);
		pnlManagement.add(btnChangePassword, "cell 1 3,growx");

		superadminElements.add(btnChangePassword);

		pnlManagement.add(btnRemoveUser, "cell 0 4,growx,aligny top");
		pnlManagement.add(btnDemote, "cell 0 5,growx,aligny top");
		pnlManagement.add(rankList, "cell 1 2,grow");
		pnlManagement.add(btnRecover, "cell 1 4,growx,aligny top");
		pnlManagement.add(btnPromote, "cell 1 5,growx,aligny top");
		pnlManagement.add(lblUsername_1, "cell 0 1,alignx center,aligny top");
		pnlManagement.add(lblRank, "cell 1 1,alignx center,aligny top");

		JPanel panel_5 = new JPanel();
		panel_5.setBorder(new LineBorder(Color.WHITE));
		panel_5.setBackground(Color.DARK_GRAY);
		pnlAdministrationHolder.add(panel_5, "cell 4 0 1 2,grow");
		panel_5.setLayout(new MigLayout("", "[grow]", "[][grow][]"));

		JLabel lblConsole = new JLabel("Console");
		lblConsole.setForeground(Color.LIGHT_GRAY);
		panel_5.add(lblConsole, "cell 0 0");

		scrollPane = new JScrollPane();
		panel_5.add(scrollPane, "cell 0 1,grow");

		listConsole = new JList<String>();
		listConsole.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(listConsole);
		listConsole.setFont(new Font("Consolas", Font.PLAIN, 11));
		listConsole.setForeground(Color.GREEN);
		listConsole.setBackground(Color.GRAY);

		listConsole.setModel(consoleModel);

		inputField = new JTextField();
		inputField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyPressed(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {
				//System.out.println(e.getKeyCode());
				if (e.getKeyCode() == 10) { 	//The keycode for VK_ENTER is 10.
					ProcessCommand(inputField.getText());
				}
				if (e.getKeyChar() == 38) {		//The UP arrow, to load a previous command;
					if (cmdindex < consoleCommands.size() - 1) {
						cmdindex++;
					}else {
						cmdindex = 0;
					}
					inputField.setText(consoleCommands.get(cmdindex));
				}
				if (e.getKeyChar() == 40) {		//The UP arrow, to load a previous command;
					if (cmdindex > 0) {
						cmdindex--;
					}else {
						cmdindex = consoleCommands.size() - 1;
					}
					inputField.setText(consoleCommands.get(cmdindex));
				}
			}
		});
		inputField.setBackground(Color.GRAY);
		inputField.setForeground(Color.WHITE);
		panel_5.add(inputField, "flowx,cell 0 2,growx");
		inputField.setColumns(10);

		JButton btnExecute = new JButton("Execute");
		btnExecute.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ProcessCommand(inputField.getText());
			}
		});
		btnExecute.setForeground(Color.WHITE);
		btnExecute.setBackground(Color.GRAY);
		panel_5.add(btnExecute, "cell 0 2");

		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new LineBorder(Color.WHITE));
		panel_4.setBackground(Color.DARK_GRAY);
		pnlAdministrationHolder.add(panel_4, "cell 0 1,growx,aligny top");

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
										.addGroup(gl_panel_4.createParallelGroup(Alignment.LEADING)
												.addGroup(gl_panel_4.createSequentialGroup()
														.addGap(10)
														.addGroup(gl_panel_4.createParallelGroup(Alignment.LEADING)
																.addComponent(lblUsersHead)
																.addComponent(lblUsersOnlineHead)
																.addComponent(lblLocalSize)
																.addComponent(lblServerUptimeHead))
																.addPreferredGap(ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
																.addGroup(gl_panel_4.createParallelGroup(Alignment.LEADING, false)
																		.addComponent(lblUsers, GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
																		.addComponent(lblUsersOnline, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																		.addComponent(lblBufferSize, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																		.addComponent(lblServerUptime)))
																		.addGroup(gl_panel_4.createSequentialGroup()
																				.addGroup(gl_panel_4.createParallelGroup(Alignment.LEADING)
																						.addComponent(lblTechnicalStatistics)
																						.addGroup(gl_panel_4.createSequentialGroup()
																								.addGap(10)
																								.addGroup(gl_panel_4.createParallelGroup(Alignment.LEADING)
																										.addComponent(lblNetworkOverheadHead)
																										.addComponent(lblMemoryUsageHead)
																										.addComponent(lblNetworkIpHead))))
																										.addPreferredGap(ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
																										.addGroup(gl_panel_4.createParallelGroup(Alignment.LEADING, false)
																												.addComponent(lblMemoryUsage, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																												.addComponent(lblNetworkOverhead, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																												.addComponent(lblNetworkIP))))
																												.addGap(20))
																												.addGroup(gl_panel_4.createSequentialGroup()
																														.addComponent(lblStatistics)
																														.addContainerGap(128, Short.MAX_VALUE))))
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
														.addGap(18)
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
																				.addContainerGap(46, Short.MAX_VALUE))
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
		pnlDate.setLayout(new MigLayout("", "[][grow][200px:n:256px][grow][]", "[14px]"));

		lblMode = new JLabel("DATA_MODE");
		lblMode.setHorizontalAlignment(SwingConstants.CENTER);
		lblMode.setForeground(Color.ORANGE);
		pnlDate.add(lblMode, "cell 2 0,alignx center,aligny top");

		listData = new JList<String>();
		listData.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listData.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				eventHandler.UpdateLogs();
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

		eventHandler = new EventHandler();
		eventTicker = new Timer(1, eventHandler);

		new JLoginDialog(client);

		eventTicker.start();

		this.setSize(640, 480);
	}

	public DefaultListModel<String> ConvertArrayToModel(ArrayList<String> s) {
		DefaultListModel<String> out = new DefaultListModel<String>();
		for (String a : s) {
			out.addElement(a);
		}
		return out;
	}

	public void buttonOne() {
		if (!builderVisible) {
			quizVisible = !quizVisible;
			if (quizVisible) {
				this.getContentPane().remove(pnlInternalPane);
				this.getContentPane().add(quizUI, BorderLayout.CENTER);
			}else {
				this.getContentPane().remove(quizUI);
				this.getContentPane().add(pnlInternalPane, BorderLayout.CENTER);
			}
			this.getContentPane().validate();
			this.getContentPane().repaint();
		}
	}

	public void buttonTwo() {
		if (!quizVisible) {
			builderVisible = !builderVisible;
			if (builderVisible) {
				this.getContentPane().remove(pnlInternalPane);
				this.getContentPane().add(builderUI, BorderLayout.CENTER);
			}else {
				this.getContentPane().remove(builderUI);
				this.getContentPane().add(pnlInternalPane, BorderLayout.CENTER);
			}
			this.getContentPane().validate();
			this.getContentPane().repaint();
		}
	}

	public void buttonThree() {

	}

	public void ShowUIDialog(String title, String message) {
		JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(this), message, title, JOptionPane.INFORMATION_MESSAGE);
	}

	public void ProcessCommand(String command) {
		if (command != null && command.length() > 0) {
			String cmd = command.split(" ")[0].toLowerCase();
			consoleCommands.add(command);
			inputField.setText("");
			if (cmd.equals("request")) {
				String netcommand = command.substring("request ".length(), command.length());
				client.Log(netcommand);
				client.instance.remoteRequest("$" + netcommand);
			}else if (cmd.equals("help")) {
				if (command.length() == "help".length()) {		//This is just plain help.
					client.SilentLog("Interactive Shell by HackJunky -- HELP SCREEN");
					client.SilentLog("To view an in-depth help, type 'help COMMAND_NAME'.");
					client.SilentLog("REQUEST \t Send a direct network command.");
					client.SilentLog("CLEAR \t Clears the console screen.");
				}else {											//This is 'help COMMAND'.
					String helpArea = command.substring("help ".length(), command.length());
					if (helpArea.equals("request")) {
						client.SilentLog("Queries a remote request to be executed on the next ADO tick. The command goes directly to the server.");
						client.SilentLog("List of Available Commands -- Example Format: COMMAND PARAM1 param2 ...");
						client.SilentLog("Help parameters written in uppercase are REQUIRED. Lowercase params are optional.");
						client.SilentLog("CREATE \t Creates a 'SEE BELOW'");
						client.SilentLog("\t USER \t Creates a user. Parameters: USERNAME, PASSWORD, RANK");
						client.SilentLog("\t ENTRY \t Creates an entry in the database. Parameters: Date, Pilot, Aircraft.");
						client.SilentLog("\t LOG \t Creates a log file. Paramters: FLIGHT/Repair/TRAINING, Date, Pilot, Aircraft, Log Entry");
						client.SilentLog("REMOVE \t Removes a 'SEE BELOW'");
						client.SilentLog("\t USER \t Removes a user. Parameters: USERNAME");
						client.SilentLog("\t ENTRY \t Removes an entry in the database. Parameters: DATE/PILOT/AIRCRAFT, VALUE.");
						client.SilentLog("\t LOG \t Removes a log file. Paramters: FLIGHT/Repair/TRAINING, Date, Pilot, Aircraft, Log Entry Regex");

					}else if (helpArea.equalsIgnoreCase("clear")) {
						client.SilentLog("Executing this command removes all entries on this console screen. It does not affect the system in any way.");
					}
				}
			}else {
				command.equals("Invalid command.");
			}
		}
	}

	public class EventHandler implements ActionListener {
		int secondsTicker = 0;
		UI superInstance;

		public EventHandler() {
			superInstance = UI.this;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			lblUsername.setText(username + "!");

			secondsTicker++;
			if (secondsTicker == 500) {
				secondsTicker = 0;
				uptime++;
			}

			//Mesa Icon
			if (mesaIcon != null && pnlMesaIcon != null && pnlMesaIcon.getGraphics() != null) {
				pnlMesaIcon.getGraphics().drawImage(mesaIcon, 1, 1, 30, 30, superInstance);
				pnlNotification1.getGraphics().drawImage(notify1Icon, 1, 1, 30, 30, superInstance);
				pnlNotification2.getGraphics().drawImage(notify2Icon, 1, 1, 30, 30, superInstance);
			}

			lblTime.setText(convertUptime());

			if (client.payload != null && client.authenticated == true) {
				try {
					if (CheckForDLM(client.payload.getUserModel())) {
						if (!DLMComparator(userList.getModel(),client.payload.getUserModel())) {
							userList.setModel(client.payload.getUserModel());
						}
					}
					if (CheckForDLM(client.payload.getRankModel())) {
						if (!DLMComparator(rankList.getModel(),client.payload.getRankModel())) {
							rankList.setModel(client.payload.getRankModel());
						}
					}

					//Set the JList modality based on the Mode, and the JLabel
					if (dataMode.equals(DATA_MODE.MODE_AIRCRAFT)) {
						if (CheckForDLM(client.payload.getNameModel())) {
							if (!DLMComparator(listData.getModel(), client.getPayload().getNameModel())) {
								lblMode.setText("Aircraft");
								listData.setModel(client.payload.getNameModel());
								UpdateLogs();
							}
						}
					}else if (dataMode.equals(DATA_MODE.MODE_DATE)) {
						if (CheckForDLM(client.payload.getDateModel())) {
							if (!DLMComparator(listData.getModel(), client.getPayload().getDateModel())) {
								lblMode.setText("Date");
								listData.setModel(client.payload.getDateModel());
								UpdateLogs();
							}
						}
					}else if (dataMode.equals(DATA_MODE.MODE_PILOT)) {
						if (CheckForDLM(client.payload.getPilotModel())) {
							if (!DLMComparator(listData.getModel(), client.getPayload().getPilotModel())) {
								lblMode.setText("Pilot");
								listData.setModel(client.payload.getPilotModel());
								UpdateLogs();
							}
						}
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}

			//Show/Hide controls based on rank
			if (client != null && client.instance != null) {
				String rank = client.instance.getRank();
				lblUserPermissions.setText(rank);
				if (rank.equals("user")) {
					for (Component o : adminElements) {
						o.setEnabled(false);
					}
					for (Component o : superadminElements) {
						o.setEnabled(false);
					}
				}else if (client.instance.getRank().equals("admin")) {
					for (Component o : adminElements) {
						o.setEnabled(true);
					}
					for (Component o : superadminElements) {
						o.setEnabled(false);
					}
				}else if (client.instance.getRank().equals("superadmin")) {
					for (Component o : adminElements) {
						o.setEnabled(true);
					}
					for (Component o : superadminElements) {
						o.setEnabled(true);
					}
				}
			}

			listConsole.setSelectedIndex(listConsole.getModel().getSize());
			listConsole.ensureIndexIsVisible(listConsole.getSelectedIndex() - 1);
		}

		public void UpdateLogs() {
			if (client.payload != null && listData.getSelectedValue() != null) {
				String selection = listData.getSelectedValue();
				ArrayList<Payload.Entry> entries = null;
				client.Log("Performing local search for '" + selection + "' via " + dataMode.toString() + ".");
				if (dataMode.equals(DATA_MODE.MODE_AIRCRAFT)) {
					entries = client.payload.getDataByAircraft(selection);
				}else if (dataMode.equals(DATA_MODE.MODE_PILOT)) {
					entries = client.payload.getDataByPilot(selection);
				}else if (dataMode.equals(DATA_MODE.MODE_DATE)) {
					entries = client.payload.getDataByDate(selection);
				} 
				if (entries != null) {
					ArrayList<String> training = new ArrayList<String>();
					ArrayList<String> flight = new ArrayList<String>();
					ArrayList<String> Repair = new ArrayList<String>();
					int t = 0;
					int f = 0;
					int m = 0;
					for (Payload.Entry entry : entries) {
						String tData = entry.getTrainingData();
						String fData = entry.getFlightData();
						String mData = entry.getRepairData();

						String append = "";

						if (dataMode.equals(DATA_MODE.MODE_AIRCRAFT)) {
							//We want to add on the pilot, and the date
							append = "[" + entry.getPilot() + "][" + entry.getDate() + "] ";
						}else if (dataMode.equals(DATA_MODE.MODE_PILOT)) {
							append = "[" + entry.getAircraft() + "][" + entry.getDate() + "] ";
						}else if (dataMode.equals(DATA_MODE.MODE_DATE)) {
							append = "[" + entry.getPilot() + "][" + entry.getAircraft() + "] ";
						} 

						if (tData.length() > 0) {
							t++;
							training.add(append + tData);
						}

						if (fData.length() > 0) {
							f++;
							flight.add(append + fData);
						}
						if (mData.length() > 0) {
							m++;
							Repair.add(append + mData);
						}
					}
					if (training.size() == 0) {
						training.add("No training sessions were found for these parameters");
					}
					if (flight.size() == 0) {
						flight.add("No flights were found for these parameters.");
					}
					if (Repair.size() == 0) {
						Repair.add("No repairs were found for these parameters.");
					}
					tLogsList.setModel(ConvertArrayToModel(training));
					fLogsList.setModel(ConvertArrayToModel(flight));
					mLogsList.setModel(ConvertArrayToModel(Repair));
					client.Log("Result: " + t + " training entries; " + f + " flight entries; " + m + " Repair entries.");
				}else {
					client.Log("No results found!");
				}
			}
		}

		public boolean DLMComparator(ListModel a, DefaultListModel<String> b) {
			//For some reason, we cannot compare them as the objects they are, so we can do a line-by-line check.
			//First things first, are they the same size?
			if (a.getSize() == b.getSize()) {
				for (int i = 0; i < a.getSize(); i++) {
					if (!a.getElementAt(i).toString().equals(b.getElementAt(i))) {
						return false;
					}
				}
			}else {
				return false;
			}
			return true;
		}

		public boolean CheckForDLM(DefaultListModel<String> model) {
			if (model.getSize() > 0 && !model.elementAt(0).equals("")) {
				return true;
			}
			return false;
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
	}
}	
