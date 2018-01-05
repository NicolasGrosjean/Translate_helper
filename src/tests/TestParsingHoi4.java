package tests;

import java.util.Iterator;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import parsing.HoI4ParsedFile;
import parsing.HoI4ParsedEntry;
import parsing.Language;
import parsing.Parse;
import parsing.ParsedEntry;

public class TestParsingHoi4 {
	private static Parse parsedFiles;

	@BeforeClass
	public static void SetUp() {
		parsedFiles = new Parse(Parse.listDirectoryFiles("./test_localisation_files/hoi4"),
				new Language("FRENCH", 2, "fr"), new Language("ENGLISH", 1, "en"),
				"config/fake_translations.txt", "config/accepted_loanwords.txt");
	}

	@Test
	public void testListDirectoryFiles() {
		String dir = "./test_localisation_files/hoi4/";
		LinkedList<String> filePaths = Parse.listDirectoryFiles(dir);
		String[] expected = {
				"accepted_copy_l_english",
				"accepted_copy_l_french",
				"accepted_copy_with_accent_l_english",
				"accepted_copy_with_accent_l_french",
				"bad_copy_l_english",
				"bad_copy_l_french",
				"fake_translation_l_english",
				"fake_translation_l_french",
				"text_comment_l_english",
				"text_comment_l_french",
				"text_disordered_not_translated_l_english",
				"text_disordered_not_translated_l_french",
				"text_disordered_translated_l_english",
				"text_disordered_translated_l_french",
				"text_l_english",
				"text_l_french",
				"text_only_in_english_l_english",
				"text_only_in_french_l_french",
				"text_without_english_l_english",
				"text_without_english_l_french",
				"text_without_french_l_english",
				"text_without_french_l_french"
		};
		Assert.assertEquals("Incorrect number of file", expected.length, filePaths.size());
		for (int i = 0; i < expected.length; i++) {
			Assert.assertEquals("Incorrect file found!", dir + expected[i] + ".yml",
					filePaths.removeFirst().replace("\\", "/"));
		}
	}

	@Test
	public void acceptedCopy() {
		HoI4ParsedFile f = (HoI4ParsedFile) parsedFiles.getFile("accepted_copy_l");
		if (f != null) {
			Assert.assertEquals("It should have nothing to translate", 0, f.getNumberLineToTranslate());
			Assert.assertEquals("It should have no missing source text", 0, f.getNumberMissingSourceLines());
		} else {
			Assert.fail("File not parsed");
		}
	}

	@Test
	public void acceptedCopyWithAccents() {
		HoI4ParsedFile f = (HoI4ParsedFile) parsedFiles.getFile("accepted_copy_with_accent_l");
		if (f != null) {
			Assert.assertEquals("It should have nothing to translate", 0, f.getNumberLineToTranslate());
			Assert.assertEquals("It should have no missing source text", 0, f.getNumberMissingSourceLines());
		} else {
			Assert.fail("File not parsed");
		}
	}


	@Test
	public void badCopy() {
		HoI4ParsedFile f = (HoI4ParsedFile) parsedFiles.getFile("bad_copy_l");
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
		HoI4ParsedFile f = (HoI4ParsedFile) parsedFiles.getFile("fake_translation_l");
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
		HoI4ParsedFile f = (HoI4ParsedFile) parsedFiles.getFile("text_comment_l");
		if (f != null) {
			Assert.assertEquals("It should have nothing to translate", 0, f.getNumberLineToTranslate());
			Assert.assertEquals("It should have no missing source text", 0, f.getNumberMissingSourceLines());
		} else {
			Assert.fail("File not parsed");
		}
	}
	
	@Test
	public void textDisorderedNotTranslated_l() {
		HoI4ParsedFile f = (HoI4ParsedFile) parsedFiles.getFile("text_disordered_not_translated_l");
		if (f != null) {
			Assert.assertEquals("It should have 2 lines to translate", 2, f.getNumberLineToTranslate());
			Assert.assertEquals("It should have no missing source text", 0, f.getNumberMissingSourceLines());
			Iterator<ParsedEntry> lineToTranslateIterator = f.getDescendingIteratorLineToTranslate();
			Assert.assertEquals("Nothing in the iterator", true, lineToTranslateIterator.hasNext());
			HoI4ParsedEntry e = (HoI4ParsedEntry) lineToTranslateIterator.next();
			Assert.assertEquals("Invalid source line number", 1, e.getSourceLineNumber());
			Assert.assertEquals("Invalid destination line number", 2, e.getDestinationLineNumber());
			Assert.assertEquals("Invalid ID", "TEXT1", e.getID());
			Assert.assertEquals("Invalid reason", ParsedEntry.copyText, e.getReason());
			e = (HoI4ParsedEntry) lineToTranslateIterator.next();
			Assert.assertEquals("Invalid source line number", 2, e.getSourceLineNumber());
			Assert.assertEquals("Invalid destination line number", 1, e.getDestinationLineNumber());
			Assert.assertEquals("Invalid ID", "TEXT2", e.getID());
			Assert.assertEquals("Invalid reason", ParsedEntry.copyText, e.getReason());
		} else {
			Assert.fail("File not parsed");
		}
	}
	
	@Test
	public void textDisorderedTranslated_l() {
		HoI4ParsedFile f = (HoI4ParsedFile) parsedFiles.getFile("text_disordered_translated_l");
		if (f != null) {
			Assert.assertEquals("It should have nothing to translate", 0, f.getNumberLineToTranslate());
			Assert.assertEquals("It should have no missing source text", 0, f.getNumberMissingSourceLines());
		} else {
			Assert.fail("File not parsed");
		}
	}
	
	@Test
	public void text() {
		HoI4ParsedFile f = (HoI4ParsedFile) parsedFiles.getFile("text_l");
		if (f != null) {
			Assert.assertEquals("It should have nothing to translate", 0, f.getNumberLineToTranslate());
			Assert.assertEquals("It should have no missing source text", 0, f.getNumberMissingSourceLines());
		} else {
			Assert.fail("File not parsed");
		}
	}
	
	@Test
	public void textOnlyInEnglish() {
		HoI4ParsedFile f = (HoI4ParsedFile) parsedFiles.getFile("text_only_in_english_l");
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
		HoI4ParsedFile f = (HoI4ParsedFile) parsedFiles.getFile("text_only_in_french_l");
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
	public void textWithoutEnglish() {
		HoI4ParsedFile f = (HoI4ParsedFile) parsedFiles.getFile("text_without_english_l");
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
		HoI4ParsedFile f = (HoI4ParsedFile) parsedFiles.getFile("text_without_french_l");
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
