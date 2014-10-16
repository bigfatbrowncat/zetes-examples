package parrot.server.data.objects;

public class Message {
	public final long id;
	public final long userId;
	public final long timeMillis;
	public final String text;
	
	public Message(long id, long userId, long timeMillis, String text) {
		this.id = id;
		this.userId = userId;
		this.timeMillis = timeMillis;
		this.text = text;
	}
}
