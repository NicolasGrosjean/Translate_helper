package mains;

import javax.swing.JOptionPane;

import gui.Window;

public class Diagnostic {

	public static void main(String[] args) {
		Window window = null;
		try {
			//TODO read the configuration files

			window = new Window("Translate helper", 800, 450);
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
