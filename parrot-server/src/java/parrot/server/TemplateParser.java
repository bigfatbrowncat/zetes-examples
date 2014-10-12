package parrot.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateParser {
	private InputStream inputStream;
	private HashMap<String, String> variables = new HashMap<>();
	
	public TemplateParser(InputStream stream) throws FileNotFoundException {
		this.inputStream = stream;
	}
	
	public void setVariable(String variable, String value) {
		variables.put(variable, value);
	}
	
	public void process(PrintStream target) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		Pattern pattern = Pattern.compile("@\\{([a-zA-Z0-9_]*)\\}");
		String line;
		while ((line = reader.readLine()) != null) {
			while (true) {
				Matcher matcher = pattern.matcher(line);
				if (matcher.find()) {
					String group = matcher.group(1);
					line = line.substring(0, matcher.start()) + variables.get(group) + line.substring(matcher.end());
				} else {
					break;
				}
			}
			target.println(line);
			
		}
	}
}
