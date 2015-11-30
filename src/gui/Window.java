package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import config.WorkingSession;

public class Window extends JFrame {

	// Object container
	private JPanel container = new JPanel();

	// Menus
	private JMenuItem wsOpenRecently;

	// Configuration
	private WorkingSession ws;

	// Table
	private JTable table;
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
		String columnTitles[] = {"", "File", "Missing source text", "Translated", " "};
		Parse p = new Parse(ws);
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
		table.getColumn(columnTitles[4]).setCellEditor(new DetailsButton(new JCheckBox()));
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

	/**
	 * Use the itextpdf library to print pdf
	 * @param outputFileName
	 * @throws DocumentException 
	 * @throws FileNotFoundException 
	 */
	private void printPDF(String outputFileName) throws FileNotFoundException, DocumentException {
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(outputFileName));
		document.open();
		
		//TODO print only selected files
		//TODO manage the case where the destination language is none
		//TODO move the anchor in a forth column (details)
		//TODO add the number of elements in the details

		// Print the table
		PdfPTable pdfTable=new PdfPTable(3);
		for (int j = 1; j < table.getColumnCount() - 1; j++) {
			pdfTable.addCell(table.getColumnName(j));
		}
		for(int i=0; i< table.getRowCount() ;i++){
			// File name (= anchor, i.e internal link)
			Anchor fileName = new Anchor(table.getModel().getValueAt(i, 1).toString());
			fileName.setReference("#" + table.getModel().getValueAt(i, 1).toString());
			PdfPCell cell = new PdfPCell(fileName);
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

			// Translation percentage done
			value = table.getModel().getValueAt(i, 3);
			cell = new PdfPCell(new Phrase(value.toString()));
			iValue = (Integer)value;
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
		document.add(pdfTable);

		// New page
		document.newPage();

		// Print the details
		Font font = FontFactory.getFont("Times-Roman", 12);
		Font fontboldFile = FontFactory.getFont("Times-Roman", 20, Font.BOLD);
		Font fontboldType = FontFactory.getFont("Times-Roman", 16, Font.BOLD);
		for(int i = 0; i < table.getRowCount(); i++) {
			ParsedFile f = null;
			if (table.getValueAt(i, 1) instanceof ParsedFile) {
				f = (ParsedFile)table.getValueAt(i, 1);
			}
			if (f.getNumberLineToTranslate() > 0 || f.getNumberMissingSourceLines() > 0) {
				Anchor fileAnchor = new Anchor(":");
				fileAnchor.setName(f.getName());
				Paragraph fileName = new Paragraph(f.getName(), fontboldFile);
				fileName.add(fileAnchor);
				document.add(fileName);
			}
			if (f.getNumberMissingSourceLines() > 0) {
				document.add(new Paragraph("Missing source text", fontboldType));
				document.add(new Paragraph(f.getMissingSourceText(), font));
			}
			if (f.getNumberLineToTranslate() > 0) {
				document.add(new Paragraph("Missing translation", fontboldType));
				document.add(new Paragraph(f.getMissingTranslation(), font));
			}
		}
		document.close();
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
