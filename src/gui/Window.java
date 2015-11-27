package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
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

	// Table
	private int tableRowHeight;

	private String[] columnToolTips = {null, null,
		    "Number of lines which don't have any source text",
		    "Percentage of lines which are translated", null};

	public Window(String title, int width, int height, WorkingSession ws,
			int tableRowHeight) {
		this.ws = ws;
		this.tableRowHeight = tableRowHeight;
	
		// Window
		this.setTitle(title);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(true);
		Window.setLookAndFeel("Nimbus");

		// Object container
		container.setPreferredSize(new Dimension(width, height));
		container.setBackground(Color.white);
		container.setLayout(new BorderLayout());

		// Load the working session
		loadWorkingSession(ws);
				
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

	private void loadWorkingSession(WorkingSession ws) {
		// Display the information about the working session
		JPanel wsInformation = new JPanel(new BorderLayout());
		JLabel currentConfiguration = new JLabel("Current configuration: " +
				"directory=" + ws.getDirectory() + "       " +
				"from " + ws.getSourceLanguage() + " to " + ws.getDestinationLanguage());
		wsInformation.add(currentConfiguration);
		container.add(wsInformation, BorderLayout.NORTH);
		
		// Table of the files
		String columnTitles[] = {"", "File", "Missing source", "Translated", " "};
		Parse p = new Parse(ws);
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
		table.getColumn(" ").setCellEditor(new DetailsButton(new JCheckBox()));
		table.getColumn(" ").setPreferredWidth(50);	
		
		// The table is add with a scroll pane (useful if it has many lines)
		container.add(new JScrollPane(table), BorderLayout.CENTER);	
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
					wSDialog = new WorkingSessionDialog(Window.this,
							"New configuration", true);
				} else {
					wSDialog = new WorkingSessionDialog(Window.this,
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

	public static void setLookAndFeel(String lf) {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if (lf.equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		}
		catch (InstantiationException e) {}
		catch (ClassNotFoundException e) {}
		catch (UnsupportedLookAndFeelException e) {}
		catch (IllegalAccessException e) {}
	}
}
