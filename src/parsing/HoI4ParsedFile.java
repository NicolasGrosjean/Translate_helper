package parsing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedList;

import translator.TranslatorParsedFile;
import translator.TranslatedEntry;

public class HoI4ParsedFile extends TranslatorParsedFile {
	/**
	 * Path of the file
	 */
	private String troncatedFilePath;

	/**
	 * List of the lines to translate
	 */
	private LinkedList<HoI4ParsedEntry> linesToTranslate;

	/**
	 * List of the line where the "localisation" is missing for the source language
	 */
	private LinkedList<HoI4ParsedEntry> missingSourceLines;
	
	public HoI4ParsedFile(String troncatedFilePath)
	{
		this.troncatedFilePath = troncatedFilePath;
		int start = troncatedFilePath.lastIndexOf("\\") + 1;
		int end = troncatedFilePath.length() - 2;
		if (start > end)
		{
			this.name = "";
		} else {
			this.name = troncatedFilePath.substring(start, end);
		}
		this.usefulLineNumber = -1;
		this.linesToTranslate = new LinkedList<>();
		this.missingSourceLines = new LinkedList<>();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected LinkedList<HoI4ParsedEntry> getLinesToTranslate() {
		return linesToTranslate;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected LinkedList<HoI4ParsedEntry> getMissingSourceLines() {
		return missingSourceLines;
	}

	@Override
	public TranslatedEntry getNextEntryToTranslateAndSave(TranslatedEntry entryToSave, Language sourceLanguage,
			Language destinationLanguage) {
		// Save in memory
		linesToTranslate.get(lineToTranslateIndex).saveEntry(entryToSave);

		TranslatedEntry nextEntry = getNextEntryToTranslate();

		// Save in files
		saveEntryInfile(getFilePath(sourceLanguage), sourceLanguage.getCode(), 
				entryToSave.getSourceLineNumber(), entryToSave.getId(), entryToSave.getSource());
		saveEntryInfile(getFilePath(destinationLanguage), destinationLanguage.getCode(),
				entryToSave.getDestLineNumber(), entryToSave.getId(), entryToSave.getDestination());
		return nextEntry;
	}
	
	private void saveEntryInfile(String filePath, String languageName, int lineNumber, String id, String text) {

		BufferedReader file = null;
		StringBuilder builder = new StringBuilder();
		try {
			// TODO check the file exists or create it
			file = new BufferedReader(new FileReader(filePath));
			String line;
			int i = 0;
			while ((line = file.readLine()) != null) {
				i++;
				if (i == 1)
				{
					line = "\uFEFFl_" + languageName.toLowerCase() + ":";
				}
				if (i == lineNumber) {
					// Add the line without the last semicolon
					line = " " + id + ":0 \"" + text + "\"";
				}
				// TODO : Manage missing line number
				builder.append(line + "\n");
			}
		} catch (FileNotFoundException e) {
			builder.append("\uFEFFl_" + languageName.toLowerCase() + ":\n");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (file != null)
					file.close();
			} catch (IOException e) {
				throw new IllegalArgumentException(e.getMessage());
			}
		}
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
	}	

	public void setUsefulLineNumber(int usefulLineNumber) {
		this.usefulLineNumber = usefulLineNumber;
	}

	@Override
	public String getMissingSourceText() {
		StringBuilder builder = new StringBuilder();
		for (HoI4ParsedEntry e : missingSourceLines) {
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
	
	public String getFilePath(Language language)
	{
		return troncatedFilePath + "_" + language.getCode().toLowerCase() + ".yml";
	}
}
