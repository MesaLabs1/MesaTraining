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
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import javax.swing.JProgressBar;

public class QuizUI extends Applet{
	public QuizUI() {
		setBackground(Color.BLACK);
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(null);
		panel_1.setBackground(Color.DARK_GRAY);
		add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new MigLayout("", "[grow]", "[grow][grow][]"));
		
		JPanel panel_2 = new JPanel();
		panel_2.setBackground(Color.GRAY);
		panel_2.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		panel_1.add(panel_2, "cell 0 0 1 2,grow");
		panel_2.setLayout(new MigLayout("", "[grow]", "[][][grow][][][][][]"));
		
		JLabel lblQuestion = new JLabel("Question:");
		lblQuestion.setFont(new Font("Copperplate", Font.PLAIN, 24));
		panel_2.add(lblQuestion, "cell 0 0,alignx left,aligny bottom");
		
		JTextArea txtrThisIsA = new JTextArea();
		txtrThisIsA.setText("This is a default question that exceeds the size of the standard text area. This question should not increase the length of the form.");
		txtrThisIsA.setLineWrap(true);
		txtrThisIsA.setBackground(Color.GRAY);
		panel_2.add(txtrThisIsA, "cell 0 1,grow");
		
		JLabel label = new JLabel("");
		panel_2.add(label, "flowx,cell 0 2,grow");
		
		JRadioButton rdbtnNewRadioButton = new JRadioButton("Response 1");
		panel_2.add(rdbtnNewRadioButton, "cell 0 3");
		
		JRadioButton rdbtnResponse = new JRadioButton("Response 2");
		panel_2.add(rdbtnResponse, "cell 0 4");
		
		JRadioButton rdbtnNewRadioButton_1 = new JRadioButton("Response 3");
		panel_2.add(rdbtnNewRadioButton_1, "cell 0 5");
		
		JRadioButton rdbtnResponse_1 = new JRadioButton("Response 4");
		panel_2.add(rdbtnResponse_1, "cell 0 6");
		
		JRadioButton radioButton = new JRadioButton("Response 4");
		panel_2.add(radioButton, "cell 0 7");
		
		JLabel lblNewLabel = new JLabel("");
		panel_2.add(lblNewLabel, "cell 0 2,grow");
		
		JLabel label_1 = new JLabel("");
		panel_2.add(label_1, "cell 0 2,grow");
		
		JLabel lblNewLabel_1 = new JLabel("connecting...");
		lblNewLabel_1.setForeground(Color.LIGHT_GRAY);
		lblNewLabel_1.setFont(new Font("Vijaya", Font.BOLD, 14));
		panel_1.add(lblNewLabel_1, "cell 0 2,alignx center,aligny top");
		
		JPanel panel_4 = new JPanel();
		panel_4.setBackground(Color.GRAY);
		panel_4.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		add(panel_4, BorderLayout.NORTH);
		panel_4.setLayout(new MigLayout("", "[][][grow][]", "[]"));
		
		JLabel lblProgress = new JLabel("Progress:");
		panel_4.add(lblProgress, "cell 0 0");
		
		JProgressBar progressBar = new JProgressBar();
		panel_4.add(progressBar, "cell 1 0");
		
		JLabel lblProgressText = new JLabel("XXX/XXX Questions");
		panel_4.add(lblProgressText, "cell 3 0,alignx right");
		
		this.setSize(505, 401);
	}
	private static final long serialVersionUID = -3138705569063633507L;
}
