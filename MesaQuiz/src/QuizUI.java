import java.applet.Applet;
import javax.swing.JDesktopPane;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.Canvas;
import java.awt.Label;
import javax.swing.JSplitPane;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.border.BevelBorder;
import javax.swing.BoxLayout;
import net.miginfocom.swing.MigLayout;
import javax.swing.border.LineBorder;
import java.awt.Font;

public class QuizUI extends Applet{
	public QuizUI() {
		setBackground(Color.BLACK);
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.WEST);
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(null);
		panel_1.setBackground(Color.DARK_GRAY);
		add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new MigLayout("", "[128px:n:128px][grow][128px:n:128px]", "[grow][grow][grow][grow][grow]"));
		
		JPanel panel_2 = new JPanel();
		panel_2.setBackground(Color.GRAY);
		panel_2.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		panel_1.add(panel_2, "cell 1 1 1 3,grow");
		
		JLabel lblNewLabel_1 = new JLabel("connecting...");
		lblNewLabel_1.setForeground(Color.LIGHT_GRAY);
		lblNewLabel_1.setFont(new Font("Vijaya", Font.BOLD, 14));
		panel_1.add(lblNewLabel_1, "cell 1 4,alignx center,aligny top");
		
		JLabel lblNewLabel = new JLabel("ver 1.0");
		lblNewLabel.setForeground(Color.LIGHT_GRAY);
		panel_1.add(lblNewLabel, "cell 2 4,alignx right,aligny bottom");
		
		JPanel panel_4 = new JPanel();
		panel_4.setBackground(Color.GRAY);
		panel_4.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		add(panel_4, BorderLayout.NORTH);
		panel_4.setLayout(new MigLayout("", "[1px][1px]", "[32px]"));
		
		Component verticalStrut_1 = Box.createVerticalStrut(32);
		panel_4.add(verticalStrut_1, "cell 1 0,growx,aligny top");
		
		JLabel label = new JLabel("");
		panel_4.add(label, "cell 0 0,alignx left,growy");
	}
	private static final long serialVersionUID = -3138705569063633507L;
}
