package serverBL.authorizedUsersManagement;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;
public class DriverRestrictedInterfaceTest extends TestCase {
	
	public void testgetPermission() {
		
		Driver driver = new Driver("hevero", "password", 3, 12345, null);
		DriverRestrictedInterface dri = new DriverRestrictedInterface(driver);
		
		System.out.println("DriverRestrictedInterface In Test getCardKey");
		assertEquals(dri.getCardKey(),12345);
	}
	
	public void testisPresent() {
		Driver driver = new Driver("hevero", "password", 3, 12345, null);
		DriverRestrictedInterface dri = new DriverRestrictedInterface(driver);
		
		System.out.println("DriverRestrictedInterface In Test isPresent");
		assertEquals(dri.isPresent(),false);
	}
	
	public void testsetPresent() {
		Driver driver = new Driver("hevero", "password", 3, 12345, null);
		DriverRestrictedInterface dri = new DriverRestrictedInterface(driver);
		assertFalse(dri.isPresent());
		
		System.out.println("UserRestrictedInterface In Test setPresent");
		dri.setPresent(true);
		assertTrue(dri.isPresent());
	}
	
	public void testgetExpirationDate() {
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		Date date=new Date();
		try {
			date = format.parse("1-12-2019");
		} catch (ParseException e) {
			// This string has been checked and won't cause an exception
			e.printStackTrace();
		}
		
		Driver driver = new Driver("hevero", "password", 3, 12345, date);
		DriverRestrictedInterface dri = new DriverRestrictedInterface(driver);
		
		System.out.println("UserRestrictedInterface In Test getExpirationDate");	
		assertEquals(dri.getExpirationDate(),date);
	}
	
	
	
	
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(UserTest.class);
	}

}