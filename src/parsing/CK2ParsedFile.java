package parsing;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;

import mains.Diagnostic;
import translator.ITranslatorParsedFile;
import translator.TranslatedEntry;

public class CK2ParsedFile implements ITranslatorParsedFile {
	/**
	 * Path of the file
	 */
	private String filePath;
	
	/**
	 * Name of the file
	 */
	private String name;

	/**
	 * The total number of the lines of the file (translated + no-translated + commented)
	 */
	private int lineNumber;

	/**
	 * The total number of useful lines (translated + no-translated)
	 */
	private int usefulLineNumber;

	/**
	 * List of the lines to translate
	 */
	private LinkedList<ParsedEntry> linesToTranslate;

	/**
	 * List of the line where the "localisation" is missing for the source language
	 */
	private LinkedList<ParsedEntry> missingSourceLines;
	
	private int lineToTranslateIndex = 0;

	public CK2ParsedFile(String filePath) {
		this.filePath = filePath;
		name = filePath.substring(filePath.lastIndexOf("\\") + 1);
		lineNumber = -1; // default value to ensure unique initialization later
		usefulLineNumber = -1; // default value to ensure unique initialization later
		linesToTranslate = new LinkedList<ParsedEntry>();
		missingSourceLines = new LinkedList<ParsedEntry>();
	}

	public String getName() {
		return name;
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

	public int getNumberLineToTranslate() {
		return linesToTranslate.size();
	}

	public int getNumberMissingSourceLines() {
		return missingSourceLines.size();
	}

	public String getMissingSourceText() {
		StringBuilder builder = new StringBuilder();
		for (ParsedEntry e : missingSourceLines) {
			builder.append(e + System.lineSeparator());
		}
		return builder.toString();
	}

	public String getMissingTranslation() {
		StringBuilder builder = new StringBuilder();
		for (ParsedEntry e : linesToTranslate) {
			builder.append(e + System.lineSeparator());
		}
		return builder.toString();
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public TranslatedEntry getFirstEntryToTranslate() {
		lineToTranslateIndex = 0;
		if (linesToTranslate.size() > 0) {
			return new TranslatedEntry(linesToTranslate.getFirst());
		}
		return null;
	}

	@Override
	public TranslatedEntry getPreviousEntryToTranslate() {
		if (lineToTranslateIndex == 0)
		{
			return null;
		}
		lineToTranslateIndex--;
		return new TranslatedEntry(linesToTranslate.get(lineToTranslateIndex));
	}

	@Override
	public TranslatedEntry getNextEntryToTranslate() {
		lineToTranslateIndex++;
		if (lineToTranslateIndex < linesToTranslate.size()) {
			return new TranslatedEntry(linesToTranslate.get(lineToTranslateIndex));
		}
		return null;
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
		    		localisations[sourceLanguage.getDefaultColumn()] = entryToSave.getSource();
		    		localisations[destinationLanguage.getDefaultColumn()] = entryToSave.getDestination();
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
	public TranslatedEntry getNextEntryToTranslateAndSetLoanWord(TranslatedEntry loanWordEntry) {
		String filename= Diagnostic.acceptedLoanwordFile;
		FileWriter fw = null;
		try
		{
			fw = new FileWriter(filename,true);
		    fw.write("\n" + loanWordEntry.getSource());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fw != null)
					fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return getNextEntryToTranslate();
	}
}
