package jlanguagetool;

import java.io.IOException;
import java.util.List;

import org.languagetool.JLanguageTool;
import org.languagetool.gui.Main;
import org.languagetool.language.BritishEnglish;
import org.languagetool.language.French;
import org.languagetool.rules.RuleMatch;

public class MainJLanguageToolTesting {

	public static void main(String[] args) throws IOException {
		JLanguageTool langTool = new JLanguageTool(new BritishEnglish());
		List<RuleMatch> matches = langTool.check("A sentence with a error in the Hitchhiker's Guide tot he Galaxy");
		 
//		for (RuleMatch match : matches) {
//		  System.out.println("Potential error at line " +
//		      match.getLine() + ", column " +
//		      match.getColumn() + ": " + match.getMessage());
//		  System.out.println("Suggested correction: " +
//		      match.getSuggestedReplacements());
//		}
		
		langTool = new JLanguageTool(new French());
		matches = langTool.check("Se module est en cours de développment");
		 
		for (RuleMatch match : matches) {
		  System.out.println("Potential error at line " +
		      match.getLine() + ", column " +
		      match.getColumn() + ": " + match.getShortMessage());
		  System.out.println("Suggested correction: " +
		      match.getSuggestedReplacements());
		}
		
//		Main.main(args);
	}

}
