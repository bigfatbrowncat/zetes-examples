package dropfile.protocol;

import java.io.IOException;
import java.net.Socket;

public class Connection {
	private static final int ATTEMPTS_MAX = 3;
	private static final int ATTEMPTS_PAUSE = 1000;
	private static final int PORT = 80;
	
	private Socket socket;
	private boolean success;
	
	public Connection(String remoteAddress) {
		int attempts = 0;
		success = false;
		while (attempts < ATTEMPTS_MAX) {
			try {
				socket = new Socket(remoteAddress, PORT);
				success = true;
				break;
			} catch (IOException e) {
				attempts ++;
				e.printStackTrace();
			}
			
			try {
				Thread.sleep(ATTEMPTS_PAUSE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		try {
			if (success) socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean getSuccess() {
		return success;
	}
}
