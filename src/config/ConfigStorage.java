package config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import parsing.Language;

public class ConfigStorage {
	private static final String wsNameAttribute = "name";
	private static final String wsDirectoryAttribute = "directory";
	private static final String wsSourceLanguageAttribute = "source";
	private static final String wsDestinationLanguageAttribute = "destination";

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
		while (it.hasNext()) {
			Element wsElem = it.next();
			//			List<Element> modDirectoriesElem = wsElem.getChildren();
			//			LinkedList<String> modDirectories = new LinkedList<String>();
			//			for (Element modDirElem : modDirectoriesElem) {
			//				modDirectories.addFirst(modDirElem.getText());
			//			}
			workingSessions.addLast(new WorkingSession(wsElem.getAttributeValue(wsNameAttribute),
					wsElem.getAttributeValue(wsDirectoryAttribute),
					new Language(wsElem.getAttributeValue(wsSourceLanguageAttribute),
							WorkingSession.getLanguageDefaultColumn(wsElem.getAttributeValue(wsSourceLanguageAttribute))),
					new Language(wsElem.getAttributeValue(wsDestinationLanguageAttribute),
							WorkingSession.getLanguageDefaultColumn(wsElem.getAttributeValue(wsDestinationLanguageAttribute)))));
		}
	}

	/**
	 * Transform a working session to the corresponding Element
	 * @param ws
	 */
	private Element workingSessionToElement(WorkingSession ws) {
		// Create a working session XML element
		Element workingSessionElem = new Element("workingSession");
		workingSessionElem.setAttribute(new Attribute(wsNameAttribute, ws.getName()));
		workingSessionElem.setAttribute(new Attribute(wsDirectoryAttribute, ws.getDirectory()));
		workingSessionElem.setAttribute(new Attribute(wsSourceLanguageAttribute, ws.getSourceLanguage().getCode()));
		workingSessionElem.setAttribute(new Attribute(wsDestinationLanguageAttribute, ws.getDestinationLanguage().getCode()));
		return workingSessionElem;
	}

	/**
	 * Say if there are working sessions in the configuration storage
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
		// TODO : Manage the case where the list is empty
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
			new XMLOutputter(Format.getPrettyFormat()).output(new Document(root), new FileOutputStream(configurationFile));
		} catch (java.io.IOException e) {
			throw new IllegalArgumentException("ERROR : problem to write the configuration file named "
					+ configurationFile);
		}
	}
}
