import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;
import net.miginfocom.swing.MigLayout;
import java.awt.Color;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JSplitPane;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JProgressBar;

public class UI extends JFrame{
	private static final long serialVersionUID = -6340304491773037483L;

	public static void main(String[] args) {
		new UI();
	}

	public UI() {
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
		
		JList list = new JList();
		list.setBackground(Color.GRAY);
		list.setForeground(Color.RED);
		panel_1.add(list, "cell 0 1,grow");
		
		JPanel panel_2 = new JPanel();
		panel_2.setBackground(Color.DARK_GRAY);
		panel_1.add(panel_2, "cell 1 1,grow");
		
		JButton btnNewButton = new JButton("Add File(s)...");
		
		JButton btnNewButton_1 = new JButton("Remove File");
		
		JButton btnNewButton_2 = new JButton("Send to Step 2");
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addComponent(btnNewButton_1, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
						.addComponent(btnNewButton, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
						.addComponent(btnNewButton_2))
					.addContainerGap())
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnNewButton)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnNewButton_1)
					.addPreferredGap(ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
					.addComponent(btnNewButton_2)
					.addContainerGap())
		);
		panel_2.setLayout(gl_panel_2);
		
		JLabel lblStepDesignate = new JLabel("Step 2: Designate update protocol for each file.");
		lblStepDesignate.setForeground(Color.GREEN);
		panel_1.add(lblStepDesignate, "cell 0 3");
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setBackground(Color.LIGHT_GRAY);
		panel_1.add(splitPane, "cell 0 4,grow");
		
		JList list_2 = new JList();
		list_2.setForeground(Color.ORANGE);
		list_2.setBackground(Color.GRAY);
		splitPane.setRightComponent(list_2);
		
		JPanel panel_3 = new JPanel();
		splitPane.setLeftComponent(panel_3);
		panel_3.setLayout(new BorderLayout(0, 0));
		
		Component horizontalStrut = Box.createHorizontalStrut(128);
		panel_3.add(horizontalStrut, BorderLayout.SOUTH);
		
		JList list_1 = new JList();
		list_1.setForeground(Color.RED);
		list_1.setBackground(Color.GRAY);
		panel_3.add(list_1, BorderLayout.CENTER);
		
		JPanel panel_4 = new JPanel();
		panel_4.setBackground(Color.DARK_GRAY);
		panel_1.add(panel_4, "cell 1 4,grow");
		
		JButton btnNewButton_3 = new JButton("Update");
		
		JLabel lblProtocolList = new JLabel("Protocol List");
		lblProtocolList.setForeground(Color.GREEN);
		
		JButton btnOverwrite = new JButton("Create");
		
		JButton btnDelete = new JButton("Add Delete");
		
		JButton btnAddCheck = new JButton("Add Check");
		GroupLayout gl_panel_4 = new GroupLayout(panel_4);
		gl_panel_4.setHorizontalGroup(
			gl_panel_4.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_4.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_4.createParallelGroup(Alignment.LEADING)
						.addComponent(btnNewButton_3, GroupLayout.PREFERRED_SIZE, 135, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblProtocolList)
						.addComponent(btnOverwrite, GroupLayout.PREFERRED_SIZE, 135, GroupLayout.PREFERRED_SIZE)
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
					.addComponent(btnNewButton_3)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnOverwrite)
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
	}
}
