package parrot.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.Provider;
import java.security.Security;
import java.util.Locale;
import java.util.Set;
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

import parrot.Parrot;
import parrot.server.SessionManager.Session;
import parrot.server.data.DataConnector;
import parrot.server.data.objects.User;
import parrot.server.templates.TemplateParser;
import parrot.server.templates.TemplateParser.ParsedTemplate;
import parrot.server.templates.TemplateParser.ParsedTemplate.Context;

import com.almworks.sqlite4java.SQLiteException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Main implements Container {
	
	protected static final String SERVER_NAME;

	protected ParsedTemplate indexTemplate = TemplateParser.parse(getClass().getClassLoader().getResourceAsStream("parrot/server/templates/index.html.template"));
	protected ParsedTemplate.Context rootContext; 
	
	private final Executor executor;
	private final Server server;
	private final Connection connection;
	private final SocketAddress address;

	protected final GsonBuilder gsonBuilder = new GsonBuilder();
	protected final SessionManager sessionManager = new SessionManager();
	protected final DataConnector dataConnector;

	static {
		SERVER_NAME = "Parrot/" + Parrot.VERSION + " (Zetes " + zetes.hands.About.VERSION + ")";
	}
		
	public Main(int size, int port, String databaseFile) throws IOException, SQLiteException {
		
		Locale.setDefault(Locale.US);
		
		rootContext = new Context();
		rootContext.setVariableValue("zetesVersion", zetes.hands.About.VERSION);
		rootContext.setVariableValue("parrotVersion", Parrot.VERSION);
		
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
		Task task = new Task(this, request, response);
		executor.execute(task);
	}

	public static void main(String[] list) throws IOException, SQLiteException {
		Main container = new Main(10, 8080, "parrot.db");
		container.connect();
		
		//dataConnector.close();
		
	}
}