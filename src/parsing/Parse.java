package parsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
	 * List of expression which are in fact fake translations
	 */
	private LinkedList<String> fakeTranslation;

	/**
	 * List of accepted words from foreign languages
	 */
	private LinkedList<String> acceptedLoanword;

	/**
	 * Parse the files in order to store the entries which are not translated
	 * or without source language text
	 * @param filePaths The list of the names of the files to parse
	 */
	public Parse(LinkedList<String> filePaths, String codeSourceLanguage,
			int defaultSourceLanguageColumn, String codeDestinationLanguage,
			int defaultDestinationLanguageColumn, String fakeTranslationFile,
			String acceptedLoanwordFile) {
		sourceLanguage = new Language(codeSourceLanguage, defaultSourceLanguageColumn);
		destinationLanguage = new Language(codeDestinationLanguage, defaultDestinationLanguageColumn);
		files = new LinkedList<ParsedFile>();
		fakeTranslation = readList(fakeTranslationFile);
		acceptedLoanword = readList(acceptedLoanwordFile);
		for (String filePath : filePaths) {
			files.addLast(parseAFile(filePath));
		}
	}

	public Parse(LinkedList<String> filePaths, Language sourceLanguage,
			Language destinationLanguage, String fakeTranslationFile,
			String acceptedLoanwordFile) {
		this(filePaths, sourceLanguage.getCode(), sourceLanguage.getDefaultColumn(),
				destinationLanguage.getCode(), destinationLanguage.getDefaultColumn(),
				fakeTranslationFile, acceptedLoanwordFile);
	}

	public Parse(WorkingSession ws, String fakeTranslationFile,
			String acceptedLoanwordFile) {
		this(Parse.listDirectoryFiles(ws.getDirectory()),
				ws.getSourceLanguage(), ws.getDestinationLanguage(),
				fakeTranslationFile, acceptedLoanwordFile);
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
	 * Read a file and list all its lines
	 * @param file
	 * @return
	 */
	private static LinkedList<String> readList(String file) {
		LinkedList<String> readList = new LinkedList<String>();
		FileInputStream f = null;
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	readList.addLast(line);
		    }
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (f != null)
					f.close();
			} catch (IOException e) {
				throw new IllegalArgumentException(e.getMessage());
			}
		}
		return readList;
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
			array[i][0] = new Boolean(true); // check box
			array[i][1] = f; // file (only its name will be displayed)		
			array[i][2] = f.getNumberMissingSourceLines(); // number of lines with missing source text			
			if (destinationLanguage.isNone()) {
				// The destination language is unknown				
				array[i][3] = -1;
			} else {
				// Percentage of translation done
				array[i][3] = (f.getUsefulLineNumber() - f.getNumberLineToTranslate()) + "/" + f.getUsefulLineNumber();
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
		int usefulLineNumber = 0;
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
				if (ID.equals("")) {
					// The line is invalid
					expression.close();
					line.close();
					throw new IllegalArgumentException("Line " + lineNumber
							+ " of the file " + filePath + " is invalid");
				}
				if (ID.charAt(0) == '#') {
					// The line is commented => nothing to do
				} else if (ID.equals("CODE")) {
					//TODO : change the integers
				} else {
					usefulLineNumber++;
					int i = 1;
					int min = Math.min(sourceLanguageColumn, destinationLanguageColumn);
					int max = Math.max(sourceLanguageColumn, destinationLanguageColumn);
					String sourceExpression = "";
					String destinationExpression = "";
					// Search the column of the first expression to analyze
					while (expression.hasNext() && (i < min)) {
						expression.next();
						i++;
					}
					// If we are at the good column, we store the expression
					if (i == min && expression.hasNext()) {
						if (sourceLanguageColumn == min) {
							sourceExpression = expression.next();
						} else {
							destinationExpression = expression.next();
						}
						i++; // the expression scanner has moved
					}
					// Search the column of the second expression to analyze
					while (expression.hasNext() && (i < max)) {
						expression.next();
						i++;
					}
					// If we are at the good column, we store the expression
					if (i == max && expression.hasNext()) {
						if (sourceLanguageColumn == max) {
							sourceExpression = expression.next();
						} else {
							destinationExpression = expression.next();
						}
						i++; // the expression scanner has moved
					}

					// We can now analyze the two expression
					// Firstly individually
					String sourceAnalysis = analyzeExpression(sourceExpression);
					if (!sourceAnalysis.equals("")) {
						parsedFile.addLastMissingSourceLine(lineNumber, ID, sourceAnalysis);
					}
					String destinationAnalysis = analyzeExpression(destinationExpression);
					if (!destinationAnalysis.equals("")) {
						parsedFile.addLastLineToTranslate(lineNumber, ID, destinationAnalysis);
					} else {
						if (sourceExpression.equals(destinationExpression) &&
								!acceptedLoanword.contains(destinationExpression)) {
							parsedFile.addLastLineToTranslate(lineNumber, ID, ParsedEntry.copyText);
						}
					}
				}			
				expression.close();				
			}
			line.close();
			parsedFile.setLineNumber(lineNumber);
			parsedFile.setUsefulLineNumber(usefulLineNumber);
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

	private String analyzeExpression(String expression) {
		if (expression.equals("")) {
			return ParsedEntry.missingText;
		} else if (fakeTranslation.contains(expression)) {
			return ParsedEntry.fakeText;
		} else {
			return "";
		}
	}
}
