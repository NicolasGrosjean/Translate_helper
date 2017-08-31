package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import translator.ITranslator;
import translator.TranslatedEntry;

public class TranslatorDialog extends JDialog {
	public TranslatorDialog(JFrame parent, String title, boolean modal, ITranslator file) {
		// Create the JDialog
		super(parent, title, modal);
		setSize(1000, 600);
		setLocationRelativeTo(null);

		JTextArea sourceTextArea = new JTextArea();
		sourceTextArea.setEditable(false);
		JTextArea destTextArea = new JTextArea();
		TranslatedEntry firstEntry = file.getFirstEntryToTranslate();
		if (firstEntry != null) {
			sourceTextArea.setText(firstEntry.getSource());
			destTextArea.setText(firstEntry.getDestination());
		}

		JPanel pan = new JPanel(new GridLayout(1, 2, 5, 5));
		pan.add(sourceTextArea);
		pan.add(destTextArea);
		getContentPane().add(pan);
		setVisible(true);
	}
}
