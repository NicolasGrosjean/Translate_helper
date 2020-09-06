package tests;

import java.util.Iterator;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import parsing.HoI4ParsedEntry;
import parsing.HoI4ParsedFile;
import parsing.Language;
import parsing.Parse;
import parsing.ParsedEntry;

public class TestParsingCK3 {
	private static Parse parsedFiles;

	@BeforeClass
	public static void SetUp() {
		// Translate English to French
		parsedFiles = new Parse(Parse.listDirectoryFiles("./test_localisation_files/ck3"),
				new Language("ENGLISH", 1, "en"), new Language("FRENCH", 2, "fr"),
				"config/fake_translations.txt", "config/accepted_loanwords.txt", false,
				"english", "french");
	}

	@Test
	public void testListDirectoryEnglishFiles() {
		String dir = "./test_localisation_files/ck3/english/";
		LinkedList<String> filePaths = Parse.listDirectoryFiles(dir);
		String[] expected = {
				"council_l_english",
				"opinions/council_l_english",
		};
		Assert.assertEquals("Incorrect number of file", expected.length, filePaths.size());
		for (int i = 0; i < expected.length; i++) {
			Assert.assertEquals("Incorrect file found!", dir + expected[i] + ".yml",
					filePaths.removeFirst().replace("\\", "/"));
		}
	}

	@Test
	public void testListDirectoryFrenchFiles() {
		String dir = "./test_localisation_files/ck3/french/";
		LinkedList<String> filePaths = Parse.listDirectoryFiles(dir);
		String[] expected = {
				"council_l_french",
				"opinions/council_l_french",
		};
		Assert.assertEquals("Incorrect number of file", expected.length, filePaths.size());
		for (int i = 0; i < expected.length; i++) {
			Assert.assertEquals("Incorrect file found!", dir + expected[i] + ".yml",
					filePaths.removeFirst().replace("\\", "/"));
		}
	}
	
	private void assertLineToTranslate(Iterator<ParsedEntry> lineToTranslateIterator, int lineNb, String id, String reason) {
		Assert.assertEquals("Nothing in the iterator", true, lineToTranslateIterator.hasNext());
		HoI4ParsedEntry e = (HoI4ParsedEntry) lineToTranslateIterator.next();
		Assert.assertEquals("Invalid source line number", lineNb, e.getSourceLineNumber());
		Assert.assertEquals("Invalid destination line number", lineNb, e.getDestinationLineNumber());
		Assert.assertEquals("Invalid ID", id, e.getID());
		Assert.assertEquals("Invalid reason", reason, e.getReason());
	}

	@Test
	public void council() {
		HoI4ParsedFile f = (HoI4ParsedFile) parsedFiles.getFile("council");
		if (f != null) {
			Assert.assertEquals("It should have 11 lines to translate", 13, f.getNumberLineToTranslate());
			Assert.assertEquals("It should have no missing source text", 0, f.getNumberMissingSourceLines());
			Iterator<ParsedEntry> lineToTranslateIterator = f.getDescendingIteratorLineToTranslate();
			assertLineToTranslate(lineToTranslateIterator, 6, "COUNCIL_NOT_VALID_CHARACTER", ParsedEntry.nonUpdated);
			assertLineToTranslate(lineToTranslateIterator, 19, "START_COUNCIL_TASK_TITLE", ParsedEntry.copyText);
			assertLineToTranslate(lineToTranslateIterator, 32, "COUNCILLOR_LIEGE_MISSING_SPOUSE", ParsedEntry.nonUpdated);
			assertLineToTranslate(lineToTranslateIterator, 35, "learn_on_the_job_modifier", ParsedEntry.copyText);
			assertLineToTranslate(lineToTranslateIterator, 93, "councillor_court_chaplain_islam_duchy", ParsedEntry.copyText);
			assertLineToTranslate(lineToTranslateIterator, 95, "councillor_court_chaplain_islam_kingdom", ParsedEntry.copyText);
			assertLineToTranslate(lineToTranslateIterator, 101, "councillor_court_chaplain_ismaili_county", ParsedEntry.copyText);
			assertLineToTranslate(lineToTranslateIterator, 103, "councillor_court_chaplain_ismaili_kingdom", ParsedEntry.copyText);
			assertLineToTranslate(lineToTranslateIterator, 109, "councillor_court_chaplain_bon_religion_kingdom", ParsedEntry.copyText);
			assertLineToTranslate(lineToTranslateIterator, 113, "councillor_court_chaplain_buddhism_religion_county", ParsedEntry.copyText);
			assertLineToTranslate(lineToTranslateIterator, 114, "councillor_court_chaplain_buddhism_religion_county_female", ParsedEntry.copyText);
			assertLineToTranslate(lineToTranslateIterator, 117, "councillor_court_chaplain_buddhism_religion_duchy", ParsedEntry.copyText);
			assertLineToTranslate(lineToTranslateIterator, 119, "councillor_court_chaplain_buddhism_religion_kingdom", ParsedEntry.copyText);
		} else {
			Assert.fail("File not parsed");
		}
	}

	@Test
	public void opinionCouncil() {
		HoI4ParsedFile f = (HoI4ParsedFile) parsedFiles.getFile("opinions\\council");
		if (f != null) {
			Assert.assertEquals("It should have nothing to translate", 0, f.getNumberLineToTranslate());
			Assert.assertEquals("It should have no missing source text", 0, f.getNumberMissingSourceLines());
		} else {
			Assert.fail("File not parsed");
		}
	}
}
