package parsing;

import translator.TranslatedEntry;

public class HoI4ParsedEntry extends ParsedEntry {
	public static int MISSING_ENTRY = -1;

	int sourceLineNumber = MISSING_ENTRY;
	int destinationLineNumber = MISSING_ENTRY;
	
	int sourceVersionNumber;
	int destinationVersionNumber;

	public HoI4ParsedEntry(int sourceLineNumber, int destinationLineNumber, String id, String reason, String sourceText,
			String destinationText, int sourceVersionNumber, int destinationVersionNumber) {
		super(MISSING_ENTRY, id, reason, sourceText, destinationText);
		this.sourceLineNumber = sourceLineNumber;
		this.destinationLineNumber = destinationLineNumber;
		this.sourceVersionNumber = sourceVersionNumber;
		this.destinationVersionNumber = destinationVersionNumber;
	}

	@Override
	public int getSourceLineNumber() {
		return sourceLineNumber;
	}

	@Override
	public int getDestinationLineNumber() {
		return destinationLineNumber;
	}

	public int getSourceVersionNumber() {
		return sourceVersionNumber;
	}

	public int getDestinationVersionNumber() {
		return destinationVersionNumber;
	}

	public void setSourceLineNumber(int sourceLineNumber) {
		this.sourceLineNumber = sourceLineNumber;
	}

	public void setDestinationLineNumber(int destinationLineNumber) {
		this.destinationLineNumber = destinationLineNumber;
	}
	
	@Override
	public String toString() {
		return entryToString(sourceLineNumber);
	}

	public String getSourceToString() {
		return entryToString(sourceLineNumber);
	}

	public String getDestinationToString() {
		return entryToString(destinationLineNumber);
	}

	private String entryToString(int lineNumber) {
		if (getReason().equals(missingText)) {
			return lineNumber + " : " + getID();
		} else {
			return lineNumber + " : " + getID() + " (" + getReason() + ")";
		}
	}

	@Override
	public void saveEntry(TranslatedEntry entry)
	{
		super.saveEntry(entry);
		int newVersionNumber = Math.max(sourceVersionNumber, destinationVersionNumber);
		sourceVersionNumber = newVersionNumber;
		destinationVersionNumber = newVersionNumber;
	}
}
