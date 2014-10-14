package parrot.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import org.simpleframework.http.Cookie;

public class SessionManager {
	public class Session {
		private static final int sessionExpirationAgeMillis = 1000 * 60 * 60 * 24;	// One day

		public final UUID id;
		public final String login;
		public final long expirationTimeMillis;

		protected Session(UUID id, String login) {
			super();
			this.id = id;
			this.login = login;
			expirationTimeMillis = System.currentTimeMillis() + sessionExpirationAgeMillis;
		}

		protected Session(Session oldSession) {
			super();
			this.id = oldSession.id;
			this.login = oldSession.login;
			expirationTimeMillis = System.currentTimeMillis() + sessionExpirationAgeMillis;
		}
		
		public Cookie asCookie(String name) {
			Cookie res = new Cookie(name, id.toString());
			res.setExpiry((int)((expirationTimeMillis - System.currentTimeMillis()) / 1000));
			return res;
		}
	}

	
	private final Map<UUID, Session> openSessions = new HashMap<UUID, Session>();
	
	private void removeExpiredSessions() {
		long timeMillis = System.currentTimeMillis();
		HashSet<UUID> toRem = new HashSet<>();
		for (UUID u : openSessions.keySet()) {
			if (openSessions.get(u).expirationTimeMillis < timeMillis) {
				toRem.add(u);
			}
		}
		for (UUID u : toRem) {
			openSessions.remove(u);
		}
	}
	
	public synchronized Session createSession(String login) {
		Session newSession = new Session(UUID.randomUUID(), login);
		openSessions.put(newSession.id, newSession);
		return newSession;
	}
	
	public synchronized Session getSession(UUID id) {
		removeExpiredSessions();
		Session session = openSessions.get(id); 
		return session;
	}
	
	public synchronized Session renewSession(Session oldSession) {
		Session newSession = new Session(oldSession);
		openSessions.put(oldSession.id, newSession);
		return newSession;
	}
	
	public synchronized void eraseSession(Session session) {
		if (openSessions.containsKey(session.id)) {
			openSessions.remove(session.id);
		}
	}

	public synchronized Session fromCookie(Cookie cookie) {
		return getSession(UUID.fromString(cookie.getValue()));
	}
}
