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
import parsing.Language;
import parsing.PHParsedFile;
import translator.TranslatedEntry;

public class TestSavingPH {
	private static TranslatedEntry entryToSave;
	private static Language sourceLanguage;
	private static Language destinationLanguage;
	
	private static final String DIRECTORY = "./test_localisation_files/ph/";

	@BeforeClass
	public static void SetUp() {
		entryToSave = new TranslatedEntry("Toto", "Tata", 3, 3, "ID_2");
		sourceLanguage = new Language("ENGLISH", 1, "en");
		destinationLanguage = new Language("FRENCH", 2, "fr");
	}
	
	@Test
	public void testSaveWithSameLineNumber() throws IOException {
		testSaveSeveralLines("save", false, false,
				new TranslatedEntry(entryToSave.getSource(), 
						entryToSave.getDestination(), entryToSave.getSourceLineNumber(),
						entryToSave.getDestLineNumber(), entryToSave.getId()));
	}

	@Test
	public void testSaveWithSpecialChar() throws IOException {
		String destinationText = "§YReligious head suitability: $SCORE$§!";
		testSaveOneLine("save2", destinationText, entryToSave.getSource(), false, 0, 0);
	}

	@Test
	public void testSaveWithAccent() throws IOException {
		String destinationText = "Gagné ou perdu ?";
		testSaveOneLine("save3", destinationText, entryToSave.getSource(), false, 1, 0);
	}

	@Test
	public void testSaveUnknownSourceLineNumber() throws IOException {
		testSaveSeveralLines("save4", true, false,
				new TranslatedEntry(entryToSave.getSource(), 
						entryToSave.getDestination(), entryToSave.getDestLineNumber(),
						HoI4ParsedEntry.MISSING_ENTRY, entryToSave.getId()));		
	}

	@Test
	public void testSaveUnknownDestLineNumber() throws IOException {
		testSaveSeveralLines("save5", false, true,
				new TranslatedEntry(entryToSave.getSource(), 
						entryToSave.getDestination(), HoI4ParsedEntry.MISSING_ENTRY,
						entryToSave.getSourceLineNumber(), entryToSave.getId()));		
	}

	@Test
	public void testSaveSource() throws IOException {
		testSaveOneLine("save6", "", "Changed source text", false, 0, 1);
	}

	@Test
	public void testWithoutDestFile() throws IOException {
		String destinationText = "Fichier créé avec succès";
		testSaveOneLine("save7", destinationText, entryToSave.getSource(), true, 3, 0);
	}
	
	@Test
	public void testSeveralLinesWithoutDestFile() throws IOException {
		String fileName = "save8";
		// Create data
		PHParsedFile file = new PHParsedFile(DIRECTORY, fileName);
		String sourceText1 = "OK";
		String sourceText3 = "What?";
		String sourceText4 = "Okay";
		file.addLastLineToTranslate(2, HoI4ParsedEntry.MISSING_ENTRY, "ID_1", "", sourceText1, "", 0, 0);
		String oldSource = "";
		String oldDest = "";
		oldSource = "Toto";
		oldDest = "";
		file.addLastLineToTranslate(3, HoI4ParsedEntry.MISSING_ENTRY, "ID_2", "", oldSource, oldDest, 0, 0);
		file.addLastLineToTranslate(4, HoI4ParsedEntry.MISSING_ENTRY, "ID_3", "", sourceText3, "", 0, 0);
		file.addLastLineToTranslate(5, HoI4ParsedEntry.MISSING_ENTRY, "ID_4", "", sourceText4, "", 0, 0);
		
		// Create source file corresponding to these data
		String[] sourceData = {PHParsedFile.getHeader("en", fileName, "English") + "\n" +
				file.getLine("ID_1", sourceText1) + "\n" +
				file.getLine("ID_2", oldSource) + "\n" +
				file.getLine("ID_3", sourceText3) + "\n" +
				file.getLine("ID_4", sourceText4) + "\n" +
				PHParsedFile.getFooter(),};
		List<String> sourceLines = Arrays.asList(sourceData);
		Path sourceFiletoWtrite = Paths.get(DIRECTORY + "StringTableEn" + fileName + ".xml");
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
		String[] expected = (PHParsedFile.getHeader("fr", fileName, "French") + "\n" + 
				file.getLine("ID_1", "") + "\n" + 
				file.getLine(entryToSave.getId(), entryToSave.getDestination()) + "\n" + 
				file.getLine("ID_3", "") + "\n" + 
				file.getLine("ID_4", "") + "\n" + 
				PHParsedFile.getFooter()).split("\n");
		FileInputStream fis = new FileInputStream(DIRECTORY + "StringTableFr" + fileName + ".xml");
		BufferedReader br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
		try {
			String line = null;
			int i = 0;
			while ((line = br.readLine()) != null) {
				Assert.assertTrue("Incorrect line number!", expected.length > i);
				assertLinesEquals(expected[i], line);
				i++;
			}
			Assert.assertEquals("Incorrect line number!", expected.length, i);
		} finally {
			br.close();
		}
		// Check also the source
		String[] expectedSource = (PHParsedFile.getHeader("en", fileName, "English") + "\n" + 
				file.getLine("ID_1", sourceText1) + "\n" + 
				file.getLine(entryToSave.getId(), entryToSave.getSource()) + "\n" + 
				file.getLine("ID_3", sourceText3) + "\n" + 
				file.getLine("ID_4", sourceText4) + "\n" + 
				PHParsedFile.getFooter()).split("\n");
		fis = new FileInputStream(DIRECTORY + "StringTableEn" + fileName + ".xml");
		br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
		try {
			String line = null;
			int i = 0;
			while ((line = br.readLine()) != null) {
				Assert.assertTrue("Incorrect line number!", expectedSource.length > i);
				Assert.assertEquals("Incorrect line!", expectedSource[i].replaceAll("\t", ""), line.replaceAll("\t", ""));
				i++;
			}
			Assert.assertEquals("Incorrect line number!", expectedSource.length, i);
		} finally {
			br.close();
		}
	}
	
	@Test
	public void testSeveralLinesWithoutDestFileWithComment() throws IOException {
		String fileName = "save9";
		// Create data
		PHParsedFile file = new PHParsedFile(DIRECTORY, fileName);
		String sourceText1 = "OK";
		String sourceText3 = "What?";
		String sourceText4 = "Okay";
		file.addLastLineToTranslate(3, HoI4ParsedEntry.MISSING_ENTRY, "ID_1", "", sourceText1, "", 0, 0);
		String oldSource = "";
		String oldDest = "";
		oldSource = "Toto";
		oldDest = "";
		file.addLastLineToTranslate(4, HoI4ParsedEntry.MISSING_ENTRY, "ID_2", "", oldSource, oldDest, 0, 0);
		file.addLastLineToTranslate(5, HoI4ParsedEntry.MISSING_ENTRY, "ID_3", "", sourceText3, "", 0, 0);
		file.addLastLineToTranslate(6, HoI4ParsedEntry.MISSING_ENTRY, "ID_4", "", sourceText4, "", 0, 0);
		
		// Create source file corresponding to these data
		String[] sourceData = { PHParsedFile.getHeader("en", fileName, "English") + "\n" +
				"\t<!--COMMENT--> \n" +
				file.getLine("ID_1", sourceText1) + "\n" +
				file.getLine("ID_2", oldSource) + "\n" +
				file.getLine("ID_3", sourceText3) + "\n" +
				file.getLine("ID_4", sourceText4) + "\n" +
				PHParsedFile.getFooter(),};
		List<String> sourceLines = Arrays.asList(sourceData);
		Path sourceFiletoWtrite = Paths.get(DIRECTORY + "StringTableEn" + fileName + ".xml");
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
		String[] expected = (PHParsedFile.getHeader("fr", fileName, "French") + "\n" +
				file.getLine("ID_1", entryToSave.getDestination() + "2") + "\n" +
				file.getLine(entryToSave.getId(), entryToSave.getDestination() + "3") + "\n" +
				file.getLine("ID_3", "") + "\n" + 
				file.getLine("ID_4", "") + "\n" + 
				PHParsedFile.getFooter()).split("\n");
		FileInputStream fis = new FileInputStream(DIRECTORY + "StringTableFr" + fileName + ".xml");
		BufferedReader br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
		try {
			String line = null;
			int i = 0;
			while ((line = br.readLine()) != null) {
				Assert.assertTrue("Incorrect line number!", expected.length > i);
				assertLinesEquals(expected[i], line);
				i++;
			}
			Assert.assertEquals("Incorrect line number!", expected.length, i);
		} finally {
			br.close();
		}
		// Check also the source
		String[] expectedSource = (PHParsedFile.getHeader("en", fileName, "English") + "\n" + 
				file.getLine("ID_1", entryToSave.getSource() + "2") + "\n" +
				file.getLine(entryToSave.getId(), entryToSave.getSource() + "3") + "\n" + 
				file.getLine("ID_3", sourceText3) + "\n" + 
				file.getLine("ID_4", sourceText4) + "\n" + 
				PHParsedFile.getFooter()).split("\n");
		fis = new FileInputStream(DIRECTORY + "StringTableEn" + fileName + ".xml");
		br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
		try {
			String line = null;
			int i = 0;
			while ((line = br.readLine()) != null) {
				Assert.assertTrue("Incorrect line number!", expectedSource.length > i);
				Assert.assertEquals("Incorrect line!", expectedSource[i].replaceAll("\t", ""), line.replaceAll("\t", ""));
				i++;
			}
			Assert.assertEquals("Incorrect line number!", expectedSource.length, i);
		} finally {
			br.close();
		}
	}
	
	private void testSaveSeveralLines(String fileName, boolean missingSource,
			boolean missingDest, TranslatedEntry entryToSave)
			throws IOException {
		// Create data
		PHParsedFile file = new PHParsedFile(DIRECTORY, fileName);
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
			oldDestLine = file.getLine("ID_2", destText2) + "\n";
		} else if (missingDest) { // missingSource == true == missingDest has no meaning
			oldSource = "Toto";
			oldDest = "";
			file.addLastLineToTranslate(3, HoI4ParsedEntry.MISSING_ENTRY, "ID_2", "", oldSource, oldDest, 0, 0);
			oldSourceLine = file.getLine("ID_2", oldSource) + "\n";
			oldDestLine = "";
		} else {
			oldSource = "Toto";
			oldDest = destText2;
			file.addLastLineToTranslate(3, 3, "ID_2", "", oldSource, oldDest, 0, 0);
			oldSourceLine = file.getLine("ID_2", oldSource) + "\n";
			oldDestLine = file.getLine("ID_2", destText2) + "\n";
		}
		file.addLastLineToTranslate(4, 4, "ID_3", "", sourceText3, destText3, 0, 0);
		file.addLastLineToTranslate(5, 5, "ID_4", "", sourceText4, destText4, 0, 0);
		
		// Create files corresponding to these data
		String[] sourceData = { PHParsedFile.getHeader("en", fileName, "English") + "\n" +
				file.getLine("ID_1", sourceText1) + "\n" +
				oldSourceLine +
				file.getLine("ID_3", sourceText3) + "\n" +
				file.getLine("ID_4", sourceText4) + "\n" +
				PHParsedFile.getFooter(),};
		List<String> sourceLines = Arrays.asList(sourceData);
		Path sourceFiletoWtrite = Paths.get(DIRECTORY + "StringTableEn" + fileName + ".xml");
		Files.write(sourceFiletoWtrite, sourceLines, StandardCharsets.UTF_8);
		String[] destData = { PHParsedFile.getHeader("en", fileName, "English") + "\n" +
				file.getLine("ID_1", destText1) + "\n" +
				oldDestLine +
				file.getLine("ID_3", destText3) + "\n" +
				file.getLine("ID_4", destText4) + "\n" +
				PHParsedFile.getFooter(),};
		List<String> destLines = Arrays.asList(destData);
		Path destFiletoWtrite = Paths.get(DIRECTORY + "StringTableFr" + fileName + ".xml");
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
		String[] expected = (PHParsedFile.getHeader("fr", fileName, "French") + "\n" + 
				file.getLine("ID_1", destText1) + "\n" + 
				file.getLine(entryToSave.getId(), entryToSave.getDestination()) + "\n" + 
				file.getLine("ID_3", destText3) + "\n" + 
				file.getLine("ID_4", destText4) + "\n" + 
				PHParsedFile.getFooter()).split("\n");
		FileInputStream fis = new FileInputStream(DIRECTORY + "StringTableFr" + fileName + ".xml");
		BufferedReader br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
		try {
			String line = null;
			int i = 0;
			while ((line = br.readLine()) != null) {
				Assert.assertTrue("Incorrect line number!", expected.length > i);
				assertLinesEquals(expected[i], line);
				i++;
			}
			Assert.assertEquals("Incorrect line number!", expected.length, i);
		} finally {
			br.close();
		}
		// Check also the source
		String[] expectedSource = ( PHParsedFile.getHeader("en", fileName, "English") + "\n" +
				file.getLine("ID_1", sourceText1) + "\n" + 
				file.getLine(entryToSave.getId(), entryToSave.getSource()) + "\n" + 
				file.getLine("ID_3", sourceText3) + "\n" + 
				file.getLine("ID_4", sourceText4) + "\n" + 
				PHParsedFile.getFooter()).split("\n");
		fis = new FileInputStream(DIRECTORY + "StringTableEn" + fileName + ".xml");
		br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
		try {
			String line = null;
			int i = 0;
			while ((line = br.readLine()) != null) {
				Assert.assertTrue("Incorrect line number!", expectedSource.length > i);
				Assert.assertEquals("Incorrect line!", expectedSource[i].replaceAll("\t", ""), line.replaceAll("\t", ""));
				i++;
			}
			Assert.assertEquals("Incorrect line number!", expectedSource.length, i);
		} finally {
			br.close();
		}
	}
	
	private void testSaveOneLine(String fileName, String destinationText,
			String sourceText, boolean missingDest, int sourceVersionNumber,
			int destinationVersionNumber) throws IOException {
		// Create data
		PHParsedFile file = new PHParsedFile(DIRECTORY, fileName);
		String oldDestText = "TOTO";
		String id = "ID_1";
		if (missingDest) {
			file.addLastLineToTranslate(2, HoI4ParsedEntry.MISSING_ENTRY, id, "", sourceText, "", sourceVersionNumber, 0);
		} else {
			file.addLastLineToTranslate(2, 2, id, "", sourceText, oldDestText, sourceVersionNumber, destinationVersionNumber);
		}
		
		// Create files corresponding to these data
		String[] sourceData = { PHParsedFile.getHeader("en", fileName, "English") + "\n" +
				file.getLine("ID_1", entryToSave.getSource()) + "\n" +
				PHParsedFile.getFooter(), };
		List<String> sourceLines = Arrays.asList(sourceData);
		Path sourceFiletoWtrite = Paths.get(DIRECTORY + "StringTableEn" + fileName + ".xml");
		Files.write(sourceFiletoWtrite, sourceLines, StandardCharsets.UTF_8);
		if (!missingDest) {
			String[] destData = {PHParsedFile.getHeader("fr", fileName, "French") + "\n" +
					file.getLine("ID_1", oldDestText) + "\n" +
					PHParsedFile.getFooter(), };
			List<String> destLines = Arrays.asList(destData);
			Path destFiletoWtrite = Paths.get(DIRECTORY + "StringTableFr" + fileName + ".xml");
			Files.write(destFiletoWtrite, destLines, StandardCharsets.UTF_8);
		}
		
		TranslatedEntry entryToSave2 = file.getFirstEntryToTranslate();
		
		// Modify and save the file
		entryToSave2.setSource(entryToSave.getSource());
		entryToSave2.setDestination(destinationText);
		file.getNextEntryToTranslateAndSave(entryToSave2, sourceLanguage, destinationLanguage);
		
		// Check that is what we expect
		String expected[] = (PHParsedFile.getHeader("fr", fileName, "French") + "\n" +  file.getLine("ID_1", destinationText) +
				"\n" +  PHParsedFile.getFooter()).split("\n");
		FileInputStream fis = new FileInputStream(DIRECTORY + "StringTableFr" + fileName + ".xml");
		BufferedReader br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
		try {
			String line = null;
			int i = 0;
			while ((line = br.readLine()) != null) {
				Assert.assertTrue("Incorrect line number!", expected.length > i);
				assertLinesEquals(expected[i], line);
				i++;
			}
			Assert.assertEquals("Incorrect line number!", expected.length, i);
		} finally {
			br.close();
		}
		// Check also source
		String sourceExpected[] = (PHParsedFile.getHeader("en", fileName, "English") + "\n" + file.getLine("ID_1", entryToSave.getSource()) + 
				"\n" + PHParsedFile.getFooter()).split("\n");
		fis = new FileInputStream(DIRECTORY + "StringTableEn" + fileName + ".xml");
		br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
		try {
			String line = null;
			int i = 0;
			while ((line = br.readLine()) != null) {
				Assert.assertTrue("Incorrect line number!", sourceExpected.length > i);
				assertLinesEquals(sourceExpected[i], line);
				i++;
			}
			Assert.assertEquals("Incorrect line number!", sourceExpected.length, i);
		} finally {
			br.close();
		}
	}
	
	private void assertLinesEquals(String expected, String line) {
		String lineWTabs = line.replaceAll("\t", "");
		int i = 0;
		while (i < lineWTabs.length()) {
			if (lineWTabs.charAt(i) == ' ') {
				i++;
			} else {
				break;
			}
		}
		Assert.assertEquals("Incorrect line!", expected.replaceAll("\t", ""), lineWTabs.substring(i));
	}
	
	@AfterClass
	public static void AfterCLass()
	{
		new File("./test_localisation_files/ph/StringTableEnsave.xml").delete();
		new File("./test_localisation_files/ph/StringTableFrsave.xml").delete();
		new File("./test_localisation_files/ph/StringTableEnsave2.xml").delete();
		new File("./test_localisation_files/ph/StringTableFrsave2.xml").delete();
		new File("./test_localisation_files/ph/StringTableEnsave3.xml").delete();
		new File("./test_localisation_files/ph/StringTableFrsave3.xml").delete();
		new File("./test_localisation_files/ph/StringTableEnsave4.xml").delete();
		new File("./test_localisation_files/ph/StringTableFrsave4.xml").delete();
		new File("./test_localisation_files/ph/StringTableEnsave5.xml").delete();
		new File("./test_localisation_files/ph/StringTableFrsave5.xml").delete();
		new File("./test_localisation_files/ph/StringTableEnsave6.xml").delete();
		new File("./test_localisation_files/ph/StringTableFrsave6.xml").delete();
		new File("./test_localisation_files/ph/StringTableEnsave7.xml").delete();
		new File("./test_localisation_files/ph/StringTableFrsave7.xml").delete();
		new File("./test_localisation_files/ph/StringTableEnsave8.xml").delete();
		new File("./test_localisation_files/ph/StringTableFrsave8.xml").delete();
		new File("./test_localisation_files/ph/StringTableEnsave9.xml").delete();
		new File("./test_localisation_files/ph/StringTableFrsave9.xml").delete();
	}

}
