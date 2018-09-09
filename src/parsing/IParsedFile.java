package parsing;

import java.util.Iterator;

import translator.ITranslator;

public interface IParsedFile {
	public String getName();
	
	public int getNumberMissingSourceLines();
	
	public int getNumberLineToTranslate();
	
	public int getUsefulLineNumber();
	
	/**
	 * All the missing source texts concatenated in a String separated by \n
	 * @return
	 */
	public String getMissingSourceText();
	
	/**
	 * All the missing translations concatenated in a String separated by \n
	 * @return
	 */
	public String getMissingTranslation();
	
	public ITranslator createAllLines(Language sourceLanguage, Language destinationLanguage, boolean acceptAllCopies);
	
	/** ---------------- FOR TESTING -------------------------------**/
	public Iterator<ParsedEntry> getDescendingIteratorLineToTranslate();

	public Iterator<ParsedEntry> getDescendingIteratorMissingSourceLines();
}
