package tests;

import java.util.Iterator;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import parsing.ParsedEntry;
import parsing.IParsedFile;
import parsing.Language;
import parsing.Parse;
import parsing.CK2ParsedFile;

/**
 * Test the parsing package NOTA : To run it with Eclipse, go to the project >
 * Properties > Add Library... > JUnit
 * 
 * @author Nicolas Grosjean
 *
 */
public class TestParsingCK2 {
	private static Parse parsedFiles;

	@BeforeClass
	public static void SetUp() {
		parsedFiles = new Parse(Parse.listDirectoryFiles("./test_localisation_files/ck2"),
				new Language("FRENCH", 2, "fr"), new Language("ENGLISH", 1, "en"),
				"config/fake_translations.txt", "config/accepted_loanwords.txt");
	}

	@Test
	public void testListDirectoryFiles() {
		String dir = "./test_localisation_files/ck2/";
		LinkedList<String> filePaths = Parse.listDirectoryFiles(dir);
		Assert.assertEquals("Incorrect file found!", dir + "accepted_copy.csv",
				filePaths.removeFirst().replace("\\", "/"));
		Assert.assertEquals("Incorrect file found!", dir + "accepted_copy_with_accents.csv",
				filePaths.removeFirst().replace("\\", "/"));
		Assert.assertEquals("Incorrect file found!", dir + "code_line.csv",
				filePaths.removeFirst().replace("\\", "/"));
		Assert.assertEquals("Incorrect file found!", dir + "code_line_to_translate.csv",
				filePaths.removeFirst().replace("\\", "/"));
		Assert.assertEquals("Incorrect file found!", dir + "comment_line.csv",
				filePaths.removeFirst().replace("\\", "/"));
		Assert.assertEquals("Incorrect file found!", dir + "fake_translation.csv",
				filePaths.removeFirst().replace("\\", "/"));
		Assert.assertEquals("Incorrect file found!", dir + "missing_source.csv",
				filePaths.removeFirst().replace("\\", "/"));
		Assert.assertEquals("Incorrect file found!", dir + "missing_translation.csv",
				filePaths.removeFirst().replace("\\", "/"));
		Assert.assertEquals("Incorrect file found!", dir + "nothing_to_translate.csv",
				filePaths.removeFirst().replace("\\", "/"));
		Assert.assertEquals("Incorrect file found!", dir + "refused_copy.csv",
				filePaths.removeFirst().replace("\\", "/"));
		Assert.assertEquals("Incorrect file found!", dir + "several_lines_to_end.csv",
				filePaths.removeFirst().replace("\\", "/"));
	}

	@Test
	public void nothingToTranslate() {
		IParsedFile f = parsedFiles.getFile("nothing_to_translate.csv");
		if (f != null) {
			Assert.assertEquals("It should have nothing to translate", 0, f.getNumberLineToTranslate());
			Assert.assertEquals("It should have no missing source text", 0, f.getNumberMissingSourceLines());
		} else {
			Assert.assertEquals("File not parsed", true, false); // Exception
		}
	}

	@Test
	public void codeLine() {
		IParsedFile f = parsedFiles.getFile("code_line.csv");
		if (f != null) {
			Assert.assertEquals("It should have nothing to translate", 0, f.getNumberLineToTranslate());
			Assert.assertEquals("It should have no missing source text", 0, f.getNumberMissingSourceLines());
		} else {
			Assert.assertEquals("File not parsed", true, false); // Exception
		}
	}

	@Test
	public void commentLine() {
		IParsedFile f = parsedFiles.getFile("comment_line.csv");
		if (f != null) {
			Assert.assertEquals("It should have nothing to translate", 0, f.getNumberLineToTranslate());
			Assert.assertEquals("It should have no missing source text", 0, f.getNumberMissingSourceLines());
		} else {
			Assert.assertEquals("File not parsed", true, false); // Exception
		}
	}

	@Ignore @Test
	public void codeLineToTranslate() {
		// TODO Check that this test is true
		CK2ParsedFile f = (CK2ParsedFile) parsedFiles.getFile("code_line_to_translate.csv");
		if (f != null) {
			Assert.assertEquals("It should have 1 line to translate", 1, f.getNumberLineToTranslate());
			Assert.assertEquals("It should have no missing source text", 0, f.getNumberMissingSourceLines());
			Iterator<ParsedEntry> lineToTranslateIterator = f.getDescendingIteratorLineToTranslate();
			Assert.assertEquals("Nothing in the iterator", true, lineToTranslateIterator.hasNext());
			ParsedEntry e = lineToTranslateIterator.next();
			Assert.assertEquals("Invalid line number", 2, e.getDestinationLineNumber());
			Assert.assertEquals("Invalid ID", "TRADE.0005A", e.getID());
			Assert.assertEquals("Invalid reason", ParsedEntry.missingText, e.getReason());
		} else {
			Assert.assertEquals("File not parsed", true, false); // Exception
		}
	}

	@Test
	public void missingTranslation() {
		CK2ParsedFile f = (CK2ParsedFile) parsedFiles.getFile("missing_translation.csv");
		if (f != null) {
			Assert.assertEquals("It should have 1 line to translate", 1, f.getNumberLineToTranslate());
			Assert.assertEquals("It should have no missing source text", 0, f.getNumberMissingSourceLines());
			Iterator<ParsedEntry> lineToTranslateIterator = f.getDescendingIteratorLineToTranslate();
			Assert.assertEquals("Nothing in the iterator", true, lineToTranslateIterator.hasNext());
			ParsedEntry e = lineToTranslateIterator.next();
			Assert.assertEquals("Invalid line number", 2, e.getDestinationLineNumber());
			Assert.assertEquals("Invalid ID", "TRADE.0005A", e.getID());
			Assert.assertEquals("Invalid reason", ParsedEntry.missingText, e.getReason());
		} else {
			Assert.assertEquals("File not parsed", true, false); // Exception
		}
	}

	@Test
	public void missingSource() {
		CK2ParsedFile f = (CK2ParsedFile) parsedFiles.getFile("missing_source.csv");
		if (f != null) {
			Assert.assertEquals("It should have 2 lines to translate", 2, f.getNumberLineToTranslate());
			Assert.assertEquals("It should have 2 missing source texts", 2, f.getNumberMissingSourceLines());
			Iterator<ParsedEntry> lineToTranslateIterator = f.getDescendingIteratorLineToTranslate();
			Iterator<ParsedEntry> missingSourceLinesIterator = f.getDescendingIteratorMissingSourceLines();
			// First line to translate
			Assert.assertEquals("Nothing in the iterator", true, lineToTranslateIterator.hasNext());
			ParsedEntry e = lineToTranslateIterator.next();
			Assert.assertEquals("Invalid line number", 2, e.getDestinationLineNumber());
			Assert.assertEquals("Invalid ID", "TRADE.0005B", e.getID());
			Assert.assertEquals("Invalid reason", ParsedEntry.missingText, e.getReason());
			// Second line to translate
			Assert.assertEquals("Nothing in the iterator", true, lineToTranslateIterator.hasNext());
			e = lineToTranslateIterator.next();
			Assert.assertEquals("Invalid line number", 1, e.getDestinationLineNumber());
			Assert.assertEquals("Invalid ID", "TRADE.0005A", e.getID());
			Assert.assertEquals("Invalid reason", ParsedEntry.missingText, e.getReason());
			// First missing source text
			Assert.assertEquals("Nothing in the iterator", true, missingSourceLinesIterator.hasNext());
			e = missingSourceLinesIterator.next();
			Assert.assertEquals("Invalid line number", 2, e.getSourceLineNumber());
			Assert.assertEquals("Invalid ID", "TRADE.0005B", e.getID());
			Assert.assertEquals("Invalid reason", ParsedEntry.missingText, e.getReason());
			// Second missing source text
			Assert.assertEquals("Nothing in the iterator", true, missingSourceLinesIterator.hasNext());
			e = missingSourceLinesIterator.next();
			Assert.assertEquals("Invalid line number", 1, e.getSourceLineNumber());
			Assert.assertEquals("Invalid ID", "TRADE.0005A", e.getID());
			Assert.assertEquals("Invalid reason", ParsedEntry.missingText, e.getReason());
		} else {
			Assert.assertEquals("File not parsed", true, false); // Exception
		}
	}

	@Test
	public void acceptedCopy() {
		CK2ParsedFile f = (CK2ParsedFile) parsedFiles.getFile("accepted_copy.csv");
		if (f != null) {
			Assert.assertEquals("It should have nothing to translate", 0, f.getNumberLineToTranslate());
			Assert.assertEquals("It should have no missing source text", 0, f.getNumberMissingSourceLines());
		} else {
			Assert.assertEquals("File not parsed", true, false); // Exception
		}
	}

	@Test
	public void acceptedCopyWithAccents() {
		CK2ParsedFile f = (CK2ParsedFile) parsedFiles.getFile("accepted_copy_with_accents.csv");
		if (f != null) {
			Assert.assertEquals("It should have nothing to translate", 0, f.getNumberLineToTranslate());
			Assert.assertEquals("It should have no missing source text", 0, f.getNumberMissingSourceLines());
		} else {
			Assert.assertEquals("File not parsed", true, false); // Exception
		}
	}

	@Test
	public void fake_translation() {
		CK2ParsedFile f = (CK2ParsedFile) parsedFiles.getFile("fake_translation.csv");
		if (f != null) {
			Assert.assertEquals("It should have 1 line to translate", 1, f.getNumberLineToTranslate());
			Assert.assertEquals("It should have no missing source text", 0, f.getNumberMissingSourceLines());
			Iterator<ParsedEntry> lineToTranslateIterator = f.getDescendingIteratorLineToTranslate();
			Assert.assertEquals("Nothing in the iterator", true, lineToTranslateIterator.hasNext());
			ParsedEntry e = lineToTranslateIterator.next();
			Assert.assertEquals("Invalid line number", 2, e.getDestinationLineNumber());
			Assert.assertEquals("Invalid ID", "TRADE.0005A", e.getID());
			Assert.assertEquals("Invalid reason", ParsedEntry.fakeText, e.getReason());
		} else {
			Assert.assertEquals("File not parsed", true, false); // Exception
		}
	}

	@Test
	public void refusedCopy() {
		CK2ParsedFile f = (CK2ParsedFile) parsedFiles.getFile("refused_copy.csv");
		if (f != null) {
			Assert.assertEquals("It should have 1 line to translate", 1, f.getNumberLineToTranslate());
			Assert.assertEquals("It should have no missing source text", 0, f.getNumberMissingSourceLines());
			Iterator<ParsedEntry> lineToTranslateIterator = f.getDescendingIteratorLineToTranslate();
			Assert.assertEquals("Nothing in the iterator", true, lineToTranslateIterator.hasNext());
			ParsedEntry e = lineToTranslateIterator.next();
			Assert.assertEquals("Invalid line number", 2, e.getDestinationLineNumber());
			Assert.assertEquals("Invalid ID", "TRADE.0005A", e.getID());
			Assert.assertEquals("Invalid reason", ParsedEntry.copyText, e.getReason());
		} else {
			Assert.assertEquals("File not parsed", true, false); // Exception
		}
	}
}
