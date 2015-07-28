import java.applet.Applet;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.ImageIcon;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.border.BevelBorder;
import net.miginfocom.swing.MigLayout;

import javax.swing.border.LineBorder;

import java.awt.Font;

import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.JTextArea;
import javax.swing.JProgressBar;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.plaf.ColorUIResource;
import javax.swing.border.EtchedBorder;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;

public class QuizUI extends Applet {
	JPanel pnlSideMenu;
	JPanel pnlMain;
	JPanel pnlHolder;
	JPanel pnlInstruction;
	JPanel pnlQuiz;
	
	public QuizUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			UIManager.getLookAndFeelDefaults().put("Tree.background", new ColorUIResource(Color.darkGray));
			UIManager.getDefaults().put("Button.showMnemonics", Boolean.TRUE);
		}catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}catch (InstantiationException e) {
			e.printStackTrace();
		}catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		
		setBackground(Color.BLACK);
		setLayout(new BorderLayout(0, 0));
		
		pnlMain = new JPanel();
		pnlMain.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		pnlMain.setBackground(Color.DARK_GRAY);
		add(pnlMain, BorderLayout.CENTER);
		pnlMain.setLayout(new MigLayout("", "[0px:n:0px][grow][grow][grow]", "[][grow][grow][grow][]"));
		
		JLabel lblTime = new JLabel("XX:XX Remaining");
		lblTime.setFont(new Font("Consolas", Font.PLAIN, 26));
		lblTime.setForeground(Color.WHITE);
		pnlMain.add(lblTime, "cell 2 0 2 2,alignx right,aligny top");
		
		pnlSideMenu = new JPanel();
		pnlSideMenu.setBackground(Color.GRAY);
		pnlSideMenu.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		JLabel lblNewLabel_3 = new JLabel("Lesson Plan");
		lblNewLabel_3.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
		
		JTree tree = new JTree();
		tree.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		tree.setBackground(Color.DARK_GRAY);
		tree.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("Name's Lesson Plan") {
				private static final long serialVersionUID = -5189884384669868456L;

				{
					DefaultMutableTreeNode node_1;
					node_1 = new DefaultMutableTreeNode("Lesson 1");
						node_1.add(new DefaultMutableTreeNode("Activity 1"));
						node_1.add(new DefaultMutableTreeNode("Activity 2"));
						node_1.add(new DefaultMutableTreeNode("Activity ..."));
					add(node_1);
					node_1 = new DefaultMutableTreeNode("Lesson 2");
						node_1.add(new DefaultMutableTreeNode("Activity ..."));
					add(node_1);
					add(new DefaultMutableTreeNode("Quiz! Lessons 1 and 2"));
					node_1 = new DefaultMutableTreeNode("Lesson 3");
						node_1.add(new DefaultMutableTreeNode("Activity 1"));
						node_1.add(new DefaultMutableTreeNode("Quiz!"));
					add(node_1);
					node_1 = new DefaultMutableTreeNode("Lesson 4");
						node_1.add(new DefaultMutableTreeNode("Activity ..."));
					add(node_1);
					add(new DefaultMutableTreeNode("Lesson Closer"));
				}
			}
		));
		pnlSideMenu.setLayout(new MigLayout("", "[232px,grow]", "[][][0px:n:0px][grow][]"));
		
		pnlSideMenu.setVisible(false);
		
		JLabel lblDownloading = new JLabel("downloading...");
		lblDownloading.setForeground(Color.RED);
		lblDownloading.setHorizontalAlignment(SwingConstants.RIGHT);
		pnlSideMenu.add(lblDownloading, "cell 0 0,alignx right,aligny center");
		pnlSideMenu.add(lblNewLabel_3, "flowx,cell 0 1,alignx left,aligny top");
		
		Component horizontalStrut_3 = Box.createHorizontalStrut(256);
		pnlSideMenu.add(horizontalStrut_3, "cell 0 2");
		pnlSideMenu.add(tree, "cell 0 3,grow");
		
		JButton btnGoToSelected = new JButton("Go to Selected Lesson");
		btnGoToSelected.setForeground(Color.WHITE);
		btnGoToSelected.setBackground(Color.DARK_GRAY);
		pnlSideMenu.add(btnGoToSelected, "cell 0 4,growx");
		
		pnlHolder = new JPanel();
		pnlHolder.setBackground(Color.GRAY);
		pnlHolder.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		
		pnlQuiz = new JPanel();
		pnlQuiz.setBackground(Color.GRAY);
		
		pnlMain.add(pnlHolder, "cell 2 2,grow");
		
		pnlHolder.setLayout(new BorderLayout(0, 0));
		
		pnlInstruction = new JPanel();
		pnlInstruction.setBackground(Color.GRAY);
		pnlInstruction.setLayout(new MigLayout("", "[grow][]", "[][grow][]"));
		
		JLabel lblTitle = new JLabel("Learning Material Title");
		pnlInstruction.add(lblTitle, "flowx,cell 0 0,alignx left,aligny top");
		lblTitle.setFont(new Font("Copperplate", Font.PLAIN, 24));
		
		JLabel lblPages = new JLabel("Total Pages: XX");
		pnlInstruction.add(lblPages, "cell 1 0,alignx right,aligny center");
		
		JTextArea txtInformation = new JTextArea();
		txtInformation.setEditable(false);
		txtInformation.setEnabled(false);
		txtInformation.setDisabledTextColor(Color.BLACK);
		txtInformation.setLineWrap(true);
		txtInformation.setWrapStyleWord(true);
		txtInformation.setFont(new Font("Monospaced", Font.ITALIC, 13));
		txtInformation.setText("This is instructional material. It can take up many lines, and therefore can be used to teach quite a large quantity of information.\r\n\r\nThis is another line.");
		txtInformation.setForeground(Color.BLACK);
		txtInformation.setBackground(Color.GRAY);
		pnlInstruction.add(txtInformation, "cell 0 1 2 1,grow");
		
		JPanel pnlBar = new JPanel();
		pnlBar.setBackground(Color.GRAY);
		pnlInstruction.add(pnlBar, "cell 0 2 2 1,growx,aligny bottom");
		
		JButton btnBack = new JButton("<");
		btnBack.setForeground(Color.LIGHT_GRAY);
		btnBack.setBackground(Color.DARK_GRAY);
		pnlBar.add(btnBack);
		
		JLabel lblPage = new JLabel("Page ##");
		lblPage.setFont(new Font("Trajan Pro", Font.PLAIN, 14));
		pnlBar.add(lblPage);
		
		JButton btnForward = new JButton(">");
		btnForward.setForeground(Color.LIGHT_GRAY);
		btnForward.setBackground(Color.DARK_GRAY);
		pnlBar.add(btnForward);
		
		
		pnlHolder.add(pnlQuiz, BorderLayout.CENTER);
		//pnlHolder.add(pnlInstruction, BorderLayout.CENTER);
		
		Component horizontalStrut_2 = Box.createHorizontalStrut(128);
		pnlInstruction.add(horizontalStrut_2, "cell 0 0,grow");
		pnlMain.add(pnlSideMenu, "flowy,cell 0 1 1 3,grow");
		
		pnlQuiz.setLayout(new MigLayout("", "[grow][grow]", "[][grow][][grow]"));
		
		JLabel lblQuestion = new JLabel("Question");
		lblQuestion.setFont(new Font("Copperplate", Font.PLAIN, 24));
		pnlQuiz.add(lblQuestion, "flowx,cell 0 0,alignx left,aligny bottom");
		
		JLabel lblAdditionalMedia = new JLabel("this question has additional media");
		lblAdditionalMedia.setForeground(Color.YELLOW);
		lblAdditionalMedia.setHorizontalAlignment(SwingConstants.CENTER);
		lblAdditionalMedia.setFont(new Font("Lucida Grande", Font.ITALIC, 9));
		pnlQuiz.add(lblAdditionalMedia, "flowx,cell 1 0,alignx right,aligny center");
		
		JTextArea txtQuestion = new JTextArea();
		txtQuestion.setDisabledTextColor(Color.BLACK);
		txtQuestion.setEnabled(false);
		txtQuestion.setFont(new Font("Monospaced", Font.ITALIC, 13));
		txtQuestion.setWrapStyleWord(true);
		txtQuestion.setEditable(false);
		txtQuestion.setText("This is a default question that exceeds the size of the standard text area. This question should not increase the length of the form.");
		txtQuestion.setLineWrap(true);
		txtQuestion.setBackground(Color.GRAY);
		pnlQuiz.add(txtQuestion, "cell 0 1 2 1,grow");
		
		JPanel pnlMultipleChoice = new JPanel();
		pnlMultipleChoice.setBackground(Color.GRAY);
		pnlQuiz.add(pnlMultipleChoice, "cell 0 2,alignx center,aligny top");
		pnlMultipleChoice.setLayout(new MigLayout("", "[grow]", "[][grow][][][]"));
		
		JRadioButton rdbtnNewRadioButton = new JRadioButton("Response 1");
		rdbtnNewRadioButton.setBackground(Color.GRAY);
		pnlMultipleChoice.add(rdbtnNewRadioButton, "cell 0 0,growx");
		
		JRadioButton rdbtnResponse = new JRadioButton("Response 2");
		rdbtnResponse.setBackground(Color.GRAY);
		pnlMultipleChoice.add(rdbtnResponse, "cell 0 1");
		
		JRadioButton rdbtnNewRadioButton_1 = new JRadioButton("Response 3");
		rdbtnNewRadioButton_1.setBackground(Color.GRAY);
		pnlMultipleChoice.add(rdbtnNewRadioButton_1, "cell 0 2");
		
		JRadioButton rdbtnResponse_1 = new JRadioButton("Response 4");
		rdbtnResponse_1.setBackground(Color.GRAY);
		pnlMultipleChoice.add(rdbtnResponse_1, "cell 0 3");
		
		JRadioButton rdbtnResponse_2 = new JRadioButton("Response 5");
		rdbtnResponse_2.setBackground(Color.GRAY);
		pnlMultipleChoice.add(rdbtnResponse_2, "cell 0 4");
		
		JButton btnPlayMedia = new JButton("Play Media");
		btnPlayMedia.setIcon(new ImageIcon(QuizUI.class.getResource("/res/media_icn.png")));
		btnPlayMedia.setForeground(Color.WHITE);
		btnPlayMedia.setBackground(Color.DARK_GRAY);
		pnlQuiz.add(btnPlayMedia, "cell 1 0,alignx right,aligny center");
		
		JButton btnIDK = new JButton("Don't Know This");
		btnIDK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnIDK.setBackground(Color.DARK_GRAY);
		btnIDK.setForeground(Color.WHITE);
		pnlQuiz.add(btnIDK, "flowx,cell 1 2,alignx right,aligny bottom");
		
		JButton btnIan = new JButton("Know This");
		btnIan.setForeground(Color.WHITE);
		btnIan.setBackground(Color.DARK_GRAY);
		pnlQuiz.add(btnIan, "cell 1 2,alignx right,aligny bottom");
		
		JLabel lblNumber = new JLabel("#XX");
		lblNumber.setFont(new Font("Dialog", Font.BOLD, 24));
		pnlQuiz.add(lblNumber, "cell 0 0");
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(64);
		pnlQuiz.add(horizontalStrut_1, "cell 1 2");
		
		JLabel lblStatus = new JLabel("connecting...");
		lblStatus.setForeground(Color.LIGHT_GRAY);
		lblStatus.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 14));
		pnlMain.add(lblStatus, "cell 2 3,alignx center,aligny top");
		
		JLabel lblNewLabel_1 = new JLabel("ver x.xx.x");
		lblNewLabel_1.setForeground(Color.WHITE);
		pnlMain.add(lblNewLabel_1, "cell 2 4 2 1,alignx right");
		
		JPanel pnlHeader = new JPanel();
		pnlHeader.setBackground(Color.GRAY);
		pnlHeader.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		add(pnlHeader, BorderLayout.NORTH);
		pnlHeader.setLayout(new MigLayout("", "[][][][][][grow][]", "[][]"));
		
		JLabel lblNewLabel = new JLabel("Lesson XX, Section XX - STATUS");
		lblNewLabel.setFont(new Font("Lucida Grande", Font.BOLD, 18));
		pnlHeader.add(lblNewLabel, "cell 0 0 7 1,growx,aligny center");
		
		JLabel lblProgress = new JLabel("Progress:");
		pnlHeader.add(lblProgress, "flowx,cell 1 1");
		
		JLabel lblProgressText = new JLabel("XXX/XXX Questions");
		pnlHeader.add(lblProgressText, "cell 4 1,alignx center");
		
		JButton btnViewPlan = new JButton("View Lesson Plan");
		btnViewPlan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (pnlSideMenu.isVisible()) {
					pnlSideMenu.setVisible(false);
					((MigLayout)pnlMain.getLayout()).setColumnConstraints("[0px:n:0px,grow][grow][][grow]");
					QuizUI.this.validate();
				}else {
					pnlSideMenu.setVisible(true);
					((MigLayout)pnlMain.getLayout()).setColumnConstraints("[grow][grow][][grow]");
					QuizUI.this.validate();
				}
			}
		});
		pnlHeader.add(btnViewPlan, "cell 6 1");
		btnViewPlan.setForeground(Color.WHITE);
		btnViewPlan.setBackground(Color.DARK_GRAY);
		
		JProgressBar progressBar = new JProgressBar();
		pnlHeader.add(progressBar, "cell 1 1 3 1,growx");
		
		this.setSize(640, 480);
		
		SwitchToQuizMode();
	}
	
	public void SwitchToQuizMode() {
		pnlHolder.add(pnlQuiz, BorderLayout.CENTER);
		pnlHolder.remove(pnlInstruction);
	}
	
	public void SwitchToReadingMode() {
		pnlHolder.add(pnlInstruction, BorderLayout.CENTER);
		pnlHolder.remove(pnlQuiz);
	}
	
	private static final long serialVersionUID = -3138705569063633507L;
}
