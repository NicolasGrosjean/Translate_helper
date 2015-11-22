package parsing;

import java.io.File;
import java.util.LinkedList;

public class Parse {
	/**
	 * List of the "localisation" files
	 */
	private LinkedList<ParsedFile> files;

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
	 * @param filePaths The list of the names of the files to parse
	 */
	public Parse(LinkedList<String> filePaths, String sourceLanguage,
			int defaultSourceLanguageColumn, String destinationLanguage,
			int defaultDestinationLanguageColumn) {
		this.sourceLanguage = sourceLanguage;
		this.defaultSourceLanguageColumn = defaultSourceLanguageColumn;
		this.destinationLanguage = destinationLanguage;
		this.defaultDestinationLanguageColumn = defaultDestinationLanguageColumn;
		files = new LinkedList<ParsedFile>();
		for (String filePath : filePaths) {
			files.addLast(parseAFile(filePath));
		}
	}

	public ParsedFile removeFirstFile() {
		return files.removeFirst();
	}

	public ParsedFile getFirstFile() {
		return files.getFirst();
	}

	/**
	 * Get the stored file from its name or null if not found
	 * @return
	 */
	public ParsedFile getFile(String name) {
		for (ParsedFile f : files) {
			if (f.getName().equals(name)) {
				return f;
			}
		}
		return null;
	}

	/**
	 * List all the files of a directory
	 * @param directoryPath Path of the directory
	 * @return
	 */
	public static LinkedList<String> listDirectoryFiles(String directoryPath) {
		LinkedList<String> filePaths = new LinkedList<String>();
		File fileDirectory = new File(directoryPath);
		if (!fileDirectory.isDirectory()) {
			throw new IllegalArgumentException(directoryPath + " is not a directory");
		}
		for (File f : fileDirectory.listFiles()) {
			if (f.isFile()) {
				filePaths.addLast(f.getPath());;
			}
		}
		return filePaths;
	}

	private ParsedFile parseAFile(String filePath) {
		// TODO
		return null;
	}
}
