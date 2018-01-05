package parsing;

public class HoI4ParsedEntry extends ParsedEntry {
	public static int MISSING_ENTRY = -1;
	
	int sourceLineNumber = MISSING_ENTRY;
	int destinationLineNumber = MISSING_ENTRY;

	public HoI4ParsedEntry(int sourceLineNumber, int destinationLineNumber, String id, String reason, String sourceText,
			String destinationText) {
		super(MISSING_ENTRY, id, reason, sourceText, destinationText);
		this.sourceLineNumber = sourceLineNumber;
		this.destinationLineNumber = destinationLineNumber;
	}

	public int getLineNumber() {
		throw new RuntimeException(
				"For an HoI4ParsedEntry, lineNumber has no meaning. Use sourceLineNumber or destinationLineNumber");
	}

	public int getSourceLineNumber() {
		return sourceLineNumber;
	}

	public int getDestinationLineNumber() {
		return destinationLineNumber;
	}

	@Override
	public String toString() {
		if (getReason().equals(missingText)) {
			return sourceLineNumber + " : " + getID();
		} else {
			return sourceLineNumber + " : " + getID() + " (" + getReason() + ")";
		}
	}
}
