package parsing;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

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
		return this.directory + "\\StringTable" + StringUtils.capitalize(language.getLocale().toString()) + this.name + ".xml";
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
		// Save text in memory
		PHParsedEntry entryInMemory = linesToTranslate.get(lineToTranslateIndex);
		entryInMemory.saveEntry(entryToSave);

		TranslatedEntry nextEntry = getNextEntryToTranslate();
		allLines.sort(new SourceSorter());
		int sourceSaveLineNumber = saveXMLFile(getFilePath(sourceLanguage), sourceLanguage.getLocale().toString(),
				sourceLanguage.getName(), allLines, true, entryToSave);
		allLines.sort(new DestSorter());
		int destSaveLineNumber = saveXMLFile(getFilePath(destinationLanguage), destinationLanguage.getLocale().toString(),
				destinationLanguage.getName(), allLines, false, entryToSave);
		
		// Update line number in memory
		if (sourceSaveLineNumber != -1) {
			entryInMemory.setSourceLineNumber(sourceSaveLineNumber);
		} else if (destSaveLineNumber != -1) {
			entryInMemory.setDestinationLineNumber(destSaveLineNumber);
		}
		return nextEntry;
	}
	
	private int saveXMLFile(String filePath, String langCode, String langName,
			List<PHParsedEntry> entries, boolean source, TranslatedEntry missingEntryTosave) {
		StringBuilder builder = new StringBuilder();
		builder.append(getHeader(langCode, name, langName));
		builder.append("\n");
		int lineNumber = 2;
		boolean entryToSaveOverrided = false;
		for (PHParsedEntry entryToSave : entries)
		{
			builder.append(getLine(entryToSave.getID(), source ? entryToSave.getSourceText() :
				entryToSave.getDestinationText()));
			builder.append("\n");
			lineNumber++;
			if (entryToSave.getID().equals(missingEntryTosave.getId())) {
				entryToSaveOverrided = true;
			}
		}
		if (!entryToSaveOverrided) {
			builder.append(getLine(missingEntryTosave.getId(), source ? missingEntryTosave.getSource() :
				missingEntryTosave.getDestination()));
			builder.append("\n");
		}
		builder.append(getFooter());
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filePath),
					StandardCharsets.UTF_8), true);
			writer.print(builder.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null)
				writer.close();
		}
		return entryToSaveOverrided ? -1 : lineNumber;
	}
	
	public void setUsefulLineNumber(int usefulLineNumber) {
		this.usefulLineNumber = usefulLineNumber;
	}
	
	@Override
	public String getMissingSourceText() {
		StringBuilder builder = new StringBuilder();
		for (PHParsedEntry e : missingSourceLines) {
			builder.append(e.getSourceToString() + System.lineSeparator());
		}
		return builder.toString();
	}

	@Override
	public String getMissingTranslation() {
		StringBuilder builder = new StringBuilder();
		for (PHParsedEntry e : linesToTranslate) {
			builder.append(e.getDestinationToString() + System.lineSeparator());
		}
		return builder.toString();
	}

	@Override
	public ITranslator createAllLines(Language sourceLanguage, Language destinationLanguage, boolean acceptAllCopies) {
		Parse parseObj = new Parse(new LinkedList<String>(), sourceLanguage, destinationLanguage, null, null, acceptAllCopies);
		return parseObj.parseAxmlFile(directory, name, true);
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
	
	public static String getHeader(String langCode, String fileName, String languageName) {
		String contributor = "en".equals(langCode) ? "Oxymoron Games" : "Scribio";
		return "<Database>\n"
				+ "\t<GameDBStringTable ID=\"LOC_" + langCode.toUpperCase() + "_" + fileName + "\">\n\n"
				+ "\t\t<LanguageCode>" + langCode + "</LanguageCode>\n"
				+ "\t\t<LanguageNameLocalized>" + languageName + "</LanguageNameLocalized>\n\n"
		        + "\t\t<Contributors>\n"
				+ "\t\t\t<Name>" + contributor + "</Name>\n"
		        + "\t\t</Contributors>\n\n"
				+ "\t\t<LocalizedStrings>";
	}
	
	public static String getFooter() {
		return "\t\t</LocalizedStrings>\n\t</GameDBStringTable>\n</Database>";
	}
	
	public static String getLine(String id, String text) {
		return "\t\t\t<GameDBLocalizedString>\t<LocID>" + id + "</LocID>\t<Text>" + text + "</Text>\t</GameDBLocalizedString>"; 
	}
	
	private class SourceSorter implements Comparator<PHParsedEntry>{

	    public int compare(PHParsedEntry entry1, PHParsedEntry entry2){
	    	int lineNumber1 = entry1.getSourceLineNumber() != PHParsedEntry.MISSING_ENTRY ? entry1.getSourceLineNumber() :
	    		entry1.getDestinationLineNumber();
	    	int lineNumber2 = entry2.getSourceLineNumber() != PHParsedEntry.MISSING_ENTRY ? entry2.getSourceLineNumber() :
	    		entry2.getDestinationLineNumber();
	    	return lineNumber1 - lineNumber2;
	    }
	}
	
	private class DestSorter implements Comparator<PHParsedEntry>{

	    public int compare(PHParsedEntry entry1, PHParsedEntry entry2){
	    	int lineNumber1 = entry1.getDestinationLineNumber() != PHParsedEntry.MISSING_ENTRY ? entry1.getDestinationLineNumber() :
	    		entry1.getSourceLineNumber();
	    	int lineNumber2 = entry2.getDestinationLineNumber() != PHParsedEntry.MISSING_ENTRY ? entry2.getDestinationLineNumber() :
	    		entry2.getSourceLineNumber();
	    	return lineNumber1 - lineNumber2;
	    }
	}
}
