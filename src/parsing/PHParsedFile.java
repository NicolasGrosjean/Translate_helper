package parsing;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

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
	
	/**
	 * List of all the lines
	 */
	private LinkedList<PHParsedEntry> allLines;
	
	/**
	 * Map all the lines by Id. It is the same objects than the previous list.
	 */
	private Map<String, PHParsedEntry> allLinesById;

	/**
	 * List of the lines to translate
	 */
	private LinkedList<PHParsedEntry> linesToTranslate;

	/**
	 * List of the line where the "localisation" is missing for the source language
	 */
	private LinkedList<PHParsedEntry> missingSourceLines;
	
	public PHParsedFile(String directory, String name)
	{
		this.directory = directory;
		this.name = name;
		this.usefulLineNumber = -1;
		this.linesToTranslate = new LinkedList<>();
		this.missingSourceLines = new LinkedList<>();
		this.allLines = new LinkedList<>();
		this.allLinesById = new HashMap<>();
	}
	
	public String getFilePath(Language language)
	{
		return this.directory + "\\StringTable" + language.getName() + "_" + this.name + ".xml";
	}
	
	public PHParsedEntry addLine(String id, String sourceText, String destinationText,
			int sourceLineNumber, int destinationLineNumber)
	{
		PHParsedEntry entry = new PHParsedEntry(sourceLineNumber, destinationLineNumber, 
				id, sourceText, destinationText);
		allLines.add(entry);
		allLinesById.put(id, entry);
		return entry;
	}
	
	public void addLineToTranslate(PHParsedEntry entry, String reason)
	{
		entry.setReason(reason);
		this.linesToTranslate.add(entry);
	}
	
	public void addLastLineToTranslate(int sourceLineNumber, int destinationLineNumber, String id, String reason,
			String sourceText, String destinationText, int sourceVersionNumber, int destinationVersionNumber) {
		PHParsedEntry entry = addLine(id, sourceText, destinationText, sourceLineNumber, destinationLineNumber);
		addLineToTranslate(entry, reason);
	}
	
	public void addMissingSourceLine(PHParsedEntry entry)
	{
		entry.setReason(ParsedEntry.missingText);
		this.missingSourceLines.add(entry);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected LinkedList<PHParsedEntry> getLinesToTranslate() {
		return linesToTranslate;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected LinkedList<PHParsedEntry> getMissingSourceLines() {
		return missingSourceLines;
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
		LinkedList<ParsedEntry> list = new LinkedList<>();
		for (ParsedEntry e: linesToTranslate)
		{
			list.add(e);
		}
		return list.iterator();
	}

	@Override
	public Iterator<ParsedEntry> getDescendingIteratorMissingSourceLines() {
		LinkedList<ParsedEntry> list = new LinkedList<>();
		for (ParsedEntry e: missingSourceLines)
		{
			list.add(e);
		}
		return list.iterator();
	}
}
