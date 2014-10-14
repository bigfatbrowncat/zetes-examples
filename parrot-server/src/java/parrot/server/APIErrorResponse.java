package parrot.server;

public class APIErrorResponse {
	public static final int CODE_INCORRECT_REQUEST = 1;
	public static final int CODE_INVALID_CREDENTIALS = 2;
	public static final int CODE_LOGIN_OCCUPIED = 3;
	public static final int CODE_LOGIN_INVALID = 4;
	public static final int CODE_PASSWORD_CONFIRMATION_ERROR = 5;
	
	public final int code;
	public final String message;
	
	public APIErrorResponse(int code, String message) {
		super();
		this.code = code;
		this.message = message;
	}
	
	
}
