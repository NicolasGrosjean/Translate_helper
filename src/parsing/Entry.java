package parsing;

public class Entry {
	private int lineNumber;
	private String ID;

	public Entry(int lineNumber, String ID) {
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
