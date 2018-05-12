package tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import parsing.CK2ParsedFile;
import parsing.Language;
import translator.TranslatedEntry;

public class TestSavingCK2 {
	private static TranslatedEntry entryToSave;
	private static Language sourceLanguage;
	private static Language destinationLanguage;

	@BeforeClass
	public static void SetUp() {
		entryToSave = new TranslatedEntry("Toto", "Tata", 3, 3, "toto");
		sourceLanguage = new Language("ENGLISH", 1, "en");
		destinationLanguage = new Language("FRENCH", 2, "fr");
	}

	@Test
	public void testSaveInCK2LocalisationFile() throws IOException {
		// Create data
		String filePath = "./test_localisation_files/ck2/save_file.csv";
		CK2ParsedFile file = new CK2ParsedFile(filePath);
		file.setLineNumber(5);
		file.addLastLineToTranslate(2, "TRADE.0005A", "", "OK", "J'en prends note");
		file.addLastLineToTranslate(3, "TRADE.0005B", "", "Toto", "Titi et grosminet");
		file.addLastLineToTranslate(4, "TRADE.0005C", "", "What?", "Quoi?");
		file.addLastLineToTranslate(5, "TRADE.0005D", "", "Okay", "D'ac");
		
		// Create a file corresponding to these data
		String[] expected = { "CODE;ENGLISH;FRENCH;;;;;;;;;;;;x",
							"TRADE.0005A;OK;J'en prends note;;Ja;;;Amigo;;;;;;;x",
							"TRADE.0005B;Toto;Titi et grosminet;;Nein;;Holla;;;;;;;;x",
							"TRADE.0005C;What?;Quoi?;;Schnell;;;;Gracie;;;;;;x",
							"TRADE.0005D;Okay;D'ac;;OK;;;;Ok;;;;;;x",};
		List<String> lines = Arrays.asList(expected);
		Path filetoWtrite = Paths.get(filePath);
		Files.write(filetoWtrite, lines, Charset.forName("Cp1252"));

		// Skip first line to translate
		file.getFirstEntryToTranslate();
		TranslatedEntry nextEntry = file.getNextEntryToTranslate();
		Assert.assertEquals("Incorrect next entry!", "Toto", nextEntry.getSource());
		Assert.assertEquals("Incorrect next entry!", "Titi et grosminet", nextEntry.getDestination());
		
		// Modify and save the file
		nextEntry = file.getNextEntryToTranslateAndSave(entryToSave, sourceLanguage, destinationLanguage);
		Assert.assertEquals("Incorrect next entry!", "What?", nextEntry.getSource());
		Assert.assertEquals("Incorrect next entry!", "Quoi?", nextEntry.getDestination());

		// Check that is what we expect
		expected[2] = "TRADE.0005B;Toto;Tata;;Nein;;Holla;;;;;;;;x";
		FileInputStream fis = new FileInputStream(filePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		try {
			String line = null;
			int i = 0;
			while ((line = br.readLine()) != null) {
				Assert.assertEquals("Incorrect line!", expected[i], line);
				i++;
			}
			Assert.assertEquals("Incorrect line number!", expected.length, i);
		} finally {
			br.close();
		}
	}

	@Test
	public void testSaveInCK2LocalisationFileSpecialChar() throws IOException {
		// Create data
		String filePath = "./test_localisation_files/ck2/save_file2.csv";
		CK2ParsedFile file = new CK2ParsedFile(filePath);
		file.setLineNumber(2);
		file.addLastLineToTranslate(2, "TRADE.0005A", "", "§YReligious head suitability: $SCORE$§!", "");
		
		// Create a file corresponding to these data
		String[] expected = { "CODE;ENGLISH;FRENCH;;;;;;;;;;;;x",
								"TRADE.0005A;§YReligious head suitability: $SCORE$§!;;;Ja;;;Amigo;;;;;;;x"};
		List<String> lines = Arrays.asList(expected);
		Path filetoWtrite = Paths.get(filePath);
		Files.write(filetoWtrite, lines, Charset.forName("Cp1252"));
		
		file.getFirstEntryToTranslate();
		// Modify and save the file
		TranslatedEntry entryToSave2 = new TranslatedEntry("§YReligious head suitability: $SCORE$§!",
				"Tata", 2, 2, "TRADE.0005A");
		file.getNextEntryToTranslateAndSave(entryToSave2, sourceLanguage, destinationLanguage);
		
		// Check that is what we expect
		expected[1] = "TRADE.0005A;§YReligious head suitability: $SCORE$§!;Tata;;Ja;;;Amigo;;;;;;;x";
		FileInputStream fis = new FileInputStream(filePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		try {
			String line = null;
			int i = 0;
			while ((line = br.readLine()) != null) {
				Assert.assertEquals("Incorrect line!", expected[i], line);
				i++;
			}
			Assert.assertEquals("Incorrect line number!", expected.length, i);
		} finally {
			br.close();
		}
	}

	@Test
	public void testSaveInCK2LocalisationFileAccents() throws IOException {
		// Create data
		String filePath = "./test_localisation_files/ck2/save_file2.csv";
		CK2ParsedFile file = new CK2ParsedFile(filePath);
		file.setLineNumber(2);
		String source = "Send a letter to our religious head in the hope of getting our sins forgiven and increasing our piety.";
		String dest = "Envoyons une lettre au chef religieux en espérant que nos péchés seront pardonnés et que notre piété s’améliorera."; 
				
		file.addLastLineToTranslate(2, "TRADE.0005A", "", source, dest);
		
		// Create a file corresponding to these data
		String[] expected = { "CODE;ENGLISH;FRENCH;;;;;;;;;;;;x",
				"buy_indulgence_for_sins_desc;" + source + ";" + dest + ";;Ja;;;Amigo;;;;;;;x"};
		List<String> lines = Arrays.asList(expected);
		Path filetoWtrite = Paths.get(filePath);
		Files.write(filetoWtrite, lines, Charset.forName("Cp1252"));
		
		file.getFirstEntryToTranslate();
		// Modify and save the file
		TranslatedEntry entryToSave2 = new TranslatedEntry(source, dest, 2, 2,
				"buy_indulgence_for_sins_desc");
		file.getNextEntryToTranslateAndSave(entryToSave2, sourceLanguage, destinationLanguage);
		
		// Check that is what we expect
		expected[1] = "buy_indulgence_for_sins_desc;" + source + ";" + dest + ";;Ja;;;Amigo;;;;;;;x";
		FileInputStream fis = new FileInputStream(filePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		try {
			String line = null;
			int i = 0;
			while ((line = br.readLine()) != null) {
				Assert.assertEquals("Incorrect line!", expected[i], line);
				i++;
			}
			Assert.assertEquals("Incorrect line number!", expected.length, i);
		} finally {
			br.close();
		}
	}

	@Test
	public void testNavigationAndSave() throws IOException {
		// Create data
		String filePath = "./test_localisation_files/ck2/save_file3.csv";
		CK2ParsedFile file = new CK2ParsedFile(filePath);
		file.setLineNumber(4);
		String code2 = "LINE2";
		String engText2 = "Line 2";
		String modEngText2 = engText2 + " modified";
		String frText2 = "Ligne 2";
		String modFrText2 = frText2 + " modifiée";
		String code3 = "LINE3";
		String engText3 = "Line 3";
		String modEngText3 = "Modified " + engText3.toLowerCase();
		String frText3 = "Ligne 3";
		String modFrText3 = frText3 + " a été modifiée";
		String code4 = "LINE4";
		String engText4 = "Line 4";
		String frText4 = "Ligne 4";
		file.addLastLineToTranslate(2, code2, "", engText2, frText2);
		file.addLastLineToTranslate(3, code3, "", engText3, frText3);
		file.addLastLineToTranslate(4, code4, "", engText4, frText4);
		
		// Create a file corresponding to these data
		String[] expected = { "CODE;ENGLISH;FRENCH;;;;;;;;;;;;x",
						code2 + ";" + modEngText2 + ";" + modFrText2 + ";;;;;;;;;;;;x",
						code3 + ";" + modEngText3 + ";" + modFrText3 + ";;;;;;;;;;;;x",
						code4 + ";" + engText4 + ";" + frText4 + ";;;;;;;;;;;;x"};
		List<String> lines = Arrays.asList(expected);
		Path filetoWtrite = Paths.get(filePath);
		Files.write(filetoWtrite, lines, Charset.forName("Cp1252"));
		
		TranslatedEntry firstEntry = file.getFirstEntryToTranslate();
		Assert.assertEquals("Incorrect entry at start", 2, firstEntry.getSourceLineNumber());
		Assert.assertEquals("Incorrect entry at start", 2, firstEntry.getDestLineNumber());
		Assert.assertEquals("Incorrect entry at start", code2, firstEntry.getId());
		Assert.assertEquals("Incorrect entry at start", engText2, firstEntry.getSource());
		Assert.assertEquals("Incorrect entry at start", frText2, firstEntry.getDestination());
		
		TranslatedEntry nextEntry = file.getNextEntryToTranslate();
		Assert.assertEquals("Incorrect entry at start", 3, nextEntry.getSourceLineNumber());
		Assert.assertEquals("Incorrect entry at start", 3, nextEntry.getDestLineNumber());
		Assert.assertEquals("Incorrect entry after next", code3, nextEntry.getId());
		Assert.assertEquals("Incorrect entry after next", engText3, nextEntry.getSource());
		Assert.assertEquals("Incorrect entry after next", frText3, nextEntry.getDestination());
		
		TranslatedEntry prevEntry = file.getPreviousEntryToTranslate();
		Assert.assertEquals("Incorrect entry at start", 2, prevEntry.getSourceLineNumber());
		Assert.assertEquals("Incorrect entry at start", 2, prevEntry.getDestLineNumber());
		Assert.assertEquals("Incorrect entry at start", code2, prevEntry.getId());
		Assert.assertEquals("Incorrect entry at start", engText2, prevEntry.getSource());
		Assert.assertEquals("Incorrect entry at start", frText2, prevEntry.getDestination());
						
		// Modify and save the file
		TranslatedEntry entryToSave2 = new TranslatedEntry(modEngText2,
				modFrText2, 2, 2, "LINE2");
		nextEntry = file.getNextEntryToTranslateAndSave(entryToSave2, sourceLanguage, destinationLanguage);
		Assert.assertEquals("Incorrect entry at start", 3, nextEntry.getSourceLineNumber());
		Assert.assertEquals("Incorrect entry at start", 3, nextEntry.getDestLineNumber());
		Assert.assertEquals("Incorrect entry after next", code3, nextEntry.getId());
		Assert.assertEquals("Incorrect entry after next", engText3, nextEntry.getSource());
		Assert.assertEquals("Incorrect entry after next", frText3, nextEntry.getDestination());
		
		TranslatedEntry entryToSave3 = new TranslatedEntry(modEngText3,
				modFrText3, 3, 3, "LINE3");
		nextEntry = file.getNextEntryToTranslateAndSave(entryToSave3, sourceLanguage, destinationLanguage);
		Assert.assertEquals("Incorrect entry at start", 4, nextEntry.getSourceLineNumber());
		Assert.assertEquals("Incorrect entry at start", 4, nextEntry.getDestLineNumber());
		Assert.assertEquals("Incorrect entry after next", code4, nextEntry.getId());
		Assert.assertEquals("Incorrect entry after next", engText4, nextEntry.getSource());
		Assert.assertEquals("Incorrect entry after next", frText4, nextEntry.getDestination());
		
		// Go back
		prevEntry = file.getPreviousEntryToTranslate();
		Assert.assertEquals("Incorrect entry at start", 3, prevEntry.getSourceLineNumber());
		Assert.assertEquals("Incorrect entry at start", 3, prevEntry.getDestLineNumber());
		Assert.assertEquals("Incorrect entry at start", code3, prevEntry.getId());
		Assert.assertEquals("Incorrect entry at start", modEngText3, prevEntry.getSource());
		Assert.assertEquals("Incorrect entry at start", modFrText3, prevEntry.getDestination());
		
		prevEntry = file.getPreviousEntryToTranslate();
		Assert.assertEquals("Incorrect entry at start", 2, prevEntry.getSourceLineNumber());
		Assert.assertEquals("Incorrect entry at start", 2, prevEntry.getDestLineNumber());
		Assert.assertEquals("Incorrect entry at start", code2, prevEntry.getId());
		Assert.assertEquals("Incorrect entry at start", modEngText2, prevEntry.getSource());
		Assert.assertEquals("Incorrect entry at start", modFrText2, prevEntry.getDestination());
		
		// Check that is what we expect
		FileInputStream fis = new FileInputStream(filePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		try {
			String line = null;
			int i = 0;
			while ((line = br.readLine()) != null) {
				Assert.assertEquals("Incorrect line!", expected[i], line);
				i++;
			}
			Assert.assertEquals("Incorrect line number!", expected.length, i);
		} finally {
			br.close();
		}
	}
	
	@AfterClass
	public static void AfterCLass()
	{
		new File("./test_localisation_files/ck2/save_file.csv").delete();
		new File("./test_localisation_files/ck2/save_file2.csv").delete();
		new File("./test_localisation_files/ck2/save_file3.csv").delete();
	}
}
