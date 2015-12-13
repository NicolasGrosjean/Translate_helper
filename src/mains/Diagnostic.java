package mains;

import javax.swing.JOptionPane;

import config.ConfigStorage;
import config.WorkingSession;
import gui.Window;

public class Diagnostic {

	public static void main(String[] args) {
		Window window = null;
		try {
			//Read the configuration files
			WorkingSession.setAvailableLanguages("config/available_languages.csv");
			final String configurationFile = "config/config.xml";
			final String fakeTranslationFile = "config/fake_translations.txt";
			final String acceptedLoanwordFile = "config/accepted_loanwords.txt";
			ConfigStorage configuration = new ConfigStorage(configurationFile);

			// Create the window
			if (configuration.hasWorkingSession()) {
				// Try to create a window with the first working session
				WorkingSession ws;
				ws = configuration.getFirst();
				window = new Window("Translate helper", 800, 450, ws, 30, configuration, fakeTranslationFile, acceptedLoanwordFile);
			} else {
				// Create an empty window
				window = new Window("Translate helper", 800, 450, null, 30, configuration, fakeTranslationFile, acceptedLoanwordFile);
			}
		} catch (Exception e) {
			try {
				if (!e.getMessage().equals("")) {
					JOptionPane.showMessageDialog(window, e.getMessage(), "ERROR: ", JOptionPane.ERROR_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(window, "An error has occured", "ERROR: ", JOptionPane.ERROR_MESSAGE);
				}
				// Close application
				if (window != null) {
					window.dispose();
				}
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(window, "An error has occured", "ERROR: ", JOptionPane.ERROR_MESSAGE);
				// Close application
				if (window != null) {
					window.dispose();
				}
			}
		}
	}

}
