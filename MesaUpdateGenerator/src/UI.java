import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.border.BevelBorder;

import net.miginfocom.swing.MigLayout;

import java.awt.Color;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.GroupLayout;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JSplitPane;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JProgressBar;

import java.io.*;

import javax.swing.ListSelectionModel;

public class UI extends JFrame{
	private static final long serialVersionUID = -6340304491773037483L;

	DefaultListModel<String> stepOne = new DefaultListModel<String>();
	DefaultListModel<String> stepTwoA = new DefaultListModel<String>();
	DefaultListModel<String> stepTwoB = new DefaultListModel<String>();
	
	JList<String> fileList;
	JList<String> fileList2;
	JList<String> protocolList2;
	
	public static void main(String[] args) {
		new UI();
	}

	public UI() {
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
		setResizable(false);
		setTitle("Mesa Update Creator");
		this.setSize(640, 480);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.BLACK);
		panel.setBorder(null);
		getContentPane().add(panel, BorderLayout.NORTH);
		
		JLabel lblInOrderTo = new JLabel("In order to create an update for this system, use the wizard below.");
		lblInOrderTo.setForeground(Color.GREEN);
		panel.add(lblInOrderTo);
		
		JPanel panel_1 = new JPanel();
		panel_1.setForeground(Color.GREEN);
		panel_1.setBackground(Color.DARK_GRAY);
		panel_1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new MigLayout("", "[460px:n:460px,grow][64px:n:64px,grow][]", "[][140px:n:140px,grow][][][grow][][]"));
		
		JLabel lblStepLoad = new JLabel("Step 1: Load files into the program.");
		lblStepLoad.setForeground(Color.GREEN);
		panel_1.add(lblStepLoad, "cell 0 0");
		
		fileList = new JList<String>();
		fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fileList.setBackground(Color.GRAY);
		fileList.setForeground(Color.RED);
		panel_1.add(fileList, "cell 0 1,grow");
		
		JPanel panel_2 = new JPanel();
		panel_2.setBackground(Color.DARK_GRAY);
		panel_1.add(panel_2, "cell 1 1,grow");
		
		JButton btnAdd = new JButton("Add File(s)...");
		btnAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setMultiSelectionEnabled(true);
				chooser.showOpenDialog(UI.this);
				File[] files = chooser.getSelectedFiles();
				for (File f : files) {
					stepOne.addElement(f.getPath());
				}
			}
		});
		
		JButton btnRemove = new JButton("Remove File");
		btnRemove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stepOne.remove(UI.this.fileList.getSelectedIndex());
			}
		});
		
		JButton btnSend = new JButton("Send to Step 2");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stepTwoA.addElement(stepOne.getElementAt(UI.this.fileList.getSelectedIndex()));
				stepOne.remove(UI.this.fileList.getSelectedIndex());
				stepTwoB.addElement("Create");
			}
		});
		
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addComponent(btnRemove, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
						.addComponent(btnAdd, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
						.addComponent(btnSend))
					.addContainerGap())
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnAdd)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnRemove)
					.addPreferredGap(ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
					.addComponent(btnSend)
					.addContainerGap())
		);
		panel_2.setLayout(gl_panel_2);
		
		JLabel lblStepDesignate = new JLabel("Step 2: Designate update protocol for each file.");
		lblStepDesignate.setForeground(Color.GREEN);
		panel_1.add(lblStepDesignate, "cell 0 3");
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setBackground(Color.LIGHT_GRAY);
		panel_1.add(splitPane, "cell 0 4,grow");
		
		protocolList2 = new JList<String>();
		protocolList2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		protocolList2.setForeground(Color.ORANGE);
		protocolList2.setBackground(Color.GRAY);
		splitPane.setRightComponent(protocolList2);
		
		JPanel panel_3 = new JPanel();
		splitPane.setLeftComponent(panel_3);
		panel_3.setLayout(new BorderLayout(0, 0));
		
		Component horizontalStrut = Box.createHorizontalStrut(128);
		panel_3.add(horizontalStrut, BorderLayout.SOUTH);
		
		fileList2 = new JList<String>();
		fileList2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fileList2.setForeground(Color.RED);
		fileList2.setBackground(Color.GRAY);
		panel_3.add(fileList2, BorderLayout.CENTER);
		
		JPanel panel_4 = new JPanel();
		panel_4.setBackground(Color.DARK_GRAY);
		panel_1.add(panel_4, "cell 1 4,grow");
		
		JButton btnUpdate = new JButton("Update");
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stepTwoB.set(fileList2.getSelectedIndex(), "Update");
			}
		});
		
		JLabel lblProtocolList = new JLabel("Protocol List");
		lblProtocolList.setForeground(Color.GREEN);
		
		JButton btnCreate = new JButton("Create");
		btnCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stepTwoB.set(fileList2.getSelectedIndex(), "Create");
			}
		});
		
		JButton btnDelete = new JButton("Add Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String file = JOptionPane.showInputDialog("Enter the full path of the file to delete: ");
				stepTwoA.addElement(file);
				stepTwoB.addElement("Delete");
			}
		});
		
		JButton btnAddCheck = new JButton("Add Check");
		btnAddCheck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String file = JOptionPane.showInputDialog("Enter the full path of the file to verify: ");
				stepTwoA.addElement(file);
				stepTwoB.addElement("Verify");
			}
		});
		GroupLayout gl_panel_4 = new GroupLayout(panel_4);
		gl_panel_4.setHorizontalGroup(
			gl_panel_4.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_4.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_4.createParallelGroup(Alignment.LEADING)
						.addComponent(btnUpdate, GroupLayout.PREFERRED_SIZE, 135, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblProtocolList)
						.addComponent(btnCreate, GroupLayout.PREFERRED_SIZE, 135, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnDelete, GroupLayout.PREFERRED_SIZE, 135, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnAddCheck, GroupLayout.PREFERRED_SIZE, 135, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_panel_4.setVerticalGroup(
			gl_panel_4.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_4.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblProtocolList)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnUpdate)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnCreate)
					.addPreferredGap(ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
					.addComponent(btnAddCheck)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnDelete)
					.addContainerGap())
		);
		panel_4.setLayout(gl_panel_4);
		
		JLabel lblProgress = new JLabel("Progress:");
		lblProgress.setForeground(Color.GREEN);
		lblProgress.setBackground(Color.GREEN);
		panel_1.add(lblProgress, "flowx,cell 0 6");
		
		JProgressBar progressBar = new JProgressBar();
		panel_1.add(progressBar, "cell 0 6,growx");
		
		JButton btnAbort = new JButton("Abort");
		btnAbort.setEnabled(false);
		panel_1.add(btnAbort, "cell 1 6,growx");
		
		JButton btnFinish = new JButton("Start");
		panel_1.add(btnFinish, "cell 2 6,growx");
		
		fileList.setModel(stepOne);
		fileList2.setModel(stepTwoA);
		protocolList2.setModel(stepTwoB);
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
	}
}
