import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import java.awt.Color;

import javax.swing.JButton;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.JList;
import javax.swing.Timer;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import net.miginfocom.swing.MigLayout;


public class NewEntry extends JDialog{
	private static final long serialVersionUID = -1539819973323570130L;
	private JTextField txtDay;
	private JTextField txtMonth;
	private JTextField txtYear;
	private JTextField txtHour;
	private JTextField txtMinute;
	private JTextField txtAircraft;
	private JTextField txtPilot;

	private JTextArea txtNotes;

	private JPanel pnlCollab;

	private JList<String> listCollab;
	private DefaultListModel<String> collabModel;

	private WizardHelper helper;
	private Timer timer;

	JPanel pnl3;
	JPanel pnl2;
	JPanel pnl1;
	JPanel panel;

	JButton btnNext;
	JButton btnPrevious;
	JButton btnFinish;

	JRadioButton radioFlight;
	JRadioButton radioRepair;
	JRadioButton radioTraining;

	JRadioButton radioAlone;
	JRadioButton radioCollab;

	JRadioButton radioAM;
	JRadioButton radioPM;

	JLabel lblProgress;
	JLabel lblHeader;

	Client network;

	enum ReportType {
		FullReport, RepairOnly, TrainingOnly, FlightOnly
	}

	public NewEntry(Client c, ReportType r) {
		network = c;
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

		helper = new WizardHelper();
		timer = new Timer(100, helper);

		collabModel = new DefaultListModel<String>();

		getContentPane().setLocation(284, 0);
		getContentPane().setBackground(Color.LIGHT_GRAY);
		setType(Type.UTILITY);
		setTitle("WebConnect - Create a New Database Entry");
		this.setSize(new Dimension(715, 300));
		getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel pnlFooter = new JPanel();
		pnlFooter.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		getContentPane().add(pnlFooter, BorderLayout.SOUTH);

		btnPrevious = new JButton("Previous");
		btnPrevious.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (NewEntry.this.btnPrevious.isEnabled()) {
					helper.Previous();
				}
			}
		});

		lblProgress = new JLabel("Step X of X");
		lblProgress.setFont(new Font("Tahoma", Font.BOLD, 14));

		btnNext = new JButton("Next");
		btnNext.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				if (NewEntry.this.btnNext.isEnabled()) {
					helper.Next();
				}
			}
		});

		btnFinish = new JButton("Finish");
		btnFinish.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (NewEntry.this.btnFinish.isEnabled()) {
					TransmitData();
					NewEntry.this.dispose();
					NewEntry.this.setEnabled(false);
				}
			}
		});
		btnFinish.setEnabled(false);

		GroupLayout gl_pnlFooter = new GroupLayout(pnlFooter);
		gl_pnlFooter.setHorizontalGroup(
				gl_pnlFooter.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_pnlFooter.createSequentialGroup()
						.addComponent(btnPrevious)
						.addGap(212)
						.addComponent(lblProgress)
						.addPreferredGap(ComponentPlacement.RELATED, 212, Short.MAX_VALUE)
						.addComponent(btnNext)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnFinish))
				);
		gl_pnlFooter.setVerticalGroup(
				gl_pnlFooter.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlFooter.createSequentialGroup()
						.addGap(5)
						.addGroup(gl_pnlFooter.createParallelGroup(Alignment.BASELINE, false)
								.addComponent(btnFinish)
								.addComponent(btnNext)
								.addComponent(btnPrevious)
								.addComponent(lblProgress)))
				);
		pnlFooter.setLayout(gl_pnlFooter);

		JPanel pnlHeader = new JPanel();
		pnlHeader.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		getContentPane().add(pnlHeader, BorderLayout.NORTH);

		lblHeader = new JLabel("Create a database entry in a few easy steps. Use this Entry Wizard to speed up the process!");
		pnlHeader.add(lblHeader);

		panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);
		getContentPane().add(panel);
		panel.setLayout(new MigLayout("", "[grow]", "[179px]"));

		pnl1 = new JPanel();
		//panel.add(pnl1, "flowx,cell 0 0,grow");
		pnl1.setBackground(Color.LIGHT_GRAY);
		pnl1.setLayout(new MigLayout("", "[][32px:n:32px][2px:n][32px:n:32px][2px:n][48px:n][][25px][32px:n:32px][][::32px][][][][2px][3px][5px][39px][3px][94px][8px][1px][9px][25px][43px][92px]", "[26px][14px][23px][][14px][][]"));

		JLabel lblDateAndTime = new JLabel("Date and Time");
		lblDateAndTime.setFont(new Font("Tahoma", Font.BOLD, 16));
		pnl1.add(lblDateAndTime, "cell 0 0,grow");

		JLabel lblPleaseSetThe = new JLabel("Please enter the date and time that this flight took place.");
		pnl1.add(lblPleaseSetThe, "cell 0 1 20 1,growx,aligny top");

		radioAM = new JRadioButton("AM");
		radioAM.setSelected(true);
		radioAM.setBackground(Color.LIGHT_GRAY);
		pnl1.add(radioAM, "cell 12 2,alignx left,aligny top");
		radioAM.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (NewEntry.this.radioAM.isSelected()) {
					NewEntry.this.radioPM.setSelected(false);
				}
			}
		});

		JLabel lblThisFlightOccurred = new JLabel("This flight occurred on");
		pnl1.add(lblThisFlightOccurred, "flowx,cell 0 3,alignx right,aligny center");

		txtDay = new JTextField();
		pnl1.add(txtDay, "cell 1 3,growx,aligny bottom");
		txtDay.setColumns(10);

		JLabel label = new JLabel("/");
		pnl1.add(label, "cell 2 3,growx,aligny top");

		txtMonth = new JTextField();
		txtMonth.setColumns(10);
		pnl1.add(txtMonth, "cell 3 3,growx,aligny bottom");

		JLabel label_2 = new JLabel("/");
		pnl1.add(label_2, "cell 4 3,alignx left,aligny top");

		txtYear = new JTextField();
		txtYear.setColumns(10);
		pnl1.add(txtYear, "cell 5 3,growx,aligny bottom");

		JLabel lblNewLabel = new JLabel("and started at about ");
		pnl1.add(lblNewLabel, "cell 6 3,alignx center,aligny center");

		txtHour = new JTextField();
		txtHour.setColumns(10);
		pnl1.add(txtHour, "cell 8 3,growx,aligny center");

		JLabel label_3 = new JLabel(":");
		pnl1.add(label_3, "cell 9 3,alignx left,aligny top");

		txtMinute = new JTextField();
		txtMinute.setColumns(10);
		pnl1.add(txtMinute, "cell 10 3,growx,aligny bottom");

		JLabel lblDay = new JLabel("day");
		pnl1.add(lblDay, "cell 1 4,alignx center,aligny top");

		JLabel lblMonth = new JLabel("month");
		pnl1.add(lblMonth, "cell 3 4,alignx center,aligny top");

		JLabel lblYear = new JLabel("year");
		pnl1.add(lblYear, "cell 5 4,alignx center,aligny top");

		JLabel lblHour = new JLabel("hour");
		pnl1.add(lblHour, "cell 8 4,alignx center,aligny top");

		JLabel lblMinute = new JLabel("minute");
		pnl1.add(lblMinute, "cell 10 4,alignx center,aligny top");

		radioPM = new JRadioButton("PM");
		radioPM.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (NewEntry.this.radioPM.isSelected()) {
					NewEntry.this.radioAM.setSelected(false);
				}
			}
		});
		radioPM.setBackground(Color.LIGHT_GRAY);
		pnl1.add(radioPM, "cell 12 4,alignx left,aligny top");

		JLabel lblWasCompletedUsing = new JLabel("This flight used the ");
		pnl1.add(lblWasCompletedUsing, "cell 0 5,alignx left,aligny center");

		txtAircraft = new JTextField();
		pnl1.add(txtAircraft, "cell 1 5 5 1,growx,aligny center");
		txtAircraft.setColumns(10);

		JLabel lblAircraft = new JLabel("aircraft.");
		pnl1.add(lblAircraft, "cell 6 5,alignx left,aligny center");

		JLabel lblAircraftName = new JLabel("aircraft name");
		pnl1.add(lblAircraftName, "cell 1 6 5 1,alignx center,aligny top");

		pnl2 = new JPanel();
		panel.add(pnl2, "cell 0 0,grow");
		pnl2.setBackground(Color.LIGHT_GRAY);
		pnl2.setLayout(new MigLayout("", "[8px:n][][][100px:n][][][][grow]", "[20px][3px][17px][19px][84px]"));

		JLabel lblPilots = new JLabel("Pilots");
		lblPilots.setFont(new Font("Tahoma", Font.BOLD, 16));
		pnl2.add(lblPilots, "cell 0 0 2 1,alignx left,aligny top");

		JLabel lblThisFlightWas = new JLabel("This flight was completed by ");
		pnl2.add(lblThisFlightWas, "cell 1 2,alignx right,aligny top");

		txtPilot = new JTextField();
		pnl2.add(txtPilot, "cell 2 1 2 2,growx,aligny top");
		txtPilot.setColumns(10);

		JLabel lblYourFirst = new JLabel("UCMNetID");
		pnl2.add(lblYourFirst, "cell 2 3 2 1,alignx center,aligny top");

		JLabel lblAndWasDone = new JLabel("and was done so");
		pnl2.add(lblAndWasDone, "cell 4 2 2 1,alignx left,aligny top");

		radioAlone = new JRadioButton("alone.");
		radioAlone.setSelected(true);
		radioAlone.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (NewEntry.this.radioAlone.isSelected()) {
					NewEntry.this.radioCollab.setSelected(false);
					pnlCollab.setVisible(false);
				}
			}
		});
		radioAlone.setBackground(Color.LIGHT_GRAY);
		pnl2.add(radioAlone, "cell 6 0 1 2,alignx left,aligny bottom");

		radioCollab = new JRadioButton("in collaboration with");
		radioCollab.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (NewEntry.this.radioCollab.isSelected()) {
					NewEntry.this.radioAlone.setSelected(false);
					pnlCollab.setVisible(true);
				}
			}
		});
		radioCollab.setBackground(Color.LIGHT_GRAY);
		pnl2.add(radioCollab, "cell 6 2 1 2,alignx left,aligny bottom");

		pnlCollab = new JPanel();
		pnlCollab.setBackground(Color.LIGHT_GRAY);
		pnl2.add(pnlCollab, "cell 1 4 4 1,grow");
		pnlCollab.setLayout(new MigLayout("", "[4px:n][72px:n:72px][8px:n:8px][grow]", "[15px][2px][23px][11px][23px]"));

		JLabel lblTheFollowingPilots = new JLabel("these pilots:");
		pnlCollab.add(lblTheFollowingPilots, "cell 0 0 2 1,alignx right,aligny center");

		listCollab = new JList<String>();
		listCollab.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		listCollab.setModel(collabModel);
		listCollab.setBackground(Color.GRAY);
		pnlCollab.add(listCollab, "cell 3 0 1 5,grow");

		JButton btnAdd = new JButton("Add");
		btnAdd.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				String resp = JOptionPane.showInputDialog("Enter the name of the collaborative pilot:");
				if (resp != null && resp.length() > 0) {
					collabModel.addElement(resp);
				}
			}
		});
		btnAdd.setForeground(Color.WHITE);
		btnAdd.setBackground(Color.DARK_GRAY);
		pnlCollab.add(btnAdd, "cell 0 4 2 1,growx,aligny top");

		JButton btnRemove = new JButton("Remove");
		btnRemove.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (listCollab.getSelectedValue() != null && listCollab.getSelectedValue().length() > 0) {
					collabModel.remove(listCollab.getSelectedIndex());
				}
			}
		});
		btnRemove.setForeground(Color.WHITE);
		btnRemove.setBackground(Color.DARK_GRAY);
		pnlCollab.add(btnRemove, "cell 0 2 2 1,growx,aligny top");

		JLabel lblNoteIfYoure = new JLabel("Note: Even if this is maintenance, fill out the form anyway.");
		lblNoteIfYoure.setForeground(Color.BLACK);
		pnl2.add(lblNoteIfYoure, "cell 5 4 3 1,alignx left,aligny bottom");

		pnlCollab.setVisible(false);

		pnl3 = new JPanel();
		//panel.add(pnl3, "cell 0 0,grow");
		pnl3.setBackground(Color.LIGHT_GRAY);
		pnl3.setLayout(new MigLayout("", "[4px:n:4px][][][46px][grow]", "[20px][14px][23px][23px][][56px]"));

		JLabel lblAddReportData = new JLabel("Add Report Data");
		lblAddReportData.setFont(new Font("Tahoma", Font.BOLD, 16));
		pnl3.add(lblAddReportData, "cell 0 0 4 1,alignx left,aligny top");

		JLabel lblStuff = new JLabel("You've provided all the information necessary to submit this report. Please fill out the form below with additional details.");
		pnl3.add(lblStuff, "cell 1 1 4 1,growx,aligny top");

		JLabel lblThisWasA = new JLabel("I completed a");
		pnl3.add(lblThisWasA, "cell 1 2,growx,aligny center");

		radioFlight = new JRadioButton("Flight.");
		radioFlight.setSelected(true);
		radioFlight.setBackground(Color.LIGHT_GRAY);
		pnl3.add(radioFlight, "cell 2 2,alignx left,aligny top");

		radioFlight.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if (NewEntry.this.radioFlight.isSelected()) {
					NewEntry.this.radioRepair.setSelected(false);
					NewEntry.this.radioTraining.setSelected(false);
				}
			}
		});

		radioTraining = new JRadioButton("Training.");
		radioTraining.setBackground(Color.LIGHT_GRAY);
		pnl3.add(radioTraining, "cell 2 3,alignx left,aligny top");

		radioTraining.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if (NewEntry.this.radioTraining.isSelected()) {
					NewEntry.this.radioRepair.setSelected(false);
					NewEntry.this.radioFlight.setSelected(false);
				}
			}
		});

		radioRepair = new JRadioButton("Repair.");
		radioRepair.setBackground(Color.LIGHT_GRAY);
		pnl3.add(radioRepair, "cell 2 4,alignx left,aligny top");

		radioRepair.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if (NewEntry.this.radioRepair.isSelected()) {
					NewEntry.this.radioFlight.setSelected(false);
					NewEntry.this.radioTraining.setSelected(false);
				}
			}
		});

		JLabel lblNotes = new JLabel("Notes:");
		pnl3.add(lblNotes, "cell 3 2,growx,aligny center");

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		pnl3.add(panel_2, "cell 4 2 1 4,grow");
		panel_2.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		panel_2.add(scrollPane, BorderLayout.CENTER);

		txtNotes = new JTextArea();
		txtNotes.setLineWrap(true);
		txtNotes.setText("Enter your notes here.");
		txtNotes.setForeground(Color.WHITE);
		scrollPane.setViewportView(txtNotes);
		txtNotes.setBackground(Color.GRAY);

		if (r.equals(ReportType.FlightOnly)) {
			radioFlight.setSelected(true);
			radioTraining.setSelected(false);
			radioRepair.setSelected(false);

			radioFlight.setEnabled(true);
			radioTraining.setEnabled(false);
			radioRepair.setEnabled(false);
		}else if (r.equals(ReportType.RepairOnly)) {
			radioFlight.setSelected(false);
			radioTraining.setSelected(false);
			radioRepair.setSelected(true);

			radioFlight.setEnabled(false);
			radioTraining.setEnabled(false);
			radioRepair.setEnabled(false);
		}else if (r.equals(ReportType.TrainingOnly)) {
			radioFlight.setSelected(false);
			radioTraining.setSelected(true);
			radioRepair.setSelected(false);

			radioFlight.setEnabled(false);
			radioTraining.setEnabled(false);
			radioRepair.setEnabled(false);
		}
		this.setVisible(true);
		this.setModal(true);
		timer.start();
	}

	public boolean TransmitData() {
		try {
			String hour = txtHour.getText();
			String minute = txtMinute.getText();

			String day = txtDay.getText();
			String month = txtMonth.getText();
			String year = txtYear.getText();

			String pilots = txtPilot.getText();

			String notes = txtNotes.getText();
			String type = "UNKNOWN";

			if (radioCollab.isSelected()) {
				for (int i = 0; i < collabModel.getSize(); i++) {
					pilots += ";" + collabModel.getElementAt(i);
				}
			}

			if (radioRepair.isSelected()) {
				type = "maintenance";
			}else if (radioTraining.isSelected()) {
				type = "training";
			}else if (radioFlight.isSelected()) {
				type = "flight";
			}

			String date =  month + day + year + ";" + hour + minute + "00";

			String req = "$CREATE ENTRY " + pilots + " " + date + " " + txtAircraft.getText() + " " + type + " " + notes;
			network.instance.RemoteRequest(req);
			return true;
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public class WizardHelper implements ActionListener {
		private int stage = 0;
		private int prevStage = -1;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (stage != prevStage) {
				if (stage == 0) {
					panel.add(pnl1, "flowx,cell 0 0,grow");
					panel.remove(pnl2);
					panel.remove(pnl3);
					btnPrevious.setEnabled(false);
					btnFinish.setEnabled(false);
					btnNext.setEnabled(true);
					lblProgress.setText("Step 1 of 3");
					lblHeader.setText("Create a database entry in a few easy steps. Use this Entry Wizard to speed up the process!");
				}else if (stage == 1) {
					panel.add(pnl2, "flowx,cell 0 0,grow");
					panel.remove(pnl1);
					panel.remove(pnl3);
					btnFinish.setEnabled(false);
					btnPrevious.setEnabled(true);
					btnNext.setEnabled(true);
					lblProgress.setText("Step 2 of 3");
					lblHeader.setText("This form can also be used for maintenance entries. Enter your name for 'pilot' if so.");
				}else if (stage == 2) {
					panel.add(pnl3, "flowx,cell 0 0,grow");
					panel.remove(pnl2);
					panel.remove(pnl1);
					btnNext.setEnabled(false);
					btnPrevious.setEnabled(true);
					btnFinish.setEnabled(true);
					lblProgress.setText("Step 3 of 3");
					lblHeader.setText("Collaborations are supported! You can enter collaborative pilots below.");
				}
				NewEntry.this.validate();
				NewEntry.this.repaint();
			}
			prevStage = stage;
		}

		public boolean CheckForLetters(String text) {
			if (text.matches("[0-9]+")) {
				return false;
			}else {
				return true;
			}
		}

		public boolean CheckStageOne() {
			String hour = txtHour.getText();
			String minute = txtMinute.getText();

			String day = txtDay.getText();
			String month = txtMonth.getText();
			String year = txtYear.getText();

			String aircraft = txtAircraft.getText();

			if (!CheckForLetters(hour) && hour.length() == 2) {
				txtHour.setBackground(Color.WHITE);
				if (!CheckForLetters(minute) && minute.length() == 2) {
					txtMinute.setBackground(Color.WHITE);
					if (!CheckForLetters(day) && day.length() == 2) {
						txtDay.setBackground(Color.WHITE);
						if (!CheckForLetters(month) && month.length() == 2) {
							txtMonth.setBackground(Color.WHITE);
							if (!CheckForLetters(year) && year.length() == 4) {
								txtYear.setBackground(Color.WHITE);
								if (aircraft.length() > 0) {
									txtAircraft.setBackground(Color.WHITE);
									return true;
								}else {
									txtAircraft.setBackground(Color.RED);
								}
							}else {
								txtYear.setBackground(Color.RED);
							}
						}else {
							txtMonth.setBackground(Color.RED);
						}
					}else {
						txtDay.setBackground(Color.RED);
					}
				}else {
					txtMinute.setBackground(Color.RED);
				}
			}else {
				txtHour.setBackground(Color.RED);
			}
			return false;
		}

		public boolean CheckStageTwo() {
			if (txtPilot.getText().length() > 0) {
				txtPilot.setBackground(Color.WHITE);
				return true;
			}else {
				txtPilot.setBackground(Color.RED);
			}
			return false;
		}

		public boolean CheckStageThree() {
			if (txtNotes.getText().length() > 0) {
				return true;
			}
			return false;
		}

		public void Next() {
			if (stage == 0) {
				if (CheckStageOne()) {
					stage++;
				}
			}else if (stage == 1) {
				if (CheckStageTwo()) {
					stage++;
				}
			}
		}

		public void Previous() {
			if (stage == 1) {
				stage--;
			}else if (stage == 2) {
				stage--;
			}
		}
	}
}
