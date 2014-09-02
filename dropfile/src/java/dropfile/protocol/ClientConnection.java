package dropfile.protocol;

import java.io.IOException;
import java.net.Socket;

public class ClientConnection extends Connection {
	private static final String TAG = "client";
	protected static final String CMD_DROPFILE_CLIENT = "DROPFILE_CLIENT";

	private String remoteAddress;

	protected ClientConnection(Socket serverSocket, String remoteAddress) throws IOException {
		super(TAG, serverSocket);
		this.remoteAddress = remoteAddress;
		
		handshake(CMD_DROPFILE_CLIENT, ServerConnection.CMD_DROPFILE_SERVER);
	}
	
	@Override
	public String getRemoteAddress() {
		return remoteAddress;
	}
}
