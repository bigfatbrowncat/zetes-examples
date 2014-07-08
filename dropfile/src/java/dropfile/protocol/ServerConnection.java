package dropfile.protocol;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

public class ServerConnection implements Connection {
	
	private Socket socket;
	private HashMap<Integer, FileTransmitter> receivingFileTransmitters = new HashMap<>();
	
	private Thread talkingThread = new Thread(new Runnable() {
		
		public int fillBufferCompletely(InputStream is, byte[] bytes) throws IOException {
		    int size = bytes.length;
		    int offset = 0;
		    while (offset < size) {
		        int read = is.read(bytes, offset, size - offset);
		        if (read == -1) {
		            if ( offset == 0 ) {
		                return -1;
		            } else {
		                return offset;
		            }
		        } else {
		            offset += read;
		        }
		    }

		    return size;
		}
		
		@Override
		public void run() {
			try {
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
				while (!socket.isClosed()) {
					String command = in.readLine();
					if (command == null) break;
					System.out.println("command: " + command);
					String[] commandArgs = command.split(" ");
					if (commandArgs[0].equals("START_FILE")) {
						System.out.println("received start_file");
						int id = Integer.parseInt(commandArgs[1]);
						String fileName = commandArgs[2]; 
						long fileSize = Long.parseLong(commandArgs[3]);
						
						File targetFile = new File(fileName);
						if (targetFile.exists()) {
							if (!targetFile.delete()) {
								throw new RuntimeException("Can't recreate the target file");
							}
						}
												
						receivingFileTransmitters.put(id, new FileTransmitter(fileName, fileSize, targetFile));
					} else if (commandArgs[0].equals("FILE_PART")) {
						System.out.println("received file_part");

						int id = Integer.parseInt(commandArgs[1]);
						int partIndex = Integer.parseInt(commandArgs[2]);
						FileTransmitter fileTransmitter = receivingFileTransmitters.get(id);
						int partSize = fileTransmitter.getPartSize(partIndex);
						
						byte[] data = new byte[partSize];
						System.out.println("reading " + data.length + " bytes...");
						fillBufferCompletely(socket.getInputStream(), data);
						
						fileTransmitter.putPart(partIndex, data);
						System.out.println("put");
						if (fileTransmitter.isComplete()) {
							fileTransmitter.close();
							
							System.out.println("file is complete " + id);
							// TODO Add file received event
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	});
	
	protected ServerConnection(Socket clientSocket) {
		this.socket = clientSocket;
	}
	
	@Override
	public String getLocalAddress() {
		return socket.getLocalAddress().toString();
	}
	
	@Override
	public String getRemoteAddress() {
		return socket.getRemoteSocketAddress().toString();
	}
	
	@Override
	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void startConversation() {
		talkingThread.start();
	}
	
}
