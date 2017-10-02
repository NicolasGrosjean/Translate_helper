package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

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
	
	private JTextPane sourceTextPane;
	private JTextPane destTextPane;
	private JLabel destLangLabel;
	
	private static final int STRING_NOT_FOUND = -1;
	private static final char[] CK2_COLOR_CODES = {'B', 'C', 'F', 'G', 'K', 'M', 'P', 'R', 'Y', 'Z'};
	
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
		sourceTextPane = new JTextPane();
		sourceTextPane.setFont(textFont);
		new LanguageToolSupport(sourceTextPane, 
	        		new UndoRedoSupport(sourceTextPane, JLanguageTool.getMessageBundle()),
	        		Languages.getLanguageForLocale(sourceLanguage.getLocale()));
		sourceTextPane.getDocument().addDocumentListener(new TpDocumentListener(sourceTextPane));
		
		// Destination
		destTextPane = new JTextPane();
		destTextPane.setFont(textFont);
		new LanguageToolSupport(destTextPane, 
	        		new UndoRedoSupport(destTextPane, JLanguageTool.getMessageBundle()),
	        		Languages.getLanguageForLocale(destinationLanguage.getLocale()));
		destTextPane.getDocument().addDocumentListener(new TpDocumentListener(destTextPane));
		
		entry = file.getFirstEntryToTranslate();
		updateTextAreaAndTitle();
		
		JPanel sourcePan = new JPanel(new BorderLayout());
		sourcePan.add(sourceLangLabel, BorderLayout.NORTH);
		sourcePan.add(new JScrollPane(sourceTextPane), BorderLayout.CENTER);
		
		JPanel destPan = new JPanel(new BorderLayout());
		destPan.add(destLangLabel, BorderLayout.NORTH);
		destPan.add(new JScrollPane(destTextPane), BorderLayout.CENTER);
		
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
			if (!sourceTextPane.getText().equals(destTextPane.getText()))
			{
				JOptionPane.showMessageDialog(null, "Source and destination texts are different.\n" +
						"Loan words define same source and destination texts which form a correct translation",
						"ERROR", JOptionPane.ERROR_MESSAGE);
				return;
			}
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
			sourceTextPane.setText(entry.getSource());
			destTextPane.setText(entry.getDestination());
			destLangLabel.setText(destinationLanguageCode);
			setTitle(fileName + " - " + entry.getId());
			oldSourceText = entry.getSource();
			oldDestinationText = entry.getDestination();
			if (automaticGoogleCall && destTextPane.getText().equals("")) {
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
		entry.setSource(sourceTextPane.getText());
		entry.setDestination(destTextPane.getText());
	}
	
	private void callGoogleTranslate()
	{
		try {
			destTextPane.setText(google.translate(sourceTextPane.getText()));
			destLangLabel.setText(destinationLanguageCode + " (GOOGLE TRANSLATION)");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Impossible to get the translation from google",
					"ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private boolean hasChangedText()
	{
		return (!sourceTextPane.getText().equals(oldSourceText) ||
				!destTextPane.getText().equals(oldDestinationText));
	}
	
	// TODO Adapt this to other Paradox games
	private static void ck2TextColoration(JTextPane textPane)
	{
		// Erase all the colorations
		changeColor(textPane, Color.BLACK, 0, textPane.getText().length(), false);
		
		// Coloration
		// §Y...§!
		for (char color : CK2_COLOR_CODES)
		{
			colorCodes(textPane, color);
		}
		
		// \n
		int lineBreak = textPane.getText().indexOf("\\n");
		while (lineBreak != STRING_NOT_FOUND)
		{
			changeColor(textPane, Color.RED, lineBreak, 2, true);
			lineBreak = textPane.getText().indexOf("\\n", lineBreak + 1);
		}
		
		// ¤
		int wealth = textPane.getText().indexOf("¤");
		while (wealth != STRING_NOT_FOUND)
		{
			changeColor(textPane, Color.RED, wealth, 1, true);
			wealth = textPane.getText().indexOf("¤", wealth + 1);
		}
		
		// [...]
		int bracketBegin = textPane.getText().indexOf("[");
		while (bracketBegin != STRING_NOT_FOUND)
		{
			int bracketEnd = textPane.getText().indexOf("]", bracketBegin);
			if (bracketEnd == STRING_NOT_FOUND)
			{
				break;
			}
			changeColor(textPane, Color.BLUE, bracketBegin, bracketEnd - bracketBegin + 1, false);
			bracketBegin = textPane.getText().indexOf("[", bracketEnd);
		}
		
		// $...$
		int dollardBegin = textPane.getText().indexOf("$");
		while (dollardBegin != STRING_NOT_FOUND)
		{
			int dollardEnd = textPane.getText().indexOf("$", dollardBegin + 1);
			if (dollardEnd == STRING_NOT_FOUND)
			{
				break;
			}
			changeColor(textPane, Color.BLUE, dollardBegin, dollardEnd - dollardBegin + 1, false);
			dollardBegin = textPane.getText().indexOf("$", dollardEnd + 1);
		}
	}
	
	/**
	 * Color the color code of a textPane. EX : §Y...§!
	 * 
	 * @param textPane
	 * @param color : letter of the color code (in the example, Y)
	 */
	private static void colorCodes(JTextPane textPane, char color)
	{
		int colorBegin = textPane.getText().indexOf('§' + String.valueOf(color));
		while (colorBegin != STRING_NOT_FOUND)
		{
			int colorEnd = textPane.getText().indexOf("§!", colorBegin);
			if (colorEnd == STRING_NOT_FOUND)
			{
				break;
			}
			changeColor(textPane, Color.RED, colorBegin, 2, true);
			changeColor(textPane, Color.RED, colorEnd, 2, true);
			colorBegin = textPane.getText().indexOf('§' + String.valueOf(color), colorEnd);
		}
	}
	
	private static void changeColor(JTextPane tp, Color c, int beginIndex, int length, boolean bold) {
        SimpleAttributeSet aset = new SimpleAttributeSet();
        StyleConstants.setForeground(aset, c);
        StyleConstants.setBold(aset, bold);
        StyledDocument doc = (StyledDocument)tp.getDocument();
        doc.setCharacterAttributes(beginIndex, length, aset, false);
    }
	
	/**
	 * Class to color text when we modify a JTextPane
	 * 
	 * @author Nicolas
	 *
	 */
	private class TpDocumentListener implements DocumentListener {
		private JTextPane tp;
		
	    public TpDocumentListener(JTextPane tp) {
			this.tp = tp;
		}
	    
	    @Override
		public void insertUpdate(DocumentEvent e) {
	    	SwingUtilities.invokeLater(new Runnable()
	        {
	            public void run()
	            {
	            	ck2TextColoration(tp);
	            }
	        });
	    }
		
		@Override
	    public void removeUpdate(DocumentEvent e) {
	    	SwingUtilities.invokeLater(new Runnable()
	        {
	            public void run()
	            {
	            	ck2TextColoration(tp);
	            }
	        });
	    }

		@Override
		public void changedUpdate(DocumentEvent arg0) { }
	}
}
