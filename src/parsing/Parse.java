package parsing;

import java.util.LinkedList;

public class Parse {
	/**
	 * List of the "localisation" files
	 */
	private LinkedList<File> files;

	/**
	 * Code for the source language in the "localisation" files
	 */
	private String sourceLanguage;

	/**
	 * Default column for the text of the source language
	 */
	private int defaultSourceLanguageColumn;

	/**
	 * Code for the destination language in the "localisation" files
	 */
	private String destinationLanguage;

	/**
	 * Default column for the text of the destination language
	 */
	private int defaultDestinationLanguageColumn;

	/**
	 * Parse the files in order to store the entries which are not translated
	 * or without source language text
	 * @param fileNames The list of the names of the files to parse
	 */
	public Parse(LinkedList<String> fileNames, String sourceLanguage,
			int defaultSourceLanguageColumn, String destinationLanguage,
			int defaultDestinationLanguageColumn) {
		this.sourceLanguage = sourceLanguage;
		this.defaultSourceLanguageColumn = defaultSourceLanguageColumn;
		this.destinationLanguage = destinationLanguage;
		this.defaultDestinationLanguageColumn = defaultDestinationLanguageColumn;
		files = new LinkedList<File>();
		for (String fileName : fileNames) {
			files.addLast(parseAFile(fileName));
		}
	}

	private File parseAFile(String file) {
		// TODO
		return null;
	}
}
