package parsing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

import config.WorkingSession;

public class Parse {
	/**
	 * List of the "localisation" files
	 */
	private LinkedList<ParsedFile> files;

	/**
	 * Definition of the source language in the "localisation" files
	 */
	private Language sourceLanguage;

	/**
	 * Definition of the destination language in the "localisation" files
	 */
	private Language destinationLanguage;

	/**
	 * Parse the files in order to store the entries which are not translated
	 * or without source language text
	 * @param filePaths The list of the names of the files to parse
	 */
	public Parse(LinkedList<String> filePaths, String codeSourceLanguage,
			int defaultSourceLanguageColumn, String codeDestinationLanguage,
			int defaultDestinationLanguageColumn) {
		sourceLanguage = new Language(codeSourceLanguage, defaultSourceLanguageColumn);
		destinationLanguage = new Language(codeDestinationLanguage, defaultDestinationLanguageColumn);
		files = new LinkedList<ParsedFile>();
		for (String filePath : filePaths) {
			files.addLast(parseAFile(filePath));
		}
	}

	public Parse(LinkedList<String> filePaths, Language sourceLanguage,
			Language destinationLanguage) {
		this(filePaths, sourceLanguage.getCode(), sourceLanguage.getDefaultColumn(),
				destinationLanguage.getCode(), destinationLanguage.getDefaultColumn());
	}

	public Parse(WorkingSession ws) {
		this(Parse.listDirectoryFiles(ws.getDirectory()),
				ws.getSourceLanguage(), ws.getDestinationLanguage());
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
	 * Get a string to display the diagnostic of missing source text
	 * @return
	 */
	public String getListMissingSourceText() {
		String res = "";
		for (ParsedFile f : files) {
			res += f.getName() + "\n";
			if (f.getNumberMissingSourceLines() > 0) {
				res += f.getMissingSourceText() + "\n";
			} else {
				res += "Aucune localisation manquante!\n\n";
			}
		}
		return res;
	}

	/**
	 * Convert the files data to an array to display
	 * @return
	 */
	public Object[][] toArray() {
		Object[][] array = new Object[files.size()][];
		int i =0;
		for (ParsedFile f : files) {
			array[i] = new Object[5];
			array[i][0] = new Boolean(false); // check box
			array[i][1] = f; // file (only its name will be displayed)		
			array[i][2] = f.getNumberMissingSourceLines(); // number of lines with missing source text			
			if (destinationLanguage.isNone()) {
				// The destination language is unknown				
				array[i][3] = -1;
			} else {
				// Percentage of translation done
				array[i][3] = (f.getLineNumber() - f.getNumberLineToTranslate()) + "/" + f.getLineNumber();
			}
			array[i][4] = "Details"; // button text
			i++;
		}
		return array;
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
		int sourceLanguageColumn = sourceLanguage.getDefaultColumn();
		int destinationLanguageColumn = destinationLanguage.getDefaultColumn();
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
