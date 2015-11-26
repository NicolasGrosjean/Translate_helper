package parsing;

import java.util.Iterator;
import java.util.LinkedList;

public class ParsedFile {
	/**
	 * Name of the file
	 */
	private String name;

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

	public ParsedFile(String name) {
		this.name = name;
		lineNumber = -1; // default value to ensure
		linesToTranslate = new LinkedList<ParsedEntry>();
		missingSourceLines = new LinkedList<ParsedEntry>();
	}

	public String getName() {
		return name;
	}

	public int getLineNumber() {
		if (lineNumber > 0) {
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

	public void addLastLineToTranslate(int lineNumber, String ID) {
		linesToTranslate.addLast(new ParsedEntry(lineNumber, ID));
	}

	public void addLastMissingSourceLine(int lineNumber, String ID) {
		missingSourceLines.addLast(new ParsedEntry(lineNumber, ID));
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
			res += e.getLineNumber() + " : " + e.getID() + "\n";
		}
		return res;
	}

	@Override
	public String toString() {
		return name;
	}
}
