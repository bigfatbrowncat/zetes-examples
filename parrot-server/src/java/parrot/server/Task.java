package parrot.server;

import java.io.IOException;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.simpleframework.http.Cookie;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import parrot.server.SessionManager.Session;
import parrot.server.data.objects.Message;
import parrot.server.data.objects.User;
import parrot.server.templates.TemplateParser.ParsedTemplate;
import parrot.server.templates.TemplateParser.ParsedTemplate.Context;

import com.almworks.sqlite4java.SQLiteException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Task implements Runnable {
	private static enum ResponseFormat {
		JSON("application/json"), HTML("text/html");
		public final String mime; 
		ResponseFormat(String mime) {
			this.mime = mime;
		}
	};

	private static final String COOKIE_SESSION_ID = "sessionId";

	private static final String ADDR_API = "api"; 
	private static final String ADDR_GET_USERS = "users"; 
	private static final String ADDR_REGISTER = "register"; 
	private static final String ADDR_LOGIN = "login"; 
	private static final String ADDR_LOGOUT = "logout"; 
	private static final String ADDR_ADD_MESSAGE = "add_message";
	private static final String ADDR_GET_MESSAGES = "get_messages";

	private final Main main; 
	private final Response response;
	private final Request request;

	private final long time = System.currentTimeMillis();

	public Task(Main main, Request request, Response response) {
		this.main = main;
		this.response = response;
		this.request = request;
	}
	
	private void responseHeaders(ResponseFormat format, int code) {
		response.setValue("Content-Type", format.mime);
		response.setValue("Server", Main.SERVER_NAME);
		response.setDate("Date", time);
		response.setDate("Last-Modified", time);
		response.setCode(code);
	}
	
	private void sendJson(Object object) throws IOException {
		PrintStream body = response.getPrintStream();
		Gson gg = main.gsonBuilder.create();
		System.out.println(object);
		String responseJson = gg.toJson(object);
		System.out.println(responseJson);
		body.println(responseJson);
		body.close();
	}
	
	private void responseAPIGetUsers() throws IOException, SQLiteException {
		responseHeaders(ResponseFormat.JSON, 200);
		User[] users = main.dataConnector.getUsers();
		sendJson(users);
	}

	private void responseAPIGetUser(String login) throws IOException, SQLiteException {
		responseHeaders(ResponseFormat.JSON, 200);
		User user = main.dataConnector.getUser(login);
		sendJson(user);
	}
	
	private static final Pattern LOGIN_PATTERN = Pattern.compile("[a-zA-Z0-9]+");
	private boolean validateLogin(String login) {
		return LOGIN_PATTERN.matcher(login).matches() && login.length() <= 20;
	}
	
	private void responseAPIRegister() throws IOException, SQLiteException {
		String login = request.getParameter("login"); 
		String password1 = request.getParameter("password1"); 
		String password2 = request.getParameter("password2"); 
		String name = request.getParameter("name");

		if (login != null && password1 != null && password2 != null && name != null) {
			if (password1.equals(password2)) {
				// Trying to find the user
				User user = main.dataConnector.getUser(login);
				if (user == null) {
					if (validateLogin(login)) {
						responseHeaders(ResponseFormat.JSON, 201);
						user = main.dataConnector.addUser(login, password1, name);
						sendJson(user);
					} else {
						responseHeaders(ResponseFormat.JSON, 400);
						sendJson(new APIErrorResponse(APIErrorResponse.CODE_LOGIN_INVALID, "Login is invalid. It should start with a latin letter and contain only latin letters or digits"));
					}
				} else { 
					responseHeaders(ResponseFormat.JSON, 409);
					sendJson(new APIErrorResponse(APIErrorResponse.CODE_LOGIN_OCCUPIED, "Login occupied"));
				}
			} else { 
				responseHeaders(ResponseFormat.JSON, 400);
				sendJson(new APIErrorResponse(APIErrorResponse.CODE_PASSWORD_CONFIRMATION_ERROR, "Password and confirmation aren't equal"));
			}
		} else {
			responseHeaders(ResponseFormat.JSON, 400);
			sendJson(new APIErrorResponse(APIErrorResponse.CODE_INCORRECT_REQUEST, "Login, two passwords and username should be present"));
		}
		
	}
	
	private void responseAPILogin() throws IOException {
		String login = request.getParameter("login"); 
		String password = request.getParameter("password"); 
		if (login != null && password != null) {
			// Validating login and password
			User user = main.dataConnector.getUser(login);
			if (user != null && user.password.equals(password)) {
				// Login complete
				Session session = main.sessionManager.createSession(login);

				responseHeaders(ResponseFormat.JSON, 200);
				//response.setCookie(session.asCookie(COOKIE_SESSION_ID));
				sendJson(session);
			} else {
				responseHeaders(ResponseFormat.JSON, 401);
				sendJson(new APIErrorResponse(APIErrorResponse.CODE_INVALID_CREDENTIALS, "Invalid user name or password"));
			}
		} else {
			responseHeaders(ResponseFormat.JSON, 400);
			sendJson(new APIErrorResponse(APIErrorResponse.CODE_INCORRECT_REQUEST, "Login and password should be present"));
		}
	}
	
	private void responseAPILogout() throws IOException {
		Cookie sessionIdCookie = request.getCookie(COOKIE_SESSION_ID);
		if (sessionIdCookie != null) {
			Session session = main.sessionManager.fromCookie(sessionIdCookie);
			if (session != null) {
				main.sessionManager.eraseSession(session);
			}
		}
		responseHeaders(ResponseFormat.JSON, 200);
		sendJson(new Object());
	}
	
	private void responseAPIAddMessage() throws IOException {
		String text = request.getParameter("text");
		Session session;
		final User user;
		
		Cookie sessionIdCookie = request.getCookie(COOKIE_SESSION_ID);
		if (sessionIdCookie != null && (session = main.sessionManager.fromCookie(sessionIdCookie)) != null) {
			// Session is open
			session = main.sessionManager.renewSession(session);
			user = main.dataConnector.getUser(session.login);
			if (user != null) {
				long timeMillis = System.currentTimeMillis();
				Message message = main.dataConnector.addMessage(user.id, timeMillis, text);

				responseHeaders(ResponseFormat.JSON, 200);
				sendJson(message);
				return;
			}
		}

		responseHeaders(ResponseFormat.JSON, 403);
		sendJson(new APIErrorResponse(APIErrorResponse.CODE_LOGIN_REQUIRED, "You should login to write messages"));
	}

	private void responseAPIGetMessages() throws IOException {
		Cookie sessionIdCookie = request.getCookie(COOKIE_SESSION_ID);
		if (sessionIdCookie != null) {
			Session session = main.sessionManager.fromCookie(sessionIdCookie);
			if (session != null) {
				Message[] messages = main.dataConnector.getMessages();
				sendJson(messages);
				return;
			}
		}

		responseHeaders(ResponseFormat.JSON, 403);
		sendJson(new APIErrorResponse(APIErrorResponse.CODE_LOGIN_REQUIRED, "You should login to write messages"));
	}

	private void responseUIRoot() throws IOException {
		Session session;
		final User user;
		
		Cookie sessionIdCookie = request.getCookie(COOKIE_SESSION_ID);
		if (sessionIdCookie != null && (session = main.sessionManager.fromCookie(sessionIdCookie)) != null) {
			// Session is open
			session = main.sessionManager.renewSession(session);
			user = main.dataConnector.getUser(session.login);				
		} else {
			session = null;
			user = null;
		}
		
		responseHeaders(ResponseFormat.HTML, 200);
		PrintStream body = response.getPrintStream();
		
		ParsedTemplate.Context context = main.rootContext.clone();
		if (user != null) {
			context.setVariableValue("userName", user.name);
		} else {
			context.setVariableValue("userName", "");
		}
		context.setIfHandler(new ParsedTemplate.IfHandler() {
			
			@Override
			public boolean choose(Context context, String argument) {
				if (argument.equals("loggedIn")) {
					return (user != null);
				} else {
					throw new RuntimeException("Invalid if argument");
				}
			}
		});

		main.indexTemplate.process(context, body);
		body.close();
	}

	public void run() {

		try {
			
			if (request.getMethod().equals("GET")) {
				String[] requestPathParts = request.getPath().getSegments();
				
				// Not just "/"
				if (requestPathParts.length > 0) {

					// API ("/api")
					if (requestPathParts[0].equals(ADDR_API)) {
						// Not just "/api"
						if (requestPathParts.length > 1) {
							// "/api/users"
							if (requestPathParts[1].equals(ADDR_GET_USERS)) {
								// Not just "/api/users"
								if (requestPathParts.length > 2) {
									// "/api/users/<login>"
									String login = requestPathParts[2];
									// Not just "/api/users/<login>"
									if (requestPathParts.length > 3) {
										// Don't know what to do, just falling
									} else {
										// Handling "/api/users/<login>"
										responseAPIGetUser(login);
										return;
									}
								} else {
									// Handling "/api/users"
									responseAPIGetUsers();
									return;
								}

							} else if (requestPathParts[1].equals(ADDR_ADD_MESSAGE)) {
								// Handling "/api/add_message"
								responseAPIAddMessage();
								return;
							} else if (requestPathParts[1].equals(ADDR_GET_MESSAGES)) {
								responseAPIGetMessages();
								return;
							} else if (requestPathParts[1].equals(ADDR_REGISTER)) {
								// Handling "/api/register"
								responseAPIRegister();
								return;
							} else if (requestPathParts[1].equals(ADDR_LOGIN)) {
								// Handling "/api/login"
								responseAPILogin();
								return;
							} else if (requestPathParts[1].equals(ADDR_LOGOUT)) {
								// Handling "/api/logout"
								responseAPILogout();
								return;
							}
						}
					} /*else if (requestPathParts[0].equals(ADDR_LOGIN)) {
						responseUILogin();
						return;
					}*/

				} else {
					responseUIRoot();
					return;
				}
			}

			throw new InvalidRequestException(request); 
								
		} catch (Exception e) {
			try {
				PrintStream body = response.getPrintStream();
				e.printStackTrace(body);
				body.close();

				response.setValue("Content-Type", "text/plain");
				response.setValue("Server", main.SERVER_NAME);
				response.setDate("Date", time);
				response.setDate("Last-Modified", time);
				response.setCode(500);

				
			} catch (IOException ioException) {
				System.out.println("Connection is closed by peer");
				ioException.printStackTrace();
			}
			
		}
	}
}
