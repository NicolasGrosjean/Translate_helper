package parsing;

import java.util.Iterator;

import translator.ITranslatorParsedFile;
import translator.TranslatedEntry;

public class HoI4IParsedFile implements ITranslatorParsedFile {

	@Override
	public TranslatedEntry getFirstEntryToTranslate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslatedEntry getPreviousEntryToTranslate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslatedEntry getNextEntryToTranslate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslatedEntry getNextEntryToTranslateAndSave(TranslatedEntry entryToSave, Language sourceLanguage,
			Language destinationLanguage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranslatedEntry getNextEntryToTranslateAndSetLoanWord(TranslatedEntry loanWordEntry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberMissingSourceLines() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumberLineToTranslate() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getUsefulLineNumber() {
		// TODO Auto-generated method stub
		return 0;
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
	public Iterator<ParsedEntry> getDescendingIteratorLineToTranslate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<ParsedEntry> getDescendingIteratorMissingSourceLines() {
		// TODO Auto-generated method stub
		return null;
	}

}
