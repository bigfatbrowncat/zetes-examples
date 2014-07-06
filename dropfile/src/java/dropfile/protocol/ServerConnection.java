package dropfile.protocol;

import java.io.IOException;
import java.net.Socket;

public class ServerConnection implements Connection {
	private Socket clientSocket;
	protected ServerConnection(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	
	@Override
	public String getLocalAddress() {
		return clientSocket.getLocalAddress().toString();
	}
	
	@Override
	public String getRemoteAddress() {
		return clientSocket.getRemoteSocketAddress().toString();
	}
	
	@Override
	public void close() {
		try {
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
