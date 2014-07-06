package dropfile.protocol;

public interface Connection {
	String getRemoteAddress();
	String getLocalAddress();
	void close();
}
