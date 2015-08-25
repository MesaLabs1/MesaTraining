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
import javax.swing.JRadioButton;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;


public class NewUserDialog extends JDialog{
	private static final long serialVersionUID = 8224336328398525415L;
	private JTextField usernameField;
	private JPasswordField passwordField;
	JLabel lblHelptext_1;
	
	JRadioButton radioUser;
	JRadioButton radioAdmin;
	JRadioButton radioSuperadmin;
	
	Client superInstance;
	
	public NewUserDialog(Client c) {
		setTitle("Mesa Labs WebConnect - Change Password");
		setResizable(false);
		
		superInstance = c;
		
		//Set Size of Dialog to something smaller than 640x480, preferably in multiples of 8 or 16.
		this.setSize(new Dimension(445, 214));
		
		//A Java toolkit that provides us with information about the target system.
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		
		//Center this dialog on the screen
		this.setLocation(new Point(toolkit.getScreenSize().width / 2- (256),  toolkit.getScreenSize().height / 2 - (128)));
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new MigLayout("", "[40px:n,grow][][]", "[][][][2px:n:2px][][][][]"));
		
		JLabel lblDescription = new JLabel("Create a new user by providing the required information.");
		lblDescription.setFont(new Font("Lucida Grande", Font.ITALIC, 10));
		panel_1.add(lblDescription, "flowx,cell 0 0");
		
		JLabel lblid = new JLabel("UCMNetID (Username)");
		lblid.setFont(new Font("Lucida Grande", Font.BOLD, 11));
		panel_1.add(lblid, "flowx,cell 0 2");
		
		usernameField = new JTextField();
		panel_1.add(usernameField, "cell 0 4,growx");
		usernameField.setColumns(10);
		
		lblHelptext_1 = new JLabel("Invalid Username or Password.");
		lblHelptext_1.setFont(new Font("Tahoma", Font.ITALIC, 10));
		lblHelptext_1.setForeground(Color.RED);
		panel_1.add(lblHelptext_1, "cell 1 4 2 1");
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setFont(new Font("Lucida Grande", Font.BOLD, 11));
		panel_1.add(lblPassword, "cell 0 5");
		lblHelptext_1.setText("");
		
		passwordField = new JPasswordField();
		panel_1.add(passwordField, "cell 0 6,growx");
		
		radioUser = new JRadioButton("User");
		radioUser.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if (radioUser.isSelected()) {
					radioAdmin.setSelected(false);
					radioSuperadmin.setSelected(false);
				}
			}
		});
		radioUser.setSelected(true);
		panel_1.add(radioUser, "flowx,cell 0 7,alignx center");
		
		radioAdmin = new JRadioButton("Admin");
		radioAdmin.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (radioAdmin.isSelected()) {
					radioUser.setSelected(false);
					radioSuperadmin.setSelected(false);
				}
			}
		});
		panel_1.add(radioAdmin, "cell 0 7,alignx center");
		
		radioSuperadmin = new JRadioButton("Superadmin");
		radioSuperadmin.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (radioSuperadmin.isSelected()) {
					radioUser.setSelected(false);
					radioAdmin.setSelected(false);
				}
			}
		});
		panel_1.add(radioSuperadmin, "cell 0 7,alignx center");
		
		JButton btnAuthenticate = new JButton("Confirm");
		rootPane.setDefaultButton(btnAuthenticate);
		btnAuthenticate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ShowHelpText("Working...");
				String selectedRank = "user";
				if (radioSuperadmin.isSelected()) {
					selectedRank = "superadmin";
				}else if (radioAdmin.isSelected()) {
					selectedRank = "admin";
				}else if (radioUser.isSelected()) {
					selectedRank = "user";
				}
				superInstance.instance.remoteRequest("$CREATE USER " + usernameField.getText() + " " + new String(passwordField.getPassword()) + " " + selectedRank);
				NewUserDialog.this.setVisible(false);
				NewUserDialog.this.dispose();
			}
		});
		panel_1.add(btnAuthenticate, "cell 1 7 2 1");
		
		this.setAlwaysOnTop(true);
		this.setVisible(true);
	}
	
	
	public void ShowHelpText(String text) {
		lblHelptext_1.setText(text);
	}
}
