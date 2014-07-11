package dropfile;

import java.io.File;
import java.io.IOException;

import zetes.wings.abstracts.Document;
import dropfile.protocol.ClientConnection;
import dropfile.protocol.Connection;

public class Session implements Document
{
	private Connection connection;
	
	public Session(Connection connection) {
		this.connection = connection;
	}

	public Connection getConnection() {
		return connection;
	}
	
	public String getTitle()
	{
		return "Session with " + connection.getRemoteAddress();
	}
	
	public void sendFile(String fileName) {
		try {
			System.out.println("sending file " + fileName);
			connection.sendFile(new File(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void dispose()
	{
		connection.close();
	}
	
	@Override
	protected void finalize() throws Throwable
	{
		dispose();
		super.finalize();
	}
}
