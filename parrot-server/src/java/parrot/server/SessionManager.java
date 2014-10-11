package parrot.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import org.simpleframework.http.Cookie;

public class SessionManager {
	public class Session {
		private final UUID id;
		public final String login;
		private long expirationTimeMillis;

		protected Session(UUID id, String login) {
			super();
			this.id = id;
			this.login = login;
			renew();
		}
		
		public long getExpirationTimeMillis() {
			return expirationTimeMillis;
		}
		
		public Cookie asCookie(String name) {
			Cookie res = new Cookie(name, id.toString());
			res.setExpiry((int)((expirationTimeMillis - System.currentTimeMillis()) / 1000));
			return res;
		}
		
		public void renew() {
			expirationTimeMillis = System.currentTimeMillis() + sessionExpirationAgeMillis;
		}
	}

	private static final int sessionExpirationAgeMillis = 1000 * 60 * 60 * 24;	// One day

	private final Map<UUID, Session> openSessions = new HashMap<UUID, Session>();
	
	private void removeExpiredSessions() {
		long timeMillis = System.currentTimeMillis();
		HashSet<UUID> toRem = new HashSet<>();
		for (UUID u : openSessions.keySet()) {
			if (openSessions.get(u).getExpirationTimeMillis() < timeMillis) {
				toRem.add(u);
			}
		}
		for (UUID u : toRem) {
			openSessions.remove(u);
		}
	}
	
	public Session createSession(String login) {
		Session newSession = new Session(UUID.randomUUID(), login);
		openSessions.put(newSession.id, newSession);
		return newSession;
	}
	
	public Session getSession(UUID id) {
		removeExpiredSessions();
		Session session = openSessions.get(id); 
		return session;
	}

	public Session fromCookie(Cookie cookie) {
		return getSession(UUID.fromString(cookie.getValue()));
	}
}
