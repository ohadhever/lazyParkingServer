package serverBL.authorizedUsersManagement;


import junit.framework.TestCase;
public class UserRestrictedInterfaceTest extends TestCase {
	
	public void testgetPermission() {
		
		User user = new User("hevero", "password", 2);
		UserRestrictedInterface uri = new UserRestrictedInterface(user);
		System.out.println("UserRestrictedInterface In Test getPermission");
		assertEquals(uri.getPermission(),2);
	}
	
	public void testgetUserName() {
		User user = new User("hevero", "password", 2);
		UserRestrictedInterface uri = new UserRestrictedInterface(user);
		
		System.out.println("UserRestrictedInterface In Test getUserName");
		assertEquals(uri.getUserName(),"hevero");
	}
	
	public void testgetcheckPassword() {
		User user = new User("hevero", "password", 2);
		UserRestrictedInterface uri = new UserRestrictedInterface(user);
		
		System.out.println("UserRestrictedInterface In Test getcheckPassword");
		assertEquals(uri.checkPassword("password"),2);
	}
	
	
	
	
	
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(UserTest.class);
	}

}
