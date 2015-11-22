package tests;

import java.util.LinkedList;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import parsing.ParsedEntry;
import parsing.Parse;
import parsing.ParsedFile;

/**
 * To run it with Eclipse, go to the project > Properties > Add Library... > JUnit
 * @author Nicolas Grosjean
 *
 */
public class TestParsing {
	private static Parse parsedFiles;

	@BeforeClass
	public static void SetUp() {
		parsedFiles = new Parse(Parse.listDirectoryFiles("C:/Users/Nicolas/workspace/Translate_helper/test_localisation_files"),
				"FRENCH", 2, "ENGLISH", 0);
	}

	@Test
	public void testListDirectoryFiles() {
		LinkedList<String> filePaths = Parse.listDirectoryFiles("C:/Users/Nicolas/workspace/Translate_helper/test_localisation_files");
		Assert.assertEquals("Incorrect file found!",
				"C:/Users/Nicolas/workspace/Translate_helper/test_localisation_files/code_line.csv",
				filePaths.removeFirst().replace("\\", "/"));
		Assert.assertEquals("Incorrect file found!",
				"C:/Users/Nicolas/workspace/Translate_helper/test_localisation_files/code_line_to_translate.csv",
				filePaths.removeFirst().replace("\\", "/"));		
		Assert.assertEquals("Incorrect file found!",
				"C:/Users/Nicolas/workspace/Translate_helper/test_localisation_files/comment_line.csv",
				filePaths.removeFirst().replace("\\", "/"));
		Assert.assertEquals("Incorrect file found!",
				"C:/Users/Nicolas/workspace/Translate_helper/test_localisation_files/missing_source.csv",
				filePaths.removeFirst().replace("\\", "/"));
		Assert.assertEquals("Incorrect file found!",
				"C:/Users/Nicolas/workspace/Translate_helper/test_localisation_files/nothing_to_translate.csv",
				filePaths.removeFirst().replace("\\", "/"));
		Assert.assertEquals("Incorrect file found!",
				"C:/Users/Nicolas/workspace/Translate_helper/test_localisation_files/order_changed.csv",
				filePaths.removeFirst().replace("\\", "/"));
		Assert.assertEquals("Incorrect file found!",
				"C:/Users/Nicolas/workspace/Translate_helper/test_localisation_files/translate.csv",
				filePaths.removeFirst().replace("\\", "/"));
	}

	@Test
	public void nothingToTranslate() {
		ParsedFile f = parsedFiles.getFile("nothing_to_translate.csv");
		if (f != null) {
			Assert.assertEquals("It should have nothing to translate", 0, f.numberLineToTranslate());
			Assert.assertEquals("It should have no missing source text", 0, f.numberMissingSourceLines());
		} else {
			Assert.assertEquals("File not parsed", true, false); // Exception
		}
	}

	@Test
	public void codeLine() {
		ParsedFile f = parsedFiles.getFile("code_line.csv");
		if (f != null) {
			Assert.assertEquals("It should have nothing to translate", 0, f.numberLineToTranslate());
			Assert.assertEquals("It should have no missing source text", 0, f.numberMissingSourceLines());
		} else {
			Assert.assertEquals("File not parsed", true, false); // Exception
		}
	}

	@Test
	public void commentLine() {
		ParsedFile f = parsedFiles.getFile("comment_line.csv");
		if (f != null) {
			Assert.assertEquals("It should have nothing to translate", 0, f.numberLineToTranslate());
			Assert.assertEquals("It should have no missing source text", 0, f.numberMissingSourceLines());
		} else {
			Assert.assertEquals("File not parsed", true, false); // Exception
		}
	}

	@Test
	public void codeLineToTranslate() {
		ParsedFile f = parsedFiles.getFile("code_line_to_translate.csv");
		if (f != null) {
			Assert.assertEquals("It should have 1 line to translate", 1, f.numberLineToTranslate());
			Assert.assertEquals("It should have no missing source text", 0, f.numberMissingSourceLines());
			Assert.assertEquals("Nothing in the iterator", true, f.getDescendingIteratorLineToTranslate().hasNext());
			ParsedEntry e = f.getDescendingIteratorLineToTranslate().next();
			Assert.assertEquals("Invalid line number", 2, e.getLineNumber());
			Assert.assertEquals("Invalid ID", "TRADE.0005A", e.getID());
		} else {
			Assert.assertEquals("File not parsed", true, false); // Exception
		}
	}

	@Test
	public void translate() {
		ParsedFile f = parsedFiles.getFile("translate.csv");
		if (f != null) {
			Assert.assertEquals("It should have 1 line to translate", 1, f.numberLineToTranslate());
			Assert.assertEquals("It should have no missing source text", 0, f.numberMissingSourceLines());
			Assert.assertEquals("Nothing in the iterator", true, f.getDescendingIteratorLineToTranslate().hasNext());
			ParsedEntry e = f.getDescendingIteratorLineToTranslate().next();
			Assert.assertEquals("Invalid line number", 2, e.getLineNumber());
			Assert.assertEquals("Invalid ID", "TRADE.0005A", e.getID());
		} else {
			Assert.assertEquals("File not parsed", true, false); // Exception
		}
	}

	@Test
	public void orderChanged() {
		ParsedFile f = parsedFiles.getFile("order_changed.csv");
		if (f != null) {
			Assert.assertEquals("It should have 1 line to translate", 1, f.numberLineToTranslate());
			Assert.assertEquals("It should have no missing source text", 0, f.numberMissingSourceLines());
			Assert.assertEquals("Nothing in the iterator", true, f.getDescendingIteratorLineToTranslate().hasNext());
			ParsedEntry e = f.getDescendingIteratorLineToTranslate().next();
			Assert.assertEquals("Invalid line number", 2, e.getLineNumber());
			Assert.assertEquals("Invalid ID", "TRADE.0005A", e.getID());
		} else {
			Assert.assertEquals("File not parsed", true, false); // Exception
		}
	}
	
	@Test
	public void missingSource() {
		ParsedFile f = parsedFiles.getFile("order_changed.csv");
		if (f != null) {
			Assert.assertEquals("It should have 2 lines to translate", 2, f.numberLineToTranslate());
			Assert.assertEquals("It should have 2 missing source texts", 2, f.numberMissingSourceLines());
			Assert.assertEquals("Nothing in the iterator", true, f.getDescendingIteratorLineToTranslate().hasNext());
			// First line to translate
			ParsedEntry e = f.getDescendingIteratorLineToTranslate().next();
			Assert.assertEquals("Invalid line number", 1, e.getLineNumber());
			Assert.assertEquals("Invalid ID", "TRADE.0005A", e.getID());
			Assert.assertEquals("Nothing in the iterator", true, f.getDescendingIteratorLineToTranslate().hasNext());
			// Second line to translate
			e = f.getDescendingIteratorLineToTranslate().next();
			Assert.assertEquals("Invalid line number", 2, e.getLineNumber());
			Assert.assertEquals("Invalid ID", "TRADE.0005B", e.getID());
			Assert.assertEquals("Nothing in the iterator", true, f.getDescendingIteratorMissingSourceLines().hasNext());
			// First missing source text
			e = f.getDescendingIteratorMissingSourceLines().next();
			Assert.assertEquals("Invalid line number", 1, e.getLineNumber());
			Assert.assertEquals("Invalid ID", "TRADE.0005A", e.getID());
			Assert.assertEquals("Nothing in the iterator", true, f.getDescendingIteratorMissingSourceLines().hasNext());
			// Second missing source text
			e = f.getDescendingIteratorMissingSourceLines().next();
			Assert.assertEquals("Invalid line number", 2, e.getLineNumber());
			Assert.assertEquals("Invalid ID", "TRADE.0005B", e.getID());
		} else {
			Assert.assertEquals("File not parsed", true, false); // Exception
		}
	}
}
