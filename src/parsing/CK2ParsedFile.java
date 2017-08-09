package parsing;

import java.util.Iterator;
import java.util.LinkedList;

public class CK2ParsedFile implements IParsedFile {
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

	public CK2ParsedFile(String name) {
		this.name = name;
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

	public Iterator<ParsedEntry> getDescendingIteratorLineToTranslate() {
		return linesToTranslate.descendingIterator();
	}

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
		String res = "";
		for (ParsedEntry e : missingSourceLines) {
			res += e + "\n";
		}
		return res;
	}

	public String getMissingTranslation() {
		String res = "";
		for (ParsedEntry e : linesToTranslate) {
			res += e + "\n";
		}
		return res;
	}

	@Override
	public String toString() {
		return name;
	}
}
