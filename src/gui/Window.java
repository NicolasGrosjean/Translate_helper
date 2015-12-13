package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import parsing.Parse;
import parsing.ParsedFile;
import renderer.ButtonRenderer;
import renderer.ColoredInteger;
import renderer.Percentage;
import config.ConfigStorage;
import config.WorkingSession;

public class Window extends JFrame {

	// Object container
	private JPanel container = new JPanel();

	// Menus
	private JMenuItem wsOpenRecently;
	private JMenuItem wsModify;

	// Configuration
	private WorkingSession ws;
	private final ConfigStorage configuration;
	private String fakeTranslationFile;
	private String acceptedLoanwordFile;

	// Table
	private JTable table;
	private int tableRowHeight;
	private JScrollPane tableSP;

	private String[] columnToolTips = {null, null,
		    "Number of lines which don't have any source text",
		    "Percentage of lines which are translated", null};

	public Window(String title, int width, int height, WorkingSession ws,
			int tableRowHeight, ConfigStorage configuration,
			String fakeTranslationFile, String acceptedLoanwordFile) {
		this.tableRowHeight = tableRowHeight;
		this.configuration = configuration;
		this.fakeTranslationFile = fakeTranslationFile;
		this.acceptedLoanwordFile = acceptedLoanwordFile;
	
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
		if (ws != null) {
			loadWorkingSession(ws);
		}
				
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
		wsOpenRecently = new JMenu("Open recent");
		if (!configuration.hasWorkingSession()) {
			// No working session so the menu is not accessible
			wsOpenRecently.setEnabled(false);
		} else {
			updateOpenRecentlyMenu();
		}
		wsMenu.add(wsOpenRecently);
		wsMenu.addSeparator();
		// Modify current working session
		wsModify= new JMenuItem("Modify");
		wsModify.setEnabled(configuration.hasWorkingSession());
		wsModify.addActionListener(new DialogWorkingSession(false));
		wsMenu.add(wsModify);
		windowMenuBar.add(wsMenu);
		setJMenuBar(windowMenuBar);

		// Bottom
		JPanel bottom = new JPanel(new GridLayout(1, 4, 5, 5));
		JButton selectAll = new JButton("Select All");
		selectAll.addActionListener(new SelectAllListener());
		JButton deselectAll = new JButton("Deselect All");
		deselectAll.addActionListener(new DeselectAllListener());
		JButton exportPdf = new JButton("Export to PDF...");
		exportPdf.addActionListener(new ExportPDFListener());
		bottom.add(selectAll);
		bottom.add(deselectAll);
		bottom.add(exportPdf);
		container.add(bottom, BorderLayout.SOUTH);

		// Window displaying
		pack();
		setLocationRelativeTo(null);
		this.setVisible(true);
	}

	/**
	 * Load a working session : parse the directory and create the JTable
	 * @param ws The Working session to load
	 */
	private void loadWorkingSession(WorkingSession ws) {
		// Set the new current working session
		this.ws = ws;

		// Remove the old components
		if (table != null) {
			container.remove(tableSP);
		}

		// The working session can be modified
		// (it is null at the window initialization)
		if (wsModify != null) {
			wsModify.setEnabled(true);
		}

		// Display the information about the working session
		JPanel wsInformation = new JPanel(new BorderLayout());
		JLabel currentConfiguration = new JLabel("Current configuration: " +
				"directory=" + ws.getDirectory() + "       " +
				"from " + ws.getSourceLanguage() + " to " + ws.getDestinationLanguage());
		wsInformation.add(currentConfiguration);
		container.add(wsInformation, BorderLayout.NORTH);
		
		// Table of the files
		String columnTitles[] = {"", "File", "Missing source text", "Translated", " "};
		Parse p = new Parse(ws, fakeTranslationFile, acceptedLoanwordFile);
		DiagnosticTableModel tableModel = new DiagnosticTableModel(p.toArray(), columnTitles);
		table = new JTable(tableModel) {
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
		// Table configuration
		table.setRowHeight(tableRowHeight);
		table.getColumn(columnTitles[0]).setMaxWidth(20);
		table.getColumn(columnTitles[1]).setPreferredWidth(400);
		table.getColumn(columnTitles[2]).setCellRenderer(new ColoredInteger());
		table.getColumn(columnTitles[2]).setPreferredWidth(90);
		table.getColumn(columnTitles[3]).setCellRenderer(new Percentage());
		table.getColumn(columnTitles[4]).setCellRenderer(new ButtonRenderer());
		table.getColumn(columnTitles[4]).setCellEditor(new DetailsButton(new JCheckBox(),
				!ws.getDestinationLanguage().isNone()));
		table.getColumn(columnTitles[4]).setPreferredWidth(50);

		// The table can be sorted with the column headers
		if (ws.getDestinationLanguage().isNone()) {
			table.setAutoCreateRowSorter(true);
		} else {
			// Sort the third column according the percentage
			TableRowSorter<DiagnosticTableModel> sorter =
					new TableRowSorter<DiagnosticTableModel>(tableModel);
			sorter.setComparator(3, Percentage.comparator);
			table.setRowSorter(sorter);
		}

		// The table is add with a scroll pane (useful if it has many lines)
		tableSP = new JScrollPane(table);
		container.add(tableSP, BorderLayout.CENTER);

		// Refresh the window
		pack();
		repaint();
	}

	/**
	 * Update the menu open recently according the configurations
	 */
	private void updateOpenRecentlyMenu() {
		// Cleaning
		wsOpenRecently.removeAll();
		// All working session except the first are added
		wsOpenRecently.setEnabled(configuration.getSize() > 1);
		boolean first = true;
		Iterator<WorkingSession> it = configuration.iterator();
		while (it.hasNext()) {
			WorkingSession ws = it.next();
			if (first) {
				first = false;
			} else {
				JMenuItem wsMenuItem = new JMenuItem(ws.getName());
				wsMenuItem.addActionListener(new OpenRecentWorkingSession(ws));
				wsOpenRecently.add(wsMenuItem);
			}
		}
	}

	/**
	 * Use the itextpdf library to print pdf
	 * @param outputFileName
	 * @throws DocumentException 
	 * @throws FileNotFoundException 
	 */
	private void printPDF(String title, String outputFileName) throws FileNotFoundException, DocumentException {
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(outputFileName));
		document.open();

		// Put the title
		Font titleFont = FontFactory.getFont("Times-Roman", 30, Font.BOLD);
        Paragraph titleParagraph = new Paragraph(title, titleFont);
        titleParagraph.setSpacingAfter(20);
        titleParagraph.setAlignment(Element.ALIGN_CENTER);
        document.add(titleParagraph);

        // Put a description
        Font descriptionFont = FontFactory.getFont("Times-Roman", 14, Font.BOLD);
        Paragraph descriptionParagraph = new Paragraph("Diagnostic done with " +
        		ws.getSourceLanguage() + " as source language and " +
        		ws.getDestinationLanguage() + " as destination language.", descriptionFont);
        descriptionParagraph.setSpacingAfter(20);
        descriptionParagraph.setAlignment(Element.ALIGN_CENTER);
        document.add(descriptionParagraph);

		// Print the table
		PdfPTable pdfTable = null;
		if (ws.getDestinationLanguage().isNone()) {
			pdfTable = new PdfPTable(2); // No translation column
		} else {
			pdfTable = new PdfPTable(3);
		}
		int columnNumber = (ws.getDestinationLanguage().isNone())? table.getColumnCount() - 2 : table.getColumnCount() - 1;
		for (int j = 1; j < columnNumber; j++) {
			pdfTable.addCell(table.getColumnName(j));
		}
		for (int i = 0; i < table.getRowCount(); i++) {
			// Only selected files are printed
			if (table.getValueAt(i, 0) instanceof Boolean &&
					!(Boolean)table.getValueAt(i, 0)) {
				continue;
			}

			// File name (= anchor, i.e internal link)
			ParsedFile f = null;
			if (table.getValueAt(i, 1) instanceof ParsedFile) {
				f = (ParsedFile)table.getValueAt(i, 1);
			}
			PdfPCell cell = null;
			if (f.getNumberMissingSourceLines() > 0 ||
					(f.getNumberLineToTranslate() > 0 && !ws.getDestinationLanguage().isNone())) {
				Anchor fileName = new Anchor(table.getModel().getValueAt(i, 1).toString());
				fileName.setReference("#" + table.getModel().getValueAt(i, 1).toString());
				cell = new PdfPCell(fileName);
			} else {
				cell = new PdfPCell(new Phrase(table.getModel().getValueAt(i, 1).toString()));
			}
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			pdfTable.addCell(cell);

			// Number of missing source text lines
			Object value = table.getModel().getValueAt(i, 2);
			cell = new PdfPCell(new Phrase(value.toString()));
			int iValue = (Integer)value;
			if (iValue == 0) {
				cell.setBackgroundColor(BaseColor.GREEN);
			} else{
				cell.setBackgroundColor(BaseColor.RED);
			}
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			pdfTable.addCell(cell);

			// Translation percentage done if the language destination is specified
			if (!ws.getDestinationLanguage().isNone()) {
				value = table.getModel().getValueAt(i, 3);
				cell = new PdfPCell(new Phrase(value.toString()));
				iValue = Percentage.stringToValue((String)value);
				if (iValue == 100) {
					cell.setBackgroundColor(BaseColor.GREEN);
				} else if (iValue >= 50) {
					cell.setBackgroundColor(BaseColor.ORANGE);
				} else if (iValue >= 0) {
					cell.setBackgroundColor(BaseColor.RED);
				} else {
					cell.setBackgroundColor(BaseColor.BLUE);
				}
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				pdfTable.addCell(cell);
			}

		}
		float[] relativeWidths;
		if (ws.getDestinationLanguage().isNone()) {
			relativeWidths = new float[] {0.75f, 0.25f};
		} else {
			relativeWidths = new float[] {0.55f, 0.25f, 0.2f};
		}
		pdfTable.setWidths(relativeWidths);
		pdfTable.setWidthPercentage(100f);
		document.add(pdfTable);

		// Add indication concerning the links
		Font italicFont = FontFactory.getFont("Times-Roman", 12, Font.ITALIC);
		Paragraph indicationParagraph = new Paragraph(
				"Click on the filename (in case of missing problems) to see the details", italicFont);
		indicationParagraph.setAlignment(Element.ALIGN_CENTER);
        document.add(indicationParagraph);

		// New page
		document.newPage();

		// Print the details
		Paragraph detailTitleParagraph = new Paragraph(title + " - details", titleFont);
		detailTitleParagraph.setSpacingAfter(20);
		detailTitleParagraph.setAlignment(Element.ALIGN_CENTER);
        document.add(detailTitleParagraph);
		Font font = FontFactory.getFont("Times-Roman", 12);
		Font fontboldFile = FontFactory.getFont("Times-Roman", 20, Font.BOLD);
		Font fontboldType = FontFactory.getFont("Times-Roman", 16, Font.BOLD);
		for(int i = 0; i < table.getRowCount(); i++) {
			// Only details of selected files are printed
			if (table.getValueAt(i, 0) instanceof Boolean &&
					!(Boolean)table.getValueAt(i, 0)) {
				continue;
			}

			ParsedFile f = null;
			if (table.getValueAt(i, 1) instanceof ParsedFile) {
				f = (ParsedFile)table.getValueAt(i, 1);
			}
			if (f.getNumberMissingSourceLines() > 0 ||
					(f.getNumberLineToTranslate() > 0 && !ws.getDestinationLanguage().isNone())) {
				Anchor fileAnchor = new Anchor(":");
				fileAnchor.setName(f.getName());
				Paragraph fileName = new Paragraph(f.getName(), fontboldFile);
				fileName.add(fileAnchor);
				document.add(fileName);
			}
			if (f.getNumberMissingSourceLines() > 0) {
				document.add(new Paragraph("Missing source text (" +
						f.getNumberMissingSourceLines() + " elements)", fontboldType));
				document.add(new Paragraph(f.getMissingSourceText(), font));
			}
			if (f.getNumberLineToTranslate() > 0 && !ws.getDestinationLanguage().isNone()) {
				document.add(new Paragraph("Missing translation (" +
						f.getNumberLineToTranslate() + " elements)", fontboldType));
				document.add(new Paragraph(f.getMissingTranslation(), font));
			}
		}
		document.close();

		JOptionPane.showMessageDialog(this, "The file " + outputFileName +
				" was successfully generated.", "", JOptionPane.INFORMATION_MESSAGE);
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
				WorkingSession workingSession = wSDialog.getWorkingSession();

				if (workingSession !=null) {
					// The user defined a working session
					loadWorkingSession(workingSession);

					// Update the configuration
					if (newWS) {
						// A new working session is added
						configuration.addFirstWorkingSession(workingSession);
						configuration.saveConfigFile();
						// This list of no-displayed working sessions is changed
						updateOpenRecentlyMenu();
					} else {
						// The first working session is changed
						configuration.replaceFirstWorkingSession(workingSession);
						configuration.saveConfigFile();
					}

					// Close the dialog
					wSDialog.dispose();
				}
			} catch (IllegalArgumentException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR: ", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	class OpenRecentWorkingSession implements ActionListener {
		private WorkingSession ws;

		OpenRecentWorkingSession(WorkingSession ws) {
			this.ws = ws;
		}

		public void actionPerformed(ActionEvent arg0) {
			try {
				loadWorkingSession(ws);

				// Update the configuration and the menu
				configuration.becomeFirst(ws);
				configuration.saveConfigFile();
				updateOpenRecentlyMenu();
			} catch (IllegalArgumentException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR: ", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	class SelectAllListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {	
			for (int i = 0; i < table.getRowCount(); i++) {
				table.setValueAt(new Boolean(true), i, 0);
			}
			table.repaint();
		}
	}

	class DeselectAllListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			for (int i = 0; i < table.getRowCount(); i++) {
				table.setValueAt(new Boolean(false), i, 0);
			}
			table.repaint();
		}
	}

	class ExportPDFListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			// Check if there are unselected files
			boolean unselectedFiles = false;
			boolean selectedFiles = false;
			for (int i = 0; i < table.getRowCount(); i++) {
				if (table.getValueAt(i, 0) instanceof Boolean) {
					if ((Boolean)table.getValueAt(i, 0)) {
						selectedFiles = true;
					} else {
						unselectedFiles = true;
					}
				}
			}
			if (!selectedFiles) {
				JOptionPane.showMessageDialog(null, "At least one file must be selected!", "ERROR", JOptionPane.ERROR_MESSAGE);
				return;
			} else if (unselectedFiles) {
				JOptionPane.showMessageDialog(null, "Only selected files will appear in the PDF!", "WARNING", JOptionPane.WARNING_MESSAGE);
			}

			PdfDialog dialog = new PdfDialog(Window.this, "Export to PDF", true, ws.getName());
			String res = dialog.getPdfParameters();
			if (res != null) {
				try {
					printPDF(res.split(";")[0], res.split(";")[1]);
				} catch (FileNotFoundException | DocumentException e) {
					JOptionPane.showMessageDialog(Window.this, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
}
