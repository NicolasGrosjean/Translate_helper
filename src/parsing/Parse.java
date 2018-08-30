package parsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

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
				String troncated = getFilePathWithoutLanguageYML(filePath);
				if (troncated.equals(""))
				{
					System.err.println(filePath + " was bad named. It doesn't respect format : dir/name_l_language.yml");
					continue;
				}
				if (!parsedTroncatedFiles.contains(troncated))
				{
					HoI4ParsedFile parsedFile = parseAymlFile(troncated);
					if (parsedFile != null) {
						files.addLast(parsedFile);
					}
					parsedTroncatedFiles.add(troncated);
				}
			} else if (filePath.endsWith(".xml")) {
				String fileName = new File(filePath).getName();
				String name = getFileNameWithoutLanguageXML(fileName);
				if (name.equals(""))
				{
					System.err.println(filePath + " was bad named. It doesn't respect format : dir/StringTableLanguage_name.xml");
					continue;
				}
				if (!parsedTroncatedFiles.contains(name))
				{
					PHParsedFile parsedFile = parseAxmlFile(new File(filePath).getParent(), name);
					if (parsedFile != null) {
						files.addLast(parsedFile);
					}
					parsedTroncatedFiles.add(name);
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

	private CK2ParsedFile parseAcsvFile(String filePath) {
		return parseAcsvFile(filePath, false);
	}

	public CK2ParsedFile parseAcsvFile(String filePath, boolean returnAllLines) {
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
					
					// We can now analyze the two expressions
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

	public HoI4ParsedFile parseAymlFile(String troncatedFilePath, boolean returnAllLines) {
		HoI4ParsedFile parsedFile = new HoI4ParsedFile(troncatedFilePath);
		File sourceFile = new File(parsedFile.getFilePath(sourceLanguage));
		File destinationFile = new File(parsedFile.getFilePath(destinationLanguage));
		if (!sourceFile.exists() && !destinationFile.exists()) {
			// TODO Manage better this error
			System.err.println(parsedFile.getFilePath(sourceLanguage) +
					" and " + parsedFile.getFilePath(destinationLanguage)
					+ " don't exist");
			return null;
		} else if (sourceFile.exists() && !destinationFile.exists()) {
			FileInputStream sourceFIS = null;
			try {
				sourceFIS = new FileInputStream(sourceFile);
				Scanner line = new Scanner(sourceFIS, "UTF-8");
				line.useDelimiter("\n");
				int lineNumber = 0;
				int usefulLineNumber = 0;				
				while (line.hasNext()) {
					lineNumber++;
					String sLine = line.next().replace("\uFEFF", "");
					String[] splittedComments = sLine.split("#");
					if (splittedComments.length == 0) {
						continue;
					}
					String unCommented = splittedComments[0];
					if (unCommented.startsWith("l_") || !unCommented.contains(":")) {
						// The first line which define the language doesn't interest us, like comments
						// or empty line
						continue;
					}
					usefulLineNumber++;
					String[] splitted = unCommented.split(":");
					String id = splitted[0].trim();
					String text = getTextFromSplitted(splitted, parsedFile.getFilePath(sourceLanguage), lineNumber);
					if (text == null) {
						continue;
					}
					int versionNumber = 0;
					Scanner scanner = new Scanner(splitted[1]).useDelimiter("[^0-9]+");
					if (scanner.hasNextInt()) {
						versionNumber = scanner.nextInt();
					}
					parsedFile.addLastLineToTranslate(lineNumber, HoI4ParsedEntry.MISSING_ENTRY, id,
							ParsedEntry.missingText, text, "", versionNumber, 0);
				}
				line.close();
				parsedFile.setUsefulLineNumber(usefulLineNumber);				
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
			return parsedFile;
		} else if (!sourceFile.exists() && destinationFile.exists()) {
			FileInputStream destinationFIS = null;
			try {
				destinationFIS = new FileInputStream(destinationFile);
				Scanner line = new Scanner(destinationFIS, "UTF-8");
				line.useDelimiter("\n");
				int lineNumber = 0;
				int usefulLineNumber = 0;				
				while (line.hasNext()) {
					lineNumber++;
					String sLine = line.next().replace("\uFEFF", "");
					String[] splittedComments = sLine.split("#");
					if (splittedComments.length == 0) {
						continue;
					}
					String unCommented = splittedComments[0];
					if (unCommented.startsWith("l_") || !unCommented.contains(":")) {
						// The first line which define the language doesn't interest us, like comments
						// or empty line
						continue;
					}
					usefulLineNumber++;
					String[] splitted = unCommented.split(":");
					String id = splitted[0].trim();
					String text = getTextFromSplitted(splitted, parsedFile.getFilePath(destinationLanguage),
							lineNumber);
					if (text == null) {
						continue;
					}
					int versionNumber = 0;
					Scanner scanner = new Scanner(splitted[1]).useDelimiter("[^0-9]+");
					if (scanner.hasNextInt()) {
						versionNumber = scanner.nextInt();
					}
					parsedFile.addLastMissingSourceLine(HoI4ParsedEntry.MISSING_ENTRY, lineNumber, id,
							ParsedEntry.missingText, "", text, 0, versionNumber);
				}
				line.close();
				parsedFile.setUsefulLineNumber(usefulLineNumber);
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
			return parsedFile;
		} else {
			// Map destination texts
			Map<String, TextAndNumbers> destTexts = new HashMap<>();
			int destUsefulLineNumber = 0;				
			FileInputStream destinationFIS = null;
			try {
				destinationFIS = new FileInputStream(destinationFile);
				Scanner line = new Scanner(destinationFIS, "UTF-8");
				line.useDelimiter("\n");
				int lineNumber = 0;
				while (line.hasNext()) {
					lineNumber++;
					String sLine = line.next().replace("\uFEFF", "");
					String[] splittedComments = sLine.split("#");
					if (splittedComments.length == 0) {
						continue;
					}
					String unCommented = splittedComments[0];
					if (unCommented.startsWith("l_") || !unCommented.contains(":")) {
						// The first line which define the language doesn't interest us, like comments
						// or empty line
						continue;
					}
					destUsefulLineNumber++;
					String[] splitted = unCommented.split(":");
					String id = splitted[0].trim();
					String text = getTextFromSplitted(splitted, parsedFile.getFilePath(destinationLanguage),
							lineNumber);
					int versionNumber = 0;
					Scanner scanner = new Scanner(splitted[1]).useDelimiter("[^0-9]+");
					if (scanner.hasNextInt()) {
						versionNumber = scanner.nextInt();
					}
					if (text == null) {
						continue;
					}
					destTexts.put(id, new TextAndNumbers(text, lineNumber, versionNumber));
				}
				line.close();
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
			
			// Get then analyze source texts
			FileInputStream sourceFIS = null;
			try {
				sourceFIS = new FileInputStream(sourceFile);
				Scanner line = new Scanner(sourceFIS, "UTF-8");
				line.useDelimiter("\n");
				int sourceLineNumber = 0;
				int sourceUsefulLineNumber = 0;				
				while (line.hasNext()) {
					sourceLineNumber++;
					String sLine = line.next().replace("\uFEFF", "");
					String[] splittedComments = sLine.split("#");
					if (splittedComments.length == 0) {
						continue;
					}
					String unCommented = splittedComments[0];
					if (unCommented.startsWith("l_") || !unCommented.contains(":")) {
						// The first line which define the language doesn't interest us, like comments
						// or empty line
						continue;
					}
					sourceUsefulLineNumber++;
					String[] splitted = unCommented.split(":");
					if (splitted.length < 2) {
						continue;
					}
					String id = splitted[0].trim();
					String sourceText = getTextFromSplitted(splitted, parsedFile.getFilePath(sourceLanguage),
							sourceLineNumber);
					int sourceVersionNumber = 0;
					Scanner scanner = new Scanner(splitted[1]).useDelimiter("[^0-9]+");
					if (scanner.hasNextInt()) {
						sourceVersionNumber = scanner.nextInt();
					}
					if (sourceText == null) {
						continue;
					}
					int destLineNumber = (destTexts.get(id) != null) ? destTexts.get(id).lineNumber : HoI4ParsedEntry.MISSING_ENTRY;
					String destText = (destTexts.get(id) != null) ? destTexts.get(id).text : "";
					int destVersionNumber = (destTexts.get(id) != null) ? destTexts.get(id).versionNumber : 0;

					if (returnAllLines) {
						parsedFile.addLastLineToTranslate(sourceLineNumber, destLineNumber, id, "", sourceText,
								destText, sourceVersionNumber, destVersionNumber);
						continue;
					}
					
					// We can now analyze the two expressions
					// Firstly individually
					String sourceAnalysis = analyzeExpression(sourceText);
					if (!sourceAnalysis.equals("")) {
						parsedFile.addLastMissingSourceLine(sourceLineNumber, destLineNumber, id,
								sourceAnalysis, sourceText, destText, sourceVersionNumber,
								destVersionNumber);
						continue;
					}
					String destinationAnalysis = analyzeExpression(destText);
					if (!destinationAnalysis.equals("")) {
						parsedFile.addLastLineToTranslate(sourceLineNumber, destLineNumber, id,
								destinationAnalysis, sourceText, destText, sourceVersionNumber,
								destVersionNumber);
					} else {
						if (sourceText.equals(destText)
								&& !acceptedLoanword.contains(destText)) {
							parsedFile.addLastLineToTranslate(sourceLineNumber, destLineNumber, id,
									ParsedEntry.copyText, sourceText, destText, sourceVersionNumber,
									destVersionNumber);
						} else if (destVersionNumber < sourceVersionNumber) {
							parsedFile.addLastLineToTranslate(sourceLineNumber, destLineNumber, id,
									ParsedEntry.nonUpdated, sourceText, destText, sourceVersionNumber,
									destVersionNumber);
						}
					}
				}
				line.close();
				parsedFile.setUsefulLineNumber(Math.max(sourceUsefulLineNumber, destUsefulLineNumber));				
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
			return parsedFile;
		}
	}

	private PHParsedFile parseAxmlFile(String directory, String name) {
		return parseAxmlFile(directory, name, false);
	}

	public PHParsedFile parseAxmlFile(String directory, String name, boolean returnAllLines) {
		PHParsedFile parsedFile = new PHParsedFile(directory, name);
		File sourceFile = new File(parsedFile.getFilePath(sourceLanguage));
		File destinationFile = new File(parsedFile.getFilePath(destinationLanguage));
		if (!sourceFile.exists() && !destinationFile.exists()) {
			// TODO Manage better this error
			System.err.println(parsedFile.getFilePath(sourceLanguage) +
					" and " + parsedFile.getFilePath(destinationLanguage)
					+ " don't exist");
			return null;
		} else if (sourceFile.exists() && !destinationFile.exists()) {
			Element root = null;
			try {
				// Build the XML tree and gather the root
				root = new SAXBuilder().build(sourceFile).getRootElement();
			} catch (JDOMException | IOException e) {
				// No configuration file
				return parsedFile;
			}
			List<Element> gameDBLocalizedStrings = root.getChild("GameDBStringTable").getChild("LocalizedStrings").getChildren();
			Iterator<Element> it = gameDBLocalizedStrings.iterator();
			while (it.hasNext()) {
				Element gameDBLocalizedString = it.next();
				String key = gameDBLocalizedString.getChild("LocID").getValue();
				String text = gameDBLocalizedString.getChild("Text").getValue();
			}
		}
		return parsedFile;
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
	 * Return "" in case of bad format
	 * 
	 * @param filePath
	 * @return
	 */
	private static String getFilePathWithoutLanguageYML(String filePath) {
		String[] split = filePath.split("_");
		// Concatenate all except the last one
		String res = "";
		for (int i = 0; i < split.length - 1; i++)
		{
			if( i > 0)
			{
				res = res.concat("_");
			}
			res = res.concat(split[i]);
			if (split[i].equals("l"))
			{
				// The beginning of language is found
				return res;
			}
		}
		// Language prefix l_ not found
		return "";
	}
	
	/**
	 * Remove the language form file name
	 * Ex : StringTableEnglish_Tutorial.xml => Tutorial
	 * 
	 * Return "" in case of bad format
	 * 
	 * @param filePath
	 * @return
	 */
	private static String getFileNameWithoutLanguageXML(String filePath) {
		String[] split = filePath.split("_");
		// Concatenate all except the first one
		String res = "";
		for (int i = 1; i < split.length; i++)
		{
			if(i > 1)
			{
				res = res.concat("_");
			}
			res = res.concat(split[i]);
		}
		return res.split("[.]")[0];
	}
	
	/**
	 * Extract the text from the splitted line. Return null if not possible to
	 * extract.
	 * 
	 * @param splitted
	 * @param filePath
	 * @param lineNumber
	 * @return
	 */
	private String getTextFromSplitted(String[] splitted, String filePath, int lineNumber) {
		String text = "";
		for (int i = 1; i < splitted.length; i++) {
			if (i > 1) {
				text = text.concat(":");
			}
			text = text.concat(splitted[i]);
		}
		int start = text.indexOf("\"") + 1;
		int end = text.lastIndexOf("\"");
		if (start > end) {
			System.err.println("Incorrect localisation text (ex: missing double quote at the end) in " + filePath + " line " + lineNumber);
			return null;
		}
		return text.substring(start, end);
	}
	
	
	private class TextAndNumbers {
		String text;
		int lineNumber;
		int versionNumber;
		
		public TextAndNumbers(String text, int lineNumber, int versionNumber) {
			this.text = text;
			this.lineNumber = lineNumber;
			this.versionNumber = versionNumber;
		}
	}
}
