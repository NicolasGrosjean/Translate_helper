package tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import parsing.HoI4ParsedEntry;
import parsing.HoI4ParsedFile;
import parsing.Language;
import translator.TranslatedEntry;

public class TestSavingHoI4 {
	private static TranslatedEntry entryToSave;
	private static Language sourceLanguage;
	private static Language destinationLanguage;

	@BeforeClass
	public static void SetUp() {
		entryToSave = new TranslatedEntry("Toto", "Tata", 3, 3, "ID_2");
		sourceLanguage = new Language("ENGLISH", 1, "en");
		destinationLanguage = new Language("FRENCH", 2, "fr");
	}
	
	@Test
	public void testSaveWithSameLineNumber() throws IOException {
		String troncatedFilePath = "./test_localisation_files/hoi4/save_file_l";
		testSaveSeveralLines(troncatedFilePath, false, false,
				new TranslatedEntry(entryToSave.getSource(), 
						entryToSave.getDestination(), entryToSave.getSourceLineNumber(),
						entryToSave.getDestLineNumber(), entryToSave.getId()));
	}

	@Test
	public void testSaveWithSpecialChar() throws IOException {
		String troncatedFilePath = "./test_localisation_files/hoi4/save_file2_l";
		String destinationText = "§YReligious head suitability: $SCORE$§!";
		testSaveOneLine(troncatedFilePath, destinationText, entryToSave.getSource(), false);
	}

	@Test
	public void testSaveWithAccent() throws IOException {
		String troncatedFilePath = "./test_localisation_files/hoi4/save_file3_l";
		String destinationText = "Gagné ou perdu ?";
		testSaveOneLine(troncatedFilePath, destinationText, entryToSave.getSource(), false);
	}

	@Test
	public void testSaveUnknownSourceLineNumber() throws IOException {
		String troncatedFilePath = "./test_localisation_files/hoi4/save_file4_l";
		testSaveSeveralLines(troncatedFilePath, true, false,
				new TranslatedEntry(entryToSave.getSource(), 
						entryToSave.getDestination(), entryToSave.getDestLineNumber(),
						HoI4ParsedEntry.MISSING_ENTRY, entryToSave.getId()));		
	}

	@Test
	public void testSaveUnknownDestLineNumber() throws IOException {
		String troncatedFilePath = "./test_localisation_files/hoi4/save_file5_l";
		testSaveSeveralLines(troncatedFilePath, false, true,
				new TranslatedEntry(entryToSave.getSource(), 
						entryToSave.getDestination(), HoI4ParsedEntry.MISSING_ENTRY,
						entryToSave.getSourceLineNumber(), entryToSave.getId()));		
	}

	@Test
	public void testSaveSource() throws IOException {
		String troncatedFilePath = "./test_localisation_files/hoi4/save_file6_l";
		testSaveOneLine(troncatedFilePath, "", "Changed source text", false);
	}

	@Test
	public void testWithoutDestFile() throws IOException {
		String troncatedFilePath = "./test_localisation_files/hoi4/save_file7_l";
		String destinationText = "Fichier créé avec succès";
		testSaveOneLine(troncatedFilePath, destinationText, entryToSave.getSource(), true);
	}
	
	private void testSaveSeveralLines(String troncatedFilePath, boolean missingSource,
			boolean missingDest, TranslatedEntry entryToSave)
			throws IOException {
		// Create data
		HoI4ParsedFile file = new HoI4ParsedFile(troncatedFilePath);
		String sourceText1 = "OK";
		String sourceText3 = "What?";
		String sourceText4 = "Okay";
		String destText1 = "J'en prends note";
		String destText2 = "Titi et grosminet";
		String destText3 = "Quoi?";
		String destText4 = "D'ac";
		file.addLastLineToTranslate(2, 2, "ID_1", "", sourceText1, destText1);
		String oldSourceLine = "";
		String oldDestLine = "";
		String oldSource = "";
		String oldDest = "";
		if (missingSource) {
			oldSource = "";
			oldDest = destText2;
			file.addLastLineToTranslate(HoI4ParsedEntry.MISSING_ENTRY, 3, "ID_2", "", oldSource, oldDest);
			oldSourceLine = "";
			oldDestLine = " ID_2:0 \"" + destText2 + "\"\n";
		} else if (missingDest) { // missingSource == true == missingDest has no meaning
			oldSource = "Toto";
			oldDest = "";
			file.addLastLineToTranslate(3, HoI4ParsedEntry.MISSING_ENTRY, "ID_2", "", oldSource, oldDest);
			oldSourceLine = " ID_2:0 \"" + oldSource + "\"\n";
			oldDestLine = "";
		} else {
			oldSource = "Toto";
			oldDest = destText2;
			file.addLastLineToTranslate(3, 3, "ID_2", "", oldSource, oldDest);
			oldSourceLine = " ID_2:0 \"" + oldSource + "\"\n";
			oldDestLine = " ID_2:0 \"" + destText2 + "\"\n";
		}
		file.addLastLineToTranslate(4, 4, "ID_3", "", sourceText3, destText3);
		file.addLastLineToTranslate(5, 5, "ID_4", "", sourceText4, destText4);
		
		// Create files corresponding to these data
		String[] sourceData = { "\uFEFFl_english:\n" +
				" ID_1:0 \"" + sourceText1 + "\"\n" +
				oldSourceLine +
				" ID_3:0 \"" + sourceText3 + "\"\n" +
				" ID_4:0 \"" + sourceText4 + "\"",};
		List<String> sourceLines = Arrays.asList(sourceData);
		Path sourceFiletoWtrite = Paths.get(troncatedFilePath + "_english.yml");
		Files.write(sourceFiletoWtrite, sourceLines, StandardCharsets.UTF_8);
		String[] destData = { "\uFEFFl_french:\n" +
				" ID_1:0 \"" + destText1 + "\"\n" +
				oldDestLine +
				" ID_3:0 \"" + destText3 + "\"\n" +
				" ID_4:0 \"" + destText4 + "\"",};
		List<String> destLines = Arrays.asList(destData);
		Path destFiletoWtrite = Paths.get(troncatedFilePath + "_french.yml");
		Files.write(destFiletoWtrite, destLines, StandardCharsets.UTF_8);
		
		// Skip first line to translate
		file.getFirstEntryToTranslate();
		TranslatedEntry nextEntry = file.getNextEntryToTranslate();
		Assert.assertEquals("Incorrect next entry!", oldSource, nextEntry.getSource());
		Assert.assertEquals("Incorrect next entry!", oldDest, nextEntry.getDestination());
		
		// Modify and save the file
		nextEntry = file.getNextEntryToTranslateAndSave(entryToSave, sourceLanguage, destinationLanguage);
		Assert.assertEquals("Incorrect next entry!", "What?", nextEntry.getSource());
		Assert.assertEquals("Incorrect next entry!", "Quoi?", nextEntry.getDestination());
		
		// Go back to the previous entry
		TranslatedEntry prevEntry = file.getPreviousEntryToTranslate();
		Assert.assertEquals("Incorrect next entry!", entryToSave.getSource(), prevEntry.getSource());
		Assert.assertEquals("Incorrect next entry!", entryToSave.getDestination(), prevEntry.getDestination());
		
		// Save it again
		nextEntry = file.getNextEntryToTranslateAndSave(prevEntry, sourceLanguage, destinationLanguage);
		Assert.assertEquals("Incorrect next entry!", "What?", nextEntry.getSource());
		Assert.assertEquals("Incorrect next entry!", "Quoi?", nextEntry.getDestination());
		
		// Check that is what we expect
		String[] expected = { "\uFEFFl_french:",
				" ID_1:0 \"" + destText1 + "\"",
				" " + entryToSave.getId() + ":0 \"" + entryToSave.getDestination() + "\"",
				" ID_3:0 \"" + destText3 + "\"",
				" ID_4:0 \"" + destText4 + "\""};
		FileInputStream fis = new FileInputStream(troncatedFilePath + "_french.yml");
		BufferedReader br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
		try {
			String line = null;
			int i = 0;
			while ((line = br.readLine()) != null) {
				Assert.assertTrue("Incorrect line number!", expected.length > i);
				Assert.assertEquals("Incorrect line!", expected[i], line);
				i++;
			}
			Assert.assertEquals("Incorrect line number!", expected.length, i);
		} finally {
			br.close();
		}
		// Check also the source
		String[] expectedSource = { "\uFEFFl_english:",
				" ID_1:0 \"" + sourceText1 + "\"",
				" " + entryToSave.getId() + ":0 \"" + entryToSave.getSource() + "\"",
				" ID_3:0 \"" + sourceText3 + "\"",
				" ID_4:0 \"" + sourceText4 + "\""};
		fis = new FileInputStream(troncatedFilePath + "_english.yml");
		br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
		try {
			String line = null;
			int i = 0;
			while ((line = br.readLine()) != null) {
				Assert.assertTrue("Incorrect line number!", expectedSource.length > i);
				Assert.assertEquals("Incorrect line!", expectedSource[i], line);
				i++;
			}
			Assert.assertEquals("Incorrect line number!", expectedSource.length, i);
		} finally {
			br.close();
		}
	}
	private void testSaveOneLine(String troncatedFilePath, String destinationText,
			String sourceText, boolean missingDest) throws IOException {
		// Create data
		HoI4ParsedFile file = new HoI4ParsedFile(troncatedFilePath);
		String oldDestText = "TOTO";
		String id = "ID_1";
		entryToSave.setDestination(destinationText);
		file.addLastLineToTranslate(2, 2, id, "", sourceText, oldDestText);
		
		// Create files corresponding to these data
		String[] sourceData = { "\uFEFFl_english:\n" + " ID_1:0 \"" + entryToSave.getSource() + "\"" };
		List<String> sourceLines = Arrays.asList(sourceData);
		Path sourceFiletoWtrite = Paths.get(troncatedFilePath + "_english.yml");
		Files.write(sourceFiletoWtrite, sourceLines, StandardCharsets.UTF_8);
		if (!missingDest) {
			String[] destData = { "\uFEFFl_french:\n" + " ID_1:0 \"" + oldDestText + "\"" };
			List<String> destLines = Arrays.asList(destData);
			Path destFiletoWtrite = Paths.get(troncatedFilePath + "_french.yml");
			Files.write(destFiletoWtrite, destLines, StandardCharsets.UTF_8);
		}
		
		file.getFirstEntryToTranslate();
		// Modify and save the file
		TranslatedEntry entryToSave2 = new TranslatedEntry(entryToSave.getSource(),
				destinationText, missingDest ? -1 : 2, 2, id);
		file.getNextEntryToTranslateAndSave(entryToSave2, sourceLanguage, destinationLanguage);
		
		// Check that is what we expect
		String expected[] = {"\uFEFFl_french:",
				" ID_1:0 \"" + destinationText + "\"",};
		FileInputStream fis = new FileInputStream(troncatedFilePath + "_french.yml");
		BufferedReader br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
		try {
			String line = null;
			int i = 0;
			while ((line = br.readLine()) != null) {
				Assert.assertTrue("Incorrect line number!", expected.length > i);
				Assert.assertEquals("Incorrect line!", expected[i], line);
				i++;
			}
			Assert.assertEquals("Incorrect line number!", expected.length, i);
		} finally {
			br.close();
		}
		// Check also source
		String sourceExpected[] = {"\uFEFFl_english:",
				" ID_1:0 \"" + entryToSave.getSource() + "\"",};
		fis = new FileInputStream(troncatedFilePath + "_english.yml");
		br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
		try {
			String line = null;
			int i = 0;
			while ((line = br.readLine()) != null) {
				Assert.assertTrue("Incorrect line number!", sourceExpected.length > i);
				Assert.assertEquals("Incorrect line!", sourceExpected[i], line);
				i++;
			}
			Assert.assertEquals("Incorrect line number!", sourceExpected.length, i);
		} finally {
			br.close();
		}
	}
	
	@AfterClass
	public static void AfterCLass()
	{
		new File("./test_localisation_files/hoi4/save_file_l_english.yml").delete();
		new File("./test_localisation_files/hoi4/save_file_l_french.yml").delete();
		new File("./test_localisation_files/hoi4/save_file2_l_english.yml").delete();
		new File("./test_localisation_files/hoi4/save_file2_l_french.yml").delete();
		new File("./test_localisation_files/hoi4/save_file3_l_english.yml").delete();
		new File("./test_localisation_files/hoi4/save_file3_l_french.yml").delete();
		new File("./test_localisation_files/hoi4/save_file4_l_english.yml").delete();
		new File("./test_localisation_files/hoi4/save_file4_l_french.yml").delete();
		new File("./test_localisation_files/hoi4/save_file5_l_english.yml").delete();
		new File("./test_localisation_files/hoi4/save_file5_l_french.yml").delete();
		new File("./test_localisation_files/hoi4/save_file6_l_english.yml").delete();
		new File("./test_localisation_files/hoi4/save_file6_l_french.yml").delete();
		new File("./test_localisation_files/hoi4/save_file7_l_english.yml").delete();
		new File("./test_localisation_files/hoi4/save_file7_l_french.yml").delete();
	}
}
