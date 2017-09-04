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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.text.pdf.ArrayBasedStringTokenizer;

import parsing.CK2ParsedFile;
import parsing.Language;
import translator.TranslatedEntry;

public class TestSaving {
	private static TranslatedEntry entryToSave;
	private static Language destinationLanguage;

	@BeforeClass
	public static void SetUp() {
		entryToSave = new TranslatedEntry("Toto", "Tata", 3);
		destinationLanguage = new Language("FRENCH", 2);
	}

	@Test
	public void testSaveInCK2LocalisationFile() throws IOException {
		// Create data
		String filePath = "./test_localisation_files/save_file.csv";
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
		Files.write(filetoWtrite, lines, Charset.forName("UTF-8"));

		// Skip first line to translate
		file.getFirstEntryToTranslate();
		TranslatedEntry nextEntry = file.getNextEntryToTranslate();
		Assert.assertEquals("Incorrect next entry!", "Toto", nextEntry.getSource());
		Assert.assertEquals("Incorrect next entry!", "Titi et grosminet", nextEntry.getDestination());
		
		// Modify and save the file
		nextEntry = file.getNextEntryToTranslateAndSave(entryToSave, destinationLanguage);
		Assert.assertEquals("Incorrect next entry!", "What?", nextEntry.getSource());
		Assert.assertEquals("Incorrect next entry!", "Quoi?", nextEntry.getDestination());

		// Check that is what we expect
		expected[2] = "TRADE.0005B;Toto;Tata;;Nein;;Holla;;;;;;;;x";
		FileInputStream fis = new FileInputStream(filePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String line = null;
		int i = 0;
		while ((line = br.readLine()) != null) {
			Assert.assertEquals("Incorrect line!", expected[i], line);
			i++;
		}
		Assert.assertEquals("Incorrect line number!", expected.length, i);
		br.close();
	}

	@Test
	public void testSaveInCK2LocalisationFileSpecialChar() throws IOException {
		// Create data
		String filePath = "./test_localisation_files/save_file2.csv";
		CK2ParsedFile file = new CK2ParsedFile(filePath);
		file.setLineNumber(2);
		file.addLastLineToTranslate(2, "TRADE.0005A", "", "§YReligious head suitability: $SCORE$§!", "");
		
		// Create a file corresponding to these data
		String[] expected = { "CODE;ENGLISH;FRENCH;;;;;;;;;;;;x",
								"TRADE.0005A;§YReligious head suitability: $SCORE$§!;;;Ja;;;Amigo;;;;;;;x"};
		List<String> lines = Arrays.asList(expected);
		Path filetoWtrite = Paths.get(filePath);
		Files.write(filetoWtrite, lines, Charset.forName("UTF-8"));
		
		file.getFirstEntryToTranslate();
		// Modify and save the file
		TranslatedEntry entryToSave2 = new TranslatedEntry("§YReligious head suitability: $SCORE$§!", "Tata", 2);
		file.getNextEntryToTranslateAndSave(entryToSave2, destinationLanguage);
		
		// Check that is what we expect
		expected[1] = "TRADE.0005A;§YReligious head suitability: $SCORE$§!;Tata;;Ja;;;Amigo;;;;;;;x";
		FileInputStream fis = new FileInputStream(filePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String line = null;
		int i = 0;
		while ((line = br.readLine()) != null) {
			Assert.assertEquals("Incorrect line!", expected[i], line);
			i++;
		}
		Assert.assertEquals("Incorrect line number!", expected.length, i);
		br.close();
	}
	
	@AfterClass
	public static void AfterCLass()
	{
		new File("./test_localisation_files/save_file.csv").delete();
		new File("./test_localisation_files/save_file2.csv").delete();
	}
}
