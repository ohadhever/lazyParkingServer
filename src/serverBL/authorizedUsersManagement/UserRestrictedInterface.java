package serverBL.authorizedUsersManagement;

public class UserRestrictedInterface {
	private User user;
	
	public UserRestrictedInterface(User user) throws IllegalArgumentException {
		if (user == null)
			throw new IllegalArgumentException("user must not be null");
		this.user = user;
	}
	
	public int getPermission() {
		return user.getPermission();
	}
	
	public String getUserName() {
		return user.getUserName();
	}
	
	public byte[] getHashedPassword() {
		return user.getHashedPassword();
	}
	
	public int checkPassword(String passwordStr) { // returns the permission of the user (0 if the password is wrong)
		return user.checkPassword(passwordStr);
	}
	
}
