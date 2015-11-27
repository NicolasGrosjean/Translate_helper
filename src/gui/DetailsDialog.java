package gui;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import parsing.ParsedFile;

public class DetailsDialog extends JDialog {
	public DetailsDialog(JFrame parent, String title, boolean modal,
			ParsedFile file) {
		super(parent, title, modal);
		setSize(500, 600);
		setLocationRelativeTo(null);

		String toDisplay = "";
		if (file.getNumberMissingSourceLines()> 0) {
			toDisplay += missingSourceTextHeader() + 
					file.getMissingSourceText() + "\n\n";
		}
		if (file.getNumberLineToTranslate() > 0) {
			toDisplay += missingTranslation() +
					file.getMissingTranslation();
		}
		JTextArea text = new JTextArea(toDisplay);
		text.setWrapStyleWord(true);
	    text.setLineWrap(true);
	    text.setEditable(false);
		getContentPane().add(new JScrollPane(text), BorderLayout.CENTER);
		setVisible(true);
	}

	private static String missingSourceTextHeader() {
		return "######################\n# MISSING SOURCE TEXT #\n"+
				"######################\n#LINE : CODE\n";
	}

	private static String missingTranslation() {
		return "######################\n# MISSING TRANSLATION #\n"+
				"######################\n#LINE : CODE\n";
	}
}
