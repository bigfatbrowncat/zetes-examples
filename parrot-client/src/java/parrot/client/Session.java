package parrot.client;

import java.net.CookieHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import parrot.client.APIClient.Method;
import parrot.client.data.objects.Message;
import parrot.client.data.objects.MessagesSlice;

public class Session {
	private static final String API_LOGIN = "api/login";
	private static final String API_MESSAGES_SINCE = "api/messages_since";
	private static final String API_ADD_MESSAGE = "api/add_message";
	
	public static class LoginData {
		private UUID id;
		private String login;
		private long expirationTimeMillis;

		public long getExpirationTimeMillis() {
			return expirationTimeMillis;
		}
		public UUID getId() {
			return id;
		}
		public String getLogin() {
			return login;
		}
	}
	
	public final APIClient apiClient;
	public final LoginData data;
	
	private ArrayList<Message> messages = new ArrayList<>();
	
	private long serverTimeMillis = 0;
	
	public Message[] getMessages() {
		return messages.toArray(new Message[] {});
	}
	
	public Session(APIClient apiClient, String login, String password) throws ClientConnectionProblemException {
		this.apiClient = apiClient;

		HashMap<String, Object> params = new HashMap<>();
		params.put("login", login);
		params.put("password", password);
		data = apiClient.requestObject(API_LOGIN, params, LoginData.class, Method.GET, null);
	}
	
	public Message[] getLatestMessages() throws ClientConnectionProblemException {
		HashMap<String, Object> params = new HashMap<>();
		params.put("sinceTimeMillis", serverTimeMillis);
		MessagesSlice messagesSlice = apiClient.requestObject(API_MESSAGES_SINCE, params, MessagesSlice.class, Method.GET, null);
		
		for (Message m : messagesSlice.getMessages()) {
			messages.add(m);
		}
		serverTimeMillis = messagesSlice.getServerTimeMillis();
		return messagesSlice.getMessages();
	}
	
	public Message sendMessage(String messageText) throws ClientConnectionProblemException {
		Message newMessage = apiClient.requestObject(API_ADD_MESSAGE, null, Message.class, Method.POST, messageText);
		return newMessage;
	}
}
