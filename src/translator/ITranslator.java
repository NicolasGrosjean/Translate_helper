package translator;

public interface ITranslator {
	/**
	 * Get the first entry to translate or null if there is no one
	 * @return
	 */
	public TranslatedEntry getFirstEntryToTranslate();
	
	/**
	 * Get the next entry to translate or null if there is no one
	 * @return
	 */
	public TranslatedEntry getNextEntryToTranslate();
	
	/**
	 * Save an entry and get the next entry to translate or null if there is no one
	 * @param entryToSave
	 * @return
	 */
	public TranslatedEntry getNextEntryToTranslateAndSave(TranslatedEntry entryToSave);
}
