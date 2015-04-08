package htmlview;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import zetes.wings.abstracts.Document;

public class HTMLDocument implements Document {

	private String fileName;
	private String title;
	private String htmlText;
	
	HTMLDocument(String path) throws IOException {
		htmlText = FileUtils.readFileToString(new File(path));
	}
	
	@Override
	public void dispose() {

	}
 
	@Override
	public String getTitle() {
		if (title != null) {
			return title;
		} else {
			return fileName;
		}
	}
	
	public String getHTMLText() {
		return htmlText;
	}

}
