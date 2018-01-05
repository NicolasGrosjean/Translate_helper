package parsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;

import config.WorkingSession;

public class Parse {
	/**
	 * List of the "localisation" files
	 */
	private LinkedList<IParsedFile> files;

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
	public Parse(LinkedList<String> filePaths, Language sourceLanguage,
			Language destinationLanguage, String fakeTranslationFile,
			String acceptedLoanwordFile) {
		this.sourceLanguage = sourceLanguage;
		this.destinationLanguage = destinationLanguage;
		files = new LinkedList<IParsedFile>();
		fakeTranslation = readList(fakeTranslationFile);
		acceptedLoanword = readList(acceptedLoanwordFile);
		Set<String> parsedTroncatedFiles = new HashSet<>();
		for (String filePath : filePaths) {
			if (filePath.endsWith(".csv")) {
				files.addLast(parseAcsvFile(filePath));
			} else if (filePath.endsWith(".yml")){
				String troncated = getFilePathWithoutLanguage(filePath);
				if (!parsedTroncatedFiles.contains(troncated))
				{
					files.addLast(parseAymlFile(troncated));
					parsedTroncatedFiles.add(troncated);
				}
			}
		}
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
	public IParsedFile getFile(String name) {
		for (IParsedFile f : files) {
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
		for (IParsedFile f : files) {
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
		if (file == null) {
			return new LinkedList<String>();
		}
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
		for (IParsedFile f : files) {
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
	
	public static CK2ParsedFile getAllCk2Lines(String filePath, Language sourceLanguage,
			Language destinationLanguage)
	{
		Parse parseObj = new Parse(new LinkedList<String>(),
				sourceLanguage, destinationLanguage, null, null);
		return parseObj.parseAcsvFile(filePath, true);
	}

	private CK2ParsedFile parseAcsvFile(String filePath) {
		return parseAcsvFile(filePath, false);
	}

	private CK2ParsedFile parseAcsvFile(String filePath, boolean returnAllLines) {
		CK2ParsedFile parsedFile = new CK2ParsedFile(filePath);
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
				String sLine = line.next();
				// If the line is empty, skip the line
				if (sLine.equals("") || sLine.equals("\r")) {
					continue;
				}
				// Otherwise parse it
				expression = new Scanner(sLine);
				expression.useDelimiter(";");
				// The ID is the first expression of the line
				String ID = "";
				if (expression.hasNext()) {
					ID = expression.next();
				}
				if ((ID.equals("")) || (ID.charAt(0) == '#')) {
					// The line is not used => nothing to do
				} else if (ID.equals("CODE")) {
					//TODO : change the integers ?
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

					if (returnAllLines)
					{
						parsedFile.addLastLineToTranslate(lineNumber, ID, "",
								sourceExpression, destinationExpression);
						continue;
					}
					
					// We can now analyze the two expression
					// Firstly individually
					String sourceAnalysis = analyzeExpression(sourceExpression);
					if (!sourceAnalysis.equals("")) {
						parsedFile.addLastMissingSourceLine(lineNumber, ID, sourceAnalysis,
								sourceExpression, destinationExpression);
					}
					String destinationAnalysis = analyzeExpression(destinationExpression);
					if (!destinationAnalysis.equals("")) {
						parsedFile.addLastLineToTranslate(lineNumber, ID, destinationAnalysis,
								sourceExpression, destinationExpression);
					} else {
						if (sourceExpression.equals(destinationExpression) &&
								!acceptedLoanword.contains(destinationExpression)) {
							parsedFile.addLastLineToTranslate(lineNumber, ID, ParsedEntry.copyText,
									sourceExpression, destinationExpression);
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

	private HoI4ParsedFile parseAymlFile(String troncatedFilePath) {
		return parseAymlFile(troncatedFilePath, false);
	}

	private HoI4ParsedFile parseAymlFile(String troncatedFilePath, boolean returnAllLines) {
		HoI4ParsedFile parsedFile = new HoI4ParsedFile(troncatedFilePath);
		int sourceLanguageColumn = sourceLanguage.getDefaultColumn();
		int destinationLanguageColumn = destinationLanguage.getDefaultColumn();
		File sourceFile = new File(troncatedFilePath + sourceLanguage.getLanguageParameter() + ".yml");
		File destinationFile = new File(troncatedFilePath + destinationLanguage.getLanguageParameter() + ".yml");
		if (!sourceFile.exists() && !destinationFile.exists()) {
			// TODO Manage better this error
			throw new RuntimeException(troncatedFilePath + sourceLanguage.getLanguageParameter() + ".yml" +
					"and " + troncatedFilePath + destinationLanguage.getLanguageParameter() + ".yml"
					+ "don't exist");
		} else if (sourceFile.exists() && !destinationFile.exists()) {
			FileInputStream sourceFIS = null;
			try {
				sourceFIS = new FileInputStream(sourceFile);
				Scanner line = new Scanner(sourceFIS);
				Scanner expression = null;
				line.useDelimiter("\n");
				int lineNumber = 0;
				int usefulLineNumber = 0;				
				while (line.hasNext()) {
					lineNumber++;
					String sLine = line.next();
					if (sLine.startsWith("l_") || sLine.startsWith("#") || !sLine.contains(":"))
					{
						// The first line which define the language doesn't interest us, like comments or empty line
						continue;
					}
					usefulLineNumber++;
					String[] splitted = sLine.split(":");
					String id = splitted[0].trim();
					String text = splitted[1];
					text = text.substring(text.indexOf("\""), text.lastIndexOf("\""));
					parsedFile.addLastLineToTranslate(lineNumber, HoI4ParsedEntry.MISSING_ENTRY, id,
							ParsedEntry.missingText, text, "");
				}
				line.close();
				parsedFile.setUsefulLineNumber(usefulLineNumber);
				return parsedFile;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					if (sourceFIS != null)
						sourceFIS.close();
				} catch (IOException e) {
					throw new IllegalArgumentException(e.getMessage());
				}
			}
			
		} else if (!sourceFile.exists() && destinationFile.exists()) {
			FileInputStream destinationFIS = null;
			try {
				destinationFIS = new FileInputStream(destinationFile);
				Scanner line = new Scanner(destinationFIS);
				Scanner expression = null;
				line.useDelimiter("\n");
				int lineNumber = 0;
				int usefulLineNumber = 0;				
				while (line.hasNext()) {
					lineNumber++;
					String sLine = line.next();
					if (sLine.startsWith("l_") || sLine.startsWith("#") || !sLine.contains(":"))
					{
						// The first line which define the language doesn't interest us, like comments or empty line
						continue;
					}
					usefulLineNumber++;
					String[] splitted = sLine.split(":");
					String id = splitted[0].trim();
					String text = splitted[1];
					text = text.substring(text.indexOf("\""), text.lastIndexOf("\""));
					parsedFile.addLastMissingSourceLine(HoI4ParsedEntry.MISSING_ENTRY, lineNumber, id,
							ParsedEntry.missingText, "", text);
				}
				line.close();
				parsedFile.setUsefulLineNumber(usefulLineNumber);
				return parsedFile;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					if (destinationFIS != null)
						destinationFIS.close();
				} catch (IOException e) {
					throw new IllegalArgumentException(e.getMessage());
				}
			}
		} else {
			FileInputStream destinationFIS = null;			
			// TODO Map all the destination text on the IDs
			// TODO For all source ID, get the source and destination texts and analyse them
		}
	}

	private String analyzeExpression(String expression) {
		// Remove end line code
		String expr = expression.replace("\r", "").replace("\n", "");
		if (expr.equals("")) {
			return ParsedEntry.missingText;
		} else if (fakeTranslation.contains(expr) || expr.contains("#")) {
			return ParsedEntry.fakeText;
		} else {
			return "";
		}
	}
	
	/**
	 * Remove the language form filepath.
	 * EX : dir/blabla_l_english.yml => dir/blabla_l
	 * 
	 * @param filePath
	 * @return
	 */
	private String getFilePathWithoutLanguage(String filePath) {
		String[] split = filePath.split("_");
		// Concatenate all except the last one
		String res = "";
		for (int i = 0; i < split.length - 1; i++)
		{
			res.concat(split[i]);
		}
		return res;
	}
}
