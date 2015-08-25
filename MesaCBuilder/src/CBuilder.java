import java.applet.Applet;
import javax.swing.BoxLayout;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Color;
import javax.swing.border.BevelBorder;
import net.miginfocom.swing.MigLayout;
import javax.swing.JButton;
import javax.swing.JTree;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JTextArea;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

public class CBuilder extends JFrame{
	
	public CBuilder() {
		setLayout(new BorderLayout(0, 0));
		
		JPanel pnlHeader = new JPanel();
		pnlHeader.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		pnlHeader.setBackground(Color.LIGHT_GRAY);
		add(pnlHeader, BorderLayout.NORTH);
		pnlHeader.setLayout(new BorderLayout(0, 0));
		
		JPanel pnlProgress = new JPanel();
		pnlProgress.setBackground(Color.LIGHT_GRAY);
		pnlHeader.add(pnlProgress, BorderLayout.EAST);
		pnlProgress.setLayout(new MigLayout("", "[]", "[][]"));
		
		JProgressBar progressBar = new JProgressBar();
		pnlProgress.add(progressBar, "cell 0 1,alignx left,aligny top");
		
		JLabel lblProgress = new JLabel("waiting...");
		lblProgress.setFont(new Font("Consolas", Font.ITALIC, 10));
		pnlProgress.add(lblProgress, "cell 0 0,alignx center,growy");
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);
		pnlHeader.add(panel, BorderLayout.WEST);
		panel.setLayout(new MigLayout("", "[6px:n:6px][]", "[grow][][grow]"));
		
		JLabel lblCurriculumbuilderByMesa = new JLabel("MesaConnect CurriculumBuilder");
		panel.add(lblCurriculumbuilderByMesa, "cell 1 1,aligny center");
		lblCurriculumbuilderByMesa.setFont(new Font("Consolas", Font.BOLD, 18));
		
		JPanel pnlStart = new JPanel();
		pnlStart.setBackground(Color.GRAY);
		
		JPanel pnlMain = new JPanel();
		pnlMain.setBackground(Color.GRAY);
		
		JPanel pnlHolder = new JPanel();
		pnlHolder.setLayout(new BorderLayout());
		
		add(pnlHolder, BorderLayout.CENTER);
		
		pnlMain.setLayout(new MigLayout("", "[][][grow]", "[][][grow][grow]"));
		
		JLabel lblNewLabel = new JLabel("You can either select an existing entry, or create another one. Simply navigate the tree to begin.");
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setFont(new Font("Consolas", Font.PLAIN, 11));
		pnlMain.add(lblNewLabel, "cell 0 0 3 1,alignx center,aligny center");
		
		Component verticalStrut_1 = Box.createVerticalStrut(6);
		pnlMain.add(verticalStrut_1, "cell 2 1");
		
		Component verticalStrut = Box.createVerticalStrut(6);
		pnlMain.add(verticalStrut, "flowy,cell 0 2");
		
		JTree tree = new JTree();
		tree.setFont(new Font("Consolas", Font.PLAIN, 11));
		tree.setForeground(Color.WHITE);
		tree.setBackground(Color.GRAY);
		pnlMain.add(tree, "cell 0 2,grow");
		
		Component horizontalStrut = Box.createHorizontalStrut(6);
		pnlMain.add(horizontalStrut, "cell 1 2");
		
		JPanel pnlEditor = new JPanel();
		pnlEditor.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		pnlEditor.setBackground(Color.LIGHT_GRAY);
		pnlMain.add(pnlEditor, "cell 2 2 1 2,grow");
		pnlEditor.setLayout(new BorderLayout(0, 0));
		
		JPanel pnlQuizEditor = new JPanel();
		pnlQuizEditor.setBackground(Color.LIGHT_GRAY);
		pnlEditor.add(pnlQuizEditor, BorderLayout.CENTER);
		pnlQuizEditor.setLayout(new MigLayout("", "[][4px:n:4px][grow][4px:n:6px]", "[][][4px:n:6px][grow][][][][][][][][4px:n:6px][]"));
		
		JLabel lblQuizEditor = new JLabel("Quiz Editor");
		lblQuizEditor.setFont(new Font("Consolas", Font.BOLD, 16));
		pnlQuizEditor.add(lblQuizEditor, "cell 0 0");
		
		JButton btnAttach = new JButton("Attach Media");
		btnAttach.setFont(new Font("Consolas", Font.PLAIN, 11));
		btnAttach.setForeground(Color.WHITE);
		btnAttach.setBackground(Color.DARK_GRAY);
		pnlQuizEditor.add(btnAttach, "cell 2 1,alignx right");
		
		JLabel lblQuestion = new JLabel("Question");
		lblQuestion.setFont(new Font("Consolas", Font.PLAIN, 11));
		pnlQuizEditor.add(lblQuestion, "cell 0 3,alignx right,aligny top");
		
		JTextArea txtQuestion = new JTextArea();
		txtQuestion.setWrapStyleWord(true);
		txtQuestion.setLineWrap(true);
		txtQuestion.setText("This is some text that will appear as the question in a quiz.");
		txtQuestion.setForeground(Color.WHITE);
		txtQuestion.setBackground(Color.GRAY);
		pnlQuizEditor.add(txtQuestion, "cell 2 3,grow");
		
		JLabel lblPotentialAnswers = new JLabel("Answers");
		lblPotentialAnswers.setFont(new Font("Consolas", Font.PLAIN, 11));
		pnlQuizEditor.add(lblPotentialAnswers, "cell 0 5,alignx right");
		
		JRadioButton radioOne = new JRadioButton("Unused Answer 1");
		radioOne.setFont(new Font("Consolas", Font.PLAIN, 11));
		radioOne.setBackground(Color.LIGHT_GRAY);
		pnlQuizEditor.add(radioOne, "flowx,cell 2 5");
		
		JRadioButton radioTwo = new JRadioButton("Unused Answer 2");
		radioTwo.setFont(new Font("Consolas", Font.PLAIN, 11));
		radioTwo.setBackground(Color.LIGHT_GRAY);
		pnlQuizEditor.add(radioTwo, "flowx,cell 2 6");
		
		JRadioButton radioThree = new JRadioButton("Unused Answer 3");
		radioThree.setFont(new Font("Consolas", Font.PLAIN, 11));
		radioThree.setBackground(Color.LIGHT_GRAY);
		pnlQuizEditor.add(radioThree, "cell 2 7");
		
		JRadioButton radioFour = new JRadioButton("Unused Answer 4");
		radioFour.setFont(new Font("Consolas", Font.PLAIN, 11));
		radioFour.setBackground(Color.LIGHT_GRAY);
		pnlQuizEditor.add(radioFour, "cell 2 8");
		
		JRadioButton radioFive = new JRadioButton("Unused Answer 5");
		radioFive.setFont(new Font("Consolas", Font.PLAIN, 11));
		radioFive.setBackground(Color.LIGHT_GRAY);
		pnlQuizEditor.add(radioFive, "cell 2 9");
		
		JLabel lblPlaceATick = new JLabel("place a tick next to the correct answer");
		lblPlaceATick.setFont(new Font("Consolas", Font.ITALIC, 11));
		lblPlaceATick.setForeground(Color.DARK_GRAY);
		pnlQuizEditor.add(lblPlaceATick, "cell 2 10,alignx center");
		
		JButton btnSave = new JButton("Save and Close");
		btnSave.setFont(new Font("Consolas", Font.PLAIN, 11));
		btnSave.setBackground(Color.DARK_GRAY);
		btnSave.setForeground(Color.WHITE);
		pnlQuizEditor.add(btnSave, "flowx,cell 2 12,alignx right");
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setFont(new Font("Consolas", Font.PLAIN, 11));
		btnCancel.setBackground(Color.DARK_GRAY);
		btnCancel.setForeground(Color.WHITE);
		pnlQuizEditor.add(btnCancel, "cell 2 12");
		
		JPanel pnlChange = new JPanel();
		pnlChange.setBackground(Color.LIGHT_GRAY);
		pnlQuizEditor.add(pnlChange, "cell 2 5,grow");
		
		JButton btnEdit = new JButton("Edit");
		btnEdit.setFont(new Font("Consolas", Font.PLAIN, 11));
		btnEdit.setBackground(Color.DARK_GRAY);
		btnEdit.setForeground(Color.WHITE);
		pnlChange.add(btnEdit);
		
		JButton btnClear = new JButton("Clear");
		btnClear.setForeground(Color.WHITE);
		btnClear.setBackground(Color.DARK_GRAY);
		btnClear.setFont(new Font("Consolas", Font.PLAIN, 11));
		pnlChange.add(btnClear);
		
		pnlStart.setLayout(new MigLayout("", "[grow][][grow]", "[grow][][4px:n:4px][][][grow]"));
		
		JLabel lblTitle = new JLabel("please select an option below");
		lblTitle.setFont(new Font("Consolas", Font.PLAIN, 11));
		pnlStart.add(lblTitle, "cell 0 1 3 1,alignx center");
		
		JButton btnCreate = new JButton("Create New Course Plan");
		btnCreate.setForeground(Color.WHITE);
		btnCreate.setBackground(Color.DARK_GRAY);
		pnlStart.add(btnCreate, "cell 1 3,growx");
		
		JButton btnLoad = new JButton("Load Existing Course Plan");
		btnLoad.setForeground(Color.WHITE);
		btnLoad.setBackground(Color.DARK_GRAY);
		pnlStart.add(btnLoad, "cell 1 4,growx");
		
		JLabel lblVersion = new JLabel("CBuilder v1.0 by Jad Aboulhosn");
		lblVersion.setForeground(Color.WHITE);
		lblVersion.setBackground(Color.WHITE);
		pnlStart.add(lblVersion, "cell 0 5 3 1,alignx right,aligny bottom");
		
		pnlHolder.add(pnlMain, BorderLayout.CENTER);
		
		this.setSize(640, 480);
	}
	private static final long serialVersionUID = -611911711040854050L;

}
