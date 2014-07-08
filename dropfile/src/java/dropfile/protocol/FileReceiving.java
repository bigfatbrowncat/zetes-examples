package dropfile.protocol;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileReceiving {
	private static final long PART_SIZE = 16384;	// 16K
	
	private String fileName;
	private long fileSize;
	private File targetFile;
	private boolean[] partsCompleted;
	
	private RandomAccessFile targetRAF;
	
	public FileReceiving(String fileName, long fileSize, File targetFile) throws IOException {
		this.fileName = fileName;
		this.targetFile = targetFile;
				
		targetRAF =  new RandomAccessFile(targetFile, "rw");
		targetRAF.setLength(fileSize);
		
		int partsCount = (int) (fileSize / PART_SIZE);
		if (fileSize % PART_SIZE > 0) partsCount ++;
		
		partsCompleted = new boolean[partsCount];
	}
	
	public void putPart(int partIndex, byte[] data) throws IOException {
		if (partsCompleted[partIndex] == false) {
			partsCompleted[partIndex] = true;
			targetRAF.seek(partIndex * PART_SIZE);
			long currentSize = PART_SIZE;
			if (partIndex == fileSize / PART_SIZE) {
				currentSize = fileSize % PART_SIZE;
			}
			targetRAF.write(data, 0, (int)currentSize);
		} else {
			throw new RuntimeException("Part already added");
		}
	}
	
	public boolean isCompleted() {
		for (boolean part : partsCompleted) {
			if (!part) return false;
		}
		return true;
	}
	
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
