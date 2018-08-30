package tests;

import java.util.Iterator;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import parsing.HoI4ParsedEntry;
import parsing.Language;
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
				"config/fake_translations.txt", "config/accepted_loanwords.txt");
	}

	@Test
	public void testListDirectoryFiles() {
		String dir = "./test_localisation_files/ph/";
		LinkedList<String> filePaths = Parse.listDirectoryFiles(dir);
		String[] expected = {
				"English_Accepted_Copy",
				"English_Accepted_Copy_With_Accent",
				"English_Bad_Copy",
				"English_Fake_Translation",
				"English_Text",
				"English_Text_Comment",
				"English_Text_Disordered_Not_Translated",
				"English_Text_Disordered_Translated",
				"English_Text_Only_In_English",
				"English_Text_Without_English",
				"English_Text_Without_French",
				"English_Text_With_Balises",
				"French_Accepted_Copy",
				"French_Accepted_Copy_With_Accent",
				"French_Bad_Copy",
				"French_Fake_Translation",
				"French_Text",
				"French_Text_Comment",
				"French_Text_Disordered_Not_Translated",
				"French_Text_Disordered_Translated",
				"French_Text_Only_In_French",
				"French_Text_Without_English",
				"French_Text_Without_French",
				"French_Text_With_Balises",
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
			HoI4ParsedEntry e = (HoI4ParsedEntry) lineToTranslateIterator.next();
			Assert.assertEquals("Invalid source line number", 2, e.getSourceLineNumber());
			Assert.assertEquals("Invalid destination line number", 2, e.getDestinationLineNumber());
			Assert.assertEquals("Invalid ID", "TEXT", e.getID());
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
			HoI4ParsedEntry e = (HoI4ParsedEntry) lineToTranslateIterator.next();
			Assert.assertEquals("Invalid source line number", 2, e.getSourceLineNumber());
			Assert.assertEquals("Invalid destination line number", 2, e.getDestinationLineNumber());
			Assert.assertEquals("Invalid ID", "TEXT", e.getID());
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
			HoI4ParsedEntry e = (HoI4ParsedEntry) lineToTranslateIterator.next();
			Assert.assertEquals("Invalid source line number", 2, e.getSourceLineNumber());
			Assert.assertEquals("Invalid destination line number", 3, e.getDestinationLineNumber());
			Assert.assertEquals("Invalid ID", "TEXT1", e.getID());
			Assert.assertEquals("Invalid reason", ParsedEntry.copyText, e.getReason());
			e = (HoI4ParsedEntry) lineToTranslateIterator.next();
			Assert.assertEquals("Invalid source line number", 3, e.getSourceLineNumber());
			Assert.assertEquals("Invalid destination line number", 2, e.getDestinationLineNumber());
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
			HoI4ParsedEntry e = (HoI4ParsedEntry) lineToTranslateIterator.next();
			Assert.assertEquals("Invalid source line number", 2, e.getSourceLineNumber());
			Assert.assertEquals("Invalid destination line number", HoI4ParsedEntry.MISSING_ENTRY, e.getDestinationLineNumber());
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
			HoI4ParsedEntry e = (HoI4ParsedEntry) missingSourceIterator.next();
			Assert.assertEquals("Invalid source line number", HoI4ParsedEntry.MISSING_ENTRY, e.getSourceLineNumber());
			Assert.assertEquals("Invalid destination line number", 2, e.getDestinationLineNumber());
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
			HoI4ParsedEntry e = (HoI4ParsedEntry) missingSourceIterator.next();
			Assert.assertEquals("Invalid source line number", 2, e.getSourceLineNumber());
			Assert.assertEquals("Invalid destination line number", 2, e.getDestinationLineNumber());
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
			HoI4ParsedEntry e = (HoI4ParsedEntry) lineToTranslateIterator.next();
			Assert.assertEquals("Invalid source line number", 2, e.getSourceLineNumber());
			Assert.assertEquals("Invalid destination line number", 2, e.getDestinationLineNumber());
			Assert.assertEquals("Invalid ID", "TEXT", e.getID());
			Assert.assertEquals("Invalid reason", ParsedEntry.missingText, e.getReason());
		} else {
			Assert.fail("File not parsed");
		}
	}
}
