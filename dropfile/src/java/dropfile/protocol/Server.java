package dropfile.protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private static final int PORT = 32123;

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
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

				System.out.println("[server] sending server greeting");
				out.write("DROPFILE_SERVER\n");
				out.flush();

				System.out.println("[server] receiving client greeting");
				String request = in.readLine();
				if (request.equals("DROPFILE_CLIENT")) {
					
					ServerConnection connection = new ServerConnection(clientSocket);
					if (clientConnectedListener != null) {
						clientConnectedListener.onClientConnected(this, connection);
					}
				} else {
					clientSocket.close();
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
