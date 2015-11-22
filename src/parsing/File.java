package parsing;

import java.util.LinkedList;

public class File {
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
	private LinkedList<Entry> linesToTranslate;

	/**
	 * List of the line where the "localisation" is missing for the source language
	 */
	private LinkedList<Entry> missingSourceLines;

	public File(String name) {
		this.name = name;
		lineNumber = -1; // default value to ensure
		linesToTranslate = new LinkedList<Entry>();
		missingSourceLines = new LinkedList<Entry>();
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
		if (lineNumber < 0) {
			this.lineNumber = lineNumber;
		} else {
			throw new IllegalArgumentException("Number of lines of the file " + 
					name + " has been already initialized");
		}
	}

	public void addLastLineToTranslate(int lineNumber, String ID) {
		linesToTranslate.addLast(new Entry(lineNumber, ID));
	}

	public void addLastMissingSourceLine(int lineNumber, String ID) {
		missingSourceLines.addLast(new Entry(lineNumber, ID));
	}
}
