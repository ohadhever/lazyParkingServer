package serverBL.authorizedUsersManagement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import junit.framework.TestCase;

public class RandomAccessUsersStorageUnitTest extends TestCase {
	public static final String DRIVERS_FILE = "drivers_test_file";
	public static final String USERS_FILE = "users_test_file";
	
	
	public void testRandomAccessUsersStorage() {
		// Creates empty test files, deleting already existing files with the same names
		File driversFile = new File(DRIVERS_FILE);
		if (driversFile.exists())
			driversFile.delete();
		try {
			driversFile.createNewFile();
		} catch (IOException e1) {
			fail("Could not create the drivers_test_file");
		}
		File usersFile = new File(USERS_FILE);
		if (usersFile.exists())
			usersFile.delete();
		try {
			usersFile.createNewFile();
		} catch (IOException e1) {
			fail("Could not create the users_test_file");
		}
		
		RandomAccessUsersStorage testObject = null;
		try {
			 testObject = new RandomAccessUsersStorage(DRIVERS_FILE, USERS_FILE);
		} catch (FileNotFoundException e) {
			fail("The drivers file and/or the users file could not be found");
		}
		
		// Reads from empty files
		LinkedList<User> readUsersList = null;
		try {
			readUsersList = testObject.loadAllUsers();
		} catch (Exception e) {
			fail("loadAllUsers threw an exception while trying to read empty files");
		}
		assertTrue("loadAllUsers returned a non-empty list when it should have returned an empty list", readUsersList.isEmpty());
		
		// Writes a bunch of users and checks if they are restored correctly
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy"); // Because Date class is autistic
		Date date1 = null, date2 = null, date3 = null;
		try {
			date1  = format.parse("18-6-2019");
			//date2  = format.parse("05-12-1945");
			date3  = format.parse("13-9-2005");
		} catch (ParseException e) {
			// This string has been checked and won't cause an exception
		}
		User originalUser1 = new User("Operator1", "123456789", 2);
		User originalUser2 = new User("Tima", "qwerty", 2);
		User originalUser3 = new User("Operator2", "Operator2", 2);
		User originalDriver1 = new Driver("Driver1", "55555", 1, 123, date1);
		User originalDriver2 = new Driver("Driver2", "Driver2", 1, 32, date2);
		User originalDriver3 = new Driver("Tima", "qwerty2", 1, 99, date3);
		User[] writtenUsers = {originalUser1, originalUser2, originalUser3, originalDriver1, originalDriver2, originalDriver3};
		try {
			testObject.saveUser(originalDriver1);
			testObject.saveUser(originalUser1);
			testObject.saveUser(originalDriver2);
			testObject.saveUser(originalUser2);
			testObject.saveUser(originalDriver3);
			testObject.saveUser(originalUser3);
		} catch (Exception e) {
			fail("saveUser threw an exception while writing users into initially empty files");
		}
		try {
			readUsersList = testObject.loadAllUsers();
		} catch (Exception e) {
			fail("loadAllUsers threw an exception while trying to read supposedly not empty files");
		}
		int i;
		for (i = 0; !readUsersList.isEmpty(); i++) {
			User readUser = readUsersList.removeFirst();
			assertTrue("Written and read objects type missmatch", writtenUsers[i].getClass().equals(readUser.getClass()));
			assertTrue("Written and read objects are not identical", writtenUsers[i].equals(readUser));
		}
		assertTrue("Number of users written does not match number of users read", writtenUsers.length == i);
		
		// Deletes users and checks that they are indeed removed and that this doesn't affect the rest of the users
		User[] expectedReadUsers = {originalUser1, originalUser3, originalDriver2};
		try {
			testObject.deleteUser(originalUser2);
			testObject.deleteUser(originalDriver3);
			testObject.deleteUser(originalDriver1);
		} catch (Exception e) {
			fail("deleteUser threw an exception while trying to delete users from supposedly not empty files");
		}
		try {
			readUsersList = testObject.loadAllUsers();
		} catch (Exception e) {
			fail("loadAllUsers threw an exception while trying to read from the files after the deletions");
		}
		for (i = 0; !readUsersList.isEmpty(); i++) {
			User readUser = readUsersList.removeFirst();
			assertTrue("expected and read objects type missmatch", expectedReadUsers[i].getClass().equals(readUser.getClass()));
			assertTrue("expected and read objects are not identical", expectedReadUsers[i].equals(readUser));
		}
		assertTrue("Expected number of users does not match number of users read", expectedReadUsers.length == i);
		
		// Writes the deleted users back and checks that they fill the 'free spots' in the files without affecting the rest of the files
		expectedReadUsers = writtenUsers;
		try {
			testObject.saveUser(originalDriver3);
			testObject.saveUser(originalUser2);
			testObject.saveUser(originalDriver1);
		} catch (Exception e) {
			fail("saveUser threw an exception while trying to write users to files with 'free spots'");
		}
		try {
			readUsersList = testObject.loadAllUsers();
		} catch (Exception e) {
			fail("loadAllUsers threw an exception while trying to read from the files after the reinsertion of the deleted users");
		}
		for (i = 0; !readUsersList.isEmpty(); i++) {
			User readUser = readUsersList.removeFirst();
			assertTrue("expected and read objects type missmatch", expectedReadUsers[i].getClass().equals(readUser.getClass()));
			assertTrue("expected and read objects are not identical", expectedReadUsers[i].equals(readUser));
		}
		assertTrue("Expected number of users does not match number of users read", expectedReadUsers.length == i);
		
		// Modifies users and checks that the modifications were applied correctly and didn't affect the rest of the users
		originalUser1.setPassword("blabla");
		((Driver)originalDriver2).setExpirationDate(date1);
		try {
			testObject.modifyUser(originalDriver2);
			testObject.modifyUser(originalUser1);
		} catch (Exception e) {
			fail("modifyUser threw an exception while trying to modify users in non-empty files");
		}
		try {
			readUsersList = testObject.loadAllUsers();
		} catch (Exception e) {
			fail("loadAllUsers threw an exception while trying to read from the files after users have been modified");
		}
		for (i = 0; !readUsersList.isEmpty(); i++) {
			User readUser = readUsersList.removeFirst();
			assertTrue("expected and read objects type missmatch", expectedReadUsers[i].getClass().equals(readUser.getClass()));
			assertTrue("expected and read objects are not identical", expectedReadUsers[i].equals(readUser));
		}
		assertTrue("Expected number of users does not match number of users read", expectedReadUsers.length == i);
	}
	
	
}
