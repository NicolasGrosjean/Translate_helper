package config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import gui.WorkingSessionDialog;

public class ConfigStorage {
	private static final String wsNameAttribute = "name";
	private static final String wsDirectoryAttribute = "directory";
	private static final String wsSourceLanguageAttribute = "source";
	private static final String wsDestinationLanguageAttribute = "destination";
	private static final String wsAutomaticDeepLAttribute = "automaticDeepL";
	private static final String wsAutomaticGoogleAttribute = "automaticGoogle";
	private static final String wsAcceptAllCopies = "acceptAllCopies";

	/**
	 * List of working sessions
	 */
	private LinkedList<WorkingSession> workingSessions = new LinkedList<WorkingSession>();

	/**
	 * File where is loaded or saved the configuration of the software
	 */
	private final String configurationFile;

	/**
	 * Construct a configuration storage from a XML file named configFile
	 * 
	 * @param configFile
	 */
	public ConfigStorage(String configurationFile) {
		this.configurationFile = configurationFile;
		Element root = null;
		try {
			// Build the XML tree and gather the root
			root = new SAXBuilder().build(new File(configurationFile)).getRootElement();
		} catch (JDOMException | IOException e) {
			// No configuration file
			return;
		}
		// List of working session
		List<Element> workingSessionsElem = root.getChildren();
		Iterator<Element> it = workingSessionsElem.iterator();
		boolean modified = false;
		while (it.hasNext()) {
			Element wsElem = it.next();
			String automaticDeepLTranslate = wsElem.getAttributeValue(wsAutomaticDeepLAttribute);
			String automaticGoogleTranslate = wsElem.getAttributeValue(wsAutomaticGoogleAttribute);
			String acceptAllCopies = wsElem.getAttributeValue(wsAcceptAllCopies);
			WorkingSession ws = new WorkingSession(wsElem.getAttributeValue(wsNameAttribute),
						wsElem.getAttributeValue(wsDirectoryAttribute),
						WorkingSession.getLanguage(wsElem.getAttributeValue(wsSourceLanguageAttribute)),
						WorkingSession.getLanguage(wsElem.getAttributeValue(wsDestinationLanguageAttribute)),
						(automaticDeepLTranslate != null) ? Boolean.valueOf(automaticDeepLTranslate) : false,
						(automaticGoogleTranslate != null) ? Boolean.valueOf(automaticGoogleTranslate) : false,
						(acceptAllCopies != null) ? Boolean.valueOf(acceptAllCopies) : false);
			if (!ws.getErrorMessage().equals("")) {
				int option = JOptionPane.showConfirmDialog(null,
						"Error with the working session " + ((ws != null) ? ws.getName() : "")
								+ ". Do you want modify it ?", "Working session problem",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
				if (option != JOptionPane.NO_OPTION && option != JOptionPane.CANCEL_OPTION
						&& option != JOptionPane.CLOSED_OPTION) {
					WorkingSessionDialog wSDialog = new WorkingSessionDialog(new JFrame(), "Modify configuration", true, ws);
					ws = wSDialog.getWorkingSession();
					modified = true;
				} else {
					ws = null;
				}
			}
			if (ws != null) {
				workingSessions.addLast(ws);
			}
		}
		if (modified)
		{
			saveConfigFile();
		}
	}

	/**
	 * Transform a working session to the corresponding Element
	 * 
	 * @param ws
	 */
	private Element workingSessionToElement(WorkingSession ws) {
		// Create a working session XML element
		Element workingSessionElem = new Element("workingSession");
		workingSessionElem.setAttribute(new Attribute(wsNameAttribute, ws.getName()));
		workingSessionElem.setAttribute(new Attribute(wsDirectoryAttribute, ws.getDirectory()));
		workingSessionElem.setAttribute(new Attribute(wsSourceLanguageAttribute, ws.getSourceLanguage().getCode()));
		workingSessionElem
				.setAttribute(new Attribute(wsDestinationLanguageAttribute, ws.getDestinationLanguage().getCode()));
		workingSessionElem
				.setAttribute(new Attribute(wsAutomaticDeepLAttribute, Boolean.toString(ws.isAutomaticDeepLCall())));
		workingSessionElem
				.setAttribute(new Attribute(wsAutomaticGoogleAttribute, Boolean.toString(ws.isAutomaticGoogleCall())));
		workingSessionElem
			.setAttribute(new Attribute(wsAcceptAllCopies, Boolean.toString(ws.isAcceptAllCopies())));
		return workingSessionElem;
	}

	/**
	 * Say if there are working sessions in the configuration storage
	 * 
	 * @return
	 */
	public boolean hasWorkingSession() {
		return !workingSessions.isEmpty();
	}

	public WorkingSession getFirst() {
		return workingSessions.getFirst();
	}

	public void addFirstWorkingSession(WorkingSession ws) {
		workingSessions.addFirst(ws);
	}

	public void replaceFirstWorkingSession(WorkingSession ws) {
		workingSessions.removeFirst();
		workingSessions.addFirst(ws);
	}

	public int getSize() {
		return workingSessions.size();
	}

	public Iterator<WorkingSession> iterator() {
		return workingSessions.iterator();
	}

	/**
	 * The working session becomes the first of working session list
	 * 
	 * @param ws
	 */
	public void becomeFirst(WorkingSession ws) {
		if (workingSessions.remove(ws)) {
			addFirstWorkingSession(ws);
		} else {
			throw new IllegalArgumentException("ERROR : the working session was not in the list");
		}
	}

	/**
	 * Save the configuration file
	 * 
	 * @param configurationFile
	 */
	public void saveConfigFile() {
		// Create the root of XML file with the language attribute
		Element root = new Element("translateHelper");

		// Adding the working session in order
		Iterator<WorkingSession> it = workingSessions.iterator();
		while (it.hasNext()) {
			root.addContent(workingSessionToElement(it.next()));
		}

		// Write the configuration file
		try {
			new XMLOutputter(Format.getPrettyFormat()).output(new Document(root),
					new FileOutputStream(configurationFile));
		} catch (java.io.IOException e) {
			throw new IllegalArgumentException(
					"ERROR : problem to write the configuration file named " + configurationFile);
		}
	}
}
