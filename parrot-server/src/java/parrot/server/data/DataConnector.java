package parrot.server.data;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import parrot.server.data.objects.User;
import zetes.feet.WinLinMacApi;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteJob;
import com.almworks.sqlite4java.SQLiteQueue;
import com.almworks.sqlite4java.SQLiteStatement;

public class DataConnector implements Closeable {
	private static final String TABLE_USERS = "users";

	private static final String FIELD_ID = "_id";
	private static final String FIELD_NAME = "name";
	private static final String FIELD_LOGIN = "login";
	private static final String FIELD_USER_ID = "userId";
	private static final String FIELD_TIME_MILLIS = "timeMillis";
	private static final String FIELD_TEXT = "text";
	
	private static final String FIELD_PASSWORD = "password";

	private SQLiteConnection connection;
	private SQLiteQueue queue;

	static {
		System.setProperty("sqlite4java.library.path", WinLinMacApi.locateExecutable());
	}

	public DataConnector(File databaseFile) throws SQLiteException {
		connection = new SQLiteConnection(databaseFile);
		connection.open(true);

		connection.prepare(
				"CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " " + 
						"("	+ 
							FIELD_ID + " INTEGER PRIMARY KEY ASC, " + 
							FIELD_LOGIN	+ " TEXT UNIQUE, " + 
							FIELD_PASSWORD + " TEXT, " + 
							FIELD_NAME + " TEXT" + 
						");"
		).step();
		connection.prepare(
				"CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " " + 
						"("	+ 
							FIELD_ID + " INTEGER PRIMARY KEY ASC, " + 
							FIELD_USER_ID	+ " INTEGER, " + 
							FIELD_TIME_MILLIS + " INTEGER, " + 
							FIELD_TEXT + " TEXT" + 
						");"
		).step();
		
		queue = new SQLiteQueue(databaseFile);
		queue.start();
	}

	public User getUser(final String login) {
		return queue.execute(new SQLiteJob<User>() {
			protected User job(SQLiteConnection connection)	throws SQLiteException {
				SQLiteStatement st = connection.prepare(
						"SELECT " + 
							FIELD_ID + ", " +
							FIELD_NAME + ", " +
							FIELD_PASSWORD +
						" FROM " + TABLE_USERS + " WHERE " + FIELD_LOGIN + "=?;"
				);
				
				st.bind(1, login);
				
				if (st.step()) {
					return new User(st.columnLong(0), login, st.columnString(2), st.columnString(1));
				} else {
					return null;
				}
			}
		}).complete();
	}
	
	public User addUser(final String login, final String password, final String name) {
		return queue.execute(new SQLiteJob<User>() {
			protected User job(SQLiteConnection connection)	throws SQLiteException {
				SQLiteStatement st = connection.prepare(
						"INSERT INTO " + TABLE_USERS + " " + 
								"(" + 
									FIELD_LOGIN	+ ", " + 
									FIELD_PASSWORD + ", " + 
									FIELD_NAME +
								") VALUES (?, ?, ?);"
				);
				
				st.bind(1, login);
				st.bind(2, password);
				st.bind(3, name);
				
				st.step();

				long newId = connection.getLastInsertId();
				return new User(newId, login, password, name);
			}
		}).complete();
	}

	public User[] getUsers() throws SQLiteException {
		return queue.execute(new SQLiteJob<User[]>() {
			protected User[] job(SQLiteConnection connection)
					throws SQLiteException {

				SQLiteStatement st = connection.prepare(
					"SELECT " + 
						FIELD_ID + ", " + 
						FIELD_LOGIN + ", " + 
						FIELD_PASSWORD + ", " + 
						FIELD_NAME + 
					" FROM " + TABLE_USERS + ";"
				);

				LinkedList<User> resList = new LinkedList<>();

				while (st.step()) {
					resList.add(new User(
							st.columnLong(0), 
							st.columnString(1),
							st.columnString(2), 
							st.columnString(3)
						)
					);
				}
				User[] res = resList.toArray(new User[] {});
				return res;
			}
		}).complete();
	}

	@Override
	public void close() throws IOException {
		try {
			queue.stop(true).join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		connection.dispose();
	}

}
