package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

import org.languagetool.JLanguageTool;
import org.languagetool.Languages;

import com.google.common.primitives.Chars;

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
	private JLabel sourceLangLabel;
	private JLabel destLangLabel;
	
	private static final int STRING_NOT_FOUND = -1;
	private static final char[] PARADOX_COLOR_CODES = {'b', 'B', 'C', 'F', 'g', 'H', 'G', 'K', 'l',
			'M', 'P', 'R', 'T', 'W', 'Y', 'Z', '+', '-'};
	
	public TranslatorDialog(JFrame parent, String fileName, boolean modal,
			ITranslator file, Language sourceLanguage,
			Language destinationLanguage, boolean automaticGoogleCall) {		
		// Create the JDialog
		super(parent, "", modal);
		setSize(1150, 620);
		setLocationRelativeTo(null);
		
		this.fileName = fileName;
		this.automaticGoogleCall = automaticGoogleCall;
		google = new GoogleTranslate(sourceLanguage.getLocale().toString(),
				destinationLanguage.getLocale().toString());
		
		Font textFont = new Font(Font.SERIF, Font.PLAIN, 20);
		
		// Language labels
		sourceLangLabel = new JLabel(sourceLanguage.getCode());
		sourceLangLabel.setFont(textFont);
		destinationLanguageCode = destinationLanguage.getCode();
		destLangLabel = new JLabel(destinationLanguageCode);
		destLangLabel.setFont(textFont);		
		
		// Text areas
		sourceTextPane = new JTextPane();
		sourceTextPane.setFont(textFont);
		sourceTextPane.setEditorKit(new MyStyledEditorKit());
		new LanguageToolSupport(sourceTextPane, 
	        		new UndoRedoSupport(sourceTextPane, JLanguageTool.getMessageBundle()),
	        		Languages.getLanguageForLocale(sourceLanguage.getLocale()));
		sourceTextPane.getDocument().addDocumentListener(new TpDocumentListener(sourceTextPane));
		
		// Destination
		destTextPane = new JTextPane();
		destTextPane.setFont(textFont);
		destTextPane.setEditorKit(new MyStyledEditorKit());
		new LanguageToolSupport(destTextPane, 
	        		new UndoRedoSupport(destTextPane, JLanguageTool.getMessageBundle()),
	        		Languages.getLanguageForLocale(destinationLanguage.getLocale()));
		destTextPane.getDocument().addDocumentListener(new TpDocumentListener(destTextPane));
		
		entry = file.getFirstEntryToTranslate();
		updateTextAreaAndTitle();

		JButton sourceCopyBtn = new JButton();
		sourceCopyBtn.setToolTipText("Copy the source text into destination");
		try {
			ImageIcon img = new ImageIcon("config/copySourceButton.png");
			sourceCopyBtn.setIcon(img);
		} catch (Exception e) { }
		sourceCopyBtn.addActionListener(e -> {
			if (!"".equals(destTextPane.getText())) {
				int option = JOptionPane.showConfirmDialog(null,
						"Are you sure to want erase the destination text by copying the source text ?\n"
						+ "If you don't want change the destination text after, set source as loan words,\n"
						+ "to tell the sotware that the translation is correct",
						"Copy source text",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
				if (option == JOptionPane.NO_OPTION ||
						option == JOptionPane.CANCEL_OPTION ||
						option == JOptionPane.CLOSED_OPTION){
					return;
				}
			}
			destTextPane.setText(sourceTextPane.getText());
		});
		JButton sourceResetBtn = new JButton("Reset");
		sourceResetBtn.setToolTipText("Reset the source text");
		sourceResetBtn.addActionListener(e -> {
			sourceTextPane.setText(entry.getSource());
		});
		
		JPanel sourceButtonPan = new JPanel(new BorderLayout());
		sourceButtonPan.add(sourceResetBtn, BorderLayout.WEST);
		sourceButtonPan.add(sourceCopyBtn, BorderLayout.EAST);		
		JPanel sourceLangAndButtonPan = new JPanel(new BorderLayout());
		sourceLangAndButtonPan.add(sourceLangLabel);
		sourceLangAndButtonPan.add(sourceButtonPan, BorderLayout.EAST);
		JPanel sourcePan = new JPanel(new BorderLayout());
		sourcePan.add(sourceLangAndButtonPan, BorderLayout.NORTH);
		sourcePan.add(new JScrollPane(sourceTextPane), BorderLayout.CENTER);		
		
		JPanel destPan = new JPanel(new BorderLayout());
		destPan.add(destLangLabel, BorderLayout.NORTH);
		destPan.add(new JScrollPane(destTextPane), BorderLayout.CENTER);
		
		JPanel textPan = new JPanel(new GridLayout(1, 2, 5, 5));
		textPan.add(sourcePan);
		textPan.add(destPan);		
		getContentPane().add(textPan, BorderLayout.CENTER);
		
		// Right
		JPanel right = new JPanel(new GridLayout(13, 1, 5, 0));
		JButton googleTranslateButton = new JButton();
		googleTranslateButton.setToolTipText("Replace the translation text by the google translation");
		try {
			ImageIcon img = new ImageIcon("config/googleTranslate.jpg");
			googleTranslateButton.setIcon(img);
		} catch (Exception e) { }
		googleTranslateButton.addActionListener(e -> {
			callGoogleTranslate();
		});
		right.add(googleTranslateButton);
		
		JButton copyBtn = new JButton();
		copyBtn.setToolTipText("Copy the translation text into your clipboard");
		try {
			ImageIcon img = new ImageIcon("config/copyButton.png");
			copyBtn.setIcon(img);
		} catch (Exception e) { }
		copyBtn.addActionListener(e -> {
			StringSelection selection = new StringSelection(destTextPane.getText());
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(selection, selection);
		});
		right.add(copyBtn);
		

		JButton destResetBtn = new JButton("Reset");
		destResetBtn.setToolTipText("Reset the translation text");
		destResetBtn.addActionListener(e -> {
			destTextPane.setText(entry.getDestination());
		});
		right.add(destResetBtn);
		getContentPane().add(right, BorderLayout.EAST);
		
		// Bottom
		JButton loanWordBtn = new JButton("Set source as loan words");
		loanWordBtn.setMnemonic(KeyEvent.VK_L);
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
		
		JButton prevBtn = new JButton("Previous entry without saving");
		prevBtn.setMnemonic(KeyEvent.VK_P);
		prevBtn.addActionListener(e ->{ 
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
			TranslatedEntry prevEntry = file.getPreviousEntryToTranslate();
			if (prevEntry == null) {
				JOptionPane.showMessageDialog(null, "No previous entry!",
						"ERROR", JOptionPane.ERROR_MESSAGE);
			}
			else {
				entry = prevEntry;
				updateTextAreaAndTitle();
			}
		});
		
		JButton goToBtn = new JButton("Go to entry without saving");
		goToBtn.setMnemonic(KeyEvent.VK_G);
		goToBtn.addActionListener(e ->{ 
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
			String sLineNumber = JOptionPane.showInputDialog(this, "Line number in the destination to go",
					"Go to line", JOptionPane.QUESTION_MESSAGE);
			int lineNumber;
			try {
				lineNumber = Integer.parseInt(sLineNumber);
				entry = file.getEntryToTranslate(lineNumber);
				updateTextAreaAndTitle();
			} catch (Exception exception) {
				JOptionPane.showMessageDialog(null, "Impossible to go to the entry.\n" + exception.getMessage(),
						"ERROR", JOptionPane.ERROR_MESSAGE);
			}
		});
		
		JButton nextBtn = new JButton("Next entry without saving");
		nextBtn.setMnemonic(KeyEvent.VK_N);
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
		nextSaveBtn.setMnemonic(KeyEvent.VK_S);
		nextSaveBtn.addActionListener(e -> {
			updateEntry();
			entry.setSource(entry.getSource().replaceAll("\\p{javaSpaceChar}"," "));
			entry.setDestination(entry.getDestination().replaceAll("\\p{javaSpaceChar}"," "));
			entry = file.getNextEntryToTranslateAndSave(entry, sourceLanguage, destinationLanguage);
			updateTextAreaAndTitle();
		});
		
		JPanel southPan = new JPanel(new GridLayout(1, 3, 0, 5));
		JPanel btnPan1 = new JPanel(new GridLayout(1, 2, 0, 5));
		btnPan1.add(prevBtn);
		btnPan1.add(goToBtn);
		JPanel btnPan2 = new JPanel(new GridLayout(1, 2, 0, 5));
		btnPan2.add(nextBtn);
		btnPan2.add(loanWordBtn);
		southPan.add(btnPan1);
		southPan.add(btnPan2);
		southPan.add(nextSaveBtn);
		getContentPane().add(southPan, BorderLayout.SOUTH);
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent we)
		    { 
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
				dispose();
		    }
		});
		
		setVisible(true);
	}
	
	private void updateTextAreaAndTitle() {
		if (entry != null) {
			sourceTextPane.setText(entry.getSource());
			destTextPane.setText(entry.getDestination());
			destLangLabel.setText(destinationLanguageCode);
			setTitle(fileName + " - " + entry.getId() + " (line " + entry.getDestLineNumber() + ")");
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
		if ("".equals(sourceTextPane.getText())) {
			return;
		}
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
	private static void textColoration(JTextPane textPane)
	{
		// Erase all the colorations
		changeColor(textPane, Color.BLACK, 0, textPane.getText().length(), false);
		
		// Coloration
		// �Y...�! (CK2, EU4, HoI4, Stellaris)
		colorCodes(textPane, "�");
		// #Y...#! (Imperator Rome)
		colorCodes(textPane, "#");
		
		// \n
		int lineBreak = textPane.getText().indexOf("\\n");
		while (lineBreak != STRING_NOT_FOUND)
		{
			changeColor(textPane, Color.RED, lineBreak, 2, true);
			lineBreak = textPane.getText().indexOf("\\n", lineBreak + 1);
		}
		
		// �
		int wealth = textPane.getText().indexOf("�");
		while (wealth != STRING_NOT_FOUND)
		{
			changeColor(textPane, Color.RED, wealth, 1, true);
			wealth = textPane.getText().indexOf("�", wealth + 1);
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
	 * Color the color code of a textPane. EX : �Y...�! when codeChar == "�"
	 * 
	 * @param textPane
	 * @param codeChar
	 */
	private static void colorCodes(JTextPane textPane, String codeChar)
	{
		int colorBegin = textPane.getText().indexOf(codeChar);
		int colorEnd = STRING_NOT_FOUND;
		while (colorBegin != STRING_NOT_FOUND)
		{
			if (!Chars.contains(PARADOX_COLOR_CODES, textPane.getText().charAt(colorBegin + 1))) {
				colorBegin = textPane.getText().indexOf(codeChar, colorBegin + 1);
			}
			if (colorEnd == STRING_NOT_FOUND) {
				colorEnd = colorBegin + textPane.getText().substring(colorBegin).lastIndexOf(codeChar + "!");
			} else {
				colorEnd = textPane.getText().lastIndexOf(codeChar + "!", colorEnd - 1);
			}
			if (colorEnd == STRING_NOT_FOUND)
			{
				break;
			}
			changeColor(textPane, Color.RED, colorBegin, 2, true);
			changeColor(textPane, Color.RED, colorEnd, 2, true);
			colorBegin = textPane.getText().indexOf(codeChar, colorBegin + 1);
		}
	}
	
	private static void changeColor(JTextPane tp, Color c, int beginIndex, int length, boolean bold) {
        SimpleAttributeSet aset = new SimpleAttributeSet();
        StyleConstants.setForeground(aset, c);
        StyleConstants.setBold(aset, bold);
        StyledDocument doc = (StyledDocument)tp.getDocument();
        doc.setCharacterAttributes(beginIndex, length, aset, false);
    }
	
	private static void setCharNb(JLabel label, int nbChar)
	{
			label.setText(label.getText().split(" - ")[0] + " - (" + nbChar +" char" + 
							((nbChar != 1) ? "s" : "") + ")");
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
	            	textColoration(tp);
	            	setCharNb(sourceLangLabel, sourceTextPane.getText().length());
	            	setCharNb(destLangLabel, destTextPane.getText().length());
	            }
	        });
	    }
		
		@Override
	    public void removeUpdate(DocumentEvent e) {
	    	SwingUtilities.invokeLater(new Runnable()
	        {
	            public void run()
	            {
	            	textColoration(tp);
	            	setCharNb(sourceLangLabel, sourceTextPane.getText().length());
	            	setCharNb(destLangLabel, destTextPane.getText().length());
	            }
	        });
	    }

		@Override
		public void changedUpdate(DocumentEvent arg0) { }
	}
	
	
	
	
	
	
	/**
	 * Correct the JTextPane wrapping with DocumentListener
	 * @author StanislavL (https://stackoverflow.com/a/14230668)
	 *
	 */
	
	class MyStyledEditorKit extends StyledEditorKit {
	    private MyFactory factory;

	    public ViewFactory getViewFactory() {
	        if (factory == null) {
	            factory = new MyFactory();
	        }
	        return factory;
	    }
	}

	class MyFactory implements ViewFactory {
	    public View create(Element elem) {
	        String kind = elem.getName();
	        if (kind != null) {
	            if (kind.equals(AbstractDocument.ContentElementName)) {
	                return new MyLabelView(elem);
	            } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
	                return new MyParagraphView(elem);
	            } else if (kind.equals(AbstractDocument.SectionElementName)) {
	                return new BoxView(elem, View.Y_AXIS);
	            } else if (kind.equals(StyleConstants.ComponentElementName)) {
	                return new ComponentView(elem);
	            } else if (kind.equals(StyleConstants.IconElementName)) {
	                return new IconView(elem);
	            }
	        }

	        // default to text display
	        return new LabelView(elem);
	    }
	}

	class MyParagraphView extends ParagraphView {

	    public MyParagraphView(Element elem) {
	        super(elem);
	    }
	public void removeUpdate(DocumentEvent e, Shape a, ViewFactory f) {
	    super.removeUpdate(e, a, f);
	    resetBreakSpots();
	}
	public void insertUpdate(DocumentEvent e, Shape a, ViewFactory f) {
	    super.insertUpdate(e, a, f);
	    resetBreakSpots();
	}

	private void resetBreakSpots() {
	    for (int i=0; i<layoutPool.getViewCount(); i++) {
	        View v=layoutPool.getView(i);
	        if (v instanceof MyLabelView) {
	            ((MyLabelView)v).resetBreakSpots();
	        }
	    }
	}

	}

	class MyLabelView extends LabelView {

	    boolean isResetBreakSpots=false;

	    public MyLabelView(Element elem) {
	        super(elem);
	    }
	    public View breakView(int axis, int p0, float pos, float len) {
	        if (axis == View.X_AXIS) {
	            resetBreakSpots();
	        }
	        return super.breakView(axis, p0, pos, len);
	    }

	    public void resetBreakSpots() {
	        isResetBreakSpots=true;
	        removeUpdate(null, null, null);
	        isResetBreakSpots=false;
	   }

	    public void removeUpdate(DocumentEvent e, Shape a, ViewFactory f) {
	        super.removeUpdate(e, a, f);
	    }

	    public void preferenceChanged(View child, boolean width, boolean height) {
	        if (!isResetBreakSpots) {
	            super.preferenceChanged(child, width, height);
	        }
	    }
	}
}
