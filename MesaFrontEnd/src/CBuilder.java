import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JLabel;

import java.awt.Font;
import java.awt.Color;

import javax.swing.border.BevelBorder;
import javax.swing.tree.DefaultTreeCellRenderer;

import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;
import javax.swing.JTree;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JRadioButton;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class CBuilder extends JPanel {
	//Multiple Choice
	int editNode = 0;
	String[] choices = new String[5];
	
	JRadioButton radioOne;
	JRadioButton radioTwo;
	JRadioButton radioThree;
	JRadioButton radioFour;
	JRadioButton radioFive;

	JPanel pnlQuizEditor;
	
	JPanel pnlChange;
	
	public CBuilder() {
		setLayout(new BorderLayout(0, 0));

		JPanel pnlStart = new JPanel();
		pnlStart.setBackground(Color.GRAY);

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

		this.setSize(640, 480);

		JPanel outer = new JPanel();
		outer.setBackground(Color.DARK_GRAY);
		add(outer, BorderLayout.CENTER);
		outer.setLayout(new BorderLayout(0, 0));

		JPanel inner = new JPanel();
		outer.add(inner);
		inner.setLayout(new BorderLayout(0, 0));

		JPanel pnlMain = new JPanel();
		pnlMain.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		pnlMain.setBackground(Color.GRAY);

		JPanel pnlHolder = new JPanel();
		inner.add(pnlHolder, BorderLayout.CENTER);
		pnlHolder.setLayout(new BorderLayout());

		pnlMain.setLayout(new MigLayout("", "[][][grow]", "[][][grow][grow]"));

		pnlMain.setLayout(new MigLayout("", "[128px:n][][grow]", "[][][grow][grow][][][][]"));

		JLabel lblNewLabel = new JLabel("You can either select an existing entry, or create another one. Simply navigate the tree to begin.");
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setFont(new Font("Consolas", Font.PLAIN, 11));
		pnlMain.add(lblNewLabel, "cell 0 0 3 1,alignx center,aligny center");

		Component verticalStrut_1 = Box.createVerticalStrut(6);
		pnlMain.add(verticalStrut_1, "cell 2 1");

		Component horizontalStrut = Box.createHorizontalStrut(6);
		pnlMain.add(horizontalStrut, "cell 1 2");

		JPanel pnlEditor = new JPanel();
		pnlEditor.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		pnlEditor.setBackground(Color.LIGHT_GRAY);
		pnlMain.add(pnlEditor, "cell 2 2 1 2,grow");
		pnlEditor.setLayout(new BorderLayout(0, 0));

		pnlQuizEditor = new JPanel();
		pnlQuizEditor.setBackground(Color.LIGHT_GRAY);
		pnlEditor.add(pnlQuizEditor);
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

		pnlChange = new JPanel();
		pnlChange.setBackground(Color.LIGHT_GRAY);

		radioOne = new JRadioButton("Unused Answer 1");
		radioOne.setSelected(true);
		radioOne.setFont(new Font("Consolas", Font.PLAIN, 11));
		radioOne.setBackground(Color.LIGHT_GRAY);
		pnlQuizEditor.add(radioOne, "flowx,cell 2 5");

		radioTwo = new JRadioButton("Unused Answer 2");
		radioTwo.setFont(new Font("Consolas", Font.PLAIN, 11));
		radioTwo.setBackground(Color.LIGHT_GRAY);
		pnlQuizEditor.add(radioTwo, "flowx,cell 2 6");

		radioThree = new JRadioButton("Unused Answer 3");
		radioThree.setFont(new Font("Consolas", Font.PLAIN, 11));
		radioThree.setBackground(Color.LIGHT_GRAY);

		radioFour = new JRadioButton("Unused Answer 4");
		radioFour.setFont(new Font("Consolas", Font.PLAIN, 11));
		radioFour.setBackground(Color.LIGHT_GRAY);
		pnlQuizEditor.add(radioFour, "flowx,cell 2 8");

		radioFive = new JRadioButton("Unused Answer 5");
		radioFive.setFont(new Font("Consolas", Font.PLAIN, 11));
		radioFive.setBackground(Color.LIGHT_GRAY);
		pnlQuizEditor.add(radioFive, "flowx,cell 2 9");

		radioOne.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (!radioOne.isSelected()) {
					pnlQuizEditor.remove(pnlChange);
					pnlQuizEditor.add(pnlChange, "cell 2 5,alignx left,growy");

					radioTwo.setSelected(false);
					radioThree.setSelected(false);
					radioFour.setSelected(false);
					radioFive.setSelected(false);

					CBuilder.this.revalidate();

					editNode = 0;
				}else {
					radioTwo.setSelected(true);
				}
			}
		});

		radioTwo.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (!radioTwo.isSelected()) {
					pnlQuizEditor.remove(pnlChange);
					pnlQuizEditor.add(pnlChange, "cell 2 6,alignx left,growy");

					radioOne.setSelected(false);
					radioThree.setSelected(false);
					radioFour.setSelected(false);
					radioFive.setSelected(false);

					CBuilder.this.revalidate();

					editNode = 1;
				}else {
					radioThree.setSelected(true);
				}
			}
		});

		radioThree.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (!radioThree.isSelected()) {
					pnlQuizEditor.remove(pnlChange);
					pnlQuizEditor.add(pnlChange, "cell 2 7,alignx left,growy");

					radioTwo.setSelected(false);
					radioOne.setSelected(false);
					radioFour.setSelected(false);
					radioFive.setSelected(false);

					CBuilder.this.revalidate();

					editNode = 2;
				}else {
					radioFour.setSelected(true);
				}
			}
		});

		radioFour.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (!radioFour.isSelected()) {
					pnlQuizEditor.remove(pnlChange);
					pnlQuizEditor.add(pnlChange, "cell 2 8,alignx left,growy");

					radioTwo.setSelected(false);
					radioThree.setSelected(false);
					radioOne.setSelected(false);
					radioFive.setSelected(false);

					CBuilder.this.revalidate();

					editNode = 3;
				}else {
					radioFive.setSelected(true);
				}
			}
		});

		radioFive.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (!radioFive.isSelected()) {
					pnlQuizEditor.remove(pnlChange);
					pnlQuizEditor.add(pnlChange, "cell 2 9,alignx left,growy");

					radioTwo.setSelected(false);
					radioThree.setSelected(false);
					radioFour.setSelected(false);
					radioOne.setSelected(false);

					CBuilder.this.revalidate();

					editNode = 4;
				}else {
					radioOne.setSelected(true);
				}
			}
		});

		Component verticalStrut = Box.createVerticalStrut(36);
		pnlQuizEditor.add(verticalStrut, "cell 2 5");

		pnlQuizEditor.add(pnlChange, "cell 2 5,alignx left,growy");

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

		pnlQuizEditor.add(radioThree, "flowx,cell 2 7");

		Component horizontalStrut_1 = Box.createHorizontalStrut(32);
		pnlChange.add(horizontalStrut_1);

		JButton btnEdit = new JButton("Edit");
		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (editNode == 0) {
					//choices[editNode] = JOptionPane.showInputDialog(CBuilder.this, "Please enter the desired response for this choice.", "Create/Modify", choices[editNode]);
				}else if (editNode == 1) {

				}else if (editNode == 2) {

				}else if (editNode == 3) {

				}else if (editNode == 4) {

				}
			}
		});
		btnEdit.setHorizontalAlignment(SwingConstants.LEFT);
		btnEdit.setFont(new Font("Consolas", Font.PLAIN, 11));
		btnEdit.setBackground(Color.DARK_GRAY);
		btnEdit.setForeground(Color.WHITE);
		pnlChange.add(btnEdit);

		JButton btnClear = new JButton("Clear");
		btnClear.setHorizontalAlignment(SwingConstants.LEFT);
		btnClear.setForeground(Color.WHITE);
		btnClear.setBackground(Color.DARK_GRAY);
		btnClear.setFont(new Font("Consolas", Font.PLAIN, 11));
		pnlChange.add(btnClear);

		Component verticalStrut_2 = Box.createVerticalStrut(36);
		pnlQuizEditor.add(verticalStrut_2, "cell 2 6");

		Component verticalStrut_3 = Box.createVerticalStrut(36);
		pnlQuizEditor.add(verticalStrut_3, "cell 2 7");

		Component verticalStrut_4 = Box.createVerticalStrut(36);
		pnlQuizEditor.add(verticalStrut_4, "cell 2 8");

		Component verticalStrut_5 = Box.createVerticalStrut(36);
		pnlQuizEditor.add(verticalStrut_5, "cell 2 9");

		JTree tree = new JTree();
		tree.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true), "Curriculum Path", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		tree.setFont(new Font("Consolas", Font.PLAIN, 11));
		tree.setForeground(Color.WHITE);
		tree.setBackground(Color.GRAY);
		pnlMain.add(tree, "cell 0 2 1 3,grow");

		tree.setCellRenderer(new CustomCellRenderer());

		pnlHolder.add(pnlMain, BorderLayout.CENTER);

		Component verticalStrut_7 = Box.createVerticalStrut(4);
		outer.add(verticalStrut_7, BorderLayout.NORTH);

		Component verticalStrut_6 = Box.createVerticalStrut(4);
		outer.add(verticalStrut_6, BorderLayout.SOUTH);

		Component horizontalStrut_2 = Box.createHorizontalStrut(4);
		outer.add(horizontalStrut_2, BorderLayout.WEST);

		Component horizontalStrut_3 = Box.createHorizontalStrut(4);
		outer.add(horizontalStrut_3, BorderLayout.EAST);
	}
	private static final long serialVersionUID = -611911711040854050L;

	public class CustomCellRenderer extends DefaultTreeCellRenderer{
		private static final long serialVersionUID = -2748612083655962073L;

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean isSelected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {

			JComponent c = (JComponent) super.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, hasFocus);
			c.setBackground(Color.gray); 
			c.setOpaque(true);
			c.setForeground(Color.black);
			return c; 
		}
	}

}
