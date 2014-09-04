package dropfile.protocol;

import java.io.IOException;
import java.net.Socket;

public class ServerConnection extends Connection {
	private static final String TAG = "server";
	protected static final String CMD_DROPFILE_SERVER = "DROPFILE_SERVER";
	
	protected ServerConnection(Socket clientSocket) throws IOException {
		super(TAG, clientSocket);
		
		handshake(CMD_DROPFILE_SERVER, ClientConnection.CMD_DROPFILE_CLIENT);
	}
	
}
