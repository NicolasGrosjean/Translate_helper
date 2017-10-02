package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
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
import translator.GoogleTranslate;
import translator.ITranslator;
import translator.TranslatedEntry;

public class TranslatorDialog extends JDialog {
	private String fileName;
	private TranslatedEntry entry;
	private GoogleTranslate google;
	private String destinationLanguageCode;
	private boolean automaticGoogleCall;
	private String oldSourceText;
	private String oldDestinationText;
	
	private JTextArea sourceTextArea;
	private JTextArea destTextArea;
	private JLabel destLangLabel;
	
	public TranslatorDialog(JFrame parent, String fileName, boolean modal,
			ITranslator file, Language sourceLanguage,
			Language destinationLanguage, boolean automaticGoogleCall) {		
		// Create the JDialog
		super(parent, "", modal);
		setSize(1000, 600);
		setLocationRelativeTo(null);
		
		this.fileName = fileName;
		this.automaticGoogleCall = automaticGoogleCall;
		google = new GoogleTranslate(sourceLanguage.getLocale().toString(),
				destinationLanguage.getLocale().toString());
		
		Font textFont = new Font(Font.SERIF, Font.PLAIN, 20);
		
		// Language labels
		JLabel sourceLangLabel = new JLabel(sourceLanguage.getCode());
		sourceLangLabel.setFont(textFont);
		destinationLanguageCode = destinationLanguage.getCode();
		destLangLabel = new JLabel(destinationLanguageCode);
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
		
		// Right
		JPanel right = new JPanel();
		JButton googleTranslateButton = new JButton();
		try {
			ImageIcon img = new ImageIcon("config/googleTranslate.jpg");
			googleTranslateButton.setIcon(img);
		} catch (Exception e) { }
		googleTranslateButton.addActionListener(e -> {
			callGoogleTranslate();
		});
		right.add(googleTranslateButton);
		getContentPane().add(right, BorderLayout.EAST);
		
		// Bottom
		JButton loanWordBtn = new JButton("Set source as loan words");
		loanWordBtn.addActionListener(e -> {
			entry.setSource(entry.getSource().replaceAll("\\p{javaSpaceChar}"," "));
			entry.setDestination(entry.getDestination().replaceAll("\\p{javaSpaceChar}"," "));
			entry = file.getNextEntryToTranslateAndSetLoanWord(entry);
			updateTextAreaAndTitle();
		});
		
		JButton nextBtn = new JButton("Next entry without saving");
		nextBtn.addActionListener(e ->{ 
			if (hasChangedText()) {
				int option = JOptionPane.showConfirmDialog(null,
						"You have changed at least one text.\n" +
								"Do you want to cancel your changes?",
								"Changed texts",
								JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
				if(option == JOptionPane.NO_OPTION ||
						option == JOptionPane.CANCEL_OPTION ||
						option == JOptionPane.CLOSED_OPTION){
					// We don't move to the next entry
					return;
				}
			}
			entry = file.getNextEntryToTranslate();
			updateTextAreaAndTitle();
		});
		
		JButton nextSaveBtn = new JButton("Save this translation and go to next entry");
		nextSaveBtn.addActionListener(e -> {
			updateEntry();
			entry.setSource(entry.getSource().replaceAll("\\p{javaSpaceChar}"," "));
			entry.setDestination(entry.getDestination().replaceAll("\\p{javaSpaceChar}"," "));
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
			destLangLabel.setText(destinationLanguageCode);
			setTitle(fileName + " - " + entry.getId());
			oldSourceText = entry.getSource();
			oldDestinationText = entry.getDestination();
			if (automaticGoogleCall && destTextArea.getText().equals("")) {
				callGoogleTranslate();
			}
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
	
	private void callGoogleTranslate()
	{
		try {
			destTextArea.setText(google.translate(sourceTextArea.getText()));
			destLangLabel.setText(destinationLanguageCode + " (GOOGLE TRANSLATION)");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Impossible to get the translation from google",
					"ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private boolean hasChangedText()
	{
		return (!sourceTextArea.getText().equals(oldSourceText) ||
				!destTextArea.getText().equals(oldDestinationText));
	}
}
