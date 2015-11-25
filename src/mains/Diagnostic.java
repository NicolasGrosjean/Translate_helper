package mains;

import javax.swing.JOptionPane;

import parsing.Language;
import config.WorkingSession;
import gui.Window;

public class Diagnostic {

	public static void main(String[] args) {
		Window window = null;
		try {
			//TODO read the configuration files
			WorkingSession.setAvailableLanguages("C:/Users/Nicolas/workspace/Translate_helper/config/available_languages.csv");
			WorkingSession ws = new WorkingSession("TEST",
					"C:/Users/Nicolas/Documents/GitHub/L3T/L3T/localisation",
					new Language("FRENCH", 2), new Language("ENGLISH", 1));
			window = new Window("Translate helper", 800, 450, ws, 30);
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
