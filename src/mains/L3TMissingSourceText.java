package mains;


import parsing.Language;
import parsing.Parse;

public class L3TMissingSourceText {

	public static void main(String[] args) {
		Parse p = new Parse(Parse.listDirectoryFiles("C:/Users/Nicolas/Documents/GitHub/L3T/L3T/localisation"),
				new Language("FRENCH", 2, "fr"), new Language("ENGLISH", 1, "en"),
				"C:/Users/Nicolas/Documents/GitHub/L3T/L3T/fake_translations.txt",
				"C:/Users/Nicolas/Documents/GitHub/L3T/L3T/accepted_loanwords.txt");
		System.out.print(p.getListMissingSourceText());
	}

}
