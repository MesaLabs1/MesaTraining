import java.awt.Dimension;

import javax.swing.JDialog;
import net.miginfocom.swing.MigLayout;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.awt.Window.Type;
import javax.swing.JLabel;
import java.awt.Canvas;
import javax.swing.JTextField;
import java.awt.Component;
import javax.swing.Box;


public class JLoginDialog extends JDialog{
	private JTextField textField;
	
	public JLoginDialog() {
		setTitle("Mesa Labs WebConnect Single Sign-on");
		setType(Type.POPUP);
		//Set Size of Dialog to something smaller than 640x480, preferably in multiples of 8 or 16.
		this.setSize(new Dimension(512, 256));
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new MigLayout("", "[][]", "[]"));
		
		Canvas mesaIcon = new Canvas();
		panel.add(mesaIcon, "cell 0 0");
		
		JLabel lblWelcomeToMesalabs = new JLabel("Welcome to MesaLabs WebConnect! Please enter your credentials below to authenticate.");
		panel.add(lblWelcomeToMesalabs, "cell 1 0");
		
		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new MigLayout("", "[][grow][]", "[][][][][][]"));
		
		JLabel lblid = new JLabel("UCMNetID (Username)");
		panel_1.add(lblid, "cell 1 1");
		
		textField = new JTextField();
		panel_1.add(textField, "cell 1 2,growx");
		textField.setColumns(10);
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		panel_1.add(horizontalStrut, "cell 2 3");
		
		JLabel lblPassword = new JLabel("Password");
		panel_1.add(lblPassword, "cell 1 5");
	}
}
