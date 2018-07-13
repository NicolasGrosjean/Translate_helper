package parsing;

import translator.TranslatedEntry;

public class ParsedEntry {
	// The different type of text problems
	public static String missingText = "No text";
	public static String fakeText = "Fake translation";
	public static String copyText = "Copy of source text";
	public static String nonUpdated = "Not updated to source changes";

	private int lineNumber;
	private String id;
	private String reason;
	private String sourceText;
	private String destinationText;

	public ParsedEntry(int lineNumber, String id, String reason,
			String sourceText, String destinationText) {
		this.lineNumber = lineNumber;
		this.id = id;
		this.reason = reason;
		this.sourceText = sourceText;
		this.destinationText = destinationText;
	}

	public int getSourceLineNumber() {
		return lineNumber;
	}

	public int getDestinationLineNumber() {
		return lineNumber;
	}
	public String getID() {
		return id;
	}

	public String getReason() {
		return reason;
	}

	public String getSourceText() {
		return sourceText;
	}

	public String getDestinationText() {
		return destinationText;
	}
	
	public void saveEntry(TranslatedEntry entry)
	{
		sourceText = entry.getSource();
		destinationText = entry.getDestination();
	}

	@Override
	public String toString() {
		if (reason.equals(missingText)) {
			return lineNumber + " : " + id;
		} else {
			return lineNumber + " : " + id + " (" + reason + ")";
		}
	}
}
