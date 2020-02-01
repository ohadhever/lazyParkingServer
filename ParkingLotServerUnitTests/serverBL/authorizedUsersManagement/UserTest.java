package serverBL.authorizedUsersManagement;


import junit.framework.TestCase;
public class UserTest extends TestCase {
	
	public void testcheckPassword() {
		 User testUser = new User("ohad","password",3);
	
		System.out.println("User In Test checkPassword");
		assertEquals(testUser.checkPassword("password"),3);
	}
	

	public void testgetPermission() {
		 User testUser = new User("ohad","password",3);
	
		System.out.println("User In Test getPermission");
		assertEquals(testUser.getPermission(),3);
	}
	
	
	public void testgetUserName() {
		 User testUser = new User("ohad","password",3);
	
		System.out.println("User In Test getUserName");
		assertEquals(testUser.getUserName(),"ohad");
	}
	
	
	public void testsetPassword() {
		 User testUser = new User("ohad","password",3);
		 User testUserone = new User("matan","diffPassword",2);
	
		System.out.println("User In Test setPassword");
		testUser.setPassword("diffPassword");
		
		 byte[] hashedPassword1;
		 byte[] hashedPassword2;
		 
		 hashedPassword1 = testUser.getHashedPassword();
		 hashedPassword2 = testUserone.getHashedPassword();
		 
		 for(int i=0;i<hashedPassword1.length;i++) {
			 if(hashedPassword1[i]!=hashedPassword2[i]) 
				 assertEquals("set password dosent change the password",hashedPassword1[i],hashedPassword2[i]);
			 }
	}
	
	
	public void testequals() {
		 User testUser = new User("ohad","password",3);
		 User testUserone = new User("ohad","password",3);
		 User testUsertwo = new User("matan","diffPassword",2);
		 User testUserthr = new User("ohad","diffPassword",3);
		 User testUserfor = new User("ohad","password",4);
		 User testUserfif = new User("matan","password",3);
		 
		System.out.println("User In Test equals");
		
		assertTrue("User In Test equals: should be equals",testUser.equals(testUserone));
		assertFalse("User In Test equals: should not be equals by all attributes",testUser.equals(testUsertwo));
		assertFalse("User In Test equals: should not be equals by password",testUser.equals(testUserthr));
		assertFalse("User In Test equals: should not be equals by permossion",testUser.equals(testUserfor));
		assertFalse("User In Test equals: should not be equals byname",testUser.equals(testUserfif));
	}
	
	
	public void testSetGetPassword() {
		 User testUser = new User("ohad","password",3);
		 User testUsesone = new User("matan","password",2);
		 byte[] hashedPassword1;
		 byte[] hashedPassword2;
		 
		 hashedPassword1 = testUser.getHashedPassword();
		 hashedPassword2 = testUsesone.getHashedPassword();
		 
		 System.out.println("User In Test getHashedPassword");
		 
		 for(int i=0;i<hashedPassword1.length;i++) {
			 if(hashedPassword1[i]!=hashedPassword2[i]) 
				 assertEquals("two same passwords are not HASH equal",hashedPassword1[i],hashedPassword2[i]);
			 }
	}
	
	
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(UserTest.class);
	}
	
	
}


