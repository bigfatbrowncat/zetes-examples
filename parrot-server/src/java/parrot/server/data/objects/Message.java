package parrot.server.data.objects;

public class Message {
	public final long id;
	public final long userId;
	public final long timeInMillis;
	public final String text;
	
	public Message(long id, long userId, long timeInMillis, String text) {
		this.id = id;
		this.userId = userId;
		this.timeInMillis = timeInMillis;
		this.text = text;
	}
}
