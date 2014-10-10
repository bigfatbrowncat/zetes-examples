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
import zetes.feet.WinLinMacApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class Main implements Container {

	private static final String SERVER_NAME;
	
	static {
		SERVER_NAME = "Parrot/1.0 (Zetes " /*+ zetes.hands.About.VERSION*/ + ")";
	}
	
	public static class Task implements Runnable {

		private final Response response;
		private final Request request;
		private final long time = System.currentTimeMillis();

		public Task(Request request, Response response) {
			this.response = response;
			this.request = request;
		}
		
		private void responseRest(int code) {
			response.setValue("Content-Type", "application/json");
			response.setValue("Server", SERVER_NAME);
			response.setDate("Date", time);
			response.setDate("Last-Modified", time);
			response.setCode(code);
		}
		
		private void responseGetMessages() throws IOException{
			responseRest(200);
			
			PrintStream body = response.getPrintStream();
			
			UUID user1Id = UUID.randomUUID();
			UUID user2Id = UUID.randomUUID();
			
			Message m1 = new Message(120, user1Id.toString(), "Hello from user1!");
			Message m2 = new Message(121, user2Id.toString(), "Hello from user2!");
			
			List<Message> msgs = new LinkedList<Message>();
			msgs.add(m1);
			msgs.add(m2);
			
			GsonBuilder gb = new GsonBuilder();
			Gson gg = gb.create();
			String msgsAsJson = gg.toJson(msgs);
			
			body.println(msgsAsJson);
			
			/*List<Message> parsed = new ArrayList<Message>();
			Type listType = new TypeToken<List<Message>>() {}.getType();
			parsed = gg.fromJson(msgsAsJson, listType);
			body.println("\n" + parsed.get(0).text);*/
			
			body.close();
		}

		public void run() {

			try {
				
				if (request.getMethod().equals("GET")) {
					if (request.getPath().toString().equals("/get_messages")) {
						responseGetMessages();
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

	private final Executor executor;

	public Main(int size) {
		this.executor = Executors.newFixedThreadPool(size);
	}

	public void handle(Request request, Response response) {
		Task task = new Task(request, response);

		executor.execute(task);
	}

	public static void main(String[] list) throws Exception {
		System.out.println("Parrot server greets you!");

		DataConnector dc = new DataConnector(new File("test.db"));
		dc.close();
		
		Container container = new Main(10);
		Server server = new ContainerServer(container);
		Connection connection = new SocketConnection(server);
		SocketAddress address = new InetSocketAddress(8080);

		connection.connect(address);
		System.out.println("Listening to requests...");
	}
}