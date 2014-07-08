package dropfile.protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerConnection implements Connection {
	
	private Socket clientSocket;
	private Thread talkingThread = new Thread(new Runnable() {
		
		@Override
		public void run() {
			try {
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
				while (!clientSocket.isClosed()) {
					String command = in.readLine();
					String[] commandArgs = command.split(" ");
					if (commandArgs[0].equals("START_FILE")) {
						String fileName = commandArgs[1]; 
						
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	});
	
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
	
	public void startConversation() {
		talkingThread.start();
	}
	
	public void sendFile(String fileName) {
		
	}
}
