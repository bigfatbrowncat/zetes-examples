package parrot.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import parrot.server.data.DataConnector;
import parrot.server.data.objects.Message;
import parrot.server.data.objects.User;
import zetes.feet.WinLinMacApi;

import com.almworks.sqlite4java.SQLiteException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class Main implements Container {

	public class Task implements Runnable {

		private final Response response;
		private final Request request;
		private final long time = System.currentTimeMillis();

		public Task(Request request, Response response) {
			this.response = response;
			this.request = request;
		}
		
		private void responseHeaders(int code) {
			response.setValue("Content-Type", "application/json");
			response.setValue("Server", SERVER_NAME);
			response.setDate("Date", time);
			response.setDate("Last-Modified", time);
			response.setCode(code);
		}
		
		private void responseGetUsers() throws IOException, SQLiteException {
			PrintStream body = response.getPrintStream();
			responseHeaders(200);
			
			User[] users = dataConnector.getUsers();
			Gson gg = gsonBuilder.create();
			String responseJson = gg.toJson(users);
			body.println(responseJson);
			body.close();
		}
		
		private void responseAddUser() throws IOException, SQLiteException {
			PrintStream body = response.getPrintStream();
			String login = request.getParameter("login"); 
			String password = request.getParameter("password"); 
			String name = request.getParameter("name"); 
			if (login != null && password != null && name != null) {
				responseHeaders(201);
				User user = dataConnector.addUser(login, password, name);
				Gson gg = gsonBuilder.create();
				String responseJson = gg.toJson(user);
				body.println(responseJson);
			} else {
				responseHeaders(400);
				body.println("Login, password and user name should be present");
			}
			
			body.close();
		}

		public void run() {

			try {
				
				if (request.getMethod().equals("GET")) {
					if (request.getPath().toString().equals("/get_users")) {
						responseGetUsers();
						return;
					} else if (request.getPath().toString().equals("/add_user")) {
						responseAddUser();
						return;
					}
				}

				throw new InvalidRequestException(request); 
									
			} catch (Exception e) {
				try {
					
					response.setValue("Content-Type", "text/plain");
					response.setValue("Server", SERVER_NAME);
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

	private static final String SERVER_NAME;
	
	static {
		SERVER_NAME = "Parrot/1.0 (Zetes " /*+ zetes.hands.About.VERSION*/ + ")";
	}
		
	private final Executor executor;
	private final Server server;
	private final Connection connection;
	private final SocketAddress address;
	private final GsonBuilder gsonBuilder = new GsonBuilder();


	private final DataConnector dataConnector;

	public Main(int size, int port, String databaseFile) throws IOException, SQLiteException {
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
		
		//dc.close();
		
	}
}