package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.languagetool.JLanguageTool;
import org.languagetool.Languages;

import jlanguagetool.LanguageToolSupport;
import jlanguagetool.UndoRedoSupport;
import parsing.Language;
import translator.ITranslator;
import translator.TranslatedEntry;

public class TranslatorDialog extends JDialog {
	private String fileName;
	private TranslatedEntry entry;
	
	private JTextArea sourceTextArea;
	private JTextArea destTextArea;
	
	public TranslatorDialog(JFrame parent, String fileName, boolean modal,
			ITranslator file, Language destinationLanguage) {		
		// Create the JDialog
		super(parent, "", modal);
		setSize(1000, 600);
		setLocationRelativeTo(null);
		
		this.fileName = fileName;

		// Text areas
		Font textFont = new Font(Font.SERIF, Font.PLAIN, 20);
		sourceTextArea = new JTextArea();
		sourceTextArea.setEditable(false);
		sourceTextArea.setBackground(Color.LIGHT_GRAY);
		sourceTextArea.setLineWrap(true);
		sourceTextArea.setWrapStyleWord(true);
		sourceTextArea.setFont(textFont);
		// Destination
		destTextArea = new JTextArea();
		destTextArea.setLineWrap(true);
		destTextArea.setWrapStyleWord(true);
		destTextArea.setFont(textFont);
		 new LanguageToolSupport(destTextArea, 
	        		new UndoRedoSupport(destTextArea, JLanguageTool.getMessageBundle()),
	        		Languages.getLanguageForLocale(destinationLanguage.getLocale()));
		
		entry = file.getFirstEntryToTranslate();
		updateTextAreaAndTitle();

		JPanel textPan = new JPanel(new GridLayout(1, 2, 5, 5));
		textPan.add(new JScrollPane(sourceTextArea));
		textPan.add(new JScrollPane(destTextArea));		
		getContentPane().add(textPan, BorderLayout.CENTER);
		
		// Bottom
		JButton loanWordBtn = new JButton("Set source as loan words");
		loanWordBtn.addActionListener(e -> {
			entry = file.getNextEntryToTranslateAndSetLoanWord(entry);
			updateTextAreaAndTitle();
		});
		
		JButton nextBtn = new JButton("Next entry without saving");
		nextBtn.addActionListener(e ->{ 
			entry = file.getNextEntryToTranslate();
			updateTextAreaAndTitle();
		});
		
		JButton nextSaveBtn = new JButton("Save this translation and go to next entry");
		nextSaveBtn.addActionListener(e -> {
			updateEntry();
			entry = file.getNextEntryToTranslateAndSave(entry, destinationLanguage);
			updateTextAreaAndTitle();
		});
		
		JPanel btnPan = new JPanel(new GridLayout(1, 3, 5, 5));
		btnPan.add(loanWordBtn);
		btnPan.add(nextBtn);
		btnPan.add(nextSaveBtn);
		getContentPane().add(btnPan, BorderLayout.SOUTH);
		
		setVisible(true);
	}
	
	private void updateTextAreaAndTitle() {
		if (entry != null) {
			sourceTextArea.setText(entry.getSource());
			destTextArea.setText(entry.getDestination());
			setTitle(fileName + " - " + entry.getId());
		} else {
			JOptionPane.showMessageDialog(null, "The translation of this file is finished",
					"File translation end", JOptionPane.INFORMATION_MESSAGE);
			setVisible(false);
		}
	}
	
	private void updateEntry()
	{
		// TODO : Update source if we want to save it
		entry.setDestination(destTextArea.getText());
	}
}
