package translator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import mains.Diagnostic;
import parsing.IParsedFile;
import parsing.ParsedEntry;

public abstract class TranslatorParsedFile implements ITranslator, IParsedFile{
	/**
	 * Name of the file (without language specification)
	 */
	protected String name;

	/**
	 * The total number of useful lines (translated + no-translated)
	 */
	protected int usefulLineNumber;
	
	protected int lineToTranslateIndex = 0;
	
	protected abstract <T extends ParsedEntry> LinkedList<T> getLinesToTranslate();
	
	protected abstract <T extends ParsedEntry> LinkedList<T> getMissingSourceLines();
	
	@Override
	public TranslatedEntry getFirstEntryToTranslate() {
		lineToTranslateIndex = 0;
		if (getLinesToTranslate().size() > 0) {
			return new TranslatedEntry(getLinesToTranslate().getFirst());
		}
		return null;
	}

	@Override
	public TranslatedEntry getPreviousEntryToTranslate() {
		if (lineToTranslateIndex == 0)
		{
			return null;
		}
		lineToTranslateIndex--;
		return new TranslatedEntry(getLinesToTranslate().get(lineToTranslateIndex));
	}

	@Override
	public TranslatedEntry getNextEntryToTranslate() {
		lineToTranslateIndex++;
		if (lineToTranslateIndex < getLinesToTranslate().size()) {
			return new TranslatedEntry(getLinesToTranslate().get(lineToTranslateIndex));
		}
		return null;
	}

	@Override
	public TranslatedEntry getNextEntryToTranslateAndSetLoanWord(TranslatedEntry loanWordEntry) {
		String filename= Diagnostic.acceptedLoanwordFile;
		FileWriter fw = null;
		try
		{
			fw = new FileWriter(filename,true);
		    fw.write("\n" + loanWordEntry.getSource());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fw != null)
					fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return getNextEntryToTranslate();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int getNumberMissingSourceLines() {
		return getMissingSourceLines().size();
	}

	@Override
	public int getNumberLineToTranslate() {
		return getLinesToTranslate().size();
	}

	@Override
	public int getUsefulLineNumber() {
		return usefulLineNumber;
	}
}
