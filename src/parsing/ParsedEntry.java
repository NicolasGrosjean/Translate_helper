package parsing;

public class ParsedEntry {
	// The different type of text problems
	public static String missingText = "No text";
	public static String fakeText = "Fake translation";
	public static String copyText = "Copy of source text";

	private int lineNumber;
	private String ID;
	private String reason;

	public ParsedEntry(int lineNumber, String ID, String reason) {
		super();
		this.lineNumber = lineNumber;
		this.ID = ID;
		this.reason = reason;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public String getID() {
		return ID;
	}

	public String getReason() {
		return reason;
	}

	@Override
	public String toString() {
		if (reason.equals(missingText)) {
			return lineNumber + " : " + ID;
		} else {
			return lineNumber + " : " + ID +
					" (" + reason + ")";
		}
	}
}
