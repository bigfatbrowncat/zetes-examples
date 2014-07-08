package dropfile.protocol;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;

public class ClientConnection implements Connection {
	
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
	
	private static final int ATTEMPTS_MAX = 3;
	private static final int ATTEMPTS_PAUSE = 1000;
	private static final int PORT = 32123;
	
	private Socket socket;
	private String remoteAddress;

	private HashMap<Integer, FileTransmitter> sendingFileTransmitters = new HashMap<>();
	private HashMap<Integer, FileSendingIndicator> sendingIndicators = new HashMap<>();
	
	private int currentId = 0;
	
	private Object idLock = new Object();
	
	private Thread talkingThread = new Thread(new Runnable() {

		@Override
		public void run() {
			try {
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

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
	
	public ClientConnection(String remoteAddress) throws Exception {
		this.remoteAddress = remoteAddress;
		int attempts = 0;
		Exception lastException = null;
		while (attempts < ATTEMPTS_MAX) {
			try {
				// Connecting
				socket = new Socket();
				socket.connect(new InetSocketAddress(remoteAddress, PORT), ATTEMPTS_PAUSE);
				
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
				// Sending the request
				System.out.println("[client] sending client greeting");
				out.write("DROPFILE_CLIENT\n");
				out.flush();

				// Checking the response
				System.out.println("[client] receiving server greeting");
				String response = in.readLine();
				if (!response.equals("DROPFILE_SERVER")) {
					throw new IOException("Invalid server response. Maybe the DropFile port is used by another application.");
				}
				
				talkingThread.start();
				
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
	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String getLocalAddress() {
		return socket.getLocalAddress().toString();
	}
	
	@Override
	public String getRemoteAddress() {
		return remoteAddress;
	}
}
