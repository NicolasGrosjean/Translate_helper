package parsing;

public class ParsedEntry {
	// The different type of text problems
	public static String missingText = "No text";
	public static String fakeText = "Fake translation";
	public static String copyText = "Copy of source text";

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

	public int getLineNumber() {
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

	@Override
	public String toString() {
		if (reason.equals(missingText)) {
			return lineNumber + " : " + id;
		} else {
			return lineNumber + " : " + id + " (" + reason + ")";
		}
	}
}
