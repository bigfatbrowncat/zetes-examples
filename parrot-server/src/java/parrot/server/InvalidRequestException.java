package parrot.server;

import org.simpleframework.http.Request;

public class InvalidRequestException extends Exception {

	public InvalidRequestException(Request request) {
		super("Unknown request: method=" + request.getMethod() + 
			    ", path=" + request.getPath().toString());
	}

}
