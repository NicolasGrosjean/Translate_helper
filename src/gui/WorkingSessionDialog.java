package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import config.WorkingSession;
import parsing.Language;

public class WorkingSessionDialog extends JDialog {
	// Working session fields
	private String name;
	private String directory;

	// Gui components
	private JTextField wsName;
	private JTextField localisationDirectoryTF;
	private JComboBox<Language> sourceLanguageSourceComboBox;
	private JComboBox<Language> destinationLanguageComboBox;
	private JCheckBox automaticGoogleCall;
	private JCheckBox acceptAllCopiesCB;
	private JPanel container;
	private JPanel wsNamePanel;
	private JPanel localisationDirPanel;
	private JPanel languagePanel;
	private JPanel validCancelPanel;

	/**
	 * The user click on the validate button
	 */
	private boolean validated = false;

	/**
	 * Create a dialog frame to create/modify a working session
	 * WARNING : The available list of languages need to be initialized once
	 * @param parent
	 * @param title
	 * @param modal
	 * @param name
	 * @param directory
	 * @param sourceLanguage
	 * @param destinationLanguage
	 */
	public WorkingSessionDialog(JFrame parent, String title, boolean modal, String name,
			String directory, Language sourceLanguage, Language destinationLanguage,
			boolean automaticGoogleTranslate, boolean acceptAllCopies) {
		// Create the JDialog
		super(parent, title, modal);
		setSize(500, 450);
		setLocationRelativeTo(null);
		setResizable(false);

		// Copy working session fields
		this.name = name;
		this.directory = directory;

		// Create associated components
		wsName = new JTextField(this.name);
		localisationDirectoryTF = new JTextField(this.directory);
		// List the available languages for the source language
		Language[] avalaibleSourceLanguages = WorkingSession.getAvailableLanguages().toArray(new Language[0]);
		// The available languages for the destination language are NONE and the available source languages
		Language[] avalaibleDestinationLanguages = new Language[avalaibleSourceLanguages.length + 1];		
		avalaibleDestinationLanguages[0] = new Language();
		for (int i = 0; i < avalaibleSourceLanguages.length; i++) {
			avalaibleDestinationLanguages[i + 1] = avalaibleSourceLanguages[i];
		}
		sourceLanguageSourceComboBox = new JComboBox<Language>(avalaibleSourceLanguages);
		sourceLanguageSourceComboBox.setSelectedItem(sourceLanguage);
		destinationLanguageComboBox = new JComboBox<Language>(avalaibleDestinationLanguages);
		destinationLanguageComboBox.setSelectedItem(destinationLanguage);
		
		automaticGoogleCall = new JCheckBox("Call automatically Google Translate when destination text is empty");
		automaticGoogleCall.setSelected(automaticGoogleTranslate);
		
		acceptAllCopiesCB = new JCheckBox("Do not consider copy text as a non translation reason");
		acceptAllCopiesCB.setSelected(acceptAllCopies);

		// Create all necessary components
		JButton localisationDirectoryFC = new JButton("...");
		JButton validate = new JButton("OK");
		JButton cancel = new JButton("Cancel");

		// Group them by JPanel
		wsNamePanel = new JPanel(new BorderLayout());
		wsNamePanel.add(wsName, BorderLayout.CENTER);
		wsNamePanel.setBorder(BorderFactory.createTitledBorder("Configuration name"));
		localisationDirPanel = new JPanel(new BorderLayout());
		localisationDirPanel.add(localisationDirectoryTF, BorderLayout.CENTER);
		localisationDirPanel.add(localisationDirectoryFC, BorderLayout.EAST);
		localisationDirPanel.setBorder(BorderFactory.createTitledBorder("Localisation directory"));
		languagePanel = new JPanel(new GridLayout(1, 4, 5, 5));
		JLabel sourceLanguageLabel = new JLabel("From: ");	
		JLabel destinationLanguageLabel = new JLabel(" to : ");	
		languagePanel.add(sourceLanguageLabel);
		languagePanel.add(sourceLanguageSourceComboBox);
		languagePanel.add(destinationLanguageLabel);
		languagePanel.add(destinationLanguageComboBox);
		languagePanel.setBorder(BorderFactory.createTitledBorder("Translation"));
		JPanel googlePanel = new JPanel();
		googlePanel.setBorder(BorderFactory.createTitledBorder("Google translate"));
		googlePanel.add(automaticGoogleCall);
		JPanel copyPanel = new JPanel();
		copyPanel.setBorder(BorderFactory.createTitledBorder("Copy policy"));
		copyPanel.add(acceptAllCopiesCB);
		validCancelPanel = new JPanel();
		validCancelPanel.add(validate);
		validCancelPanel.add(cancel);

		// Add to the container
		container = new JPanel();
		container.setLayout(new GridLayout(6, 1, 5, 5));
		container.add(wsNamePanel);
		container.add(localisationDirPanel);
		container.add(languagePanel);
		container.add(googlePanel);
		container.add(copyPanel);
		container.add(validCancelPanel);
		setContentPane(container);

		// Add FileExplorer actions
		localisationDirectoryFC.addActionListener(new FileExplorer());

		// Add validate/cancel listener
		validate.addActionListener(new ValidateAction());
		cancel.addActionListener(new CancelAction());
	}

	public WorkingSessionDialog(JFrame parent, String title, boolean modal) {
		this(parent, title, modal, "", "", new Language(), new Language(), false, false);
	}

	public WorkingSessionDialog(JFrame parent, String title, boolean modal,
			WorkingSession ws) {
		this(parent, title, modal, ws.getName(), ws.getDirectory(),
				ws.getSourceLanguage(), ws.getDestinationLanguage(),
				ws.isAutomaticGoogleCall(), ws.isAcceptAllCopies());
	}

	public WorkingSession getWorkingSession() {
		// User can now interact with the dialog box
		setVisible(true);
		// When user stops use it (when isVisible == false), we return the working session
		if (!validated) {
			// The user cancels the creation of a working session
			return null;
		}
		// Use X.getItemAt(X.getSelectedIndex()) to avoid to do a cast
		return new WorkingSession(wsName.getText(), localisationDirectoryTF.getText(),
				sourceLanguageSourceComboBox.getItemAt(sourceLanguageSourceComboBox.getSelectedIndex()),
				destinationLanguageComboBox.getItemAt(destinationLanguageComboBox.getSelectedIndex()),
				automaticGoogleCall.isSelected(), acceptAllCopiesCB.isSelected());
	}

	class FileExplorer implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			// Use the look and feel of the system for fileChooser
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			catch (InstantiationException e) {}
			catch (ClassNotFoundException e) {}
			catch (UnsupportedLookAndFeelException e) {}
			catch (IllegalAccessException e) {}
			
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (!localisationDirectoryTF.getText().equals("")) {
				fileChooser.setSelectedFile(new File(localisationDirectoryTF.getText()));
			} 
			if (fileChooser.showOpenDialog(WorkingSessionDialog.this) == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				// We open fileChooser in the last selected directory
				fileChooser.setCurrentDirectory(fileChooser.getSelectedFile());
				// Fill the JTextField
				localisationDirectoryTF.setText(file.toString());
			}

			// Restore look and feel
			Window.setLookAndFeel("Nimbus");
		}
	}

	class ValidateAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (wsName.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "The name of this configuration is missing", "ERROR", JOptionPane.ERROR_MESSAGE);
			} else if (localisationDirectoryTF.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "The localisation directory is missing", "ERROR", JOptionPane.ERROR_MESSAGE);
			} else if (sourceLanguageSourceComboBox.getSelectedItem() == destinationLanguageComboBox.getSelectedItem()) {
				JOptionPane.showMessageDialog(null, "The source and destination languages must be different.\n"
						+ "Use " + Language.defaultLanguageCode +
						" as destination language if you are not interested by the translation", "ERROR", JOptionPane.ERROR_MESSAGE);
			} else {
				validated = true;
				setVisible(false);
			}
		}
	}

	class CancelAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			setVisible(false);
		}
	}
}
