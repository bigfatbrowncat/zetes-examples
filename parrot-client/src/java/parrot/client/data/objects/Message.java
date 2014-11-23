package parrot.client.data.objects;

public class Message {
	private long id;
	private long userId;
	private long timeMillis;
	private String text;
	
	public long getId() {
		return id;
	}
	public long getUserId() {
		return userId;
	}
	public long getTimeMillis() {
		return timeMillis;
	}
	public String getText() {
		return text;
	}
}
