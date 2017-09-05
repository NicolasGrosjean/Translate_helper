package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import parsing.IParsedFile;
import parsing.Language;
import translator.ITranslator;
import translator.ITranslatorParsedFile;

public class DetailsDialog extends JDialog {
	public DetailsDialog(JFrame parent, String title, boolean modal,
			ITranslatorParsedFile file, boolean hasDestinationLanguage,
			Language destinationLanguage, Window window) {
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
		
		// Translate action
		if (hasDestinationLanguage) {
			JButton translateBtn = new JButton("Translate");
			translateBtn.addActionListener(new TranslateListener(title, file, destinationLanguage, window));
			getContentPane().add(translateBtn, BorderLayout.SOUTH);
		}
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
	
	class TranslateListener implements ActionListener {
		private String title;
		private ITranslator file;
		private Language destinationLanguage;
		private Window window;
		

		public TranslateListener(String title, ITranslator file,
				Language destinationLanguage, Window window) {
			this.title = title;
			this.file = file;
			this.destinationLanguage = destinationLanguage;
			this.window = window;
		}
		
		public void actionPerformed(ActionEvent event) {
			// Close this dialog
			setVisible(false);
			
			// Open a new one
			new TranslatorDialog(null, title, true, file, destinationLanguage);
			
			// Refresh working session
			window.refreshWorkingSession();
		}
	}
}
