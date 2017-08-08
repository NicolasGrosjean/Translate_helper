package translator;

public class TranslatedEntry {
	private String source;
	private String destination;
	
	public TranslatedEntry(String source, String destination) {
		super();
		this.source = source;
		this.destination = destination;
	}

	public String getSource() {
		return source;
	}

	public String getDestination() {
		return destination;
	}
}
