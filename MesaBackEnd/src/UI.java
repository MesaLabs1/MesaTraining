import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import javax.swing.JToolBar;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JProgressBar;

/**
 * This class is a simple GUI for this backend. You can disable it from appearing by adding the tag -nogui to
 * the launch parameters.
 * @author hackjunky, jacrin
 *
 */
public class UI extends JFrame{
	
	/**
	 * The constructor here will simply define the GUI components, make sure the style is visually neutral (to avoid random UI inconsistencies),
	 * and set the font package of the target system.
	 */
	public UI() {
		setTitle("MESA Backend - User Interface");
		//We chose 640x480 because this is the default minimum resolution support ratio for non-graphically enabled systems.
		this.setSize(new Dimension(640, 480));
		
		//Make sure that when the user terminates the UI, we don't leave our socket running in the background.
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		
		JPanel pnlConnection = new JPanel();
		pnlConnection.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		pnlConnection.setBounds(6, 6, 256, 96);
		getContentPane().add(pnlConnection);
		pnlConnection.setLayout(null);
		
		JLabel lblConnStatusH = new JLabel("Connection Status");
		lblConnStatusH.setBounds(6, 6, 119, 16);
		pnlConnection.add(lblConnStatusH);
		
		JLabel lblClientsH = new JLabel("Clients:");
		lblClientsH.setBounds(16, 26, 57, 16);
		pnlConnection.add(lblClientsH);
		
		JLabel lblOverheadH = new JLabel("Overhead:");
		lblOverheadH.setBounds(16, 45, 71, 16);
		pnlConnection.add(lblOverheadH);
		
		JLabel lblUptime = new JLabel("XXX:XXX:XXX");
		lblUptime.setBounds(170, 6, 80, 16);
		pnlConnection.add(lblUptime);
		
		JLabel lblOverallH = new JLabel("Overall:");
		lblOverallH.setBounds(16, 66, 48, 16);
		pnlConnection.add(lblOverallH);
		
		JLabel lblClients = new JLabel("XXX");
		lblClients.setBounds(85, 26, 29, 16);
		pnlConnection.add(lblClients);
		
		JLabel lblOverhead = new JLabel("XXXXXX");
		lblOverhead.setBounds(85, 45, 57, 16);
		pnlConnection.add(lblOverhead);
		
		JLabel lblOverall = new JLabel("XXXXXXXXXXXXX");
		lblOverall.setBounds(85, 66, 112, 16);
		pnlConnection.add(lblOverall);
		
		JPanel pnlOutput = new JPanel();
		pnlOutput.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		pnlOutput.setBounds(6, 108, 628, 322);
		getContentPane().add(pnlOutput);
		pnlOutput.setLayout(null);
		
		JLabel lblOutputH = new JLabel("Output");
		lblOutputH.setBounds(6, 6, 61, 16);
		pnlOutput.add(lblOutputH);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 25, 616, 274);
		pnlOutput.add(scrollPane);
		
		JList consoleList = new JList();
		scrollPane.setViewportView(consoleList);
		
		JPanel pnlRed = new JPanel();
		pnlRed.setBackground(Color.RED);
		pnlRed.setBorder(new LineBorder(new Color(0, 0, 0)));
		pnlRed.setBounds(550, 302, 16, 16);
		pnlOutput.add(pnlRed);
		
		JPanel pnlYellow = new JPanel();
		pnlYellow.setBackground(Color.YELLOW);
		pnlYellow.setBorder(new LineBorder(new Color(0, 0, 0)));
		pnlYellow.setBounds(578, 302, 16, 16);
		pnlOutput.add(pnlYellow);
		
		JPanel pnlGreen = new JPanel();
		pnlGreen.setBackground(Color.GREEN);
		pnlGreen.setBorder(new LineBorder(new Color(0, 0, 0)));
		pnlGreen.setBounds(606, 302, 16, 16);
		pnlOutput.add(pnlGreen);
		
		JButton btnClear = new JButton("Clear");
		btnClear.setBounds(560, 5, 61, 16);
		pnlOutput.add(btnClear);
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setBounds(6, 302, 183, 14);
		pnlOutput.add(progressBar);
		
		JLabel lblRunningxxx = new JLabel("Running... (XXX%)");
		lblRunningxxx.setBounds(196, 302, 117, 16);
		pnlOutput.add(lblRunningxxx);
		
		JPanel pnlSystem = new JPanel();
		pnlSystem.setBorder(new LineBorder(new Color(0, 0, 0)));
		pnlSystem.setBounds(274, 6, 360, 96);
		getContentPane().add(pnlSystem);
		pnlSystem.setLayout(null);
		
		JLabel lblPeerInformation = new JLabel("System Information");
		lblPeerInformation.setBounds(6, 6, 132, 16);
		pnlSystem.add(lblPeerInformation);
		
		JLabel lblThreadsH = new JLabel("Threads:");
		lblThreadsH.setBounds(16, 25, 61, 16);
		pnlSystem.add(lblThreadsH);
		
		JLabel lblHandlesH = new JLabel("Handles:");
		lblHandlesH.setBounds(16, 43, 61, 16);
		pnlSystem.add(lblHandlesH);
		
		JLabel lblGCH = new JLabel("Garbage Collections:");
		lblGCH.setBounds(16, 74, 137, 16);
		pnlSystem.add(lblGCH);
		
		JLabel lblErrorsH = new JLabel("Errors:");
		lblErrorsH.setBounds(277, 74, 47, 16);
		pnlSystem.add(lblErrorsH);
		
		JLabel lblMemoryUsageH = new JLabel("Memory Usage:");
		lblMemoryUsageH.setBounds(134, 25, 102, 16);
		pnlSystem.add(lblMemoryUsageH);
		
		JLabel lblAccessCountH = new JLabel("Access Count:");
		lblAccessCountH.setBounds(134, 43, 102, 16);
		pnlSystem.add(lblAccessCountH);
		
		JLabel lblThreads = new JLabel("XXX");
		lblThreads.setBounds(77, 25, 31, 16);
		pnlSystem.add(lblThreads);
		
		JLabel lblHandles = new JLabel("XXXXX");
		lblHandles.setBounds(77, 43, 45, 16);
		pnlSystem.add(lblHandles);
		
		JLabel lblGC = new JLabel("XXXXXXXXXXXXX");
		lblGC.setBounds(154, 74, 111, 16);
		pnlSystem.add(lblGC);
		
		JLabel lblErrors = new JLabel("XXX");
		lblErrors.setBounds(322, 74, 30, 16);
		pnlSystem.add(lblErrors);
		
		JLabel lblMemUsage = new JLabel("XXXXXXXXXXXX K");
		lblMemUsage.setBounds(235, 25, 117, 16);
		pnlSystem.add(lblMemUsage);
		
		JLabel lblAccessCount = new JLabel("XXXXXXXXXXXXXX");
		lblAccessCount.setBounds(235, 43, 117, 16);
		pnlSystem.add(lblAccessCount);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBackground(Color.LIGHT_GRAY);
		setJMenuBar(menuBar);
	}
}
