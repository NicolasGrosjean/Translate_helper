package config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Pattern;

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
	 * Call automatically Google translate when the destination text is empty
	 */
	private boolean automaticGoogleCall;

	private String errorMessage;
	
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
			Language destinationLanguage, boolean automaticGoogleCall) {
		if (!isAvailableLanguagesInitialized()) {
			throw new IllegalArgumentException("The list of available languages was not initialized!");
		}
		StringBuilder errorMessage = new StringBuilder();
		setName(name);
		try {
			setDirectory(directory);
		} catch (RuntimeException e) {
			this.directory = directory;
			errorMessage.append(e.getMessage() + System.lineSeparator());
		}
		try {
			setSourceLanguage(sourceLanguage);
		} catch (RuntimeException e) {
			errorMessage.append(e.getMessage() + System.lineSeparator());
		}
		try {
			setDestinationLanguage(destinationLanguage);
		} catch (

		RuntimeException e) {
			errorMessage.append(e.getMessage() + System.lineSeparator());
		}
		setAutomaticGoogleCall(automaticGoogleCall);
		
		this.errorMessage = errorMessage.toString();
	}

	public WorkingSession(String name, String directory, Language sourceLanguage,
			boolean automaticGoogleCall) {
		this(name, directory, sourceLanguage, new Language(), automaticGoogleCall);
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
		if (destinationLanguage.isNone() ||
				WorkingSession.availableLanguages.contains(destinationLanguage)) {
			this.destinationLanguage = destinationLanguage;
		} else {
			throw new IllegalArgumentException("The destination language " +
					destinationLanguage + " is not available!");
		}
	}

	public boolean isAutomaticGoogleCall() {
		return automaticGoogleCall;
	}

	public void setAutomaticGoogleCall(boolean automaticGoogleCall) {
		this.automaticGoogleCall = automaticGoogleCall;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Parse a file to set the list of available languages
	 * @param availableLanguagesFile
	 */
	public static void setAvailableLanguages(String availableLanguagesFile) {
		availableLanguages = new LinkedList<Language>();
		File file = new File(availableLanguagesFile);
		if (!file.isFile()) {
			throw new IllegalArgumentException(availableLanguagesFile +
					" is not a file!");
		}
		FileInputStream f = null;
		Scanner scanner = null;
		try {
			f = new FileInputStream(availableLanguagesFile);
			scanner = new Scanner(f, "ISO-8859-1");
			scanner.useDelimiter(Pattern.compile("[;\r\n]"));
			while (scanner.hasNextInt()) {
				int defaultColumnNumber = scanner.nextInt();
				if (!scanner.hasNext()) {
					throw new IllegalArgumentException("Code for language missing.");
				}
				String code = scanner.next();
				if (!scanner.hasNext()) {
					throw new IllegalArgumentException("ISO 639 code for language missing.");
				}
				String isoCode = scanner.next();
				availableLanguages.addLast(new Language(code, defaultColumnNumber, isoCode));
				// If the line terminate by a ';', we skip it by read the empty string
				while (!scanner.hasNextInt() && scanner.hasNext()) {
					String s = scanner.next();
					if (!s.equals("")) {
						throw new IllegalArgumentException("Invalid string found : " + s);
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (scanner != null)
				scanner.close();
			try {
				if (f != null)
					f.close();
			} catch (IOException e) {
				throw new IllegalArgumentException(e.getMessage());
			}
		}
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

	public static Language getLanguage(String language) {
		if (!isAvailableLanguagesInitialized()) {
			throw new IllegalArgumentException("The list of available languages was not initialized!");
		}
		if (language.equals(Language.defaultLanguageCode)) {
			return new Language();
		}
		for (Language l : WorkingSession.availableLanguages) {
			if (l.getCode().equals(language)) {
				return l;
			}
		}
		throw new IllegalArgumentException("Language " + language + " is not available");
	}
}
