package htmlview;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import zetes.wings.base.DocumentBase;

public class HTMLDocument extends DocumentBase {

	private String fileName;
	private String documentTitle;
	private String htmlText;
	
	private static String readFileToString(String filePath) throws IOException {
        StringBuffer fileData = new StringBuffer();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }
	
	HTMLDocument(String fileName) throws IOException {
		this.fileName = fileName;
		htmlText = readFileToString(fileName);
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
