package mains;

import javax.swing.JOptionPane;

import config.ConfigStorage;
import config.WorkingSession;
import gui.Window;
import versionning.OnlineVersionChecker;

/**
 *
 * @author GROSJEAN Nicolas (alias Mouchi)
 *
 */
public class Diagnostic {
	public static final String acceptedLoanwordFile = "config/accepted_loanwords.txt";

	public static void main(String[] args) {
		new OnlineVersionChecker();

		Window window = null;
		try {
			//Read the configuration files
			WorkingSession.setAvailableLanguages("config/available_languages.csv");
			final String configurationFile = "config/config.xml";
			final String fakeTranslationFile = "config/fake_translations.txt";
			ConfigStorage configuration = new ConfigStorage(configurationFile);

			// Create the window
			int width = 850;
			int height = 450;
			int tableRowHeight = 30;
			if (configuration.hasWorkingSession()) {
				// Try to create a window with the first working session
				WorkingSession ws;
				ws = configuration.getFirst();
				window = new Window("Translate helper", width, height, ws, tableRowHeight, configuration, fakeTranslationFile, acceptedLoanwordFile);
			} else {
				// Create an empty window
				window = new Window("Translate helper", width, height, null, tableRowHeight, configuration, fakeTranslationFile, acceptedLoanwordFile);
			}
		} catch (Exception e) {
			try {
				if (!e.getMessage().equals("")) {
					JOptionPane.showMessageDialog(window, e.getMessage(), "ERROR: ", JOptionPane.ERROR_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(window, "An error has occured", "ERROR: ", JOptionPane.ERROR_MESSAGE);
				}
				e.printStackTrace();
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
