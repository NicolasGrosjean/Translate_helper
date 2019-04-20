package tests;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import parsing.CK2ParsedFile;
import parsing.HoI4ParsedFile;
import translator.TranslatedEntry;

public class TestNavigation {

	@Test
	public void testGoToWithCK2File() throws IOException {
		String filePath = "./test_localisation_files/ck2/save_file.csv";
		CK2ParsedFile file = new CK2ParsedFile(filePath);
		file.setLineNumber(5);
		file.addLastLineToTranslate(2, "TRADE.0005A", "", "OK", "J'en prends note");
		file.addLastLineToTranslate(3, "TRADE.0005B", "", "Toto", "Titi et grosminet");
		file.addLastLineToTranslate(4, "TRADE.0005C", "", "What?", "Quoi?");
		file.addLastLineToTranslate(5, "TRADE.0005D", "", "Okay", "D'ac");
		
		file.getFirstEntryToTranslate();
		TranslatedEntry entry = file.getEntryToTranslate(3);
		Assert.assertEquals("Incorrect next entry!", "Toto", entry.getSource());
		Assert.assertEquals("Incorrect next entry!", "Titi et grosminet", entry.getDestination());
		entry = file.getEntryToTranslate(5);
		Assert.assertEquals("Incorrect next entry!", "Okay", entry.getSource());
		Assert.assertEquals("Incorrect next entry!", "D'ac", entry.getDestination());
	}

	@Test
	public void testGoToWithHoI4File() throws IOException {
		String filePath = "./test_localisation_files/ck2/save_file.csv";
		HoI4ParsedFile file = new HoI4ParsedFile(filePath, "save_file");
		//file.setLineNumber(5);
		file.addLastLineToTranslate(2, 2, "TRADE.0005A", "", "OK", "J'en prends note", 0, 0);
		file.addLastLineToTranslate(4, 3, "TRADE.0005B", "", "Toto", "Titi et grosminet", 0, 0);
		file.addLastLineToTranslate(5, 4, "TRADE.0005C", "", "What?", "Quoi?", 0, 0);
		file.addLastLineToTranslate(8, 5, "TRADE.0005D", "", "Okay", "D'ac", 0, 0);
		
		file.getFirstEntryToTranslate();
		TranslatedEntry entry = file.getEntryToTranslate(3);
		Assert.assertEquals("Incorrect next entry!", "Toto", entry.getSource());
		Assert.assertEquals("Incorrect next entry!", "Titi et grosminet", entry.getDestination());
		entry = file.getEntryToTranslate(5);
		Assert.assertEquals("Incorrect next entry!", "Okay", entry.getSource());
		Assert.assertEquals("Incorrect next entry!", "D'ac", entry.getDestination());
	}
}
