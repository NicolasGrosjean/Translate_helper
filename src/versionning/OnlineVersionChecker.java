package versionning;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.JOptionPane;

/**
 * 
 * @author SIMON-FINE Thibaut (alias Bisougai), and GROSJEAN Nicolas (alias Mouchi)
 *
 */
public class OnlineVersionChecker {
	private static String URL_APP_INFO_TXT = "https://raw.githubusercontent.com/NicolasGrosjean/Translate_helper/master/AppInfo.txt";
	private static String VERSION = "1.5";
	private static String TOOL_URL = "https://forum.paradoxplaza.com/forum/index.php?threads/tool-translate-helper.1043308/";
	
	private String onlineVersion;
	
	public OnlineVersionChecker()
	{
		String changelogOrNothing = newVersionOnline();
		if (changelogOrNothing.length() > 0) {
			StringSelection selection = new StringSelection(TOOL_URL);
		    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		    clipboard.setContents(selection, selection);
			JOptionPane.showMessageDialog(null, "A new version of this application is available.\n" +
					"Go to the url copied in your clipboard\n" +
					"CHANGELOG:" +  changelogOrNothing,
					"New version available", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	private String newVersionOnline(){
		String changelog = "";
		try{
			URL appInfoTxt = new URL(URL_APP_INFO_TXT);
			BufferedReader in = new BufferedReader(new InputStreamReader(appInfoTxt.openStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null){
				if (inputLine.contains("AppVersion=")){
					onlineVersion = inputLine.substring(inputLine.indexOf("=") + 1, inputLine.length());
					String[] aOnlineV = onlineVersion.split("\\.");
					String[] aLocalV = VERSION.split("\\.");
					
					if (Integer.parseInt(aOnlineV[0]) < Integer.parseInt(aLocalV[0])) {
						return changelog;
					} else if (Integer.parseInt(aOnlineV[0]) == Integer.parseInt(aLocalV[0])) {
						if (Integer.parseInt(aOnlineV[1]) <= Integer.parseInt(aLocalV[1])) {
							return changelog;
						}
					}
				} else {
					if (inputLine.length() > 0) {
						changelog += "\n" + inputLine;
					}
				}
			}
	        in.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Unable to check online version",
					"Version checking error", JOptionPane.ERROR_MESSAGE);
		}
		
		return changelog;
	}
}
