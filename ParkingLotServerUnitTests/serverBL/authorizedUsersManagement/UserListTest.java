package serverBL.authorizedUsersManagement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import adminUI.ExceptionsLogger;
import junit.framework.TestCase;
import common.Reply;
public class UserListTest extends TestCase {
	
	
	public void before() {
		File driversFile = new File("UserListTestDriversFile");
		if (driversFile.exists())
			driversFile.delete();
		try {
			driversFile.createNewFile();
		} catch (IOException e1) {
			fail("Could not create the drivers_test_file");
		}
		File usersFile = new File("UserListTestUsersFile");
		if (usersFile.exists())
			usersFile.delete();
		try {
			usersFile.createNewFile();
		} catch (IOException e1) {
			fail("Could not create the users_test_file");
		}
	}
	
	public void testaddUser() {
		before();
		UsersStorage storage;
		try {
			storage = new RandomAccessUsersStorage("UserListTestDriversFile", "UserListTestUsersFile");
		} catch (FileNotFoundException e) {
			ExceptionsLogger.logException("Could not open the files for RandomAccessUsersStorage");
			storage = null;
		}
		
		UserList userlist =new UserList(storage);
	
		System.out.println("UserList In Test addUser");
		assertEquals(userlist.addUser("ohad", "hever", "passwprd", 3),"hevero");
	
	}
	
	public void testSameUserName() {
		before();
		UsersStorage storage;
		try {
			storage = new RandomAccessUsersStorage("UserListTestDriversFile", "UserListTestUsersFile");
		} catch (FileNotFoundException e) {
			ExceptionsLogger.logException("Could not open the files for RandomAccessUsersStorage");
			storage = null;
		}
		
		UserList userlist =new UserList(storage);
	
		System.out.println("UserList In Test Same addUser");
		assertNotSame(userlist.addUser("ohad", "hever", "password", 2),userlist.addUser("ohad", "hever", "password", 2));

	}
	
	
	
	public void testaddDriver() {
		
		UsernameAndCardKeyPair uakp = new UsernameAndCardKeyPair();
		UsernameAndCardKeyPair uakptemp =new UsernameAndCardKeyPair();
		
		uakp.username = "hevero";
		uakp.cardKey = 1;
		uakp.reply = Reply.SUCCESS;
		
		before();
		UsersStorage storage;
		try {
			storage = new RandomAccessUsersStorage("UserListTestDriversFile", "UserListTestUsersFile");
		} catch (FileNotFoundException e) {
			ExceptionsLogger.logException("Could not open the files for RandomAccessUsersStorage");
			storage = null;
		}
		
		UserList userlist =new UserList(storage);
		
		uakptemp = userlist.addDriver("ohad", "hever", "password", 3, null);
		
		System.out.println("UserList In Test addDriver");
		assertEquals("the username fails",uakptemp.username,uakp.username);
		assertEquals("the cardkey fails",uakptemp.cardKey,uakp.cardKey);
		assertEquals("the reply fails",uakptemp.reply,uakp.reply);
	}
	
	
	public void testSameDriverName() {
		before();
		UsersStorage storage;
		try {
			storage = new RandomAccessUsersStorage("UserListTestDriversFile", "UserListTestUsersFile");
		} catch (FileNotFoundException e) {
			ExceptionsLogger.logException("Could not open the files for RandomAccessUsersStorage");
			storage = null;
		}
		
		UserList userlist =new UserList(storage);
	
		System.out.println("UserList In Test Same addDriver");
		assertNotSame(userlist.addDriver("ohad", "hever", "paddword", 1, null),userlist.addDriver("ohad", "hever", "paddword", 2, null));
	}
	
	public void testchangePassword() {
		UserRestrictedInterface uri;

		
		before();
		UsersStorage storage;
		try {
			storage = new RandomAccessUsersStorage("UserListTestDriversFile", "UserListTestUsersFile");
		} catch (FileNotFoundException e) {
			ExceptionsLogger.logException("Could not open the files for RandomAccessUsersStorage");
			storage = null;
		}
		
		UserList userlist =new UserList(storage);
		userlist.addDriver("ohad", "hever", "passwprd", 3, null);
		userlist.changePassword("hevero", "diffpassword");
		uri=userlist.getUser("hevero");
		
		System.out.println("UserList In Test changePassword");
		assertEquals(uri.checkPassword("diffpassword"),3);
	}
	
	public void testchangeExpirationDate() {
		UserRestrictedInterface uri;
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		Date date=new Date();
		Date datesec = new Date();
		try {
			date = format.parse("1-12-2019");
			datesec = format.parse("2-11-2020");
		} catch (ParseException e) {
			// This string has been checked and won't cause an exception
			e.printStackTrace();
		}
		
		
		before();
		UsersStorage storage;
		try {
			storage = new RandomAccessUsersStorage("UserListTestDriversFile", "UserListTestUsersFile");
		} catch (FileNotFoundException e) {
			ExceptionsLogger.logException("Could not open the files for RandomAccessUsersStorage");
			storage = null;
		}
		UserList userlist =new UserList(storage);
		userlist.addDriver("ohad", "hever", "password", 3, date);
		userlist.changeExpirationDate("hevero", datesec);
		
		
		uri = userlist.getUser("hevero");
		
		System.out.println("UserList In Test changeExpirationDate");
		assertEquals(((DriverRestrictedInterface) uri).getExpirationDate(),datesec);
		
	}
	
	public void testgetUser() {
		UserRestrictedInterface uri;
		User testuser = new User("matan","password",1);
		before();
		UsersStorage storage;
		try {
			storage = new RandomAccessUsersStorage("UserListTestDriversFile", "UserListTestUsersFile");
		} catch (FileNotFoundException e) {
			ExceptionsLogger.logException("Could not open the files for RandomAccessUsersStorage");
			storage = null;
		}
		
		UserList userlist =new UserList(storage);
		
		userlist.addUser("ohsd", "hever", "password", 3);
		uri=userlist.getUser("hevero");
		byte[] one = uri.getHashedPassword();
		
		System.out.println("UserList In Test getUser");
		assertEquals("UserList In Test getUser: username should be equals",uri.getUserName(),"hevero");
		assertEquals("UserList In Test getUser: permission should be equals",uri.getPermission(),3);
		assertEquals("UserList In Test getUser: Password should be equals",uri.checkPassword("password"),3);
		
		for (int i=0;i<one.length;i++) {
			assertTrue("UserList In Test getUser: HashedPassword should be equals",one[i]==testuser.getHashedPassword()[i]);
		}
	}
	
	public void testremoveUser() {
		before();
		UsersStorage storage;
		try {
			storage = new RandomAccessUsersStorage("UserListTestDriversFile", "UserListTestUsersFile");
		} catch (FileNotFoundException e) {
			ExceptionsLogger.logException("Could not open the files for RandomAccessUsersStorage");
			storage = null;
		}
		
		UserList userlist =new UserList(storage);
		
		System.out.println("UserList In Test removeUser");
		assertEquals("UserList In Test removeUser: AddUser dident work", userlist.addUser("ohad", "hever", "passwprd", 3),"hevero");
		userlist.removeUser("hevero");
		assertNull("UserList In Test removeUser: The Uset still exists",userlist.getUser("hevero"));
	}
	
	public void testremoveDriver() {
		before();
		UsersStorage storage;
		try {
			storage = new RandomAccessUsersStorage("UserListTestDriversFile", "UserListTestUsersFile");
		} catch (FileNotFoundException e) {
			ExceptionsLogger.logException("Could not open the files for RandomAccessUsersStorage");
			storage = null;
		}
		
		UserList userlist =new UserList(storage);
		userlist.removeDriver("hevero");
		
		System.out.println("UserList In Test removeDriver");
		assertNull(userlist.getUser("hevero"));
	}
	
	
	public void testgetAllUsers() {
		UserRestrictedInterface temp;
		LinkedList<UserRestrictedInterface> list = new LinkedList<UserRestrictedInterface>();
		
		before();
		UsersStorage storage;
		try {
			storage = new RandomAccessUsersStorage("UserListTestDriversFile", "UserListTestUsersFile");
		} catch (FileNotFoundException e) {
			ExceptionsLogger.logException("Could not open the files for RandomAccessUsersStorage");
			storage = null;
		}
		
		UserList userlist =new UserList(storage);
		
		
		userlist.addDriver("ohad", "hever", "password", 1, null);
		userlist.addDriver("matan", "oren", "passworddiff1", 2, null);
		userlist.addDriver("time", "kukushkin", "passworddiff2", 3, null);
		userlist.addUser("gilad", "rozin", "passworddiff3", 3);
	
		list=userlist.getAllUsers();
		
		System.out.println("UserList In Test getAllUsers");
		for (int i=0;i<list.size();) {
			temp = list.get(i);
			
			if (temp.getUserName().matches("hevero") || 
					temp.getUserName().matches("orenm") || 
					temp.getUserName().matches("kukushkint")|| 
					temp.getUserName().matches("rozing")) {
				list.remove(temp);
			}
			else
				assertFalse("UserList In Test getAllUsers: there is no user such "+temp.getUserName(),true);
		}
		assertTrue(list.isEmpty());
		}
		
		
	
	
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(DriverTest.class);
	}
}
	

	