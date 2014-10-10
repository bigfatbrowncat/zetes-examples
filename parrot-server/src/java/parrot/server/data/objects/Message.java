package parrot.server.data.objects;

public class Message {
	public final long id;
	public final String userId;
	public final String text;
	
	public Message(long id, String userId, String text) {
		this.id = id;
		this.userId = userId;
		this.text = text;
	}
}
