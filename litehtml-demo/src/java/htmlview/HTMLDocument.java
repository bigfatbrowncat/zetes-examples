package htmlview;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import zetes.wings.base.DocumentBase;

public class HTMLDocument extends DocumentBase {

	private String fileName;
	private String documentTitle;
	private String htmlText;
	
	HTMLDocument(String fileName) throws IOException {
		this.fileName = fileName;
		htmlText = FileUtils.readFileToString(new File(fileName));
	}
	
	@Override
	public void dispose() {

	}
 
	@Override
	public String getTitle() {
		if (documentTitle != null) {
			return documentTitle;
		} else {
			return new File(fileName).getName();
		}
	}
	
	public void setDocumentTitle(String title) {
		this.documentTitle = title;
		issueTitleChanged();
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public String getHTMLText() {
		return htmlText;
	}

}
