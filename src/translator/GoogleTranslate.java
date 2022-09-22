package translator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Adaptation of the Archana Katiyar script
 * (http://archana-testing.blogspot.fr/2016/02/calling-google-translation-api-in-java.html)
 */
public class GoogleTranslate implements TranslateAPI {
	private String sourceLanguageCode;
	private String destinationLanguageCode;

	public GoogleTranslate(String sourceLanguageCode, String destinationLanguageCode) {
		this.sourceLanguageCode = sourceLanguageCode;
		this.destinationLanguageCode = destinationLanguageCode;
	}

	@Override
	public String translate(String text) throws IOException
	{
		String url = "https://translate.googleapis.com/translate_a/single?" + "client=gtx&" + "sl=" + sourceLanguageCode + "&tl="
				+ destinationLanguageCode + "&dt=t&q=" + URLEncoder.encode(text, "UTF-8");

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestProperty("User-Agent", "Mozilla/5.0");

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		return parseResult(response.toString());
	}

	private String parseResult(String inputJson) throws JSONException {
		JSONArray jsonArray = new JSONArray(inputJson);
		JSONArray jsonArray2 = (JSONArray) jsonArray.get(0);
		Iterator<Object> it = jsonArray2.iterator();
		String res = "";
		while (it.hasNext())
		{
			res += ((JSONArray) it.next()).get(0).toString();
		}
		return res;
	}

	@Override
	public String getAPIName() {
		return "Google";
	}
}
