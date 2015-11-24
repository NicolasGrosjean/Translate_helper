package parsing;

public class Language {
	/**
	 * Code of the language in the "localisation" files
	 */
	private String code;

	/**
	 * Default column in the "localisation" files
	 */
	private int defaultColumn;

	public Language(String code, int defaultColumn) {
		this.code = code;
		this.defaultColumn = defaultColumn;
	}

	public Language() {
		this("NONE", -1);
	}

	public String getCode() {
		return code;
	}

	public int getDefaultColumn() {
		return defaultColumn;
	}

	@Override
	public String toString() {
		return code;
	}
}
