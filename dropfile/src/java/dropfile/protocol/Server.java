package dropfile.protocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	protected static final int PORT = 32123;

	public interface ClientConnectedListener {
		void onClientConnected(Server sender, ServerConnection connection);
	}
	
	private ServerSocket serverSocket;
	private volatile ClientConnectedListener clientConnectedListener;
	private volatile boolean cancelled;
	
	public Server() throws IOException {
		serverSocket = new ServerSocket(PORT);
	}
	
	public void listen() {
		while (!cancelled) {
			try {
				Socket clientSocket = serverSocket.accept();

				ServerConnection connection = new ServerConnection(clientSocket);
				if (clientConnectedListener != null) {
					clientConnectedListener.onClientConnected(this, connection);
				}

				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}
	
	public void setClientConnectedListener(ClientConnectedListener clientConnectedListener) {
		this.clientConnectedListener = clientConnectedListener;
	}
	
	void cancel() {
		cancelled = true;
	}
}
