package parsing;

import java.util.LinkedList;

public class Parse {
	/**
	 * List of the "localisation" files
	 */
	private LinkedList<File> files;

	/**
	 * Parse the files in order to store the entries which are not translated 
	 * or without source language text
	 * @param fileNames The list of the names of the files to parse
	 */
	public Parse(LinkedList<String> fileNames) {
		files = new LinkedList<File>();
		for (String fileName : fileNames) {
			files.addLast(parseAFile(fileName));
		}
	}

	private File parseAFile(String file) {
		// TODO
		return null;
	}
}
