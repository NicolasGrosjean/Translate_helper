package translator;

import parsing.Language;

public interface ITranslator {
	/**
	 * Get the first entry to translate or null if there is no one
	 * @return null if no entry to translate
	 */
	public TranslatedEntry getFirstEntryToTranslate();
	
	/**
	 * Get the next entry to translate or null if there is no one
	 * @return null if no more entry to translate
	 */
	public TranslatedEntry getPreviousEntryToTranslate();
	
	/**
	 * Get the next entry to translate or null if there is no one
	 * @return null if no more entry to translate
	 */
	public TranslatedEntry getNextEntryToTranslate();
	
	/**
	 * Save an entry and get the next entry to translate or null if there is no one
	 * @param entryToSave
	 * @param sourceLanguage
	 * @param destinationLanguage
	 * @return
	 */
	public TranslatedEntry getNextEntryToTranslateAndSave(TranslatedEntry entryToSave,
			Language sourceLanguage, Language destinationLanguage);
	
	/**
	 * Set that the entry is a loan word and so is a correct translation.
	 * Get the next entry to translate.
	 * @param loanWordEntry
	 * @return
	 */
	public TranslatedEntry getNextEntryToTranslateAndSetLoanWord(TranslatedEntry loanWordEntry);
}
