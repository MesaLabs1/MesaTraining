import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JDialog;

import net.miginfocom.swing.MigLayout;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JLabel;

import java.awt.Canvas;

import javax.swing.JTextField;

import javax.swing.JPasswordField;
import javax.swing.JButton;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;

import javax.swing.border.LineBorder;
import javax.swing.border.EtchedBorder;
import java.awt.Component;
import javax.swing.Box;

/**
 * This class outlines the JLoginDialog. This login dialog extends JDialog (meaning it behaves as a popup). It is
 * intended to be called or projected BEFORE AppletMain's UI loads. It must then establish a connection with the 
 * master server, and verify your identity before you will be redirected to the AppletMain UI. The UI must then
 * be modified to match your security clearance. NOTE: This class is a visual event, meaning it retains no 
 * network-related entities, NOR SHOULD IT.
 * 
 * @author hackjunky, jacrin
 *
 */
public class JLoginDialog extends JDialog{
	private static final long serialVersionUID = 8224336328398525415L;
	private JTextField usernameField;
	private JPasswordField passwordField;
	JLabel lblHelptext_1;
	
	Client superInstance;
	
	public JLoginDialog(Client instance) {
		setTitle("Mesa Labs WebConnect Single Sign-on");
		setResizable(false);
		
		superInstance = instance;
		
		//Set Size of Dialog to something smaller than 640x480, preferably in multiples of 8 or 16.
		this.setSize(new Dimension(445, 224));
		
		//A Java toolkit that provides us with information about the target system.
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		
		//Center this dialog on the screen
		this.setLocation(new Point(toolkit.getScreenSize().width / 2- (256),  toolkit.getScreenSize().height / 2 - (128)));
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new MigLayout("", "[][][]", "[]"));
		
		JLabel lblWelcomeToMesalabs = new JLabel("Welcome to MesaLabs WebConnect! Please enter your credentials below to authenticate.");
		lblWelcomeToMesalabs.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		panel.add(lblWelcomeToMesalabs, "cell 0 0");
		
		Canvas mesaIcon = new Canvas();
		panel.add(mesaIcon, "cell 1 0");
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new MigLayout("", "[grow][][]", "[][][2px:n:2px][][][][]"));
		
		JLabel lblHelptext = new JLabel("Enter your credentials below.");
		lblHelptext.setFont(new Font("Lucida Grande", Font.ITALIC, 10));
		panel_1.add(lblHelptext, "flowx,cell 0 1");
		
		JLabel lblid = new JLabel("UCMNetID (Username)");
		lblid.setFont(new Font("Lucida Grande", Font.BOLD, 11));
		panel_1.add(lblid, "flowx,cell 0 3");
		
		lblHelptext_1 = new JLabel("Invalid Username or Password.");
		lblHelptext_1.setFont(new Font("Tahoma", Font.ITALIC, 10));
		lblHelptext_1.setForeground(Color.RED);
		panel_1.add(lblHelptext_1, "cell 1 3 2 1");
		
		usernameField = new JTextField();
		panel_1.add(usernameField, "cell 0 4,growx");
		usernameField.setColumns(10);
		
		JLabel lblExampleJsmith = new JLabel("Example: JSmith");
		lblExampleJsmith.setFont(new Font("Lucida Grande", Font.ITALIC, 10));
		panel_1.add(lblExampleJsmith, "cell 1 4");
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setFont(new Font("Lucida Grande", Font.BOLD, 11));
		panel_1.add(lblPassword, "cell 0 5");
		
		passwordField = new JPasswordField();
		panel_1.add(passwordField, "cell 0 6,growx");
		
		JButton btnAuthenticate = new JButton("Authenticate");
		rootPane.setDefaultButton(btnAuthenticate);
		btnAuthenticate.addActionListener(new ActionListener() {

			/*
			 * JPasswordField doesn't actually return a String, it returns char[]. No idea why. Just to
			 * clarify, here, we create a new String object and pass in char[]. It gives us back the pass
			 * word but in a String.
			 */
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ShowHelpText("Authenticating...");
				superInstance.Authenticate(usernameField.getText(), new String(passwordField.getPassword()), JLoginDialog.this);
			}
		});
		panel_1.add(btnAuthenticate, "cell 1 6");
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JLoginDialog.this.setVisible(false);
				System.exit(0);
				//Terminate the Applet, we might even want to have the web-page redirect to home when this happens.
			}
		});
		panel_1.add(btnCancel, "cell 2 6,growx");
		lblHelptext_1.setText("");
		
		this.setVisible(true);
	}
	
	/**
	 * We will call this method from the client layer to indicate a valid login attempt.
	 */
	public void AuthSucess() {
		//We call dispose because it manually adds a callback to windowDeactivated 
		//and windowClosed. This will notify our applet that we're ready.
		JLoginDialog.this.setVisible(false);
		JLoginDialog.this.dispose();
		
	}
	
	/**
	 * We will call this method from the client layer to indicate an invalid login attempt.
	 */
	public void AuthFailure() {
		ShowHelpText("Invalid Username or Password.");
	}
	
	/**
	 * Just a little something something for us to be able to indicate to the user information.
	 * @param text
	 */
	public void ShowHelpText(String text) {
		lblHelptext_1.setText(text);
	}
}
