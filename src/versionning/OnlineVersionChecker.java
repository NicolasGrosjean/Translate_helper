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
	private static String VERSION = "3.1";
	
	private int lastestOnlineMajorVersionNumber;
	private int lastestOnlineMinorVersionNumber;

	public OnlineVersionChecker()
	{
		String changelogOrNothing = newVersionOnline();
		if (changelogOrNothing.length() > 0) {
			StringSelection selection = new StringSelection(getGithHubDownloadUrl());
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
			boolean firstRead = true;
			while ((inputLine = in.readLine()) != null){
				if (inputLine.contains("AppVersion=")){
					String onlineVersion = inputLine.substring(inputLine.indexOf("=") + 1, inputLine.length());
					String[] aOnlineV = onlineVersion.split("\\.");
					int onlineMajorVersionNumber = Integer.parseInt(aOnlineV[0]);
					int onlineMinorVersionNumber = Integer.parseInt(aOnlineV[1]);
					if (firstRead)
					{
						firstRead = false;
						lastestOnlineMajorVersionNumber = onlineMajorVersionNumber;
						lastestOnlineMinorVersionNumber = onlineMinorVersionNumber;
					}
					String[] aLocalV = VERSION.split("\\.");
					
					if (onlineMajorVersionNumber < Integer.parseInt(aLocalV[0])) {
						return changelog;
					} else if (onlineMajorVersionNumber == Integer.parseInt(aLocalV[0])) {
						if (onlineMinorVersionNumber <= Integer.parseInt(aLocalV[1])) {
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
	
	/**
	 * Get the download GitHub URL.
	 * EX : https://github.com/NicolasGrosjean/Translate_helper/releases/download/v2.1/TranslateHelper_v2-1.rar
	 * @return string of the url to download the last version of the software
	 */
	private String getGithHubDownloadUrl()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("https://github.com/NicolasGrosjean/Translate_helper/releases/download/v");
		builder.append(lastestOnlineMajorVersionNumber);
		builder.append(".");
		builder.append(lastestOnlineMinorVersionNumber);
		builder.append("/TranslateHelper_v");
		builder.append(lastestOnlineMajorVersionNumber);
		builder.append("-");
		builder.append(lastestOnlineMinorVersionNumber);
		builder.append(".rar");
		return builder.toString();
	}
}
