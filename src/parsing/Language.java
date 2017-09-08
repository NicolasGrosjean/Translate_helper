package parsing;

import java.util.Locale;

public class Language {
	/**
	 * Code of the language in the "localisation" files
	 */
	private String code;

	/**
	 * Default column in the "localisation" files
	 */
	private int defaultColumn;
	
	/**
	 * Locale object of the language
	 */
	private Locale locale;

	public final static String defaultLanguageCode = "NONE";
	public final static int defaultLanguageColumn = -1;
	public final static String defaultLocaleISO = "en";

	public Language(String code, int defaultColumn, String languageISO) {
		this.code = code;
		this.defaultColumn = defaultColumn;
		// ISO 639
		this.locale = new Locale(languageISO);
	}

	public Language() {
		this(defaultLanguageCode, defaultLanguageColumn, defaultLocaleISO);
	}

	public String getCode() {
		return code;
	}

	public int getDefaultColumn() {
		return defaultColumn;
	}

	public Locale getLocale() {
		return locale;
	}

	/**
	 * Return if it is the "NONE" language (=no language)
	 * @return
	 */
	public boolean isNone() {
		return (code.equals(defaultLanguageCode)
				&& defaultColumn == defaultLanguageColumn);
	}

	@Override
	public String toString() {
		return code;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Language other = (Language) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (defaultColumn != other.defaultColumn)
			return false;
		return true;
	}
}
