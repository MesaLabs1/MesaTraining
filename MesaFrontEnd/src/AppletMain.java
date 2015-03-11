import java.applet.Applet;
import java.awt.Dialog;
import java.awt.Toolkit;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.border.LineBorder;

import java.awt.Color;

import javax.swing.border.EtchedBorder;
import javax.swing.JLabel;

import java.awt.Canvas;
import java.io.File;

public class AppletMain extends Applet{
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
	
	//Applet Data
	File appletRoot = new File("");
	File appletRes = new File(appletRoot.getPath() + "/resources/res/");
	

	public static void main(String[] args) {
		
	}

	public void AllocateResources() {
		//Verify all appletXXX variables are defined
		boolean verified = true;
		if (!appletRoot.exists()) {
			verified = false;
		}
		if (!appletRes.exists()) {
			verified = false;
		}
		
		//Let's allocate all the ImageIcons
		mesaIcon = new ImageIcon(appletRes.getPath() + "mesa.png");
		tab1Icon = new ImageIcon(appletRes.getPath() + "flight.png");
		tab2Icon = new ImageIcon(appletRes.getPath() + "maintinence.png");
		tab3Icon = new ImageIcon(appletRes.getPath() + "training1.png");
		tab4Icon = new ImageIcon(appletRes.getPath() + "controlpanel.png");
	}

	public AppletMain() {
		//Allocate all Resources, make sure they're there, etc etc
		AllocateResources();
		
		//A Java toolkit that provides us with information about the target system.
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		int SCREEN_SIZE_X = toolkit.getScreenSize().width;
		int SCREEN_SIZE_Y = toolkit.getScreenSize().height;

		//Set the size of the Applet to half the screen's Width, and half the height.
		//this.setSize(new Dimension(SCREEN_SIZE_X / 2, SCREEN_SIZE_Y / 2));

		/**
		 * Since we used MIGLayout, which resizes controls automatically with screen size changes, we can design this interface as
		 * large as possible, as assume it can shrink it for us if necessary. In this case, I have chosen the size of my desktop's
		 * monitor, which is 1600x900. In the future, another developer could set it to something even larger if they wanted to.
		 */
		this.setSize(1600,  900);
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		add(panel, BorderLayout.NORTH);
		panel.setLayout(new MigLayout("", "[][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][]", "[]"));

		Canvas MesaIcon = new Canvas();
		panel.add(MesaIcon, "cell 0 0");

		JLabel lblWelcomeToMesa = new JLabel("Welcome to Mesa Labs WebConnect, ");
		panel.add(lblWelcomeToMesa, "cell 1 0");

		JLabel lblUsername = new JLabel("USERNAME (ID)");
		panel.add(lblUsername, "cell 2 0");

		JLabel lblConnectionStatus = new JLabel("Connection Status:");
		panel.add(lblConnectionStatus, "cell 35 0");

		JLabel lblConnectionstatus = new JLabel("CONNECTION_STATUS");
		panel.add(lblConnectionstatus, "cell 36 0");

		JLabel lblConnectionTime = new JLabel("Connection Time:");
		panel.add(lblConnectionTime, "cell 44 0");

		JLabel lblXxxxxx = new JLabel("XX:XX:XX");
		panel.add(lblXxxxxx, "cell 45 0");

		JButton btnLogout = new JButton("Logout");
		panel.add(btnLogout, "cell 47 0");

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setSelectedIndex(0);
		tabbedPane.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		add(tabbedPane, BorderLayout.CENTER);

		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("Flight Logs", tab1Icon, panel_2, null);
		panel_2.setLayout(new MigLayout("", "[]", "[]"));

		JPanel panel_3 = new JPanel();
		tabbedPane.addTab("Maintinence Logs", tab2Icon, panel_3, null);

		JPanel panel_4 = new JPanel();
		tabbedPane.addTab("Training Logs", tab3Icon, panel_4, null);

		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Administration", tab4Icon, panel_1, null);


	}
	
	/**
	 * This class is a re-usable JDialog implementation that queries the user for a username and password.
	 * @author Hack
	 *
	 */
//	public class JLoginDialog extends JDialog {
//		private static final long serialVersionUID = -4321771706687810511L;
//		
//		public JLoginDialog() {
//			
//		}
//	}
}	
