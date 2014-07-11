package dropfile.protocol;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;

public class ClientConnection extends Connection {
	

	private static final int ATTEMPTS_MAX = 3;
	private static final int ATTEMPTS_PAUSE = 1000;
	private static final int PORT = 32123;
	
	private String remoteAddress;

	public ClientConnection(String remoteAddress) throws Exception {
		super(new Socket());
		
		this.remoteAddress = remoteAddress;
		int attempts = 0;
		Exception lastException = null;
		while (attempts < ATTEMPTS_MAX) {
			try {
				// Connecting
				getSocket().connect(new InetSocketAddress(remoteAddress, PORT), ATTEMPTS_PAUSE);
				
				PrintWriter out = new PrintWriter(getSocket().getOutputStream(), true);
				
				// Sending the request
				System.out.println("[client] sending client greeting");
				out.write("DROPFILE_CLIENT\n");
				out.flush();

				// Checking the response
				System.out.println("[client] receiving server greeting");
				String response = readLine();
				if (!response.equals("DROPFILE_SERVER")) {
					throw new IOException("Invalid server response. Maybe the DropFile port is used by another application.");
				}
				
				startConversation();
				
				break;
			} catch (IOException e) {
				attempts ++;
				e.printStackTrace();
				lastException = e;
			}
			
			try {
				Thread.sleep(ATTEMPTS_PAUSE);
			} catch (InterruptedException e) {
				e.printStackTrace();
				lastException = e;
			}
		}
		
		if (lastException != null) {
			throw lastException;
		}
	}
	
	@Override
	public String getRemoteAddress() {
		return remoteAddress;
	}
}
