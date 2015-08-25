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


public class PasswordDialog extends JDialog{
	private static final long serialVersionUID = 8224336328398525415L;
	private JTextField usernameField;
	private JPasswordField passwordField;
	JLabel lblHelptext_1;
	
	Client superInstance;
	
	public PasswordDialog(Client c) {
		setTitle("Mesa Labs WebConnect - Change Password");
		setResizable(false);	
		superInstance = c;		
		this.setSize(new Dimension(445, 194));	
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		this.setLocation(new Point(toolkit.getScreenSize().width / 2- (256),  toolkit.getScreenSize().height / 2 - (128)));
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new MigLayout("", "[grow][][]", "[][][][2px:n:2px][][][][]"));
		
		JLabel lblDescription = new JLabel("Enter the detials required below.");
		lblDescription.setFont(new Font("Lucida Grande", Font.ITALIC, 10));
		panel_1.add(lblDescription, "flowx,cell 0 0");
		
		JLabel lblid = new JLabel("Current Password");
		lblid.setFont(new Font("Lucida Grande", Font.BOLD, 11));
		panel_1.add(lblid, "flowx,cell 0 2");
		
		usernameField = new JTextField();
		panel_1.add(usernameField, "cell 0 4,growx");
		usernameField.setColumns(10);
		
		lblHelptext_1 = new JLabel("Invalid Username or Password.");
		lblHelptext_1.setFont(new Font("Tahoma", Font.ITALIC, 10));
		lblHelptext_1.setForeground(Color.RED);
		panel_1.add(lblHelptext_1, "cell 1 4 2 1");
		
		JLabel lblPassword = new JLabel("New Password");
		lblPassword.setFont(new Font("Lucida Grande", Font.BOLD, 11));
		panel_1.add(lblPassword, "cell 0 5");
		lblHelptext_1.setText("");
		
		passwordField = new JPasswordField();
		panel_1.add(passwordField, "cell 0 6,growx");
		
		JButton btnAuthenticate = new JButton("Confirm");
		rootPane.setDefaultButton(btnAuthenticate);
		btnAuthenticate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ShowHelpText("Working...");
				superInstance.instance.remoteRequest("$CHANGE PASSWORD " + superInstance.instance.getUsername());
				PasswordDialog.this.setVisible(false);
				PasswordDialog.this.dispose();
			}
		});
		panel_1.add(btnAuthenticate, "cell 1 6 2 1");
		
		this.setAlwaysOnTop(true);
		this.setVisible(true);
	}
	
	
	public void ShowHelpText(String text) {
		lblHelptext_1.setText(text);
	}
}
