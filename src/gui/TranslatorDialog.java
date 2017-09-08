package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import parsing.Language;
import translator.ITranslator;
import translator.TranslatedEntry;

public class TranslatorDialog extends JDialog {
	private TranslatedEntry entry;
	
	private JTextArea sourceTextArea;
	private JTextArea destTextArea;
	
	public TranslatorDialog(JFrame parent, String title, boolean modal,
			ITranslator file, Language destinationLanguage) {
		// Create the JDialog
		super(parent, title, modal);
		setSize(1000, 600);
		setLocationRelativeTo(null);

		// Text areas
		sourceTextArea = new JTextArea();
		sourceTextArea.setEditable(false);
		sourceTextArea.setBackground(Color.LIGHT_GRAY);
		destTextArea = new JTextArea();
		// TODO set multi-lines
		
		entry = file.getFirstEntryToTranslate();
		updateTextArea();

		JPanel textPan = new JPanel(new GridLayout(1, 2, 5, 5));
		textPan.add(sourceTextArea);
		textPan.add(destTextArea);		
		getContentPane().add(textPan, BorderLayout.CENTER);
		
		// Bottom
		JButton loanWordBtn = new JButton("Set source as loan words");
		loanWordBtn.addActionListener(e -> file.setLoanWords(entry));
		
		JButton nextBtn = new JButton("Next entry without saving");
		nextBtn.addActionListener(e ->{ 
			entry = file.getNextEntryToTranslate();
			updateTextArea();
		});
		
		JButton nextSaveBtn = new JButton("Save this translation and go to next entry");
		nextSaveBtn.addActionListener(e -> {
			updateEntry();
			entry = file.getNextEntryToTranslateAndSave(entry, destinationLanguage);
			updateTextArea();
		});
		
		JPanel btnPan = new JPanel(new GridLayout(1, 3, 5, 5));
		btnPan.add(loanWordBtn);
		btnPan.add(nextBtn);
		btnPan.add(nextSaveBtn);
		getContentPane().add(btnPan, BorderLayout.SOUTH);
		
		setVisible(true);
	}
	
	private void updateTextArea() {
		if (entry != null) {
			sourceTextArea.setText(entry.getSource());
			destTextArea.setText(entry.getDestination());
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
