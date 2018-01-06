package parsing;

import java.util.Iterator;
import java.util.LinkedList;

import translator.ITranslatorParsedFile;
import translator.TranslatedEntry;

public class HoI4ParsedFile implements ITranslatorParsedFile {
	/**
	 * Path of the file
	 */
	private String filePath;
	
	/**
	 * Name of the file (without language specification)
	 */
	private String name;

	/**
	 * The total number of useful lines (translated + no-translated)
	 */
	private int usefulLineNumber;

	/**
	 * List of the lines to translate
	 */
	private LinkedList<HoI4ParsedEntry> linesToTranslate;

	/**
	 * List of the line where the "localisation" is missing for the source language
	 */
	private LinkedList<HoI4ParsedEntry> missingSourceLines;
	
	public HoI4ParsedFile(String filePath)
	{
		this.filePath = filePath;
		int start = filePath.lastIndexOf("\\") + 1;
		int end = filePath.length() - 2;
		if (start > end)
		{
			this.name = "";
		} else {
			this.name = filePath.substring(start, end);
		}
		this.usefulLineNumber = -1;
		this.linesToTranslate = new LinkedList<>();
		this.missingSourceLines = new LinkedList<>();
	}
	
	@Override
	public TranslatedEntry getFirstEntryToTranslate() {
		throw new RuntimeException("Not yet implemented");
		// TODO Auto-generated method stub
	}

	@Override
	public TranslatedEntry getPreviousEntryToTranslate() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not yet implemented");
	}

	@Override
	public TranslatedEntry getNextEntryToTranslate() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not yet implemented");
	}

	@Override
	public TranslatedEntry getNextEntryToTranslateAndSave(TranslatedEntry entryToSave, Language sourceLanguage,
			Language destinationLanguage) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not yet implemented");
	}

	@Override
	public TranslatedEntry getNextEntryToTranslateAndSetLoanWord(TranslatedEntry loanWordEntry) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not yet implemented");
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getNumberMissingSourceLines() {
		return missingSourceLines.size();
	}

	@Override
	public int getNumberLineToTranslate() {
		return linesToTranslate.size();
	}

	@Override
	public int getUsefulLineNumber() {
		return usefulLineNumber;
	}

	public void setUsefulLineNumber(int usefulLineNumber) {
		this.usefulLineNumber = usefulLineNumber;
	}

	@Override
	public String getMissingSourceText() {
		StringBuilder builder = new StringBuilder();
		for (HoI4ParsedEntry e : linesToTranslate) {
			builder.append(e.getSourceToString() + System.lineSeparator());
		}
		return builder.toString();
	}

	@Override
	public String getMissingTranslation() {
		StringBuilder builder = new StringBuilder();
		for (HoI4ParsedEntry e : linesToTranslate) {
			builder.append(e.getDestinationToString() + System.lineSeparator());
		}
		return builder.toString();
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

	public void addLastLineToTranslate(int sourceLineNumber, int destinationLineNumber, String id, String reason,
			String sourceText, String destinationText) {
		this.linesToTranslate.addLast(
				new HoI4ParsedEntry(sourceLineNumber, destinationLineNumber, id, reason, sourceText, destinationText));
	}

	public void addLastMissingSourceLine(int sourceLineNumber, int destinationLineNumber, String id, String reason,
			String sourceText, String destinationText) {
		this.missingSourceLines.addLast(
				new HoI4ParsedEntry(sourceLineNumber, destinationLineNumber, id, reason, sourceText, destinationText));
	}

	@Override
	public String toString() {
		return name;
	}

}
