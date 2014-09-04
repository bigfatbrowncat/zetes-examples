package dropfile.protocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Server {
	protected static final int PORT = 32123;

	public enum Action { stop, listen };

	public interface ConnectionListener {
		void onEstablished(Server sender, ServerConnection connection);
		void onFailed(Server sender, Exception e);
	}
	public interface ServerListener {
		void onStopped(Server sender);
		Action onError(Server sender, Exception e);
	}

	private enum State {
		created, listening, stopped;
	}
	
	private ServerSocket serverSocket;
	private volatile State state;

	private volatile List<ConnectionListener> connectionListeners = Collections.synchronizedList(new LinkedList<ConnectionListener>());
	private volatile List<ServerListener> serverListeners = Collections.synchronizedList(new LinkedList<ServerListener>());

	public Server() throws IOException {
		state = State.created;
		serverSocket = new ServerSocket(PORT);
	}

	private Thread serverThread = new Thread(new Runnable() {
		
		@Override
		public void run() {
			Action action = Action.listen;
			while (action == Action.listen) {
				Exception serverError = null;
				try {
					Socket clientSocket = serverSocket.accept();
					try {
						ServerConnection connection = new ServerConnection(clientSocket);
						synchronized (connectionListeners) {
							for (ConnectionListener connectionListener : connectionListeners) {
								connectionListener.onEstablished(Server.this, connection);
							}
						}
					} catch (IOException e) {
						/*showError("Can't connect to client. ", e);*/
						synchronized (connectionListeners) {
							for (ConnectionListener connectionListener : connectionListeners) {
								connectionListener.onFailed(Server.this, e);
							}
						}
					}
				} catch (final IOException e) {
					/*showError("Server problem. ", e);*/
					serverError = e;
				} finally {
					if (state == State.stopped) {
						if (serverError == null) {
							synchronized (serverListeners) {
								for (ServerListener serverListener : serverListeners) {
									serverListener.onStopped(Server.this);
								}
							}
							action = Action.stop; // We've received a cancel request, so we aren't asking again
						} else {
							synchronized (serverListeners) {
								for (ServerListener serverListener : serverListeners) {
									action = serverListener.onError(Server.this, serverError);
								}
							}
						}
					}
				}
			}
			state = State.stopped;
		}
	});
	
	public boolean listen() {
		if (state == State.created) {
			serverThread.start();
			return true;
		} else {
			return false;
		}
	}

	public boolean cancel() {
		if (state == State.listening) {
			state = State.stopped;
			return true;
		} else {
			return false;
		}
	}

	public void addConnectionListener(ConnectionListener connectionListener) {
		connectionListeners.add(connectionListener);
	}
	
	public void removeConnectionListener(ConnectionListener connectionListener) {
		connectionListeners.remove(connectionListener);
	}

	public void addServerListener(ServerListener serverListener) {
		serverListeners.add(serverListener);
	}
	
	public void removeServerListener(ServerListener serverListener) {
		serverListeners.remove(serverListener);
	}

}
