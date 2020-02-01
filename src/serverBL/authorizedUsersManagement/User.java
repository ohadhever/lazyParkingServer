package serverBL.authorizedUsersManagement;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class User {
	private String userName;
	private byte[] password; // the result of the SHA-256 function applied to the original PW string
	private int permission;
	
	public User(String userName, String password, int permission) {
		this.userName = userName;
		setPassword(password);
		this.permission = permission;
	}
	
	public User(String userName, byte[] hashedPassword, int permission) {
		this.userName = userName;
		password = hashedPassword.clone();
		this.permission = permission;
	}
	
	public int getPermission() {
		return permission;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setPassword(String passwordStr) {
		password = stringToSHA256(passwordStr);
	}
	
	public byte[] getHashedPassword() {
		return password.clone();
	}
	
	public int checkPassword(String passwordStr) { // returns the permission of the user (0 if the password is wrong)
		byte[] hashedPassword = stringToSHA256(passwordStr);
		for (int i = 0; i < password.length; i++)
			if (password[i] != hashedPassword[i])
				return 0;
		return permission;
	}
	
	private static byte[] stringToSHA256(String str) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			return digest.digest(str.getBytes(StandardCharsets.UTF_8));
		} catch (NoSuchAlgorithmException e) {
			// DO NOTHING cause we know this string is a valid algorithm
		}
		return null; // should never get here
	}
	
	@Override
	public boolean equals(Object other) {
		if (!other.getClass().equals(getClass()))
			return false;
		User otherUser = (User)other;
		return (userName.equals(otherUser.userName) && Arrays.equals(password, otherUser.password) && permission == otherUser.permission);
	}
}
