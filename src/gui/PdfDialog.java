package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

public class PdfDialog extends JDialog {
	private JTextField titleTF;
	private JTextField fileTF;
	private JPanel container;
	private JPanel titlePanel;
	private JPanel filePanel;
	private JPanel validCancelPanel;

	/**
	 * The user click on the validate button
	 */
	private boolean validated = false;

	public PdfDialog(JFrame parent, String title, boolean modal, String wsName) {
		// Create the JDialog
		super(parent, title, modal);
		setSize(350, 250);
		setLocationRelativeTo(null);
		setResizable(false);

		// Create value components
		titleTF = new JTextField("Diagnostic " + wsName);
		fileTF = new JTextField();

		// Create other components
		JButton localisationDirectoryFC = new JButton("...");
		JButton validate = new JButton("OK");
		JButton cancel = new JButton("Cancel");

		// Group them by JPanel
		titlePanel = new JPanel(new BorderLayout());
		titlePanel.add(titleTF, BorderLayout.CENTER);
		titlePanel.setBorder(BorderFactory.createTitledBorder("Report title"));
		filePanel = new JPanel(new BorderLayout());
		filePanel.add(fileTF, BorderLayout.CENTER);
		filePanel.add(localisationDirectoryFC, BorderLayout.EAST);
		filePanel.setBorder(BorderFactory.createTitledBorder("Report file"));
		validCancelPanel = new JPanel();
		validCancelPanel.add(validate);
		validCancelPanel.add(cancel);

		// Add to the container
		container = new JPanel();
		container.setLayout(new GridLayout(3, 1, 5, 5));
		container.add(titlePanel);
		container.add(filePanel);
		container.add(validCancelPanel);		
		setContentPane(container);

		// Add FileExplorer actions
		localisationDirectoryFC.addActionListener(new FileExplorer());

		// Add validate/cancel listener
		validate.addActionListener(new ValidateAction());
		cancel.addActionListener(new CancelAction());
	}

	public String getPdfParameters() {
		// User can now interact with the dialog box
		setVisible(true);
		// When user stops use it (when isVisible == false), we return the working session
		if (!validated) {
			// The user cancels the creation of a working session
			return null;
		}
		return titleTF.getText() + ";" + fileTF.getText();
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
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY); // TODO : debug this line
			fileChooser.setFileFilter(new FileNameExtensionFilter("Fichiers PDF", "pdf"));
			if (!fileTF.getText().equals("")) {
				fileChooser.setSelectedFile(new File(fileTF.getText()));
			} 
			if (fileChooser.showOpenDialog(PdfDialog.this) == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				// We open fileChooser in the last selected directory
				fileChooser.setCurrentDirectory(fileChooser.getSelectedFile());
				// Fill the JTextField
				fileTF.setText(file.toString());
			}

			// Restore look and feel
			Window.setLookAndFeel("Nimbus");
		}
	}

	class ValidateAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (titleTF.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "The title of the report is missing", "ERROR", JOptionPane.ERROR_MESSAGE);
			} else if (fileTF.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "The report file is missing", "ERROR", JOptionPane.ERROR_MESSAGE);
			} else {
				// Manage the case of the already existing file
				File f = new File(fileTF.getText());
				if (f.isFile()) {
					int option = JOptionPane.showConfirmDialog(null,
							"The file " + fileTF.getText() + " already exists.\n" +
									"Are you sure to overwrite it?", "Existing file",
									JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
					if(option == JOptionPane.NO_OPTION ||
							option == JOptionPane.CANCEL_OPTION ||
							option == JOptionPane.CLOSED_OPTION){
						// The user doesn't want overwrite the file
						return;
					}
				}

				validated = true;
				// Add the file extension if necessary
				if (!fileTF.getText().contains(".pdf")) {
					fileTF.setText(fileTF.getText() + ".pdf");
				}
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
