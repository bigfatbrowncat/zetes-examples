package dropfile.protocol;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

public abstract class Connection {
	private class FileSendingIndicator {
		private boolean[] data = null;
		private FileTransmitter fileTransmitter;
		
		public FileSendingIndicator(FileTransmitter fileTransmitter) {
			this.fileTransmitter = fileTransmitter;
		}
		
		public boolean isStarted() {
			return data != null;
		}
		
		public boolean isComplete() {
			for (boolean part : data) {
				if (!part) return false;
			}
			return true;
		}
		
		public int getFirstIncompletePart() {
			for (int i = 0; i < data.length; i++) {
				if (!data[i]) return i;
			}
			return -1;
		}

		public void setPart(int partIndex) {
			data[partIndex] = true;
		}
		
		public void setStarted() {
			data = new boolean[fileTransmitter.getPartsCount()];
		}
	}
	
	private Socket socket;

	private HashMap<Integer, FileTransmitter> sendingFileTransmitters = new HashMap<>();
	private HashMap<Integer, FileTransmitter> receivingFileTransmitters = new HashMap<>();
	private HashMap<Integer, FileSendingIndicator> sendingIndicators = new HashMap<>();

	private Thread sendingThread = new Thread(new Runnable() {

		@Override
		public void run() {
			try {
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

				while (!socket.isClosed()) {
					
					boolean doneAnything = false;
					synchronized (idLock) {

						for (Integer id : sendingFileTransmitters.keySet()) {
							FileTransmitter sendingFileTransmitter = sendingFileTransmitters.get(id);
							FileSendingIndicator sendingIndicator = sendingIndicators.get(id);
							
							if (!sendingIndicator.isStarted()) {
								System.out.println("start_file " + id + " " + sendingFileTransmitter.getFileName());
								System.out.println("" + sendingFileTransmitter.getPartsCount());

								out.println("START_FILE " + id + " " + sendingFileTransmitter.getFileName() + " " + sendingFileTransmitter.getFileSize());
								sendingIndicator.setStarted();
								doneAnything = true;
							}
							
							if (!sendingIndicator.isComplete()) {
								int partIndexToSend = sendingIndicator.getFirstIncompletePart();

								System.out.println("file_part " + id + " " + partIndexToSend);
								out.println("FILE_PART " + id + " " + partIndexToSend);

								byte[] data = sendingFileTransmitter.getPart(partIndexToSend);
								System.out.println("writing " + data.length + " bytes");
								socket.getOutputStream().write(data);
								socket.getOutputStream().flush();
								sendingIndicator.setPart(partIndexToSend);
								doneAnything = true;
							}
						}
						
					}
					
					if (!doneAnything) {
						try {
							// Waiting for the next command
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		
	});
	
	protected String readLine() throws IOException {
		int c;
		StringBuilder sb = new StringBuilder();
		while ((c = socket.getInputStream().read()) != -1 && c != '\n') {
			sb.append((char)c);
		}
		String res = sb.toString();
		if (res.charAt(res.length() - 1) == '\r') {
			res = res.substring(0, res.length() - 1);
		}
		return res;
	}
	
	protected int fillBufferCompletely(byte[] bytes) throws IOException {
	    int size = bytes.length;
	    int offset = 0;
	    while (offset < size) {
	        int read = socket.getInputStream().read(bytes, offset, size - offset);
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
	
	
	private Thread receivingThread = new Thread(new Runnable() {
		
		@Override
		public void run() {
			try {
				while (!socket.isClosed()) {
					
					// Reading the command
					String command = readLine();
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
						fillBufferCompletely(data);
						
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
	
	protected Connection(Socket socket) {
		this.socket = socket;
	}
	
	protected Socket getSocket() {
		return socket;
	}
		
	private Object idLock = new Object();
	
	private int currentId = 0;

	public void sendFile(File file) throws IOException {
		String fileName = file.getName().replace(' ', '_');
		FileTransmitter sendingFileTransmitter = new FileTransmitter(fileName, file);
		FileSendingIndicator sendingIndicator = new FileSendingIndicator(sendingFileTransmitter);

		synchronized (idLock) {
			sendingFileTransmitters.put(currentId, sendingFileTransmitter);
			sendingIndicators.put(currentId, sendingIndicator);
			currentId++;
		}
		System.out.println("added to queue");
	}
	
	protected void startConversation() {
		sendingThread.start();
		receivingThread.start();
	}
	
	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getLocalAddress() {
		return getSocket().getLocalAddress().toString();
	}
	
	public String getRemoteAddress() {
		return getSocket().getRemoteSocketAddress().toString();
	}
	
}
