package mains;

import parsing.Parse;

public class L3TMissingSourceText {

	public static void main(String[] args) {
		Parse p = new Parse(Parse.listDirectoryFiles("C:/Users/Nicolas/Documents/GitHub/L3T/L3T/localisation"),
				"FRENCH", 2, "ENGLISH", 1);
		System.out.print(p.getListMissingSourceText());
	}

}
