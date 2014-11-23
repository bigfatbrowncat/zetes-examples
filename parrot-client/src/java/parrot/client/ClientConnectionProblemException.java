package parrot.client;

public class ClientConnectionProblemException extends Exception {

	public ClientConnectionProblemException() {
		super();
	}

	public ClientConnectionProblemException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ClientConnectionProblemException(String arg0) {
		super(arg0);
	}

	public ClientConnectionProblemException(Throwable arg0) {
		super(arg0);
	}
	
}
