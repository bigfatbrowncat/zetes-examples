package dropfile;

import zetes.wings.abstracts.Document;


public class Session implements Document
{
	private String fileName;
	
	public Session()
	{
	}
	
	public Session(String fileName) {
		this.setFileName(fileName);
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public String getTitle()
	{
		return "Session";
	}
	
	public void dispose()
	{		
	}
	
	@Override
	protected void finalize() throws Throwable
	{
		dispose();
		super.finalize();
	}
}
