package dropfile.protocol;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {
	
	private static final int ATTEMPTS_MAX = 3;
	private static final int ATTEMPTS_PAUSE = 1000;
	
	private enum State {
		free, connecting
	}
	
	public enum Action { abort, retry };
	
	public interface ConnectionListener {
		void onEstablished(Client client, ClientConnection connection);
		void onFailed(Client sender, Exception e, int attempt);
	}
	
	private Runnable connectionRunnable = new Runnable() {
		
		@Override
		public void run() {
			state = State.connecting;
			int attempt = 0;
			while (attempt < ATTEMPTS_MAX) {
				try {
					Socket socket = new Socket();

					// Connecting
					socket.connect(new InetSocketAddress(remoteAddress, Server.PORT), ATTEMPTS_PAUSE);
					
					ClientConnection clientConnection = new ClientConnection(socket, remoteAddress);

					if (connectionListener != null) {
						connectionListener.onEstablished(Client.this, clientConnection);
					}
					
					break;
					
				} catch (IOException e) {
					attempt ++;
					if (connectionListener != null) {
						connectionListener.onFailed(Client.this, e, attempt);
					}
				}
				
				try {
					Thread.sleep(ATTEMPTS_PAUSE);
				} catch (InterruptedException e) { }
			}
			state = State.free;
		}
	};
	
	private volatile State state;
	private Thread connectionThread;
	private ConnectionListener connectionListener;
	private String remoteAddress;
	
	
	public Client() {
		state = State.free;
	}
	
	public boolean connect(String remoteAddress, ConnectionListener listener) {
		if (state == State.free) {
			this.remoteAddress = remoteAddress;
			this.connectionListener = listener;
			connectionThread = new Thread(connectionRunnable);
			connectionThread.start();
			return true;
		} else {
			return false;
		}
	}
}
