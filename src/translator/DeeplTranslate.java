package translator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

public class DeeplTranslate implements TranslateAPI {
	private static String API_KEY_FILE = "config/deepL_api_key.txt";
	
	private String sourceLanguageCode;
	private String destinationLanguageCode;
	private String apiKey;

	public DeeplTranslate(String sourceLanguageCode, String destinationLanguageCode) {
		this.sourceLanguageCode = sourceLanguageCode.toUpperCase();
		this.destinationLanguageCode = destinationLanguageCode.toUpperCase();
		File apiKeyFile = new File(API_KEY_FILE);
		if (apiKeyFile.exists()) {
			try (BufferedReader br = new BufferedReader(new FileReader(apiKeyFile))) {
				this.apiKey = br.readLine();
			} catch (IOException e) {
				System.out.println("Fail to get DeepL API Key from " + API_KEY_FILE);
			}
		} else {
			this.apiKey = "";
		}
	}

	@Override
	public String translate(String text) throws IOException
	{
		if ("" == this.apiKey) {
			throw new IOException("API key not set");
		}
		String url = "https://api-free.deepl.com/v2/translate?source_lang=" + sourceLanguageCode + "&target_lang="
				+ destinationLanguageCode + "&text=" + URLEncoder.encode(text, "UTF-8");

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestProperty("Authorization", "DeepL-Auth-Key "+ apiKey);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		return (String)((JSONObject)((JSONArray) new JSONObject(response.toString()).get("translations")).get(0)).get("text");
	}

	@Override
	public String getAPIName() {
		return "DeepL";
	}
}
