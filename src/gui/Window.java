package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.JTableHeader;

import parsing.Parse;
import parsing.ParsedFile;
import renderer.ButtonRenderer;
import renderer.ColoredInteger;
import renderer.Percentage;
import config.WorkingSession;

public class Window extends JFrame {

	// Object container
	private JPanel container = new JPanel();

	// Menus
	private JMenuItem wsOpenRecently;

	// Configuration
	private WorkingSession ws;

	private String[] columnToolTips = {null, null,
		    "Number of lines which don't have any source text",
		    "Percentage of lines which are translated", null};

	public Window(String title, int width, int height, WorkingSession ws,
			int tableRowHeight) {
		this.ws = ws;
	
		// Window
		this.setTitle(title);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(true);

		// Object container
		container.setPreferredSize(new Dimension(width, height));
		container.setBackground(Color.white);
		container.setLayout(new BorderLayout());

		// Table of the files
		String columnTitles[] = {"", "File", "Missing source", "Translated", " "};
		Parse p = new Parse(Parse.listDirectoryFiles("C:/Users/Nicolas/Documents/GitHub/L3T/L3T/localisation"),
				"FRENCH", 2, "ENGLISH", 1);
		TableModel tableModel = new TableModel(p.toArray(), columnTitles);
		JTable table = new JTable(tableModel) {
			// Override createDefaultTableHeader to have column tool tips
			// Source : http://docs.oracle.com/javase/tutorial/uiswing/components/table.html#headertooltip
		    protected JTableHeader createDefaultTableHeader() {
		        return new JTableHeader(columnModel) {
		            public String getToolTipText(MouseEvent e) {
		                java.awt.Point p = e.getPoint();
		                int index = columnModel.getColumnIndexAtX(p.x);
		                int realIndex = columnModel.getColumn(index).getModelIndex();
		                return columnToolTips[realIndex];
		            }
		        };
		    }
		};
		// The table can be sorted with the column headers
		table.setAutoCreateRowSorter(true);

		// Table configuration
		table.setRowHeight(tableRowHeight);
		table.getColumn("").setMaxWidth(20);
		table.getColumn("File").setPreferredWidth(400);
		table.getColumn("Missing source").setCellRenderer(new ColoredInteger());
		table.getColumn("Missing source").setPreferredWidth(90);
		table.getColumn("Translated").setCellRenderer(new Percentage());
		table.getColumn(" ").setCellRenderer(new ButtonRenderer()); // render for the last column
		table.getColumn(" ").setCellEditor(new ButtonEditor(new JCheckBox()));
		table.getColumn(" ").setPreferredWidth(50);

		// The table is add with a scroll pane (useful if it has many lines)
		container.add(new JScrollPane(table));	
		
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
			// TODO : choose where to put it for it is executed only once (or look and feel be restaured)
			// Use the look and feel of the system for fileChooser
			// Check a boolean in order to have coherence but not no-beautiful waiting bar
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			catch (InstantiationException e) {}
			catch (ClassNotFoundException e) {}
			catch (UnsupportedLookAndFeelException e) {}
			catch (IllegalAccessException e) {}

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
