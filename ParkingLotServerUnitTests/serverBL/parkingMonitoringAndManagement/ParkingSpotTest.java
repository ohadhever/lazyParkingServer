package serverBL.parkingMonitoringAndManagement;

import junit.framework.TestCase;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ParkingSpotTest extends TestCase{

	public void testgetOccupied() {
		ParkingSpot spot = new ParkingSpot(1);
		
		System.out.println("ParkingSpot In Test getOccupied");
		assertFalse(spot.isOccupied());
	}
	
	
	public void testsetOccupied() {
		ParkingSpot spot = new ParkingSpot(1);
		
		System.out.println("ParkingSpot In Test setOccupied");
		spot.setOccupied(true);
		assertTrue(spot.isOccupied());
	}
	
	
	public void testreserveSpot() {
		ParkingSpot spot = new ParkingSpot(1);
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		Date date=new Date();
		try {
			date = format.parse("1-12-2019");
		} catch (ParseException e) {
			// This string has been checked and won't cause an exception
			e.printStackTrace();
		}
		
		spot.reserveSpot("ohad", date);
		
		System.out.println("ParkingSpot In Test reserveSpot");
		assertTrue(spot.isReserved());
	}
	
	public void testfreeReservation() {
		ParkingSpot spot = new ParkingSpot(1);
		
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		Date date=new Date();
		try {
			date = format.parse("1-12-2019");
		} catch (ParseException e) {
			// This string has been checked and won't cause an exception
			e.printStackTrace();
		}
		
		spot.reserveSpot("ohad", date);
		System.out.println("ParkingSpot In Test freeReservation");
		spot.freeReservation();
		assertFalse(spot.isReserved());
	}
	
	
	public void testisReserved() {
		ParkingSpot spot = new ParkingSpot(1);
		
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		Date date=new Date();
		try {
			date = format.parse("1-12-2019");
		} catch (ParseException e) {
			// This string has been checked and won't cause an exception
			e.printStackTrace();
		}
		assertFalse(spot.isReserved());
		
		
		spot.reserveSpot("ohad", date);
		System.out.println("ParkingSpot In Test isReserved");
		assertTrue(spot.isReserved());
	}
	
	
	public void testgetExpirationDate() {
		ParkingSpot spot = new ParkingSpot(1);
		
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		Date date=new Date();
		try {
			date = format.parse("1-12-2019");
		} catch (ParseException e) {
			// This string has been checked and won't cause an exception
			e.printStackTrace();
		}
		
		spot.reserveSpot("ohad", date);
		System.out.println("ParkingSpot In Test getExpirationDate");
		assertSame(spot.getExpirationDate(),date);
	}
	
	public void testgetReservedFor() {
		ParkingSpot spot = new ParkingSpot(1);
		
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		Date date=new Date();
		try {
			date = format.parse("1-12-2019");
		} catch (ParseException e) {
			// This string has been checked and won't cause an exception
			e.printStackTrace();
		}
		
		spot.reserveSpot("ohad", date);
		System.out.println("ParkingSpot In Test getReservedFor");
		assertSame(spot.getReservedFor(),"ohad");
	}
	
	public void testgetId() {
		ParkingSpot spot = new ParkingSpot(3);
		
		System.out.println("ParkingSpot In Test getId");
		assertEquals(spot.getID(),3);
	}
	
	
	
	
	
	
}