package tests;

import java.util.LinkedList;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import parsing.Language;
import parsing.Parse;

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

}
