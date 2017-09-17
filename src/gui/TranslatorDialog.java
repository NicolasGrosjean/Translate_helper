package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
			ITranslator file, Language sourceLanguage,
			Language destinationLanguage) {		
		// Create the JDialog
		super(parent, "", modal);
		setSize(1000, 600);
		setLocationRelativeTo(null);
		
		this.fileName = fileName;
		
		Font textFont = new Font(Font.SERIF, Font.PLAIN, 20);
		
		// Lanuguage labels
		JLabel sourceLangLabel = new JLabel(sourceLanguage.getCode());
		sourceLangLabel.setFont(textFont);
		JLabel destLangLabel = new JLabel(destinationLanguage.getCode());
		destLangLabel.setFont(textFont);		
		
		// Text areas
		sourceTextArea = new JTextArea();
		sourceTextArea.setLineWrap(true);
		sourceTextArea.setWrapStyleWord(true);
		sourceTextArea.setFont(textFont);
		 new LanguageToolSupport(sourceTextArea, 
	        		new UndoRedoSupport(sourceTextArea, JLanguageTool.getMessageBundle()),
	        		Languages.getLanguageForLocale(sourceLanguage.getLocale()));
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
		
		JPanel sourcePan = new JPanel(new BorderLayout());
		sourcePan.add(sourceLangLabel, BorderLayout.NORTH);
		sourcePan.add(new JScrollPane(sourceTextArea), BorderLayout.CENTER);
		
		JPanel destPan = new JPanel(new BorderLayout());
		destPan.add(destLangLabel, BorderLayout.NORTH);
		destPan.add(new JScrollPane(destTextArea), BorderLayout.CENTER);
		
		JPanel textPan = new JPanel(new GridLayout(1, 2, 5, 5));
		textPan.add(sourcePan);
		textPan.add(destPan);		
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
			entry = file.getNextEntryToTranslateAndSave(entry, sourceLanguage, destinationLanguage);
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
		entry.setSource(sourceTextArea.getText());
		entry.setDestination(destTextArea.getText());
	}
}
