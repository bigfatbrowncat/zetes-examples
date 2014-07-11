package dropfile.protocol;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerConnection extends Connection {
	
	
	protected ServerConnection(Socket clientSocket) throws IOException {
		super(clientSocket);
		
		PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

		System.out.println("[server] sending server greeting");
		out.write("DROPFILE_SERVER\n");
		out.flush();

		System.out.println("[server] receiving client greeting");
		String request = readLine();
		if (request.equals("DROPFILE_CLIENT")) {
			startConversation();
		} else {
			clientSocket.close();
		}
	}
	
}
