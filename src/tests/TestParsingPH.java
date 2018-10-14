package tests;

import java.util.Iterator;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import parsing.Language;
import parsing.PHParsedEntry;
import parsing.PHParsedFile;
import parsing.Parse;
import parsing.ParsedEntry;

public class TestParsingPH {
	private static Parse parsedFiles;

	@BeforeClass
	public static void SetUp() {
		// Translate English to French
		parsedFiles = new Parse(Parse.listDirectoryFiles("./test_localisation_files/ph"),
				new Language("ENGLISH", 1, "en"), new Language("FRENCH", 2, "fr"),
				"config/fake_translations.txt", "config/accepted_loanwords.txt", false);
	}

	@Test
	public void testListDirectoryFiles() {
		String dir = "./test_localisation_files/ph/";
		LinkedList<String> filePaths = Parse.listDirectoryFiles(dir);
		String[] expected = {
				"EnAccepted_Copy",
				"EnAccepted_Copy_With_Accent",
				"EnBad_Copy",
				"EnFake_Translation",
				"EnText",
				"EnText_Comment",
				"EnText_Disordered_Not_Translated",
				"EnText_Disordered_Translated",
				"EnText_Only_In_English",
				"EnText_Without_English",
				"EnText_Without_French",
				"EnText_With_Balises",
				"FrAccepted_Copy",
				"FrAccepted_Copy_With_Accent",
				"FrBad_Copy",
				"FrFake_Translation",
				"FrText",
				"FrText_Comment",
				"FrText_Disordered_Not_Translated",
				"FrText_Disordered_Translated",
				"FrText_Only_In_French",
				"FrText_Without_English",
				"FrText_Without_French",
				"FrText_With_Balises",
		};
		Assert.assertEquals("Incorrect number of file", expected.length, filePaths.size());
		for (int i = 0; i < expected.length; i++) {
			Assert.assertEquals("Incorrect file found!", dir + "StringTable" + expected[i] + ".xml",
					filePaths.removeFirst().replace("\\", "/"));
		}
	}

	@Test
	public void acceptedCopy() {
		PHParsedFile f = (PHParsedFile) parsedFiles.getFile("Accepted_Copy");
		if (f != null) {
			Assert.assertEquals("It should have nothing to translate", 0, f.getNumberLineToTranslate());
			Assert.assertEquals("It should have no missing source text", 0, f.getNumberMissingSourceLines());
		} else {
			Assert.fail("File not parsed");
		}
	}

	@Test
	public void acceptedCopyWithAccents() {
		PHParsedFile f = (PHParsedFile) parsedFiles.getFile("Accepted_Copy_With_Accent");
		if (f != null) {
			Assert.assertEquals("It should have nothing to translate", 0, f.getNumberLineToTranslate());
			Assert.assertEquals("It should have no missing source text", 0, f.getNumberMissingSourceLines());
		} else {
			Assert.fail("File not parsed");
		}
	}


	@Test
	public void badCopy() {
		PHParsedFile f = (PHParsedFile) parsedFiles.getFile("Bad_Copy");
		if (f != null) {
			Assert.assertEquals("It should have 1 line to translate", 1, f.getNumberLineToTranslate());
			Assert.assertEquals("It should have no missing source text", 0, f.getNumberMissingSourceLines());
			Iterator<ParsedEntry> lineToTranslateIterator = f.getDescendingIteratorLineToTranslate();
			Assert.assertEquals("Nothing in the iterator", true, lineToTranslateIterator.hasNext());
			PHParsedEntry e = (PHParsedEntry) lineToTranslateIterator.next();
			Assert.assertEquals("Invalid source line number", 0, e.getSourceLineNumber());
			Assert.assertEquals("Invalid destination line number", 0, e.getDestinationLineNumber());
			Assert.assertEquals("Invalid ID", "KEY", e.getID());
			Assert.assertEquals("Invalid reason", ParsedEntry.copyText, e.getReason());
		} else {
			Assert.fail("File not parsed");
		}
	}
	
	@Test
	public void fakeTranslation() {
		PHParsedFile f = (PHParsedFile) parsedFiles.getFile("Fake_Translation");
		if (f != null) {
			Assert.assertEquals("It should have 1 line to translate", 1, f.getNumberLineToTranslate());
			Assert.assertEquals("It should have no missing source text", 0, f.getNumberMissingSourceLines());
			Iterator<ParsedEntry> lineToTranslateIterator = f.getDescendingIteratorLineToTranslate();
			Assert.assertEquals("Nothing in the iterator", true, lineToTranslateIterator.hasNext());
			PHParsedEntry e = (PHParsedEntry) lineToTranslateIterator.next();
			Assert.assertEquals("Invalid source line number", 0, e.getSourceLineNumber());
			Assert.assertEquals("Invalid destination line number", 0, e.getDestinationLineNumber());
			Assert.assertEquals("Invalid ID", "KEY", e.getID());
			Assert.assertEquals("Invalid reason", ParsedEntry.fakeText, e.getReason());
		} else {
			Assert.fail("File not parsed");
		}
	}
	
	@Test
	public void textComment() {
		PHParsedFile f = (PHParsedFile) parsedFiles.getFile("Text_Comment");
		if (f != null) {
			Assert.assertEquals("It should have nothing to translate", 0, f.getNumberLineToTranslate());
			Assert.assertEquals("It should have no missing source text", 0, f.getNumberMissingSourceLines());
		} else {
			Assert.fail("File not parsed");
		}
	}
	
	@Test
	public void textDisorderedNotTranslated() {
		PHParsedFile f = (PHParsedFile) parsedFiles.getFile("Text_Disordered_Not_Translated");
		if (f != null) {
			Assert.assertEquals("It should have 2 lines to translate", 2, f.getNumberLineToTranslate());
			Assert.assertEquals("It should have no missing source text", 0, f.getNumberMissingSourceLines());
			Iterator<ParsedEntry> lineToTranslateIterator = f.getDescendingIteratorLineToTranslate();
			Assert.assertEquals("Nothing in the iterator", true, lineToTranslateIterator.hasNext());
			PHParsedEntry e = (PHParsedEntry) lineToTranslateIterator.next();
			Assert.assertEquals("Invalid source line number", 0, e.getSourceLineNumber());
			Assert.assertEquals("Invalid destination line number", 1, e.getDestinationLineNumber());
			Assert.assertEquals("Invalid ID", "TEXT1", e.getID());
			Assert.assertEquals("Invalid reason", ParsedEntry.copyText, e.getReason());
			e = (PHParsedEntry) lineToTranslateIterator.next();
			Assert.assertEquals("Invalid source line number", 1, e.getSourceLineNumber());
			Assert.assertEquals("Invalid destination line number", 0, e.getDestinationLineNumber());
			Assert.assertEquals("Invalid ID", "TEXT2", e.getID());
			Assert.assertEquals("Invalid reason", ParsedEntry.copyText, e.getReason());
		} else {
			Assert.fail("File not parsed");
		}
	}
	
	@Test
	public void textDisorderedTranslated_l() {
		PHParsedFile f = (PHParsedFile) parsedFiles.getFile("Text_Disordered_Translated");
		if (f != null) {
			Assert.assertEquals("It should have nothing to translate", 0, f.getNumberLineToTranslate());
			Assert.assertEquals("It should have no missing source text", 0, f.getNumberMissingSourceLines());
		} else {
			Assert.fail("File not parsed");
		}
	}
	
	@Test
	public void text() {
		PHParsedFile f = (PHParsedFile) parsedFiles.getFile("Text");
		if (f != null) {
			Assert.assertEquals("It should have nothing to translate", 0, f.getNumberLineToTranslate());
			Assert.assertEquals("It should have no missing source text", 0, f.getNumberMissingSourceLines());
		} else {
			Assert.fail("File not parsed");
		}
	}
	
	@Test
	public void textOnlyInEnglish() {
		PHParsedFile f = (PHParsedFile) parsedFiles.getFile("Text_Only_In_English");
		if (f != null) {
			Assert.assertEquals("It should have 1 line to translate", 1, f.getNumberLineToTranslate());
			Assert.assertEquals("It should have no missing source text", 0, f.getNumberMissingSourceLines());
			Iterator<ParsedEntry> lineToTranslateIterator = f.getDescendingIteratorLineToTranslate();
			Assert.assertEquals("Nothing in the iterator", true, lineToTranslateIterator.hasNext());
			PHParsedEntry e = (PHParsedEntry) lineToTranslateIterator.next();
			Assert.assertEquals("Invalid source line number", 0, e.getSourceLineNumber());
			Assert.assertEquals("Invalid destination line number", PHParsedEntry.MISSING_ENTRY, e.getDestinationLineNumber());
			Assert.assertEquals("Invalid ID", "TEXT", e.getID());
			Assert.assertEquals("Invalid reason", ParsedEntry.missingText, e.getReason());
		} else {
			Assert.fail("File not parsed");
		}
	}
	
	@Test
	public void textOnlyInFrench() {
		PHParsedFile f = (PHParsedFile) parsedFiles.getFile("Text_Only_In_French");
		if (f != null) {
			Assert.assertEquals("It should have no line to translate", 0, f.getNumberLineToTranslate());
			Assert.assertEquals("It should have 1 missing source text", 1, f.getNumberMissingSourceLines());
			Iterator<ParsedEntry> missingSourceIterator = f.getDescendingIteratorMissingSourceLines();
			Assert.assertEquals("Nothing in the iterator", true, missingSourceIterator.hasNext());
			PHParsedEntry e = (PHParsedEntry) missingSourceIterator.next();
			Assert.assertEquals("Invalid source line number", PHParsedEntry.MISSING_ENTRY, e.getSourceLineNumber());
			Assert.assertEquals("Invalid destination line number", 0, e.getDestinationLineNumber());
			Assert.assertEquals("Invalid ID", "TEXT", e.getID());
			Assert.assertEquals("Invalid reason", ParsedEntry.missingText, e.getReason());
		} else {
			Assert.fail("File not parsed");
		}
	}
	
	@Test
	public void textWith2Points() {
		PHParsedFile f = (PHParsedFile) parsedFiles.getFile("Text_With_Balises");
		if (f != null) {
			Assert.assertEquals("It should have nothing to translate", 0, f.getNumberLineToTranslate());
			Assert.assertEquals("It should have no missing source text", 0, f.getNumberMissingSourceLines());
		} else {
			Assert.fail("File not parsed");
		}
	}
	
	@Test
	public void textWithoutEnglish() {
		PHParsedFile f = (PHParsedFile) parsedFiles.getFile("Text_Without_English");
		if (f != null) {
			Assert.assertEquals("It should have no line to translate", 0, f.getNumberLineToTranslate());
			Assert.assertEquals("It should have 1 missing source text", 1, f.getNumberMissingSourceLines());
			Iterator<ParsedEntry> missingSourceIterator = f.getDescendingIteratorMissingSourceLines();
			Assert.assertEquals("Nothing in the iterator", true, missingSourceIterator.hasNext());
			PHParsedEntry e = (PHParsedEntry) missingSourceIterator.next();
			Assert.assertEquals("Invalid source line number", 0, e.getSourceLineNumber());
			Assert.assertEquals("Invalid destination line number", 0, e.getDestinationLineNumber());
			Assert.assertEquals("Invalid ID", "TEXT", e.getID());
			Assert.assertEquals("Invalid reason", ParsedEntry.missingText, e.getReason());
		} else {
			Assert.fail("File not parsed");
		}
	}
	
	@Test
	public void textWithoutFrench() {
		PHParsedFile f = (PHParsedFile) parsedFiles.getFile("Text_Without_French");
		if (f != null) {
			Assert.assertEquals("It should have 1 line to translate", 1, f.getNumberLineToTranslate());
			Assert.assertEquals("It should have no missing source text", 0, f.getNumberMissingSourceLines());
			Iterator<ParsedEntry> lineToTranslateIterator = f.getDescendingIteratorLineToTranslate();
			Assert.assertEquals("Nothing in the iterator", true, lineToTranslateIterator.hasNext());
			PHParsedEntry e = (PHParsedEntry) lineToTranslateIterator.next();
			Assert.assertEquals("Invalid source line number", 0, e.getSourceLineNumber());
			Assert.assertEquals("Invalid destination line number", 0, e.getDestinationLineNumber());
			Assert.assertEquals("Invalid ID", "TEXT", e.getID());
			Assert.assertEquals("Invalid reason", ParsedEntry.missingText, e.getReason());
		} else {
			Assert.fail("File not parsed");
		}
	}
}
