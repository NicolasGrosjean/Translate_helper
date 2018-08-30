package parsing;

import java.util.Iterator;
import java.util.LinkedList;

import translator.ITranslator;
import translator.TranslatedEntry;
import translator.TranslatorParsedFile;

/**
 * Parsed file for Project Hospital
 * 
 * @author NicolasGrosjean alias Mouchi
 *
 */
public class PHParsedFile extends TranslatorParsedFile  {	
	/**
	 * Directory of the file
	 */
	private String directory;
	
	public PHParsedFile(String directory, String name)
	{
		this.directory = directory;
		this.name = name;
		this.usefulLineNumber = -1;
	}
	
	public String getFilePath(Language language)
	{
		return this.directory + "\\StringTable" + language.getName() + "_" + this.name + ".xml";
	}

	@Override
	public TranslatedEntry getNextEntryToTranslateAndSave(TranslatedEntry entryToSave, Language sourceLanguage,
			Language destinationLanguage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMissingSourceText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMissingTranslation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITranslator createAllLines(Language sourceLanguage, Language destinationLanguage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<ParsedEntry> getDescendingIteratorLineToTranslate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<ParsedEntry> getDescendingIteratorMissingSourceLines() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected <T extends ParsedEntry> LinkedList<T> getLinesToTranslate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected <T extends ParsedEntry> LinkedList<T> getMissingSourceLines() {
		// TODO Auto-generated method stub
		return null;
	}

}
