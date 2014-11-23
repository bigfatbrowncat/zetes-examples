package parrot.client.data.objects;

public class MessagesSlice {
	private Message[] messages;
	private long serverTimeMillis;

	public Message[] getMessages() {
		return messages;
	}
	
	public long getServerTimeMillis() {
		return serverTimeMillis;
	}
}
