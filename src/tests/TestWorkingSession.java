package tests;

import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;

import parsing.Language;
import config.WorkingSession;

/**
 * Test the working session class
 * NOTA : To run it with Eclipse, go to the project > Properties > Add Library... > JUnit
 * @author Nicolas Grosjean
 *
 */
public class TestWorkingSession {
	@Test
	public void testSetAvailableLanguages() {
		WorkingSession.setAvailableLanguages("./config/available_languages.csv");
		LinkedList<Language> availableLanguages = WorkingSession.getAvailableLanguages();
		//Assert.assertEquals("Incorrect number of languages", 2, availableLanguages.size());
		// First language
		Language l = availableLanguages.removeFirst();
		Assert.assertEquals("Incorrect column number for ENGLISH", 1, l.getDefaultColumn());
		Assert.assertEquals("Incorrect code for ENGLISH", "ENGLISH", l.getCode());
		// Second language
		l = availableLanguages.removeFirst();
		Assert.assertEquals("Incorrect column number for FRENCH", 2, l.getDefaultColumn());
		Assert.assertEquals("Incorrect code for FRENCH", "FRENCH", l.getCode());
	}
}
