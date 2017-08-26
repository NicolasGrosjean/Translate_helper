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
		file.setLineNumber(4);
		file.addLastLineToTranslate(2, "TRADE.0005A", "", "OK", "J'en prends note");
		file.addLastLineToTranslate(3, "TRADE.0005B", "", "Toto", "J'en prends note");
		file.addLastLineToTranslate(4, "TRADE.0005C", "", "OK", "J'en prends note");
		
		// Create a file corresponding to these data
		String[] expected = { "CODE;ENGLISH;FRENCH;;;;;;;;;;;;x",
							"TRADE.0005A;OK;J'en prends note;;Ja;;;Amigo;;;;;;;x",
							"TRADE.0005B;Toto;Titi et grosminet;;Nein;;Holla;;;;;;;;x",
							"TRADE.0005C;What?;Quoi?;;Schnell;;;;Gracie;;;;;;x" };
		List<String> lines = Arrays.asList(expected);
		Path filetoWtrite = Paths.get(filePath);
		Files.write(filetoWtrite, lines, Charset.forName("UTF-8"));

		// Modify and save the file
		file.getNextEntryToTranslateAndSave(entryToSave, destinationLanguage);

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
	
	@AfterClass
	public static void AfterCLass()
	{
		new File("./test_localisation_files/save_file.csv").delete();
	}
}
