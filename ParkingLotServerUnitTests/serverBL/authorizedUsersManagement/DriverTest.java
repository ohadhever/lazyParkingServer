package serverBL.authorizedUsersManagement;



import junit.framework.TestCase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DriverTest extends TestCase {
	
	
	
	public void testgetCardKey() {
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		Date date=new Date();
		try {
			date = format.parse("1-12-2019");
		} catch (ParseException e) {
			// This string has been checked and won't cause an exception
			e.printStackTrace();
		}
		
		Driver driver = new Driver("ohad", "password", 3, 12345, date);
		
		System.out.println("Driver In Test getCardKey");
		assertEquals(driver.getCardKey(),12345);
	}
	
	
	public void testisPresent() {
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		Date date=new Date();
		try {
			date = format.parse("1-12-2019");
		} catch (ParseException e) {
			// This string has been checked and won't cause an exception
			e.printStackTrace();
		}
		
		Driver driver = new Driver("ohad", "password", 3, 12345, date);
		
		System.out.println("Driver In Test isPresent");
		assertFalse("Driver should not be Present ",driver.isPresent());
		
		driver.setPresent(true);
		assertTrue("Driver should  be Present ",driver.isPresent());
	}
	
	
	public void testsetPresent() {
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		Date date=new Date();
		try {
			date = format.parse("1-12-2019");
		} catch (ParseException e) {
			// This string has been checked and won't cause an exception
			e.printStackTrace();
		}
		
		Driver driver = new Driver("ohad", "password", 3, 12345, date);
		
		System.out.println("Driver In Test setPresent");
		driver.setPresent(true);
		assertTrue("Driver should  be Present ",driver.isPresent());
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
		
		
		Driver driver = new Driver("ohad", "password", 3, 12345, date);
		System.out.println("Driver In Test getExpirationDate");
		assertEquals(date,driver.getExpirationDate());
	}
	
	
	public void testsetExpirationDate() {
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		Date date=new Date();
		Date datefir=new Date();
		try {
			date = format.parse("1-12-2019");
		} catch (ParseException e) {
			// This string has been checked and won't cause an exception
			e.printStackTrace();
		}
		
		
		
		try {
			datefir = format.parse("2-10-2020");
		} catch (ParseException e) {
			// This string has been checked and won't cause an exception
			e.printStackTrace();
		}
		
		
		Driver driver = new Driver("ohad", "password", 3, 12345, date);
		
		driver.setExpirationDate(datefir);
		System.out.println("Driver In Test setExpirationDate");
		assertEquals(datefir,driver.getExpirationDate());
	}
	
	
	public void testequals() {
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		Date date=new Date();
		Date datefir=new Date();
		
		try {
			date = format.parse("1-12-2019");
		} catch (ParseException e) {
			// This string has been checked and won't cause an exception
			e.printStackTrace();
		}
		Driver driver = new Driver("ohad", "password", 3, 12345, date);
		
		
		try {
			datefir = format.parse("1-11-2019");
		} catch (ParseException e) {
			// This string has been checked and won't cause an exception
			e.printStackTrace();
		}
		Driver driverfir = new Driver("ohad", "password", 3, 12345, datefir);
		System.out.println("Driver In Test equals");
		
		assertFalse("Driver In Test equals: should not be equals by date",driver.equals(driverfir));
		
		
		Driver driversec = new Driver("matan", "password", 3, 12345, date);
		assertFalse("Driver In Test equals: should not be equals by name",driver.equals(driversec));
		
		
		Driver driverthi = new Driver("ohad", "diffpassword", 3, 12345, date);
		assertFalse("Driver In Test equals: should not be equals by password",driver.equals(driverthi));
		
		
		Driver driverfor = new Driver("ohad", "password", 2, 12345, date);
		assertFalse("Driver In Test equals: should not be equals by permisstion",driver.equals(driverfor));
		
		
		
		Driver driverfif = new Driver("ohad", "password", 3, 123456, date);
		assertFalse("Driver In Test equals: should not be equals by cardKey",driver.equals(driverfif));
		
		
		Driver driversix = new Driver("ohad", "password", 3, 12345, date);
		assertTrue("Driver In Test equals: should be equals",driver.equals(driversix));	
	}
	
	
	
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(DriverTest.class);
	}
}
