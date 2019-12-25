package translator;

import parsing.ParsedEntry;

public class TranslatedEntry {
	private String source;
	private String destination;
	private String id;

	/**
	 * Line number in the destination file corresponding to this entry
	 */
	private int destLineNumber;
	
	/**
	 * Line number in the destination file corresponding to this entry
	 */
	private int sourceLineNumber;

	public TranslatedEntry(String source, String destination, int destLineNumber,
			int sourceLineNumber, String id) {
		this.source = source;
		this.destination = destination;
		this.destLineNumber = destLineNumber;
		this.sourceLineNumber = sourceLineNumber;
		this.id = id;
	}

	public TranslatedEntry(ParsedEntry entry) {
		this(entry.getSourceText(), entry.getDestinationText(),
				entry.getDestinationLineNumber(), entry.getSourceLineNumber(),
				entry.getID());
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
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

	public void increaseDestLineNumber() {
		destLineNumber++;
	}
	
	public int getSourceLineNumber() {
		return sourceLineNumber;
	}

	public void increaseSourceLineNumber() {
		sourceLineNumber++;
	}

	public String getId() {
		return id;
	}
}
