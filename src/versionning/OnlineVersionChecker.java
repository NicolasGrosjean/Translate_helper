package versionning;

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
	// TODO change it
	private static String URL_APP_INFO_TXT = "https://raw.githubusercontent.com/NicolasGrosjean/Translate_helper/master/AppInfo.txt";
	public static String VERSION = "1.0";
	
	private String onlineVersion;
	
	public OnlineVersionChecker()
	{
		if (newVersionOnline()) {
			JOptionPane.showMessageDialog(null, "A new version of this application is available",
					"New version available", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	private boolean newVersionOnline(){
		try{
			URL appInfoTxt = new URL(URL_APP_INFO_TXT);
			BufferedReader in = new BufferedReader(new InputStreamReader(appInfoTxt.openStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null){
				if (inputLine.contains("AppVersion=")){
					onlineVersion = inputLine.substring(inputLine.indexOf("=") + 1, inputLine.length());
					String[] aOnlineV = onlineVersion.split("\\.");
					String[] aLocalV = VERSION.split("\\.");
					
					if (Integer.parseInt(aOnlineV[0]) > Integer.parseInt(aLocalV[0])) {
						return true;
					} else if (Integer.parseInt(aOnlineV[0]) == Integer.parseInt(aLocalV[0])) {
						if (Integer.parseInt(aOnlineV[1]) > Integer.parseInt(aLocalV[1])) {
							return true;
						}
					}
				}
			}
	        in.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Unable to check online version",
					"Version checking error", JOptionPane.ERROR_MESSAGE);
		}
		
		return false;
	}
}
