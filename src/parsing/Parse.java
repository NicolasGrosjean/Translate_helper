package parsing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

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
		ParsedFile parsedFile = new ParsedFile(filePath.substring(filePath.lastIndexOf("\\") + 1));
		int lineNumber = 0;
		int sourceLanguageColumn = defaultSourceLanguageColumn;
		int destinationLanguageColumn = defaultDestinationLanguageColumn;
		FileInputStream f = null;
		try {
			f = new FileInputStream(filePath);
			Scanner line = new Scanner(f);
			Scanner expression = null;
			line.useDelimiter("\n");
			// Parse the file line by line
			while (line.hasNext()) {
				lineNumber++;
				expression = new Scanner(line.next());
				expression.useDelimiter(";");
				// The ID is the first expression of the line
				String ID = "";
				if (expression.hasNext()) {
					ID = expression.next();
				}
				if (ID.charAt(0) == '#') {
					// The line is commented => nothing to do
				} else if (ID.equals("CODE")) {
					//TODO : change the integers
				} else {					
					int i = 1;
					int min = Math.min(sourceLanguageColumn, destinationLanguageColumn);
					int max = Math.max(sourceLanguageColumn, destinationLanguageColumn);
					// First expression to analyze
					while (expression.hasNext() && (i < min)) {
						expression.next();
						i++;
					}
					if (expression.hasNext() && Parse.isNotTranslatedOrMissing(expression.next())) {
						if (sourceLanguageColumn < destinationLanguageColumn) {
							parsedFile.addLastMissingSourceLine(lineNumber, ID);
						} else {
							parsedFile.addLastLineToTranslate(lineNumber, ID);
						}					
					}
					i++;
					// Second expression to analyze
					while (expression.hasNext() && (i < max)) {
						expression.next();
						i++;
					}
					if (expression.hasNext() && Parse.isNotTranslatedOrMissing(expression.next())) {
						if (sourceLanguageColumn > destinationLanguageColumn) {
							parsedFile.addLastMissingSourceLine(lineNumber, ID);
						} else {
							parsedFile.addLastLineToTranslate(lineNumber, ID);
						}					
					}
				}			
				expression.close();				
			}
			line.close();
			parsedFile.setLineNumber(lineNumber);
			return parsedFile;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (f != null)
					f.close();
			} catch (IOException e) {
				throw new IllegalArgumentException(e.getMessage());
			}
		}
		return null;
	}

	private static boolean isNotTranslatedOrMissing(String expression) {
		//TODO : use a list of no-translated words
		return (expression.equals(""));
	}
}
