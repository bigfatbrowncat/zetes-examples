package parrot.server.data.objects;

public class User {
	public final long id;
	public final String login;
	public final String password;
	public final String name;

	public User(long id, String login, String password, String name) {
		this.id = id;
		this.login = login;
		this.password = password;
		this.name = name;
	}
}
