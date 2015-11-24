package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import config.WorkingSession;

public class Window extends JFrame {

	// Object container
	private JPanel container = new JPanel();

	// Menus
	private JMenuItem wsOpenRecently;

	// Configuration
	private WorkingSession ws;

	public Window(String title, int width, int height) {
		// Window
		this.setTitle(title);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(true);

		// Object container
		container.setPreferredSize(new Dimension(width, height));
		container.setBackground(Color.white);
		container.setLayout(new BorderLayout());

		// Container adding
		this.setContentPane(container);

		// New icon image
		//setIconImage(Toolkit.getDefaultToolkit().getImage("ressources/icon.png"));

		// Menus
		JMenuBar windowMenuBar = new JMenuBar();
		// New working session
		JMenu wsMenu = new JMenu("Configuration");
		JMenuItem wsNew = new JMenuItem("New");
		wsNew.addActionListener(new DialogWorkingSession(true));
		wsMenu.add(wsNew);
		// Open recent working session
//		wsOpenRecently = new JMenu(text.workingSessionOpenRecently());
//		if (!configuration.hasWorkingSession()) {
//			// No working session so the menu is not accessible
//			wsOpenRecently.setEnabled(false);
//		} else {
//			updateOpenRecentlyMenu();
//		}
//		wsMenu.add(wsOpenRecently);
		windowMenuBar.add(wsMenu);
		setJMenuBar(windowMenuBar);

		// Window displaying
		pack();
		setLocationRelativeTo(null);
		this.setVisible(true);
	}

	class DialogWorkingSession implements ActionListener {
		private boolean newWS;

		public DialogWorkingSession(boolean newWS) {
			this.newWS = newWS;
		}

		public void actionPerformed(ActionEvent arg0) {
			WorkingSessionDialog wSDialog;
			try {
				if (newWS) {
					wSDialog = new WorkingSessionDialog(null,
							"New configuration", true);
				} else {
					wSDialog = new WorkingSessionDialog(null,
							"Modify configuration", true, ws);
				}
				WorkingSession newWS = wSDialog.getWorkingSession();
				if (newWS !=null) {
					// The user defined a working session
//					loadWorkingSession(newWS);

					// Update the configuration
//					configuration.addFirstWorkingSession(newWS);
//					configuration.saveConfigFile();
//					updateOpenRecentlyMenu();

					// Close the dialog
					wSDialog.dispose();
				}
			} catch (IllegalArgumentException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR: ", JOptionPane.ERROR_MESSAGE);
			} //catch (FileNotFoundException e) {
//				JOptionPane.showMessageDialog(null, "File " + e.getMessage() + " not found!", "ERROR: ", JOptionPane.ERROR_MESSAGE);
//			}
		}
	}
}
