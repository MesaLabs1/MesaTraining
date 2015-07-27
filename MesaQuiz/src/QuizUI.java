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
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class QuizUI extends Applet {
	
	public QuizUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
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
		
		JPanel pnlExternal = new JPanel();
		pnlExternal.setBorder(null);
		pnlExternal.setBackground(Color.DARK_GRAY);
		add(pnlExternal, BorderLayout.CENTER);
		pnlExternal.setLayout(new MigLayout("", "[grow]", "[grow][grow][]"));
		
		JPanel pnlQuiz = new JPanel();
		pnlQuiz.setBackground(Color.GRAY);
		pnlQuiz.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		
		pnlExternal.add(pnlQuiz, "cell 0 0 1 2,grow");
		pnlQuiz.setLayout(new MigLayout("", "[grow][grow]", "[][][][][grow]"));
		
		JLabel lblQuestion = new JLabel("Question:");
		lblQuestion.setFont(new Font("Copperplate", Font.PLAIN, 24));
		pnlQuiz.add(lblQuestion, "cell 0 0,alignx left,aligny bottom");
		
		JLabel lblAdditionalMedia = new JLabel("this question has additional media");
		lblAdditionalMedia.setForeground(Color.YELLOW);
		lblAdditionalMedia.setHorizontalAlignment(SwingConstants.CENTER);
		lblAdditionalMedia.setFont(new Font("Lucida Grande", Font.ITALIC, 9));
		pnlQuiz.add(lblAdditionalMedia, "flowx,cell 1 0,alignx right,aligny center");
		
		JTextArea txtQuestion = new JTextArea();
		txtQuestion.setEditable(false);
		txtQuestion.setText("This is a default question that exceeds the size of the standard text area. This question should not increase the length of the form.");
		txtQuestion.setLineWrap(true);
		txtQuestion.setBackground(Color.GRAY);
		pnlQuiz.add(txtQuestion, "cell 0 1 2 1,grow");
		
		JPanel pnlMultipleChoice = new JPanel();
		pnlMultipleChoice.setBackground(Color.GRAY);
		pnlQuiz.add(pnlMultipleChoice, "cell 0 2,growx,aligny top");
		pnlMultipleChoice.setLayout(new MigLayout("", "[grow]", "[][][][][]"));
		
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
		pnlQuiz.add(btnPlayMedia, "cell 1 0,alignx left,aligny center");
		
		JButton btnIDK = new JButton("I Don't Know");
		btnIDK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnIDK.setBackground(Color.DARK_GRAY);
		btnIDK.setForeground(Color.WHITE);
		pnlQuiz.add(btnIDK, "flowx,cell 1 2,alignx left,aligny bottom");
		
		JButton btnIan = new JButton("Confirm");
		btnIan.setForeground(Color.WHITE);
		btnIan.setBackground(Color.DARK_GRAY);
		pnlQuiz.add(btnIan, "cell 1 2,alignx left,aligny bottom");
		
		JLabel lblImage = new JLabel("");
		pnlQuiz.add(lblImage, "flowy,cell 0 3 2 1,grow");
		
		JLabel lblNewLabel_1 = new JLabel("connecting...");
		lblNewLabel_1.setForeground(Color.LIGHT_GRAY);
		lblNewLabel_1.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 14));
		pnlExternal.add(lblNewLabel_1, "cell 0 2,alignx center,aligny top");
		
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
		pnlHeader.add(btnViewPlan, "cell 6 1");
		btnViewPlan.setForeground(Color.WHITE);
		btnViewPlan.setBackground(Color.DARK_GRAY);
		
		JProgressBar progressBar = new JProgressBar();
		pnlHeader.add(progressBar, "cell 1 1 3 1,growx");
		
		this.setSize(505, 401);
	}
	private static final long serialVersionUID = -3138705569063633507L;
}
