package parrot.server.data.objects;

public class MessagesSlice {
	public final Message[] messages;
	public final long serverTimeMillis;

	public MessagesSlice(Message[] messages, long serverTimeMillis) {
		super();
		this.messages = messages;
		this.serverTimeMillis = serverTimeMillis;
	}
}
