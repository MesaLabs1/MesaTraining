import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class JDatabaseDialog extends JDialog {
	private static final long serialVersionUID = -9137889723050561040L;
	private JTextField textField;
	private JPasswordField passwordField;
	
	Utils util;

	public JDatabaseDialog(Utils u) {
		util = u;
		
		setAlwaysOnTop(true);
		setResizable(false);
		setTitle("Create a Database");
		this.setSize(new Dimension(400, 207));
		getContentPane().setLayout(new MigLayout("", "[480px]", "[16px][182px]"));
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, "cell 0 1,grow");
		panel.setLayout(new MigLayout("", "[grow]", "[][grow]"));
		
		JLabel lblPleaseDesignateA = new JLabel("Please designate a database username and password.");
		panel.add(lblPleaseDesignateA, "cell 0 0,alignx center");
		
		JPanel panel_1 = new JPanel();
		panel.add(panel_1, "cell 0 1,grow");
		panel_1.setLayout(new MigLayout("", "[grow]", "[][][][][]"));
		
		JLabel lblUsername = new JLabel("Username");
		panel_1.add(lblUsername, "cell 0 0");
		
		textField = new JTextField();
		panel_1.add(textField, "cell 0 1,growx");
		textField.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password");
		panel_1.add(lblPassword, "cell 0 2");
		
		passwordField = new JPasswordField();
		panel_1.add(passwordField, "cell 0 3,growx");
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
			}
		});
		panel_1.add(btnCancel, "flowx,cell 0 4,alignx right");
		
		JButton btnNewButton = new JButton("Create DB");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				CreateDB(textField.getText(), passwordField.getPassword().toString());
			}
		});
		panel_1.add(btnNewButton, "cell 0 4,alignx right");
	}
	
	public boolean CreateDB(String username, String password) {
		util.Log(username + ", I will create you a database, now.");
		
		return true;
	}
}
