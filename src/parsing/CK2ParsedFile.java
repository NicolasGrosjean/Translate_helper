package parsing;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import translator.ITranslator;
import translator.TranslatedEntry;
import translator.TranslatorParsedFile;

public class CK2ParsedFile extends TranslatorParsedFile {
	/**
	 * Path of the file
	 */
	private String filePath;
	/**
	 * The total number of the lines of the file (translated + no-translated + commented)
	 */
	private int lineNumber;

	/**
	 * List of the lines to translate
	 */
	private LinkedList<ParsedEntry> linesToTranslate;

	/**
	 * List of the line where the "localisation" is missing for the source language
	 */
	private LinkedList<ParsedEntry> missingSourceLines;

	public CK2ParsedFile(String filePath) {
		this.filePath = filePath;
		name = filePath.substring(filePath.lastIndexOf("\\") + 1);
		lineNumber = -1; // default value to ensure unique initialization later
		usefulLineNumber = -1; // default value to ensure unique initialization later
		linesToTranslate = new LinkedList<ParsedEntry>();
		missingSourceLines = new LinkedList<ParsedEntry>();
	}
	
	@Override
	protected LinkedList<ParsedEntry> getLinesToTranslate() {
		return linesToTranslate;
	}

	@Override
	protected LinkedList<ParsedEntry> getMissingSourceLines() {
		return missingSourceLines;
	}

	public int getLineNumber() {
		if (lineNumber >= 0) {
			return lineNumber;
		} else {
			throw new IllegalArgumentException("Number of lines of the file " + 
					name + " was not initialized");
		}
	}

	public void setLineNumber(int lineNumber) {
		if (this.lineNumber < 0) {
			this.lineNumber = lineNumber;
		} else {
			throw new IllegalArgumentException("Number of lines of the file " + 
					name + " has been already initialized");
		}
	}

	public int getUsefulLineNumber() {
		if (usefulLineNumber >= 0) {
			return usefulLineNumber;
		} else {
			throw new IllegalArgumentException("Number of useful lines of the file " +
					name + " was not initialized");
		}
	}

	public void setUsefulLineNumber(int lineNumber) {
		if (this.usefulLineNumber < 0) {
			this.usefulLineNumber = lineNumber;
		} else {
			throw new IllegalArgumentException("Number of useful lines of the file " +
					name + " has been already initialized");
		}
	}
	
	public void addLastLineToTranslate(int lineNumber, String id, String reason,
			String sourceText, String destinationText) {
		linesToTranslate.addLast(new ParsedEntry(lineNumber, id, reason, sourceText, destinationText));
	}

	public void addLastMissingSourceLine(int lineNumber, String id, String reason,
			String sourceText, String destinationText) {
		missingSourceLines.addLast(new ParsedEntry(lineNumber, id, reason, sourceText, destinationText));
	}

	@Override
	public Iterator<ParsedEntry> getDescendingIteratorLineToTranslate() {
		return linesToTranslate.descendingIterator();
	}
	
	@Override
	public Iterator<ParsedEntry> getDescendingIteratorMissingSourceLines() {
		return missingSourceLines.descendingIterator();
	}

	@Override
	public String getMissingSourceText() {
		StringBuilder builder = new StringBuilder();
		for (ParsedEntry e : missingSourceLines) {
			builder.append(e + System.lineSeparator());
		}
		return builder.toString();
	}

	@Override
	public String getMissingTranslation() {
		StringBuilder builder = new StringBuilder();
		for (ParsedEntry e : linesToTranslate) {
			builder.append(e + System.lineSeparator());
		}
		return builder.toString();
	}

	@Override
	public TranslatedEntry getNextEntryToTranslateAndSave(TranslatedEntry entryToSave,
			Language sourceLanguage, Language destinationLanguage) {
		// Save in memory
		linesToTranslate.get(lineToTranslateIndex).saveEntry(entryToSave);
		
		TranslatedEntry nextEntry = getNextEntryToTranslate();
		
		// Save in file
		BufferedReader file = null;
		StringBuilder builder = new StringBuilder();
		try {
			file = new BufferedReader(new FileReader(filePath));
			String line;
			int i = 0;
		    while ((line = file.readLine()) != null)
		    {
		    	i++;
		    	if (i == entryToSave.getDestLineNumber()) {
		    		String[] localisations = line.split(";");
		    		if (localisations.length < 14) {
		    			// Invalid number of semicolon. Create a new array and copy the previous into.
		    			String[] new_localisations = new String[14];
		    			Arrays.fill(new_localisations, "");
		    			for (int j = 0; j < localisations.length; j++) {
		    				new_localisations[j] = localisations[j].replaceAll("\\r", "");
		    			}
		    			new_localisations[13] = "x";
		    			localisations = new_localisations;
		    		}
		    		localisations[sourceLanguage.getDefaultColumn()] = entryToSave.getSource().replaceAll("\\r", "");
		    		localisations[destinationLanguage.getDefaultColumn()] = entryToSave.getDestination().replaceAll("\\r", "");
		    		line = "";
		    		for (String s: localisations)
		    		{
		    			line += s + ";";
		    		}
		    		// Add the line without the last semicolon
		    		line = line.substring(0, line.length() - 1);
		    	}
		    	builder.append(line + System.lineSeparator());
		    }
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (file != null)
					file.close();
			} catch (IOException e) {
				throw new IllegalArgumentException(e.getMessage());
			}
		}
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(filePath), "windows-1252"), true);
		    writer.print(builder.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null)
				writer.close();
		}
		return nextEntry;
	}

	@Override
	public ITranslator createAllLines(Language sourceLanguage, Language destinationLanguage, boolean acceptAllCopies) {
		Parse parseObj = new Parse(new LinkedList<String>(), sourceLanguage, destinationLanguage, null, null, acceptAllCopies);
		return parseObj.parseAcsvFile(filePath, true);
	}
}
