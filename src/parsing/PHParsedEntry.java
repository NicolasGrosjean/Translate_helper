package parsing;

public class PHParsedEntry extends HoI4ParsedEntry {
	public PHParsedEntry(int sourceLineNumber, int destinationLineNumber, String id,
			String sourceText, String destinationText) {
		super(sourceLineNumber, destinationLineNumber, id, "", sourceText, destinationText, 0, 0);
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
}
