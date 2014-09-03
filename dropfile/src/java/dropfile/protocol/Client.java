package dropfile.protocol;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {
	
	private static final int ATTEMPTS_MAX = 3;
	private static final int ATTEMPTS_PAUSE = 1000;
	
	public ClientConnection connect(String remoteAddress) throws IOException {
		int attempts = 0;
		IOException lastException = null;
		while (attempts < ATTEMPTS_MAX) {
			try {
				Socket socket = new Socket();

				// Connecting
				socket.connect(new InetSocketAddress(remoteAddress, Server.PORT), ATTEMPTS_PAUSE);
				
				return new ClientConnection(socket, remoteAddress);
			} catch (IOException e) {
				attempts ++;
				e.printStackTrace();
				lastException = e;
			}
			
			try {
				Thread.sleep(ATTEMPTS_PAUSE);
			} catch (InterruptedException e) {
			}
		}
		
		throw lastException;
	}
}
