package parsing;

public class ParsedEntry {
	private int lineNumber;
	private String ID;

	public ParsedEntry(int lineNumber, String ID) {
		super();
		this.lineNumber = lineNumber;
		this.ID = ID;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public String getID() {
		return ID;
	}
}
