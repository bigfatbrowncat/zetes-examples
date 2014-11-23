package parrot.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.simpleframework.http.Cookie;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import parrot.server.SessionManager.Session;
import parrot.server.data.objects.Message;
import parrot.server.data.objects.MessagesSlice;
import parrot.server.data.objects.User;
import parrot.server.templates.TemplateParser.ParsedTemplate;
import parrot.server.templates.TemplateParser.ParsedTemplate.Context;
import ar.com.hjg.pngj.IImageLine;
import ar.com.hjg.pngj.ImageLineHelper;
import ar.com.hjg.pngj.ImageLineInt;
import ar.com.hjg.pngj.PngReader;
import ar.com.hjg.pngj.PngWriter;
import ar.com.hjg.pngj.chunks.ChunkCopyBehaviour;
import ar.com.hjg.pngj.chunks.PngChunkTextVar;

import com.almworks.sqlite4java.SQLiteException;
import com.google.gson.Gson;

public class Task implements Runnable {
	private static enum ResponseFormat {
		JSON("application/json; charset=UTF-8"), 
		TEXT("text/plain; charset=UTF-8"), 
		HTML("text/html; charset=UTF-8"), 
		PNG("image/png"), 
		CSS("text/css; charset=UTF-8");
		
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
	private static final String ADDR_GET_MESSAGES_SINCE = "messages_since";

	private static final String ADDR_CSS = "css"; 
	private static final String ADDR_IMAGES = "images"; 
	private static final String ADDR_MASKED_IMAGE = "masked"; 

	private final Main main; 
	private final Response response;
	private final Request request;

	private final long time = System.currentTimeMillis();

	public Task(Main main, Request request, Response response) {
		this.main = main;
		this.response = response;
		this.request = request;
	}
	
	private void responseHeaders(ResponseFormat format, boolean noCache, int code) {
		response.setValue("Content-Type", format.mime);
		response.setValue("Server", Main.SERVER_NAME);
		response.setDate("Date", time);
		response.setDate("Last-Modified", time);
		if (noCache) {
			response.setValue("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
			response.setValue("Pragma", "no-cache"); // HTTP 1.0.
			response.setDate("Expires", 0); // Proxies.
		}
		
		response.setCode(code);
	}
	
	private void sendJson(Object object) throws IOException {
		PrintStream body = response.getPrintStream();
		Gson gg = main.gsonBuilder.create();
		String responseJson = gg.toJson(object);
		body.println(responseJson);
		body.close();
	}
	
	private void responseAPIGetUsers() throws IOException, SQLiteException {
		responseHeaders(ResponseFormat.JSON, true, 200);
		
		String ids = request.getParameter("ids");
		String[] idsStr = ids.split(",");
		long[] idsArr = new long[idsStr.length];
		for (int i = 0; i < idsArr.length; i++) {
			idsArr[i] = Long.parseLong(idsStr[i]);
		}
		
		User[] users = main.dataConnector.getUsers(idsArr);
		HashMap<Long, User> usersMap = new HashMap<>();
		for (int i = 0; i < users.length; i++) {
			usersMap.put(users[i].id, users[i]);
		}
		
		sendJson(usersMap);
	}

	private void responseAPIGetUser(String login) throws IOException, SQLiteException {
		responseHeaders(ResponseFormat.JSON, true, 200);
		User user = main.dataConnector.getUser(login);
		sendJson(user);
	}
	
	private void responseStaticFile(String filename, ResponseFormat format) throws IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = getClass().getClassLoader().getResourceAsStream("parrot/server/stat/" + filename);
			responseHeaders(format, false, 200);
			os = response.getOutputStream();
			IOUtils.copy(is, os);

		} catch (FileNotFoundException e) {
			responseHeaders(format, false, 404);
			e.printStackTrace(response.getPrintStream());
			response.getPrintStream().close();
		} catch (Exception e) {
			responseHeaders(format, false, 500);
			e.printStackTrace(response.getPrintStream());
			response.getPrintStream().close();
		} finally {
			if (is != null) is.close();
			if (os != null) os.close();
		}
	}

	private void responseMasked(String maskName) throws IOException {
		/*int r, g, b;
		int avg = 0;
		Random rnd = new Random();
		do {
			r = rnd.nextInt(256);
			g = rnd.nextInt(256);
			b = (int) Math.sqrt(65535 - r*r - g*g);
			avg = (r + g + b) / 3;
			if (Math.abs(r - avg) > 40 && Math.abs(g - avg) > 40 && Math.abs(b - avg) > 40) break;
		} while (true);*/

		int r, g, b;
		try {
			r = Integer.parseInt(request.getParameter("r")); 
			g = Integer.parseInt(request.getParameter("g")); 
			b = Integer.parseInt(request.getParameter("b"));
		} catch (NumberFormatException e) {
			responseHeaders(ResponseFormat.TEXT, false, 400);
			e.printStackTrace(response.getPrintStream());
			response.getPrintStream().close();
			return;
		}
		
		InputStream mask = null;
		OutputStream os = null;
		try {
			responseHeaders(ResponseFormat.PNG, false, 200);

			mask = getClass().getClassLoader().getResourceAsStream("parrot/server/" + maskName);
			os = response.getOutputStream();
			
			PngReader pngr = new PngReader(mask);
			
			int channels = pngr.imgInfo.channels;
            if (channels < 3 || pngr.imgInfo.bitDepth != 8)
                    throw new RuntimeException("This method is for RGB8/RGBA8 images");
            
            PngWriter pngw = new PngWriter(os, pngr.imgInfo);
            
            pngw.copyChunksFrom(pngr.getChunksList(), ChunkCopyBehaviour.COPY_ALL_SAFE);
            pngw.getMetadata().setText(PngChunkTextVar.KEY_Description, "Colored");
            for (int row = 0; row < pngr.imgInfo.rows; row++) { // also: while(pngr.hasMoreRows()) 
                    IImageLine l1 = pngr.readRow();
                    int[] scanline = ((ImageLineInt) l1).getScanline(); // to save typing
                    for (int j = 0; j < pngr.imgInfo.cols; j++) {
                            double maskR = (double)scanline[j * channels] / 255;
                            double maskG = (double)scanline[j * channels + 1] / 255;
                            double maskB = (double)scanline[j * channels + 2] / 255;
                            
                            double newR = maskR * 1.5 * r + (255 - r) * (maskG + maskB) / 2;
                            double newG = maskR * 1.5 * g + (255 - g) * (maskG + maskB) / 2;
                            double newB = maskR * 1.5 * b + (255 - b) * (maskG + maskB) / 2;
                            
                            scanline[j * channels] = ImageLineHelper.clampTo_0_255((int)newR);
                            scanline[j * channels + 1] = ImageLineHelper.clampTo_0_255((int)newG);
                            scanline[j * channels + 2] = ImageLineHelper.clampTo_0_255((int)newB);
                            
                            //scanline[j * channels + 1] = ImageLineHelper.clampTo_0_255(scanline[j * channels + 1] + 20);
                    }
                    pngw.writeRow(l1);
            }
            pngr.end(); // it's recommended to end the reader first, in case there are trailing chunks to read
            pngw.end();
            
            pngr.close();
            pngw.close();

		} catch (FileNotFoundException e) {
			responseHeaders(ResponseFormat.TEXT, false, 404);
			e.printStackTrace(response.getPrintStream());
			response.getPrintStream().close();
		} catch (Exception e) {
			responseHeaders(ResponseFormat.TEXT, false, 500);
			e.printStackTrace(response.getPrintStream());
			response.getPrintStream().close();
		} finally {
			if (mask != null) mask.close();
			if (os != null) os.close();
		}
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
						responseHeaders(ResponseFormat.JSON, true, 201);
						user = main.dataConnector.addUser(login, password1, name);
						sendJson(user);
					} else {
						responseHeaders(ResponseFormat.JSON, true, 400);
						sendJson(new APIErrorResponse(APIErrorResponse.CODE_LOGIN_INVALID, "Login is invalid. It should start with a latin letter and contain only latin letters or digits"));
					}
				} else { 
					responseHeaders(ResponseFormat.JSON, true, 409);
					sendJson(new APIErrorResponse(APIErrorResponse.CODE_LOGIN_OCCUPIED, "Login occupied"));
				}
			} else { 
				responseHeaders(ResponseFormat.JSON, true, 400);
				sendJson(new APIErrorResponse(APIErrorResponse.CODE_PASSWORD_CONFIRMATION_ERROR, "Password and confirmation aren't equal"));
			}
		} else {
			responseHeaders(ResponseFormat.JSON, true, 400);
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

				responseHeaders(ResponseFormat.JSON, true, 200);
				response.setCookie(session.asCookie(COOKIE_SESSION_ID));
				sendJson(session);
			} else {
				responseHeaders(ResponseFormat.JSON, true, 401);
				sendJson(new APIErrorResponse(APIErrorResponse.CODE_INVALID_CREDENTIALS, "Invalid user name or password"));
			}
		} else {
			responseHeaders(ResponseFormat.JSON, true, 400);
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
		responseHeaders(ResponseFormat.JSON, true, 200);
		sendJson(new Object());
	}
	
	private void responseAPIAddMessage() throws IOException {
		
		Session session;
		final User user;
		
		Cookie sessionIdCookie = request.getCookie(COOKIE_SESSION_ID);
		if (sessionIdCookie != null && (session = main.sessionManager.fromCookie(sessionIdCookie)) != null) {
			// Session is open
			session = main.sessionManager.renewSession(session);
			user = main.dataConnector.getUser(session.login);
			if (user != null) {
				long timeMillis = System.currentTimeMillis();
				//String encodedBase64Message = request.getContent();
				String decodedMessage = request.getContent(); //new String(org.apache.commons.codec.binary.Base64.decodeBase64(encodedBase64Message));
				
				Message message = main.dataConnector.addMessage(user.id, timeMillis, decodedMessage);

				responseHeaders(ResponseFormat.JSON, true, 200);
				sendJson(message);
				return;
			}
		}

		responseHeaders(ResponseFormat.JSON, true, 403);
		sendJson(new APIErrorResponse(APIErrorResponse.CODE_LOGIN_REQUIRED, "You should login to write messages"));
	}

	private void responseAPIGetMessagesSince() throws IOException {
		try {
			long sinceTimeMillis = Long.parseLong(request.getParameter("sinceTimeMillis"));
		
//			Cookie sessionIdCookie = request.getCookie(COOKIE_SESSION_ID);
//			if (sessionIdCookie != null) {
//				Session session = main.sessionManager.fromCookie(sessionIdCookie);
//				if (session != null) {
					responseHeaders(ResponseFormat.JSON, true, 200);
					Message[] messages = main.dataConnector.getMessagesOrderedSince(sinceTimeMillis);
					long serverTimeMillis = System.currentTimeMillis();
					sendJson(new MessagesSlice(messages, serverTimeMillis));
					return;
//				}
//			}
	
			//responseHeaders(ResponseFormat.JSON, 403);
			//sendJson(new APIErrorResponse(APIErrorResponse.CODE_LOGIN_REQUIRED, "You should login to write messages"));
		} catch (NumberFormatException e) {
			responseHeaders(ResponseFormat.JSON, true, 400);
			sendJson(new APIErrorResponse(APIErrorResponse.CODE_INCORRECT_REQUEST, "sinceTimeMillis should be a valid number"));
		}
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
		
		responseHeaders(ResponseFormat.HTML, true, 200);
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
		/*System.out.println("Request: " + request.getHeader() + "; cookies: ");
		for (Cookie c : request.getCookies()) {
			System.out.println(c.getName() + " = " + c.getValue());
		}*/
		
		try {
			
			String[] requestPathParts = request.getPath().getSegments();
			if (request.getMethod().equals("GET")) {
				
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

							} else if (requestPathParts[1].equals(ADDR_GET_MESSAGES_SINCE)) {
								responseAPIGetMessagesSince();
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
					} else if (requestPathParts[0].equals(ADDR_MASKED_IMAGE)) {
						// Masked images
						String filename = requestPathParts[1];
						responseMasked("masks/" + filename);
						return;
					} else if (requestPathParts[0].equals(ADDR_IMAGES)) {
						// Static images
						String filename = requestPathParts[1];
						responseStaticFile("images/" + filename, ResponseFormat.PNG);
						return;
					} else if (requestPathParts[0].equals(ADDR_CSS)) {
						// Static CSS
						String filename = requestPathParts[1];
						responseStaticFile("css/" + filename, ResponseFormat.CSS);
						return;
					}

				} else {
					responseUIRoot();
					return;
				}
			} else if (request.getMethod().equals("POST")) {
				
				// Not just "/"
				if (requestPathParts.length > 0) {

					// API ("/api")
					if (requestPathParts[0].equals(ADDR_API)) {
						// Not just "/api"
						if (requestPathParts.length > 1) {
							// "/api/users"

							if (requestPathParts[1].equals(ADDR_ADD_MESSAGE)) {
								// Handling "/api/add_message"
								responseAPIAddMessage();
								return;
							}
						}
					}
				}
			}

			throw new InvalidRequestException(request); 
								
		} catch (Exception e) {
			try {
				response.setValue("Content-Type", "text/plain");
				response.setValue("Server", main.SERVER_NAME);
				response.setDate("Date", time);
				response.setDate("Last-Modified", time);
				response.setCode(500);

				PrintStream body = response.getPrintStream();
				e.printStackTrace(body);
				body.close();
				
			} catch (IOException ioException) {
				System.out.println("Connection is closed by peer");
				ioException.printStackTrace();
			}
			
		}
	}
}
