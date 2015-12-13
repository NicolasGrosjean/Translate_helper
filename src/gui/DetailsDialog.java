package gui;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import parsing.ParsedFile;

public class DetailsDialog extends JDialog {
	public DetailsDialog(JFrame parent, String title, boolean modal,
			ParsedFile file, boolean hasDestinationLanguage) {
		// Create the JDialog
		super(parent, title, modal);
		setSize(500, 600);
		setLocationRelativeTo(null);

		// Get the text to display
		String toDisplay = "";
		if (file.getNumberMissingSourceLines()> 0) {
			toDisplay += missingSourceTextHeader(file.getNumberMissingSourceLines()) + 
					file.getMissingSourceText() + "\n\n";
		}
		if (hasDestinationLanguage && file.getNumberLineToTranslate() > 0) {
			toDisplay += missingTranslation(file.getNumberLineToTranslate()) +
					file.getMissingTranslation();
		}
		if (file.getNumberMissingSourceLines() == 0 &&
				(!hasDestinationLanguage || file.getNumberLineToTranslate() == 0)) {
			toDisplay += "No problems found.";
		}

		// Display it in a JTextArea to have multiple lines
		JTextArea text = new JTextArea(toDisplay);
		text.setWrapStyleWord(true);
	    text.setLineWrap(true);
	    text.setEditable(false);
		getContentPane().add(new JScrollPane(text), BorderLayout.CENTER);
		setVisible(true);
	}

	private static String missingSourceTextHeader(int elementNumber) {
		return "######################\n# MISSING SOURCE TEXT #\n"+
				"######################\n#LINE : CODE \t (" +
				elementNumber + " elements)\n";
	}

	private static String missingTranslation(int elementNumber) {
		return "######################\n# MISSING TRANSLATION #\n"+
				"######################\n#LINE : CODE \t (" +
				elementNumber + " elements)\n";
	}
}
