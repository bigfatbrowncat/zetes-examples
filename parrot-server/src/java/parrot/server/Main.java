package parrot.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.text.DateFormat;
import java.text.spi.DateFormatProvider;
import java.util.LinkedList;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.simpleframework.http.Cookie;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import parrot.server.SessionManager.Session;
import parrot.server.data.DataConnector;
import parrot.server.data.objects.User;

import com.almworks.sqlite4java.SQLiteException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Main implements Container {
	private static final String COOKIE_SESSION_ID = "sessionId";

	private static final String ADDR_API = "api"; 
	private static final String ADDR_GET_USERS = "users"; 
	private static final String ADDR_ADD_USER = "useradd"; 
	private static final String ADDR_LOGIN = "login"; 
	
	private static enum ResponseFormat {
		JSON("application/json"), HTML("text/html");
		public final String mime; 
		ResponseFormat(String mime) {
			this.mime = mime;
		}
	};

	public class Task implements Runnable {

		private final Response response;
		private final Request request;
		private final long time = System.currentTimeMillis();

		public Task(Request request, Response response) {
			this.response = response;
			this.request = request;
		}
		
		private void responseHeaders(ResponseFormat format, int code) {
			response.setValue("Content-Type", format.mime);
			response.setValue("Server", SERVER_NAME);
			response.setDate("Date", time);
			response.setDate("Last-Modified", time);
			response.setCode(code);
		}
		
		private void sendJson(Object object) throws IOException {
			PrintStream body = response.getPrintStream();
			Gson gg = gsonBuilder.create();
			String responseJson = gg.toJson(object);
			body.println(responseJson);
			body.close();
		}
		
		private void responseAPIGetUsers() throws IOException, SQLiteException {
			responseHeaders(ResponseFormat.JSON, 200);
			User[] users = dataConnector.getUsers();
			sendJson(users);
		}

		private void responseAPIGetUser(String login) throws IOException, SQLiteException {
			responseHeaders(ResponseFormat.JSON, 200);
			User user = dataConnector.getUser(login);
			sendJson(user);
		}
		
		private void responseAPIAddUser() throws IOException, SQLiteException {
			String login = request.getParameter("login"); 
			String password = request.getParameter("password"); 
			String name = request.getParameter("name"); 
			if (login != null && password != null && name != null) {
				responseHeaders(ResponseFormat.JSON, 201);
				User user = dataConnector.addUser(login, password, name);
				sendJson(user);
			} else {
				responseHeaders(ResponseFormat.JSON, 400);
				PrintStream body = response.getPrintStream();
				body.println("Error: Login, password and user name should be present");
				body.close();
			}
			
		}
		
		private void responseUIRoot() throws IOException {
			Cookie sessionIdCookie = request.getCookie(COOKIE_SESSION_ID);
			Session session;
			if (sessionIdCookie != null && (session = sessionManager.fromCookie(sessionIdCookie)) != null) {
				session.renew();
				User user = dataConnector.getUser(session.login);
				
				responseHeaders(ResponseFormat.HTML, 200);
				response.setCookie(session.asCookie(COOKIE_SESSION_ID));
				PrintStream body = response.getPrintStream();
				body.println(
					"<!DOCTYPE HTML>" +
					"<html>" +
						"<head>" +
						"</head>" +
						"<body>" +
							"<p>Root. Logged in as " + user.name + "</p>" +
						"</body>" +
					"</html>"
				);
				body.close();
				
			} else {
				responseHeaders(ResponseFormat.HTML, 200);
				PrintStream body = response.getPrintStream();
				body.println(
					"<!DOCTYPE HTML>" +
					"<html>" +
						"<head>" +
						"</head>" +
						"<body>" +
							"<p>Log in or register.</p>" +
						"</body>" +
					"</html>"
				);
				body.close();
			}
		}

		private void responseUILogin() throws IOException {
			PrintStream body = response.getPrintStream();
			String login = request.getParameter("login"); 
			String password = request.getParameter("password"); 
			
			if (login != null && password != null) {
				// Validating login and password
				User user = dataConnector.getUser(login);
				if (user != null && user.password.equals(password)) {
					// Login complete
					Session session = sessionManager.createSession(login);

					responseHeaders(ResponseFormat.HTML, 200);
					response.setValue("Refresh", "0; url=/");
					response.setCookie(session.asCookie(COOKIE_SESSION_ID));

				} else {
					// Login failed
					responseHeaders(ResponseFormat.HTML, 401);

					body.println(
							"<!DOCTYPE HTML>" +
							"<html>" +
								"<head>" +
								"</head>" +
								"<body>" +
									"<p>Incorrect user name or password.</p>" +
								"</body>" +
							"</html>"
					);
					
				}
				
			} else {
				// Invalid request
				responseHeaders(ResponseFormat.HTML, 400);
				body.println(
						"<!DOCTYPE HTML>" +
						"<html>" +
							"<head>" +
							"</head>" +
							"<body>" +
								"<p>Invald request. Missing login or password.</p>" +
							"</body>" +
						"</html>"
				);
			}

			
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

								} else if (requestPathParts[1].equals(ADDR_ADD_USER)) {
									// Handling "/api/useradd"
									responseAPIAddUser();
									return;
								}
							}
						} else if (requestPathParts[0].equals(ADDR_LOGIN)) {
							responseUILogin();
							return;
						}

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
					response.setValue("Server", SERVER_NAME);
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

	private static final String SERVER_NAME;
	
	static {
		SERVER_NAME = "Parrot/1.0 (Zetes " + zetes.hands.About.VERSION + ")";
	}
		
	private final Executor executor;
	private final Server server;
	private final Connection connection;
	private final SocketAddress address;
	private final GsonBuilder gsonBuilder = new GsonBuilder();
	private final SessionManager sessionManager = new SessionManager();

	private final DataConnector dataConnector;

	public Main(int size, int port, String databaseFile) throws IOException, SQLiteException {
		Locale.setDefault(Locale.US);
		
		executor = Executors.newFixedThreadPool(size);
		server = new ContainerServer(this);
		connection = new SocketConnection(server);
		address = new InetSocketAddress(port);
		dataConnector = new DataConnector(new File(databaseFile));
		System.out.println("Parrot server greets you!");
	}
	
	public void connect() throws IOException {
		connection.connect(address);
		System.out.println("Listening to requests...");
	}

	public void handle(Request request, Response response) {
		Task task = new Task(request, response);
		executor.execute(task);
	}

	public static void main(String[] list) throws IOException, SQLiteException {
		Main container = new Main(10, 8080, "parrot.db");
		container.connect();
		
		//dataConnector.close();
		
	}
}