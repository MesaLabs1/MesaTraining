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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.ListSelectionModel;
import javax.swing.BoxLayout;
import javax.swing.AbstractListModel;
import java.awt.FlowLayout;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

public class UI extends JFrame{
	private static final long serialVersionUID = -6340304491773037483L;

	DefaultListModel<String> stepOne = new DefaultListModel<String>();
	DefaultListModel<String> stepTwoA = new DefaultListModel<String>();
	DefaultListModel<String> stepTwoB = new DefaultListModel<String>();

	JList<String> fileList;
	JList<String> fileList2;
	JList<String> protocolList2;

	static String LOG_NAME = "update.log";
	BufferedWriter logWriter;

	FileOutputStream fos = null;
	ZipOutputStream zos = null;

	JProgressBar progressBar;

	Sidebar sidebar = null;
	boolean allowSidebarVisibility = true;

	public static void main(String[] args) {
		new UI();
	}

	public UI() {
		getContentPane().setBackground(Color.DARK_GRAY);
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

		sidebar = new Sidebar(this);

		Log("Bootstrapped Update Generator initializing...");

		try {
			fos = new FileOutputStream("update.zip");
			zos = new ZipOutputStream(fos);
			Log("Preparing 'update.zip' for target environment...");
		} catch (FileNotFoundException e1) {
			Log("Unable to create an 'update.zip' file to write to. Exiting with error code -1.");
			e1.printStackTrace();
			System.exit(-1);
		}

		setBackground(Color.BLACK);
		setResizable(false);
		setTitle("Mesa Update Creator");
		this.setSize(626, 509);

		JPanel panel = new JPanel();
		panel.setBackground(Color.BLACK);
		panel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblInOrderTo = new JLabel("In order to create an update for this system, use the wizard below.");
		lblInOrderTo.setForeground(Color.GREEN);
		panel.add(lblInOrderTo);

		JPanel panel_1 = new JPanel();
		panel_1.setForeground(Color.GREEN);
		panel_1.setBackground(Color.DARK_GRAY);
		panel_1.setBorder(null);
		getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new MigLayout("", "[460px:n:460px,grow][64px:n:64px][grow]", "[][][140px:n:140px][][grow][]"));

		JButton btnHideSidebar = new JButton("Close Console");
		btnHideSidebar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sidebar.setVisible(!sidebar.isVisible());
				allowSidebarVisibility = !allowSidebarVisibility;
				if (sidebar.isVisible()) {
					btnHideSidebar.setText("Close Console");
				}else {
					btnHideSidebar.setText("Open Console");
				}
			}
		});
		btnHideSidebar.setForeground(Color.RED);
		btnHideSidebar.setBackground(Color.BLACK);
		panel_1.add(btnHideSidebar, "cell 1 0 2 1,alignx right");

		JLabel lblStepLoad = new JLabel("Step 1: Load files into the program.");
		lblStepLoad.setForeground(Color.GREEN);
		panel_1.add(lblStepLoad, "cell 0 1 3 1,alignx center");

		JPanel panel_2 = new JPanel();
		panel_2.setBackground(Color.DARK_GRAY);
		panel_1.add(panel_2, "cell 1 2 2 1,grow");

		JButton btnAdd = new JButton("Add File(s)...");
		btnAdd.setBackground(Color.GRAY);
		btnAdd.setForeground(Color.ORANGE);
		btnAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Log("Showing the File Chooser.");
				JFileChooser chooser = new JFileChooser();
				chooser.setMultiSelectionEnabled(true);
				chooser.showOpenDialog(UI.this);
				File[] files = chooser.getSelectedFiles();
				for (File f : files) {
					Log("Adding " + f.getName() + " to the package queue.");
					stepOne.addElement(f.getPath());
				}
			}
		});

		JButton btnRemove = new JButton("Remove File");
		btnRemove.setBackground(Color.GRAY);
		btnRemove.setForeground(Color.ORANGE);
		btnRemove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Log("Removing " + stepOne.getElementAt(UI.this.fileList.getSelectedIndex()) + " from the package queue.");
				stepOne.remove(UI.this.fileList.getSelectedIndex());
			}
		});

		JButton btnSend = new JButton("Send to Step 2");
		btnSend.setBackground(Color.GRAY);
		btnSend.setForeground(Color.ORANGE);
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
				String element = stepOne.getElementAt(UI.this.fileList.getSelectedIndex());
					try {
						addToZipFile(element, zos);

						stepTwoA.addElement(element);
						stepOne.remove(UI.this.fileList.getSelectedIndex());
						stepTwoB.addElement("Create");	
					}catch (Exception ex) {
						Log("Unable to package this file. Skipping.");
						ex.printStackTrace();
					}
				}catch (Exception ex2) {
					int rand = new Random().nextInt(5);
					if (rand == 0) {
						Log("There's nothing there, silly!");
					}else if (rand == 1) {
						Log("Oh you sure got me! Try picking something next time.");
					}else if (rand == 2) {
						Log("Pick something! Oh man, I have too much free time on my hands.");
					}else if (rand == 3) {
						Log("HA! YOU THOUGHT. Pick something next time.");
					}else if (rand == 4) {
						Log("Not all heroes wear capes. Give Jad a high five for good error handling next time you see him.");
					}
				}
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
								.addComponent(btnSend, GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE))
						.addContainerGap())
				);
		gl_panel_2.setVerticalGroup(
				gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
						.addContainerGap()
						.addComponent(btnAdd)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnRemove)
						.addPreferredGap(ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
						.addComponent(btnSend)
						.addContainerGap())
				);
		panel_2.setLayout(gl_panel_2);

		JLabel lblStepDesignate = new JLabel("Step 2: Designate update protocol for each file.");
		lblStepDesignate.setForeground(Color.GREEN);
		panel_1.add(lblStepDesignate, "cell 0 3 3 1,alignx center");

		JSplitPane splitPane = new JSplitPane();
		panel_1.add(splitPane, "cell 0 4,grow");
		splitPane.setBackground(Color.LIGHT_GRAY);

		JPanel panel_3 = new JPanel();
		splitPane.setLeftComponent(panel_3);
		panel_3.setLayout(new BorderLayout(0, 0));

		Component horizontalStrut = Box.createHorizontalStrut(324);
		panel_3.add(horizontalStrut, BorderLayout.SOUTH);

		JScrollPane scrollPane_1 = new JScrollPane();
		panel_3.add(scrollPane_1, BorderLayout.CENTER);

		fileList2 = new JList<String>();
		scrollPane_1.setViewportView(fileList2);
		fileList2.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				protocolList2.setSelectedIndex(fileList2.getSelectedIndex());
			}
		});
		fileList2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fileList2.setForeground(Color.RED);
		fileList2.setBackground(Color.GRAY);

		fileList2.setModel(stepTwoA);

		JScrollPane scrollPane_2 = new JScrollPane();
		splitPane.setRightComponent(scrollPane_2);

		protocolList2 = new JList<String>();
		scrollPane_2.setViewportView(protocolList2);
		protocolList2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		protocolList2.setForeground(Color.ORANGE);
		protocolList2.setBackground(Color.GRAY);
		protocolList2.setModel(stepTwoB);

		JPanel panel_4 = new JPanel();
		panel_4.setBackground(Color.DARK_GRAY);
		panel_1.add(panel_4, "cell 1 4 2 1,grow");

		JButton btnUpdate = new JButton("Update");
		btnUpdate.setBackground(Color.GRAY);
		btnUpdate.setForeground(Color.ORANGE);
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String value = stepTwoB.getElementAt(fileList2.getSelectedIndex());
				if (!value.equals("Delete") && !value.equals("Check")) {
					stepTwoB.set(fileList2.getSelectedIndex(), "Update");
					Log("Setting '" + stepTwoA.getElementAt(UI.this.fileList2.getSelectedIndex()) + "' to an Update file.");
				}
			}
		});

		JButton btnCreate = new JButton("Create");
		btnCreate.setBackground(Color.GRAY);
		btnCreate.setForeground(Color.ORANGE);
		btnCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String value = stepTwoB.getElementAt(fileList2.getSelectedIndex());
				if (!value.equals("Delete") && !value.equals("Check")) {
					stepTwoB.set(fileList2.getSelectedIndex(), "Create");
					Log("Setting '" + stepTwoA.getElementAt(UI.this.fileList2.getSelectedIndex()) + "' to be Created.");
				}
			}
		});

		JButton btnDelete = new JButton("Add Delete");
		btnDelete.setBackground(Color.GRAY);
		btnDelete.setForeground(Color.ORANGE);
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String file = JOptionPane.showInputDialog("Enter the full path of the file to delete: ");
				stepTwoA.addElement(file);
				stepTwoB.addElement("Delete");

				Log("Setting a flag to delete '" + file + "' from the server.");
			}
		});

		JButton btnAddCheck = new JButton("Add Check");
		btnAddCheck.setBackground(Color.GRAY);
		btnAddCheck.setForeground(Color.ORANGE);
		btnAddCheck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String file = JOptionPane.showInputDialog("Enter the full path of the file to verify: ");
				stepTwoA.addElement(file);
				stepTwoB.addElement("Verify");

				Log("Setting '" + file + "' as a required element of the next update..");
			}
		});

		JButton btnNewButton = new JButton("Delete Entry");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int n = JOptionPane.showConfirmDialog(
						UI.this,
						"Deleting a Stage Two item will result it in still being in the update package, but with no instructions.\n"
								+ " This will increase the overall size of the package, but the item will have no effect on the target installation.\n Do you want"
								+ " to continue?",
								"Package Warning!",
								JOptionPane.YES_NO_OPTION);
				if (n == JOptionPane.YES_OPTION) {
					stepTwoA.remove(fileList2.getSelectedIndex());
					stepTwoB.remove(fileList2.getSelectedIndex());
				}
			}
		});
		btnNewButton.setBackground(Color.GRAY);
		btnNewButton.setForeground(Color.RED);
		GroupLayout gl_panel_4 = new GroupLayout(panel_4);
		gl_panel_4.setHorizontalGroup(
				gl_panel_4.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_4.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_panel_4.createParallelGroup(Alignment.LEADING)
								.addComponent(btnUpdate, GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
								.addComponent(btnCreate, GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
								.addComponent(btnDelete, GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
								.addComponent(btnAddCheck, GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
								.addComponent(btnNewButton, GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE))
						.addContainerGap())
				);
		gl_panel_4.setVerticalGroup(
				gl_panel_4.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_panel_4.createSequentialGroup()
						.addContainerGap()
						.addComponent(btnUpdate)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnCreate)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(btnAddCheck)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnDelete)
						.addPreferredGap(ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
						.addComponent(btnNewButton)
						.addContainerGap())
				);
		panel_4.setLayout(gl_panel_4);

		JLabel lblProgress = new JLabel("Progress:");
		lblProgress.setForeground(Color.GREEN);
		lblProgress.setBackground(Color.GREEN);
		panel_1.add(lblProgress, "flowx,cell 0 5");

		progressBar = new JProgressBar();
		panel_1.add(progressBar, "cell 0 5,growx");

		JButton btnAbort = new JButton("Abort");
		btnAbort.setEnabled(false);
		panel_1.add(btnAbort, "cell 1 5,growx");

		JButton btnFinish = new JButton("Start");
		btnFinish.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					Log("Finalizing update package with manifest...");
					progressBar.setValue(20);


					fos.close();
					zos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		panel_1.add(btnFinish, "cell 2 5,growx");

		JPanel panel_6 = new JPanel();
		panel_1.add(panel_6, "flowx,cell 0 2,grow");
		panel_6.setLayout(new BorderLayout(0, 0));

		Component horizontalStrut_1 = Box.createHorizontalStrut(250);
		panel_6.add(horizontalStrut_1, BorderLayout.SOUTH);

		JScrollPane scrollPane = new JScrollPane();
		panel_6.add(scrollPane, BorderLayout.CENTER);

		fileList = new JList<String>();
		scrollPane.setViewportView(fileList);
		fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fileList.setBackground(Color.GRAY);
		fileList.setForeground(Color.RED);

		fileList.setModel(stepOne);

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	public void addToZipFile(String fileName, ZipOutputStream zos) throws FileNotFoundException, IOException {
		Log("Writing '" + fileName + "' to zip file");

		File file = new File(fileName);
		FileInputStream fis = new FileInputStream(file);
		ZipEntry zipEntry = new ZipEntry(fileName);
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}

		zos.closeEntry();
		fis.close();
	}

	private static final int CLIENT_CODE_STACK_INDEX;
	static {
		int i = 0;
		for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
			i++;
			if (ste.getClassName().equals(UI.class.getName())) {
				break;
			}
		}
		CLIENT_CODE_STACK_INDEX = i;
	}

	//This variable belongs to the Log method, and is used to convert a System Time to a String.
	private static SimpleDateFormat timeFormatter= new SimpleDateFormat("hh:mm:ss a");


	void Log(String message) {
		try {
			logWriter = new BufferedWriter(new FileWriter(LOG_NAME, true));
		} catch (IOException e) {
			e.printStackTrace();
		}

		Date date = new Date();
		String sender = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getMethodName();
		String time = timeFormatter.format(date);

		String log = "[" + sender + "@" + time +"]: " + message;

		System.out.println(log);

		sidebar.console.addElement(log);

		sidebar.list.setSelectedIndex(sidebar.console.size() - 1);

		try {
			logWriter.write(log);
			logWriter.newLine();
			logWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
