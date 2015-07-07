import java.awt.Dimension;

import javax.swing.JFrame;

import java.awt.Window.Type;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.SoftBevelBorder;
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
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;


public class NewEntry extends JDialog{
	private static final long serialVersionUID = -1539819973323570130L;
	private JTextField txtDay;
	private JTextField txtMonth;
	private JTextField txtYear;
	private JTextField txtHour;
	private JTextField txtMinute;
	private JTextField txtAircraft;
	private JTextField txtPilot;
	
	private JPanel pnlCollab;
	
	private JList<String> listCollab;
	private DefaultListModel<String> collabModel;

	private WizardHelper helper;
	private Timer timer;

	JPanel pnl3;
	JPanel pnl2;
	JPanel pnl1;

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

	enum ReportType {
		FullReport, RepairOnly, TrainingOnly, FlightOnly
	}
	
	public NewEntry(ReportType r) {
		helper = new WizardHelper();
		timer = new Timer(100, helper);
		
		collabModel = new DefaultListModel<String>();

		getContentPane().setLocation(284, 0);
		getContentPane().setBackground(Color.LIGHT_GRAY);
		setType(Type.UTILITY);
		setTitle("WebConnect - Create a New Database Entry");
		this.setSize(new Dimension(640, 278));
		getContentPane().setLayout(null);

		JPanel pnlHeader = new JPanel();
		pnlHeader.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		pnlHeader.setBounds(0, 0, 624, 35);
		getContentPane().add(pnlHeader);

		JLabel lblCreateADatabase = new JLabel("Create a database entry in a few easy steps. Use this Entry Wizard to speed up the process!");
		pnlHeader.add(lblCreateADatabase);

		JPanel pnlFooter = new JPanel();
		pnlFooter.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		pnlFooter.setBounds(0, 209, 624, 29);
		getContentPane().add(pnlFooter);
		pnlFooter.setLayout(null);

		lblProgress = new JLabel("Step X of X");
		lblProgress.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblProgress.setBounds(277, 3, 83, 23);
		pnlFooter.add(lblProgress);

		btnPrevious = new JButton("Previous");
		btnPrevious.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				helper.Previous();
			}
		});
		btnPrevious.setBounds(10, 3, 89, 23);
		pnlFooter.add(btnPrevious);

		btnNext = new JButton("Next");
		btnNext.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				helper.Next();
			}
		});
		btnNext.setBounds(436, 3, 89, 23);
		pnlFooter.add(btnNext);

		btnFinish = new JButton("Finish");
		btnFinish.setEnabled(false);
		btnFinish.setBounds(525, 3, 89, 23);
		pnlFooter.add(btnFinish);

		pnl3 = new JPanel();
		pnl3.setBackground(Color.LIGHT_GRAY);
		pnl3.setBounds(0, 37, 624, 172);
		getContentPane().add(pnl3);
		pnl3.setLayout(null);

		JLabel lblAddReportData = new JLabel("Add Report Data");
		lblAddReportData.setBounds(10, 11, 169, 20);
		lblAddReportData.setFont(new Font("Tahoma", Font.BOLD, 16));
		pnl3.add(lblAddReportData);

		JLabel lblStuff = new JLabel("You've provided all the information necessary to submit this report. Please fill out the form below with additional details.");
		lblStuff.setBounds(20, 33, 594, 14);
		pnl3.add(lblStuff);

		JLabel lblThisWasA = new JLabel("This was a ");
		lblThisWasA.setBounds(30, 58, 65, 14);
		pnl3.add(lblThisWasA);

		radioFlight = new JRadioButton("Flight.");
		radioFlight.setSelected(true);
		radioFlight.setBackground(Color.LIGHT_GRAY);
		radioFlight.setBounds(88, 54, 72, 23);
		pnl3.add(radioFlight);

		radioRepair = new JRadioButton("Repair.");
		radioRepair.setBackground(Color.LIGHT_GRAY);
		radioRepair.setBounds(88, 79, 72, 23);
		pnl3.add(radioRepair);

		radioTraining = new JRadioButton("Training.");
		radioTraining.setBackground(Color.LIGHT_GRAY);
		radioTraining.setBounds(88, 105, 72, 23);
		pnl3.add(radioTraining);
		
		radioFlight.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if (NewEntry.this.radioFlight.isSelected()) {
					NewEntry.this.radioRepair.setSelected(false);
					NewEntry.this.radioTraining.setSelected(false);
				}
			}
		});
		
		radioRepair.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if (NewEntry.this.radioRepair.isSelected()) {
					NewEntry.this.radioFlight.setSelected(false);
					NewEntry.this.radioTraining.setSelected(false);
				}
			}
		});
		
		radioTraining.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if (NewEntry.this.radioTraining.isSelected()) {
					NewEntry.this.radioRepair.setSelected(false);
					NewEntry.this.radioFlight.setSelected(false);
				}
			}
		});

		JLabel lblNotes = new JLabel("Notes:");
		lblNotes.setBounds(169, 58, 46, 14);
		pnl3.add(lblNotes);

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel_2.setBounds(218, 59, 381, 102);
		pnl3.add(panel_2);
		panel_2.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		panel_2.add(scrollPane, BorderLayout.CENTER);

		JTextArea txtrEnterYourNotes = new JTextArea();
		txtrEnterYourNotes.setLineWrap(true);
		txtrEnterYourNotes.setText("Enter your notes here.");
		txtrEnterYourNotes.setForeground(Color.WHITE);
		scrollPane.setViewportView(txtrEnterYourNotes);
		txtrEnterYourNotes.setBackground(Color.GRAY);

		pnl2 = new JPanel();
		pnl2.setBackground(Color.LIGHT_GRAY);
		pnl2.setBounds(0, 37, 624, 172);
		getContentPane().add(pnl2);
		pnl2.setLayout(null);

		JLabel lblPilots = new JLabel("Pilots");
		lblPilots.setBounds(10, 11, 59, 20);
		lblPilots.setFont(new Font("Tahoma", Font.BOLD, 16));
		pnl2.add(lblPilots);

		JLabel lblThisFlightWas = new JLabel("This flight was completed by ");
		lblThisFlightWas.setBounds(20, 35, 138, 14);
		pnl2.add(lblThisFlightWas);

		txtPilot = new JTextField();
		txtPilot.setBounds(159, 32, 163, 20);
		pnl2.add(txtPilot);
		txtPilot.setColumns(10);

		JLabel lblYourFirst = new JLabel("your first + last name");
		lblYourFirst.setBounds(182, 52, 104, 14);
		pnl2.add(lblYourFirst);

		JLabel lblAndWasDone = new JLabel("and was done so");
		lblAndWasDone.setBounds(332, 35, 81, 14);
		pnl2.add(lblAndWasDone);

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
		radioAlone.setBounds(427, 12, 59, 23);
		pnl2.add(radioAlone);

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
		radioCollab.setBounds(427, 48, 121, 23);
		pnl2.add(radioCollab);

		pnlCollab = new JPanel();
		pnlCollab.setBackground(Color.LIGHT_GRAY);
		pnlCollab.setBounds(20, 77, 249, 84);
		pnl2.add(pnlCollab);
		pnlCollab.setLayout(null);

		JLabel lblTheFollowingPilots = new JLabel("the following pilots:");
		lblTheFollowingPilots.setBounds(10, 0, 101, 14);
		pnlCollab.add(lblTheFollowingPilots);

		listCollab = new JList<String>();
		listCollab.setModel(collabModel);
		listCollab.setBackground(Color.LIGHT_GRAY);
		listCollab.setBounds(121, -1, 122, 74);
		pnlCollab.add(listCollab);

		JButton btnAdd = new JButton("Add");
		btnAdd.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String resp = JOptionPane.showInputDialog("Enter the name of the collaborative pilot:");
				if (resp != null && resp.length() > 0) {
					collabModel.addElement(resp);
				}
			}
		});
		btnAdd.setForeground(Color.WHITE);
		btnAdd.setBackground(Color.DARK_GRAY);
		btnAdd.setBounds(22, 50, 89, 23);
		pnlCollab.add(btnAdd);

		JButton btnRemove = new JButton("Remove");
		btnRemove.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (listCollab.getSelectedValue() != null && listCollab.getSelectedValue().length() > 0) {
					collabModel.remove(listCollab.getSelectedIndex());
				}
			}
		});
		btnRemove.setForeground(Color.WHITE);
		btnRemove.setBackground(Color.DARK_GRAY);
		btnRemove.setBounds(22, 16, 89, 23);
		pnlCollab.add(btnRemove);

		JLabel lblNoteIfYoure = new JLabel("Note: If you're submitting a maintinence log, fill out this form anyway.");
		lblNoteIfYoure.setForeground(Color.BLACK);
		lblNoteIfYoure.setBounds(279, 147, 340, 14);
		pnl2.add(lblNoteIfYoure);

		pnl1 = new JPanel();
		pnl1.setBackground(Color.LIGHT_GRAY);
		pnl1.setBounds(0, 37, 624, 172);
		getContentPane().add(pnl1);
		pnl1.setLayout(null);

		JLabel lblDateAndTime = new JLabel("Date and Time");
		lblDateAndTime.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblDateAndTime.setBounds(10, 11, 125, 26);
		pnl1.add(lblDateAndTime);

		JLabel lblPleaseSetThe = new JLabel("Please enter the date and time that this flight took place.");
		lblPleaseSetThe.setBounds(20, 39, 308, 14);
		pnl1.add(lblPleaseSetThe);

		JLabel lblThisFlightOccurred = new JLabel("This flight occurred on");
		lblThisFlightOccurred.setBounds(20, 88, 113, 14);
		pnl1.add(lblThisFlightOccurred);

		txtDay = new JTextField();
		txtDay.setBounds(131, 85, 35, 20);
		pnl1.add(txtDay);
		txtDay.setColumns(10);

		JLabel label = new JLabel("/");
		label.setBounds(168, 88, 12, 14);
		pnl1.add(label);

		JLabel lblDay = new JLabel("day");
		lblDay.setBounds(141, 106, 24, 14);
		pnl1.add(lblDay);

		txtMonth = new JTextField();
		txtMonth.setColumns(10);
		txtMonth.setBounds(176, 85, 35, 20);
		pnl1.add(txtMonth);

		JLabel lblMonth = new JLabel("month");
		lblMonth.setBounds(178, 106, 33, 14);
		pnl1.add(lblMonth);

		JLabel label_2 = new JLabel("/");
		label_2.setBounds(213, 88, 12, 14);
		pnl1.add(label_2);

		txtYear = new JTextField();
		txtYear.setColumns(10);
		txtYear.setBounds(221, 85, 35, 20);
		pnl1.add(txtYear);

		JLabel lblYear = new JLabel("year");
		lblYear.setBounds(231, 106, 24, 14);
		pnl1.add(lblYear);

		JLabel lblNewLabel = new JLabel("and started at about ");
		lblNewLabel.setBounds(263, 88, 103, 14);
		pnl1.add(lblNewLabel);

		txtHour = new JTextField();
		txtHour.setColumns(10);
		txtHour.setBounds(365, 85, 35, 20);
		pnl1.add(txtHour);

		JLabel lblHour = new JLabel("hour");
		lblHour.setBounds(375, 106, 24, 14);
		pnl1.add(lblHour);

		JLabel label_3 = new JLabel(":");
		label_3.setBounds(402, 88, 12, 14);
		pnl1.add(label_3);

		txtMinute = new JTextField();
		txtMinute.setColumns(10);
		txtMinute.setBounds(410, 85, 35, 20);
		pnl1.add(txtMinute);

		JLabel lblMinute = new JLabel("minute");
		lblMinute.setBounds(412, 106, 33, 14);
		pnl1.add(lblMinute);

		radioAM = new JRadioButton("AM");
		radioAM.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (NewEntry.this.radioAM.isSelected()) {
					NewEntry.this.radioPM.setSelected(false);
				}
			}
		});
		radioAM.setSelected(true);
		radioAM.setBackground(Color.LIGHT_GRAY);
		radioAM.setBounds(460, 65, 50, 23);
		pnl1.add(radioAM);

		radioPM = new JRadioButton("PM");
		radioPM.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (NewEntry.this.radioPM.isSelected()) {
					NewEntry.this.radioAM.setSelected(false);
				}
			}
		});
		radioPM.setBackground(Color.LIGHT_GRAY);
		radioPM.setBounds(460, 102, 50, 23);
		pnl1.add(radioPM);

		JLabel lblNewLabel_1 = new JLabel("and it ");
		lblNewLabel_1.setBounds(506, 88, 46, 14);
		pnl1.add(lblNewLabel_1);

		JLabel lblWasCompletedUsing = new JLabel("was completed using the ");
		lblWasCompletedUsing.setBounds(21, 127, 125, 14);
		pnl1.add(lblWasCompletedUsing);

		txtAircraft = new JTextField();
		txtAircraft.setBounds(151, 124, 157, 20);
		pnl1.add(txtAircraft);
		txtAircraft.setColumns(10);

		JLabel lblAircraftName = new JLabel("aircraft name");
		lblAircraftName.setBounds(193, 146, 71, 14);
		pnl1.add(lblAircraftName);

		JLabel lblAircraft = new JLabel("aircraft.");
		lblAircraft.setBounds(318, 127, 46, 14);
		pnl1.add(lblAircraft);
		
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
		
		pnlCollab.setVisible(false);

		this.setVisible(true);
		this.setModal(true);
		timer.start();
	}

	public class WizardHelper implements ActionListener {
		private int stage = 0;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (stage == 0) {
				pnl1.setVisible(true);
				pnl2.setVisible(false);
				pnl3.setVisible(false);
				btnPrevious.setEnabled(false);
				btnFinish.setEnabled(false);
				lblProgress.setText("Step 1 of 3");
			}else if (stage == 1) {
				pnl1.setVisible(false);
				pnl2.setVisible(true);
				pnl3.setVisible(false);
				btnFinish.setEnabled(false);
				lblProgress.setText("Step 2 of 3");
			}else if (stage == 2) {
				pnl1.setVisible(false);
				pnl2.setVisible(false);
				pnl3.setVisible(true);
				btnNext.setEnabled(false);
				lblProgress.setText("Step 3 of 3");
			}
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
			String second = "00";
			
			String day = txtDay.getText();
			String month = txtMonth.getText();
			String year = txtYear.getText();
			
			if (!CheckForLetters(hour) && hour.length() > 0) {
				txtHour.setBackground(Color.WHITE);
				if (!CheckForLetters(minute) && minute.length() > 0) {
					if (!CheckForLetters(second) && second.length() > 0) {
						if (!CheckForLetters(day) && day.length() > 0) {
							if (!CheckForLetters(month) && month.length() > 0) {
								if (!CheckForLetters(year) && year.length() > 0) {
									return true;
								}else {
									
								}
							}else {
								
							}
						}else {
							
						}
					}else {
						
					}
				}else {
					
				}
			}else {
				txtHour.setBackground(Color.RED);
			}
			return false;
		}
		
		public boolean CheckStageTwo() {
			if (txtPilot.getText().length() > 0) {
				if (radioAlone.isSelected()) {
					
				}else {
					
				}
				txtPilot.setBackground(Color.WHITE);
			}else {
				txtPilot.setBackground(Color.RED);
			}
		}
		
		public boolean CheckStageThree() {
			
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
