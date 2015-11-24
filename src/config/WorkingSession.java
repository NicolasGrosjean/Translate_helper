package config;

import java.io.File;
import java.util.LinkedList;

import parsing.Language;

public class WorkingSession {
	/**
	 * Name of the Working session
	 */
	private String name;

	/**
	 * Path of the "localisation" directory
	 */
	private String directory;

	/**
	 * Definition of the source language
	 */
	private Language sourceLanguage;

	/**
	 * Definition of the destination language
	 */
	private Language destinationLanguage;

	/**
	 * The list of the code of available languages for all working sessions
	 */
	private static LinkedList<Language> availableLanguages = null;

	/**
	 * Create a working session.
	 * WARNING : The list of available languages must be initialized once before to do it
	 * @param name
	 * @param directory
	 * @param sourceLanguage
	 * @param destinationLanguage
	 */
	public WorkingSession(String name, String directory, Language sourceLanguage,
			Language destinationLanguage) {
		if (!isAvailableLanguagesInitialized()) {
			throw new IllegalArgumentException("The list of available languages was not initialized!");
		}
		setName(name);
		setDirectory(directory);
		setSourceLanguage(sourceLanguage);
		setDestinationLanguage(destinationLanguage);
	}

	public WorkingSession(String name, String directory, Language sourceLanguage) {
		this(name, directory, sourceLanguage, new Language());
	}

	public String getName() {
		return name;
	}

	public String getDirectory() {
		return directory;
	}

	public Language getSourceLanguage() {
		return sourceLanguage;
	}

	public Language getDestinationLanguage() {
		return destinationLanguage;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDirectory(String directory) {
		File fileDirectory = new File(directory);
		if (!fileDirectory.isDirectory()) {
			throw new IllegalArgumentException(directory + " is not a directory");
		}
		this.directory = directory;
	}

	public void setSourceLanguage(Language sourceLanguage) {
		if (WorkingSession.availableLanguages.contains(sourceLanguage)) {
			this.sourceLanguage = sourceLanguage;
		} else {
			throw new IllegalArgumentException("The source language " +
					sourceLanguage + " is not available!");
		}
	}

	public void setDestinationLanguage(Language destinationLanguage) {
		if (destinationLanguage.equals("NONE") ||
				WorkingSession.availableLanguages.contains(destinationLanguage)) {
			this.destinationLanguage = destinationLanguage;
		} else {
			throw new IllegalArgumentException("The destination language " +
					destinationLanguage + " is not available!");
		}
	}

	// TODO : parse the available languages file
	public static void setAvailableLanguages(LinkedList<Language> availableLanguages) {
		WorkingSession.availableLanguages = availableLanguages;
	}

	private static boolean isAvailableLanguagesInitialized() {
		return (WorkingSession.availableLanguages != null);
	}

	public static LinkedList<Language> getAvailableLanguages() {
		if (!isAvailableLanguagesInitialized()) {
			throw new IllegalArgumentException("The list of available languages was not initialized!");
		}
		return WorkingSession.availableLanguages;
	}
}
