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
	JLabel lblHelptext;
	JLabel lblHelptext_1;
	
	ClientMain superInstance;
	
	public JLoginDialog(ClientMain instance) {
		setTitle("Mesa Labs WebConnect Single Sign-on");
		setResizable(false);
		
		superInstance = instance;
		
		//Set Size of Dialog to something smaller than 640x480, preferably in multiples of 8 or 16.
		this.setSize(new Dimension(512, 256));
		
		//A Java toolkit that provides us with information about the target system.
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		
		//Center this dialog on the screen
		this.setLocation(new Point(toolkit.getScreenSize().width / 2- (256),  toolkit.getScreenSize().height / 2 - (128)));
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new MigLayout("", "[][]", "[]"));
		
		Canvas mesaIcon = new Canvas();
		panel.add(mesaIcon, "cell 0 0");
		
		JLabel lblWelcomeToMesalabs = new JLabel("Welcome to MesaLabs WebConnect! Please enter your credentials below to authenticate.");
		lblWelcomeToMesalabs.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		panel.add(lblWelcomeToMesalabs, "cell 1 0");
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new MigLayout("", "[][grow][][][]", "[][][][][][][][]"));
		
		lblHelptext = new JLabel("Enter your credentials below.");
		lblHelptext.setFont(new Font("Lucida Grande", Font.ITALIC, 10));
		panel_1.add(lblHelptext, "cell 1 0");
		
		JLabel lblid = new JLabel("UCMNetID (Username)");
		lblid.setFont(new Font("Lucida Grande", Font.BOLD, 11));
		panel_1.add(lblid, "cell 1 2");
		
		usernameField = new JTextField();
		panel_1.add(usernameField, "cell 1 3 3 1,growx");
		usernameField.setColumns(10);
		
		JLabel lblExampleJsmith = new JLabel("Example: JSmith");
		lblExampleJsmith.setFont(new Font("Lucida Grande", Font.ITALIC, 10));
		panel_1.add(lblExampleJsmith, "cell 4 3");
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setFont(new Font("Lucida Grande", Font.BOLD, 11));
		panel_1.add(lblPassword, "cell 1 4");
		
		passwordField = new JPasswordField();
		panel_1.add(passwordField, "cell 1 5 3 1,growx");
		
		JButton btnAuthenticate = new JButton("Authenticate");
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
		
		lblHelptext_1 = new JLabel("");
		lblHelptext_1.setFont(new Font("Lucida Grande", Font.BOLD | Font.ITALIC, 11));
		lblHelptext_1.setForeground(Color.RED);
		panel_1.add(lblHelptext_1, "cell 1 7");
		panel_1.add(btnAuthenticate, "cell 3 7");
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JLoginDialog.this.setVisible(false);
				System.exit(0);
				//Terminate the Applet, we might even want to have the web-page redirect to home when this happens.
			}
		});
		panel_1.add(btnCancel, "cell 4 7");
		
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
