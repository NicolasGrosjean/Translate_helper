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
						entryToSave.getDestLineNumber(), entryToSave.getId()), "save_file", false);
	}
	
	@Test
	public void testSaveWithSameLineNumber2() throws IOException {
		String troncatedFilePath = "./test_localisation_files/hoi4/save_file_l";
		testSaveSeveralLines(troncatedFilePath, false, false,
				new TranslatedEntry(entryToSave.getSource(), 
						entryToSave.getDestination(), entryToSave.getSourceLineNumber(),
						entryToSave.getDestLineNumber(), entryToSave.getId()), "save_file", true);
	}

	@Test
	public void testSaveWithSpecialChar() throws IOException {
		String troncatedFilePath = "./test_localisation_files/hoi4/save_file2_l";
		String destinationText = "�YReligious head suitability: $SCORE$�!";
		testSaveOneLine(troncatedFilePath, destinationText, entryToSave.getSource(), false, 0, 0, "save_file_2");
	}

	@Test
	public void testSaveWithAccent() throws IOException {
		String troncatedFilePath = "./test_localisation_files/hoi4/save_file3_l";
		String destinationText = "Gagn� ou perdu ?";
		testSaveOneLine(troncatedFilePath, destinationText, entryToSave.getSource(), false, 1, 0, "save_file_3");
	}

	@Test
	public void testSaveUnknownSourceLineNumber() throws IOException {
		String troncatedFilePath = "./test_localisation_files/hoi4/save_file4_l";
		testSaveSeveralLines(troncatedFilePath, true, false,
				new TranslatedEntry(entryToSave.getSource(), 
						entryToSave.getDestination(), entryToSave.getDestLineNumber(),
						HoI4ParsedEntry.MISSING_ENTRY, entryToSave.getId()), "save_file_4", false);		
	}

	@Test
	public void testSaveUnknownSourceLineNumber2() throws IOException {
		String troncatedFilePath = "./test_localisation_files/hoi4/save_file4_l";
		testSaveSeveralLines(troncatedFilePath, true, false,
				new TranslatedEntry(entryToSave.getSource(), 
						entryToSave.getDestination(), entryToSave.getDestLineNumber(),
						HoI4ParsedEntry.MISSING_ENTRY, entryToSave.getId()), "save_file_4", true);		
	}

	@Test
	public void testSaveUnknownDestLineNumber() throws IOException {
		String troncatedFilePath = "./test_localisation_files/hoi4/save_file5_l";
		testSaveSeveralLines(troncatedFilePath, false, true,
				new TranslatedEntry(entryToSave.getSource(), 
						entryToSave.getDestination(), HoI4ParsedEntry.MISSING_ENTRY,
						entryToSave.getSourceLineNumber(), entryToSave.getId()), "save_file_5", false);		
	}

	@Test
	public void testSaveUnknownDestLineNumber2() throws IOException {
		String troncatedFilePath = "./test_localisation_files/hoi4/save_file5_l";
		testSaveSeveralLines(troncatedFilePath, false, true,
				new TranslatedEntry(entryToSave.getSource(), 
						entryToSave.getDestination(), HoI4ParsedEntry.MISSING_ENTRY,
						entryToSave.getSourceLineNumber(), entryToSave.getId()), "save_file_5", true);		
	}

	@Test
	public void testSaveSource() throws IOException {
		String troncatedFilePath = "./test_localisation_files/hoi4/save_file6_l";
		testSaveOneLine(troncatedFilePath, "", "Changed source text", false, 0, 1, "save_file_6");
	}

	@Test
	public void testWithoutDestFile() throws IOException {
		String troncatedFilePath = "./test_localisation_files/hoi4/save_file7_l";
		String destinationText = "Fichier cr�� avec succ�s";
		testSaveOneLine(troncatedFilePath, destinationText, entryToSave.getSource(), true, 3, 0, "save_file_7");
	}

	@Test
	public void testSeveralMissingDest() throws IOException {
		// Create data
		String troncatedFilePath = "./test_localisation_files/hoi4/save_file10_l";
		HoI4ParsedFile file = new HoI4ParsedFile(troncatedFilePath, "save_file10");
		file.addLastLineToTranslate(3, HoI4ParsedEntry.MISSING_ENTRY, "ID_2", "", "B", "", 0, 0);
		file.addLastLineToTranslate(4, HoI4ParsedEntry.MISSING_ENTRY, "ID_3", "", "C", "", 0, 0);
		
		// Create files corresponding to these data
		String[] sourceData = { "\uFEFFl_english:\n" + " ID_1:0 \"A\"\n" + " ID_2:0 \"B\"\n" + " ID_3:0 \"C\"\n" + " ID_4:0 \"D\"\n" };
		List<String> sourceLines = Arrays.asList(sourceData);
		Path sourceFiletoWtrite = Paths.get(troncatedFilePath + "_english.yml");
		Files.write(sourceFiletoWtrite, sourceLines, StandardCharsets.UTF_8);
		String[] destData = { "\uFEFFl_french:\n" + " ID_1:0 \"A\"\n" + " ID_4:0 \"D\"\n" };
		List<String> destLines = Arrays.asList(destData);
		Path destFiletoWtrite = Paths.get(troncatedFilePath + "_french.yml");
		Files.write(destFiletoWtrite, destLines, StandardCharsets.UTF_8);
		
		TranslatedEntry entry = file.getFirstEntryToTranslate();
		entry.setDestination("B_FR");
		entry = file.getNextEntryToTranslateAndSave(entry, sourceLanguage, destinationLanguage);
		entry.setDestination("C_FR");
		file.getNextEntryToTranslateAndSave(entry, sourceLanguage, destinationLanguage);
		
		// Check that is what we expect
		String expected[] = { "\uFEFFl_french:", " ID_1:0 \"A\"", " ID_2:0 \"B_FR\"", " ID_3:0 \"C_FR\"", " ID_4:0 \"D\"", ""};
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
		String sourceExpected[] = { "\uFEFFl_english:", " ID_1:0 \"A\"", " ID_2:0 \"B\"", " ID_3:0 \"C\"", " ID_4:0 \"D\"", ""};
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
	
	@Test
	public void testSeveralLinesWithoutDestFile() throws IOException {
		String troncatedFilePath = "./test_localisation_files/hoi4/save_file8_l";
		// Create data
		HoI4ParsedFile file = new HoI4ParsedFile(troncatedFilePath, "save_file8");
		String sourceText1 = "OK";
		String sourceText3 = "What?";
		String sourceText4 = "Okay";
		file.addLastLineToTranslate(2, HoI4ParsedEntry.MISSING_ENTRY, "ID_1", "", sourceText1, "", 0, 0);
		String oldSourceLine = "";
		String oldSource = "";
		String oldDest = "";
		oldSource = "Toto";
		oldDest = "";
		file.addLastLineToTranslate(3, HoI4ParsedEntry.MISSING_ENTRY, "ID_2", "", oldSource, oldDest, 0, 0);
		oldSourceLine = " ID_2:0 \"" + oldSource + "\"\n";
		file.addLastLineToTranslate(4, HoI4ParsedEntry.MISSING_ENTRY, "ID_3", "", sourceText3, "", 0, 0);
		file.addLastLineToTranslate(5, HoI4ParsedEntry.MISSING_ENTRY, "ID_4", "", sourceText4, "", 0, 0);
		
		// Create source file corresponding to these data
		String[] sourceData = { "\uFEFFl_english:\n" +
				" ID_1:0 \"" + sourceText1 + "\"\n" +
				oldSourceLine +
				" ID_3:0 \"" + sourceText3 + "\"\n" +
				" ID_4:0 \"" + sourceText4 + "\"",};
		List<String> sourceLines = Arrays.asList(sourceData);
		Path sourceFiletoWtrite = Paths.get(troncatedFilePath + "_english.yml");
		Files.write(sourceFiletoWtrite, sourceLines, StandardCharsets.UTF_8);
		
		// Skip first line to translate
		TranslatedEntry firstEntry = file.getFirstEntryToTranslate();
		TranslatedEntry nextEntry = file.getNextEntryToTranslateAndSave(firstEntry, sourceLanguage, destinationLanguage);
		Assert.assertEquals("Incorrect next entry!", oldSource, nextEntry.getSource());
		Assert.assertEquals("Incorrect next entry!", oldDest, nextEntry.getDestination());
		
		// Modify and save the file
		nextEntry.setSource(entryToSave.getSource());
		nextEntry.setDestination(entryToSave.getDestination());
		nextEntry = file.getNextEntryToTranslateAndSave(nextEntry, sourceLanguage, destinationLanguage);
		Assert.assertEquals("Incorrect next entry!", "What?", nextEntry.getSource());
		Assert.assertEquals("Incorrect next entry!", "", nextEntry.getDestination());
		
		// Go back to the previous entry
		TranslatedEntry prevEntry = file.getPreviousEntryToTranslate();
		Assert.assertEquals("Incorrect next entry!", entryToSave.getSource(), prevEntry.getSource());
		Assert.assertEquals("Incorrect next entry!", entryToSave.getDestination(), prevEntry.getDestination());
		
		// Save it again
		nextEntry = file.getNextEntryToTranslateAndSave(prevEntry, sourceLanguage, destinationLanguage);
		Assert.assertEquals("Incorrect next entry!", "What?", nextEntry.getSource());
		Assert.assertEquals("Incorrect next entry!", "", nextEntry.getDestination());
		
		// Check that is what we expect
		String[] expected = { "\uFEFFl_french:",
				" ID_1:0 \"" + "" + "\"",
				" " + entryToSave.getId() + ":0 \"" + entryToSave.getDestination() + "\""};
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
	
	@Test
	public void testSeveralLinesWithoutDestFileWithComment() throws IOException {
		String troncatedFilePath = "./test_localisation_files/hoi4/save_file9_l";
		// Create data
		HoI4ParsedFile file = new HoI4ParsedFile(troncatedFilePath, "save_file9");
		String sourceText1 = "OK";
		String sourceText3 = "What?";
		String sourceText4 = "Okay";
		file.addLastLineToTranslate(3, HoI4ParsedEntry.MISSING_ENTRY, "ID_1", "", sourceText1, "", 0, 0);
		String oldSourceLine = "";
		String oldSource = "";
		String oldDest = "";
		oldSource = "Toto";
		oldDest = "";
		file.addLastLineToTranslate(4, HoI4ParsedEntry.MISSING_ENTRY, "ID_2", "", oldSource, oldDest, 0, 0);
		oldSourceLine = " ID_2:0 \"" + oldSource + "\"\n";
		file.addLastLineToTranslate(5, HoI4ParsedEntry.MISSING_ENTRY, "ID_3", "", sourceText3, "", 0, 0);
		file.addLastLineToTranslate(6, HoI4ParsedEntry.MISSING_ENTRY, "ID_4", "", sourceText4, "", 0, 0);
		
		// Create source file corresponding to these data
		String[] sourceData = { "\uFEFFl_english:\n" +
				" # Comment \n" + 
				" ID_1:0 \"" + sourceText1 + "\"\n" +
				oldSourceLine +
				" ID_3:0 \"" + sourceText3 + "\"\n" +
				" ID_4:0 \"" + sourceText4 + "\"",};
		List<String> sourceLines = Arrays.asList(sourceData);
		Path sourceFiletoWtrite = Paths.get(troncatedFilePath + "_english.yml");
		Files.write(sourceFiletoWtrite, sourceLines, StandardCharsets.UTF_8);
		
		// Modify and save first line to translate
		TranslatedEntry firstEntry = file.getFirstEntryToTranslate();
		firstEntry.setDestination(entryToSave.getDestination());
		TranslatedEntry nextEntry = file.getNextEntryToTranslateAndSave(firstEntry, sourceLanguage, destinationLanguage);
		Assert.assertEquals("Incorrect next entry!", oldSource, nextEntry.getSource());
		Assert.assertEquals("Incorrect next entry!", oldDest, nextEntry.getDestination());
		
		// Go back to the previous entry
		TranslatedEntry prevEntry = file.getPreviousEntryToTranslate();
		Assert.assertEquals("Incorrect next entry!", sourceText1, prevEntry.getSource());
		Assert.assertEquals("Incorrect next entry!", entryToSave.getDestination(), prevEntry.getDestination());
		
		// Modify and save the file
		prevEntry.setSource(entryToSave.getSource() + "2");
		prevEntry.setDestination(entryToSave.getDestination() + "2");
		nextEntry = file.getNextEntryToTranslateAndSave(prevEntry, sourceLanguage, destinationLanguage);
		Assert.assertEquals("Incorrect next entry!", oldSource, nextEntry.getSource());
		Assert.assertEquals("Incorrect next entry!", oldDest, nextEntry.getDestination());		
		
		// Modify and save the file
		nextEntry.setSource(entryToSave.getSource() + "3");
		nextEntry.setDestination(entryToSave.getDestination() + "3");
		nextEntry = file.getNextEntryToTranslateAndSave(nextEntry, sourceLanguage, destinationLanguage);
		Assert.assertEquals("Incorrect next entry!", sourceText3, nextEntry.getSource());
		Assert.assertEquals("Incorrect next entry!", "", nextEntry.getDestination());
		
		// Check that is what we expect
		String[] expected = { "\uFEFFl_french:",
				" ID_1:0 \"" + entryToSave.getDestination() + "2" + "\"",
				" " + entryToSave.getId() + ":0 \"" + entryToSave.getDestination() + "3" + "\""};
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
				" # Comment ",
				" ID_1:0 \"" + entryToSave.getSource() + "2" + "\"",
				" " + entryToSave.getId() + ":0 \"" + entryToSave.getSource() + "3" + "\"",
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
	
	private void testSaveSeveralLines(String troncatedFilePath, boolean missingSource,
			boolean missingDest, TranslatedEntry entryToSave, String name, boolean goBack)
			throws IOException {
		// Create data
		HoI4ParsedFile file = new HoI4ParsedFile(troncatedFilePath, name);
		String sourceText1 = "OK";
		String sourceText3 = "What?";
		String sourceText4 = "Okay";
		String destText1 = "J'en prends note";
		String destText2 = "Titi et grosminet";
		String destText3 = "Quoi?";
		String destText4 = "D'ac";
		file.addLastLineToTranslate(2, 2, "ID_1", "", sourceText1, destText1, 0, 0);
		String oldSourceLine = "";
		String oldDestLine = "";
		String oldSource = "";
		String oldDest = "";
		if (missingSource) {
			oldSource = "";
			oldDest = destText2;
			file.addLastLineToTranslate(HoI4ParsedEntry.MISSING_ENTRY, 3, "ID_2", "", oldSource, oldDest, 0, 0);
			oldSourceLine = "";
			oldDestLine = " ID_2:0 \"" + destText2 + "\"\n";
		} else if (missingDest) { // missingSource == true == missingDest has no meaning
			oldSource = "Toto";
			oldDest = "";
			file.addLastLineToTranslate(3, HoI4ParsedEntry.MISSING_ENTRY, "ID_2", "", oldSource, oldDest, 0, 0);
			oldSourceLine = " ID_2:0 \"" + oldSource + "\"\n";
			oldDestLine = "";
		} else {
			oldSource = "Toto";
			oldDest = destText2;
			file.addLastLineToTranslate(3, 3, "ID_2", "", oldSource, oldDest, 0, 0);
			oldSourceLine = " ID_2:0 \"" + oldSource + "\"\n";
			oldDestLine = " ID_2:0 \"" + destText2 + "\"\n";
		}
		file.addLastLineToTranslate(missingSource ? 3 : 4, missingDest ? 3 : 4, "ID_3", "", sourceText3, destText3, 0, 0);
		file.addLastLineToTranslate(missingSource ? 3 : 4, missingDest ? 4 : 5, "ID_4", "", sourceText4, destText4, 0, 0);
		
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

		if (goBack) {
			// Go back to the previous entry
			TranslatedEntry prevEntry = file.getPreviousEntryToTranslate();
			Assert.assertEquals("Incorrect next entry!", entryToSave.getSource(), prevEntry.getSource());
			Assert.assertEquals("Incorrect next entry!", entryToSave.getDestination(), prevEntry.getDestination());
			
			// Save it again
			nextEntry = file.getNextEntryToTranslateAndSave(prevEntry, sourceLanguage, destinationLanguage);
			Assert.assertEquals("Incorrect next entry!", "What?", nextEntry.getSource());
			Assert.assertEquals("Incorrect next entry!", "Quoi?", nextEntry.getDestination());
		}
		
		// Modify and save the file
		nextEntry.setDestination(nextEntry.getDestination() + "!");
		nextEntry = file.getNextEntryToTranslateAndSave(nextEntry, sourceLanguage, destinationLanguage);
		Assert.assertEquals("Incorrect next entry!", sourceText4, nextEntry.getSource());
		Assert.assertEquals("Incorrect next entry!", destText4, nextEntry.getDestination());
		
		// Check that is what we expect
		String[] expected = { "\uFEFFl_french:",
				" ID_1:0 \"" + destText1 + "\"",
				" " + entryToSave.getId() + ":0 \"" + entryToSave.getDestination() + "\"",
				" ID_3:0 \"" + destText3 + "!\"",
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
			String sourceText, boolean missingDest, int sourceVersionNumber,
			int destinationVersionNumber, String name) throws IOException {
		// Create data
		HoI4ParsedFile file = new HoI4ParsedFile(troncatedFilePath, name);
		String oldDestText = "TOTO";
		String id = "ID_1";
		if (missingDest) {
			file.addLastLineToTranslate(2, HoI4ParsedEntry.MISSING_ENTRY, id, "", sourceText, "", sourceVersionNumber, 0);
		} else {
			file.addLastLineToTranslate(2, 2, id, "", sourceText, oldDestText, sourceVersionNumber, destinationVersionNumber);
		}
		
		// Create files corresponding to these data
		String[] sourceData = { "\uFEFFl_english:\n" + " ID_1:" + sourceVersionNumber + "\"" + entryToSave.getSource() + "\"" };
		List<String> sourceLines = Arrays.asList(sourceData);
		Path sourceFiletoWtrite = Paths.get(troncatedFilePath + "_english.yml");
		Files.write(sourceFiletoWtrite, sourceLines, StandardCharsets.UTF_8);
		if (!missingDest) {
			String[] destData = { "\uFEFFl_french:\n" + " ID_1:" + destinationVersionNumber + " \"" + oldDestText + "\"" };
			List<String> destLines = Arrays.asList(destData);
			Path destFiletoWtrite = Paths.get(troncatedFilePath + "_french.yml");
			Files.write(destFiletoWtrite, destLines, StandardCharsets.UTF_8);
		}
		
		TranslatedEntry entryToSave2 = file.getFirstEntryToTranslate();
		
		// Modify and save the file
		entryToSave2.setSource(entryToSave.getSource());
		entryToSave2.setDestination(destinationText);
		file.getNextEntryToTranslateAndSave(entryToSave2, sourceLanguage, destinationLanguage);
		
		// Check that is what we expect
		int maxVersionNumber = Math.max(sourceVersionNumber, destinationVersionNumber);
		String expected[] = {"\uFEFFl_french:",
				" ID_1:" + maxVersionNumber + " \"" + destinationText + "\"",};
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
				" ID_1:" + maxVersionNumber + " \"" + entryToSave.getSource() + "\"",};
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
		new File("./test_localisation_files/hoi4/save_file8_l_english.yml").delete();
		new File("./test_localisation_files/hoi4/save_file8_l_french.yml").delete();
		new File("./test_localisation_files/hoi4/save_file9_l_english.yml").delete();
		new File("./test_localisation_files/hoi4/save_file9_l_french.yml").delete();
		new File("./test_localisation_files/hoi4/save_file10_l_english.yml").delete();
		new File("./test_localisation_files/hoi4/save_file10_l_french.yml").delete();
	}
}
