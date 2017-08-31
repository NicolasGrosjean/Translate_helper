package translator;

import parsing.ParsedEntry;

public class TranslatedEntry {
	private String source;
	private String destination;

	/**
	 * Line number in the destination file corresponding to this entry
	 */
	private int destLineNumber;

	public TranslatedEntry(String source, String destination, int destLineNumber) {
		this.source = source;
		this.destination = destination;
		this.destLineNumber = destLineNumber;
	}

	public TranslatedEntry(ParsedEntry entry) {
		this(entry.getSourceText(), entry.getDestinationText(), entry.getLineNumber());
	}

	public String getSource() {
		return source;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public int getDestLineNumber() {
		return destLineNumber;
	}
}
