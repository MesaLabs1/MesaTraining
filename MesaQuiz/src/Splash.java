import javax.swing.JDialog;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.Timer;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import java.awt.Canvas;
import javax.swing.SwingConstants;

public class Splash extends JDialog{
	private static final long serialVersionUID = -8425079915601941797L;
	
	public Splash() {
		setModal(true);
		setResizable(false);
		getContentPane().setBackground(Color.BLACK);
		setUndecorated(true);
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setBounds(10, 172, 430, 14);
		progressBar.setBackground(Color.DARK_GRAY);
		progressBar.setForeground(Color.WHITE);
		
		JLabel lblTitle = new JLabel("");
		lblTitle.setBounds(0, 11, 450, 159);
		lblTitle.setIcon(new ImageIcon(Splash.class.getResource("/res/title.png")));
		getContentPane().setLayout(null);
		getContentPane().add(progressBar);
		
		JLabel lblStatus = new JLabel("Initializing...");
		lblStatus.setBounds(10, 190, 430, 19);
		getContentPane().add(lblStatus);
		lblStatus.setFont(new Font("Simplified Arabic", Font.BOLD | Font.ITALIC, 11));
		lblStatus.setForeground(Color.WHITE);
		getContentPane().add(lblTitle);
		
		JLabel lblBG = new JLabel("");
		lblBG.setIcon(new ImageIcon(Splash.class.getResource("/res/splash.png")));
		lblBG.setBounds(0, 0, 450, 219);
		getContentPane().add(lblBG);
		
		this.setSize(new Dimension(450, 220));
		
	}
}
