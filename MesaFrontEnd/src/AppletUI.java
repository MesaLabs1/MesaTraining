import java.applet.Applet;
import java.awt.Dialog;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;
import javax.swing.Timer;

import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.border.LineBorder;

import java.awt.Color;

import javax.swing.border.EtchedBorder;
import javax.swing.JLabel;

import java.awt.Canvas;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JList;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import javax.swing.JSplitPane;
import javax.swing.JProgressBar;

import java.awt.Panel;

import javax.swing.BoxLayout;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import java.awt.FlowLayout;
import java.awt.Button;
import java.awt.Font;
import java.awt.Image;

import javax.swing.SwingConstants;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
	ImageIcon mesaIcon;

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

	JLabel lblUsername;
	JLabel lblTime;
	JPanel pnlNotification1;
	JPanel pnlNotification2;
	JPanel pnlNotification3;
	
	ClientMain clientMain;
	
	public static void main(String[] args) {
		new AppletUI();
	}

	public void AllocateResources() {
		//Verify all appletXXX variables are defined
		boolean verified = true;
		if (!appletRoot.exists()) {
			System.out.println("Failed to verify APPLET_ROOT@" + appletRoot.getPath());
			verified = false;
		}
		if (!appletRes.exists()) {
			System.out.println("Failed to verify APPLET_RES@" + appletRes.getPath());
			verified = false;
		}

		if (verified) {
			//Let's allocate all the ImageIcons
			mesaIcon = new ImageIcon(appletRes.getPath() + "/mesa.png");
			tab1Icon = new ImageIcon(appletRes.getPath() + "/flight.png");
			tab2Icon = new ImageIcon(appletRes.getPath() + "/maintinence.png");
			tab3Icon = new ImageIcon(appletRes.getPath() + "/training1.png");
			tab4Icon = new ImageIcon(appletRes.getPath() + "/controlpanel.png");
			notify1Icon = Toolkit.getDefaultToolkit().getImage(appletRes.getPath() + "/notify.png");
			notify2Icon = Toolkit.getDefaultToolkit().getImage(appletRes.getPath() + "/quiz.png");
			notify3Icon = Toolkit.getDefaultToolkit().getImage(appletRes.getPath() + "/help.png");
			notifyPop = Toolkit.getDefaultToolkit().getImage(appletRes.getPath() + "/pop.png");
		}
	}

	public AppletUI() {
		clientMain = new ClientMain(this);
		
		//A Java toolkit that provides us with information about the target system.
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		int SCREEN_SIZE_X = toolkit.getScreenSize().width;
		int SCREEN_SIZE_Y = toolkit.getScreenSize().height;

		//Allocate all Resources, make sure they're there, etc etc
		AllocateResources();

		//Set the size of the Applet to half the screen's Width, and half the height.
		//this.setSize(new Dimension(SCREEN_SIZE_X / 2, SCREEN_SIZE_Y / 2));

		/*
		 * Since we used MIGLayout, which resizes controls automatically with screen size changes, we can design this interface as
		 * large as possible, as assume it can shrink it for us if necessary. In this case, I have chosen the size of my desktop's
		 * monitor in Eclipse (to avoid scrolling around). In the future, another developer could set it to something even larger if they wanted to.
		 */
		this.setSize(1024,  512);
		setLayout(new BorderLayout(0, 0));

		JPanel pnlHeader = new JPanel();
		pnlHeader.setBorder(new LineBorder(new Color(0, 0, 0)));
		add(pnlHeader, BorderLayout.NORTH);
		pnlHeader.setLayout(new MigLayout("", "[][][][][][][][][][][][][][][grow][32px:n:32px,grow][32px:n:32px,grow][32px:n:32px,grow][::8px][]", "[32px:n:32px,grow]"));

		Canvas MesaIcon = new Canvas();
		pnlHeader.add(MesaIcon, "cell 0 0");

		JLabel lblWelcomeToMesa = new JLabel("Welcome to Mesa Labs WebConnect, ");
		pnlHeader.add(lblWelcomeToMesa, "cell 1 0");

		lblUsername = new JLabel("USERNAME (ID)");
		pnlHeader.add(lblUsername, "cell 2 0");

		JLabel lblConnectionTime = new JLabel("Connection Time:");
		pnlHeader.add(lblConnectionTime, "cell 5 0");

		lblTime = new JLabel("XX:XX:XX");
		pnlHeader.add(lblTime, "cell 6 0");

		pnlNotification1 = new JPanel();
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
		pnlNotification1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnlHeader.add(pnlNotification1, "cell 15 0,grow");

		pnlNotification2 = new JPanel();
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
		pnlHeader.add(pnlNotification2, "cell 16 0,grow");

		pnlNotification3 = new JPanel();
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
		pnlHeader.add(pnlNotification3, "cell 17 0,grow");

		JButton btnLogout = new JButton("Logout");
		pnlHeader.add(btnLogout, "cell 19 0,aligny baseline");

		JPanel pnlMain = new JPanel();
		add(pnlMain, BorderLayout.CENTER);
		pnlMain.setBorder(new LineBorder(new Color(0, 0, 0)));
		pnlMain.setLayout(new MigLayout("", "[grow][256px:n:256px,grow][]", "[grow][32px:n:32px][]"));

		JPanel pnlInternalPane = new JPanel();
		pnlInternalPane.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		pnlMain.add(pnlInternalPane, "cell 0 0 1 2,grow");
		pnlInternalPane.setLayout(new MigLayout("", "[128px:n:128px,grow][196px:n:196px,grow][grow]", "[grow]"));

		JPanel pnlDateHolder = new JPanel();
		pnlDateHolder.setBorder(new LineBorder(new Color(0, 0, 0)));
		pnlInternalPane.add(pnlDateHolder, "cell 0 0,grow");
		pnlDateHolder.setLayout(new BorderLayout(0, 0));

		JPanel pnlDate = new JPanel();
		pnlDate.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		pnlDateHolder.add(pnlDate, BorderLayout.NORTH);
		pnlDate.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblDate = new JLabel("Date");
		pnlDate.add(lblDate);

		JList listDate = new JList();
		pnlDateHolder.add(listDate, BorderLayout.CENTER);

		JPanel pnlPilotHolder = new JPanel();
		pnlPilotHolder.setBorder(new LineBorder(new Color(0, 0, 0)));
		pnlInternalPane.add(pnlPilotHolder, "cell 1 0,grow");
		pnlPilotHolder.setLayout(new BorderLayout(0, 0));

		JPanel pnlPilot = new JPanel();
		pnlPilot.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		pnlPilotHolder.add(pnlPilot, BorderLayout.NORTH);

		JLabel lblPilot = new JLabel("Pilot");
		pnlPilot.add(lblPilot);

		JList listPilot = new JList();
		pnlPilotHolder.add(listPilot, BorderLayout.CENTER);

		JPanel pnlAircraftHolder = new JPanel();
		pnlAircraftHolder.setBorder(new LineBorder(new Color(0, 0, 0)));
		pnlInternalPane.add(pnlAircraftHolder, "cell 2 0,grow");
		pnlAircraftHolder.setLayout(new BorderLayout(0, 0));

		JPanel pnlAircraftName = new JPanel();
		pnlAircraftName.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		pnlAircraftHolder.add(pnlAircraftName, BorderLayout.NORTH);

		JLabel lblAircraftName = new JLabel("Aircraft Name");
		pnlAircraftName.add(lblAircraftName);

		JList listName = new JList();
		pnlAircraftHolder.add(listName, BorderLayout.CENTER);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		pnlMain.add(tabbedPane, "cell 1 0 2 3,grow");
		tabbedPane.setSelectedIndex(-1);
		tabbedPane.setBorder(new LineBorder(new Color(0, 0, 0)));

		JPanel pnlMaintinenceLogsHolder = new JPanel();
		tabbedPane.addTab("Maintinence Logs", null, pnlMaintinenceLogsHolder, null);

		JPanel pnlTrainingLogsHolder = new JPanel();
		tabbedPane.addTab("Training Logs", tab3Icon, pnlTrainingLogsHolder, null);
		pnlTrainingLogsHolder.setLayout(new MigLayout("", "[][grow][]", "[][][][][grow][]"));

		JPanel pnlFlightLogsHolder = new JPanel();
		tabbedPane.addTab("Flight Logs", tab1Icon, pnlFlightLogsHolder, null);
		pnlFlightLogsHolder.setLayout(new MigLayout("", "[]", "[]"));

		JPanel pnlAdministrationHolder = new JPanel();
		tabbedPane.addTab("Administration", tab4Icon, pnlAdministrationHolder, null);

		JPanel pnlFooter = new JPanel();
		pnlFooter.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		pnlMain.add(pnlFooter, "cell 0 2,grow");
		pnlFooter.setLayout(new MigLayout("", "[256px:n:256px][][][][][][][][][][][][][][][][]", "[]"));

		JProgressBar progressBar = new JProgressBar();
		pnlFooter.add(progressBar, "cell 0 0,growx");

		JLabel lblStatus = new JLabel("Idle.");
		pnlFooter.add(lblStatus, "cell 1 0");

		JLabel lblUserStatus = new JLabel("User Status:");
		pnlFooter.add(lblUserStatus, "cell 15 0");

		JLabel lblUserPermissions = new JLabel("XXXXXXXXXXXXX");
		pnlFooter.add(lblUserPermissions, "cell 16 0");
		
		hideControls();
		
		eventHandler = new EventHandler();
		eventTicker = new Timer(1, eventHandler);
		
		JLoginDialog dialog = new JLoginDialog(clientMain);
		
		eventTicker.start();
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

	public class EventHandler implements ActionListener {
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
			
			//Draw the Icon
			pnlNotification1.getGraphics().drawImage(notify1Icon, 8, 8, pnlNotification1.getSize().width - 16, pnlNotification1.getSize().height - 16, superInstance);
			pnlNotification2.getGraphics().drawImage(notify2Icon, 8, 8, pnlNotification2.getSize().width - 16, pnlNotification2.getSize().height - 16, superInstance);
			pnlNotification3.getGraphics().drawImage(notify3Icon, 8, 8, pnlNotification3.getSize().width - 16, pnlNotification3.getSize().height - 16, superInstance);

			//Draw the Blip
			superInstance.getGraphics().drawImage(notifyPop, pnlNotification1.getLocation().x + pnlNotification1.getSize().width - 16, pnlNotification1.getLocation().y + pnlNotification1.getSize().height - 18, 24, 24, superInstance);

			//Fill the Blip with a number
			superInstance.getGraphics().drawString("0", pnlNotification1.getLocation().x + pnlNotification1.getSize().width - 8, pnlNotification1.getLocation().y + 30);

			lblTime.setText(convertUptime());
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
