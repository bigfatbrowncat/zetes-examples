package dropfile;

import zetes.wings.abstracts.Document;
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
