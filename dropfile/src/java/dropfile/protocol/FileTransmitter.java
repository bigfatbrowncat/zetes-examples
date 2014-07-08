package dropfile.protocol;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileTransmitter implements AutoCloseable {
	private static final long PART_SIZE = 16384;	// 16K
	
	private String fileName;
	private long fileSize;
	private File targetFile;
	private boolean[] partsCompleted;
	
	private RandomAccessFile targetRAF;

	/**
	 * Constructor for making a sending transmitter
	 * @param fileName The name of the file
	 * @param targetFile The file itself. It should exist and be readable.
	 * @throws IOException
	 */
	public FileTransmitter(String fileName, File targetFile) throws IOException {
		this(fileName, 0, targetFile);
		if (!targetFile.exists()) {
			throw new RuntimeException("targetFile doesn't exist, but no fileSize specified");
		}
	}
	
	/**
	 * Universal constructor. If <code>targetFile</code> exists,
	 * <code>fileSize</code> is ignored and a sending transmitter is made. 
	 * If it doesn't exist, a receiving transmitter is made.
	 * @param fileName name of the file
	 * @param fileSize size of the file. It is ignored if <code>targetFile</code> exists.
	 * @param targetFile The file itself. If it does exist, it will be sent. Otherwise it will be created and received.
	 * @throws IOException
	 */
	public FileTransmitter(String fileName, long fileSize, File targetFile) throws IOException {
		this.fileName = fileName;
		this.targetFile = targetFile;

		boolean receiving = !targetFile.exists();
				
		targetRAF = new RandomAccessFile(targetFile, "rw");
		
		if (receiving) {
			targetRAF.setLength(fileSize);
		} else {
			fileSize = targetRAF.length();
		}
		this.fileSize = fileSize;
		
		partsCompleted = new boolean[getPartsCount()];
		
		if (!receiving) {
			for (int i = 0; i < getPartsCount(); i++) {
				partsCompleted[i] = true;
			}
		}
	}
	
	public int getPartsCount() {
		int partsCount = (int) (fileSize / PART_SIZE);
		if (fileSize % PART_SIZE > 0) partsCount ++;
		return partsCount;
	}
	
	public int getPartSize(int partIndex) {
		long currentSize = PART_SIZE;
		if (partIndex == fileSize / PART_SIZE) {
			currentSize = fileSize % PART_SIZE;
		}
		return (int)currentSize;
	}
	
	public byte[] getPart(int partIndex) throws IOException {
		if (partsCompleted[partIndex] == true) {
			targetRAF.seek(partIndex * PART_SIZE);
			byte[] data = new byte[getPartSize(partIndex)];
			targetRAF.read(data);
			return data;
		} else {
			throw new RuntimeException("Part isn't added");
		}
	}
	
	public void putPart(int partIndex, byte[] data) throws IOException {
		if (partsCompleted[partIndex] == false) {
			partsCompleted[partIndex] = true;
			targetRAF.seek(partIndex * PART_SIZE);
			targetRAF.write(data, 0, getPartSize(partIndex));
		} else {
			throw new RuntimeException("Part already added");
		}
	}

	public boolean isComplete() {
		for (boolean part : partsCompleted) {
			if (!part) return false;
		}
		return true;
	}
	
	@Override
	public void close() throws IOException {
		targetRAF.close();
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public long getFileSize() {
		return fileSize;
	}
	
	public File getTargetFile() {
		return targetFile;
	}
}
