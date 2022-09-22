package translator;

import java.io.IOException;

public interface TranslateAPI {
	public String translate(String text) throws IOException;

	public String getAPIName();
}
