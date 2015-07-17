import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.Color;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;

public class Sidebar extends JFrame{
	private static final long serialVersionUID = -1733165438644879034L;
	
	UI parent;
	Timer timer;
	
	JList<String> list;
	
	DefaultListModel<String> console = new DefaultListModel<String>();

	public Sidebar(UI ui) {
		getContentPane().setBackground(Color.DARK_GRAY);
		getContentPane().setLayout(new MigLayout("", "[::480px,grow]", "[][4px:n:4px][grow]"));
		
		JLabel lblConsole = new JLabel("Console Output");
		lblConsole.setForeground(Color.GREEN);
		getContentPane().add(lblConsole, "cell 0 0,alignx center");
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, "cell 0 2,grow");
		
		list = new JList<String>();
		scrollPane.setViewportView(list);
		list.setModel(console);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setBackground(Color.GRAY);
		list.setForeground(Color.ORANGE);
		parent = ui;
		timer = new Timer(1, new Tick());
		timer.start();

		this.setSize(480, 400);

		this.setResizable(false);
		this.setTitle("Console");
		this.setUndecorated(true);
		
		this.setVisible(true);
	}

	public class Tick implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (parent.isVisible()) {
				Sidebar.this.setLocation(new Point(parent.getLocationOnScreen().x + parent.getSize().width, parent.getLocationOnScreen().y));
				Sidebar.this.setSize(new Dimension(Sidebar.this.getSize().width, parent.getSize().height));
				if (parent.allowSidebarVisibility && !Sidebar.this.isVisible()) {
					Sidebar.this.setVisible(true);
				}
			}else {
				if (parent.allowSidebarVisibility && Sidebar.this.isVisible()) {
					Sidebar.this.setVisible(false);
				}
			}
		}
	}
}
