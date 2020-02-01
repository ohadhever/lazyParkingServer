package serverBL.authorizedUsersManagement;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import adminUI.ExceptionsLogger;
import common.Reply;


public class UserList {
	private HashMap<String, User> users;
	private UsersStorage storage;
	private int nextCardKey;
	private boolean noStorage;
	
	public UserList(UsersStorage storage) {
		if (storage == null)
			noStorage = true;
		else
			noStorage = false;
		nextCardKey = 0;
		this.storage = storage;
		users = new HashMap<String, User>();
		if (!noStorage)
			try {
				LinkedList<User> list = storage.loadAllUsers();
				User tmp;
				int tmpKey;
				while (!list.isEmpty()) {
					tmp = list.remove();
					users.put(tmp.getUserName(), tmp);
					if (tmp instanceof Driver) {
						tmpKey = ((Driver)tmp).getCardKey();
						if (tmpKey > nextCardKey)
							nextCardKey = tmpKey;
					}
				}

			} catch (Exception e) {
				noStorage = true;
				ExceptionsLogger.logException(new Date() + ": storage exception during a call to loadAllUsers()");
			}
		nextCardKey++;
	}
	
	
	public synchronized String addUser(String firstName, String lastName, String password, int permission) {
		UsernameGenerator generator = new UsernameGenerator(firstName, lastName);
		
		String username = generator.getNextUsername();
		while (users.containsKey(username))
			username = generator.getNextUsername();
		
		User user = new User(username, password, permission);
		users.put(username, user);
		if (!noStorage)
			try {
				storage.saveUser(user);
			} catch (Exception e) {
				noStorage = true;
				ExceptionsLogger.logException(new Date() + ": storage exception during a call to saveUser()");		
			}
		return username;
	}
	
	
	public synchronized UsernameAndCardKeyPair addDriver(String firstName, String lastName, String password, int permission, Date expirationDate) {
		UsernameAndCardKeyPair pair = new UsernameAndCardKeyPair();
		if (expirationDate != null && expirationDate.before(new Date())) {
			pair.reply = Reply.DATE_EXPIRED;
			return pair;
		}
		
		UsernameGenerator generator = new UsernameGenerator(firstName, lastName);
		
		pair.username = generator.getNextUsername();
		while (users.containsKey(pair.username))
			pair.username = generator.getNextUsername();
		
		pair.cardKey = nextCardKey++;
		Driver driver = new Driver(pair.username, password, permission, pair.cardKey, expirationDate);
		users.put(pair.username, driver);
		if (!noStorage)
			try {
				storage.saveUser(driver);
			} catch (Exception e) {
				noStorage = true;
				ExceptionsLogger.logException(new Date() + ": storage exception during a call to saveUser()");
			}
		pair.reply = Reply.SUCCESS;
		return pair;
	}
	
	
	public synchronized Reply changePassword(String username, String password) {
		User user = users.get(username);
		if (user == null)
			return Reply.USERNAME_NOT_FOUND;
		
		user.setPassword(password);
		if (!noStorage)
			try {
				storage.modifyUser(user);
			} catch (Exception e) {
				noStorage = true;
				ExceptionsLogger.logException(new Date() + ": storage exception during a call to modifyUser()");		
	
			}
		return Reply.SUCCESS;
	}
	
	
	public synchronized Reply changeExpirationDate(String username, Date date) {
		User user = users.get(username);
		if (user == null || !(user instanceof Driver))
			return Reply.USERNAME_NOT_FOUND;
		if (date != null && date.before(new Date()))
			return Reply.DATE_EXPIRED;
		
		((Driver)user).setExpirationDate(date);
		if (!noStorage)
			try {
				storage.modifyUser(user);
			} catch (Exception e) {
				noStorage = true;
				ExceptionsLogger.logException(new Date() + ": storage exception during a call to modifyUser()");
			}
		return Reply.SUCCESS;
	}
	
	
	public UserRestrictedInterface getUser(String username) {
		User user = users.get(username);
		if (user == null)
			return null;
		
		if (user instanceof Driver)
			return new DriverRestrictedInterface((Driver)user);
		return new UserRestrictedInterface(user);
	}
	
	
	public synchronized Reply removeUser(String username) {
		User user = users.remove(username);
		if (user == null)
			return Reply.USERNAME_NOT_FOUND;
		
		if (!noStorage)
			try {
				storage.deleteUser(user);
			} catch (Exception e) {
				noStorage = true;
				ExceptionsLogger.logException(new Date() + ": storage exception during a call to deleteUser()");
			}
		return Reply.SUCCESS;
	}
	
	public synchronized Reply removeDriver(String username) {
		User user = users.get(username);
		if (user == null)
			return Reply.USERNAME_NOT_FOUND;
		if (!(user instanceof Driver))
			return Reply.INSUFFICIENT_PRIVILEDGE;
		users.remove(username);
		
		if (!noStorage)
			try {
				storage.deleteUser(user);
			} catch (Exception e) {
				noStorage = true;
				ExceptionsLogger.logException(new Date() + ": storage exception during a call to deleteUser()");
			}
		return Reply.SUCCESS;
	}
	
	public LinkedList<UserRestrictedInterface> getAllUsers() {
		LinkedList<UserRestrictedInterface> list = new LinkedList<UserRestrictedInterface>();
		Set<String> keys = users.keySet();
		for (String key : keys)
			list.add(getUser(key));
		return list;
	}
}
