package serverBL.authorizedUsersManagement;

import java.util.LinkedList;

public interface UsersStorage {
	public LinkedList<User> loadAllUsers() throws Exception;
	
	public void saveUser(User user) throws Exception;
	
	public void deleteUser(User user) throws Exception;
	
	public void modifyUser(User user) throws Exception;
}
