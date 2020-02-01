package serverBL.parkingMonitoringAndManagement;

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
import serverBL.authorizedUsersManagement.DriverTest;

public class ParkingSpotListTest extends TestCase{
	
	public void before() {
		File ParkingSpotsFile = new File("ParkingSpotListTestSpotsFile");
		if (ParkingSpotsFile.exists())
			ParkingSpotsFile.delete();
		try {
			ParkingSpotsFile.createNewFile();
		} catch (IOException e1) {
			fail("Could not create the ParkingSpot_test_file");
		}
	}
	
	public void testgetParkingSpotStatus() {
		before();
		ReservationsStorage storage;
		LinkedList<Integer> ids = new LinkedList<Integer>();
		try {
			storage = new RandomAccessReservationsStorage("ParkingSpotListTestSpotsFile");
		} catch (FileNotFoundException e) {
			ExceptionsLogger.logException("Could not open the files for RandomAccessUsersStorage");
			storage = null;
		}
		ids.add(1);
		ParkingSpotList psl = new ParkingSpotList(ids, storage);
		
		System.out.println("ParkingSpotList In Test getParkingSpotStatus");
		assertEquals("ParkingSpotList In Test getParkingSpotStatus: ID should not be found ",psl.getParkingSpotStatus(0),Reply.ID_NOT_FOUND);
		assertEquals("ParkingSpotList In Test getParkingSpotStatus: ID should be found ",psl.getParkingSpotStatus(1),Reply.SUCCESS);
	}
	
	public void testreserveParkingSpot() {
		before();
		ReservationsStorage storage;
		LinkedList<Integer> ids = new LinkedList<Integer>();

		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		Date date=new Date();
		Date dateOne=new Date();
		try {
			date = format.parse("1-12-2019");
			dateOne = format.parse("1-12-2018");
		} catch (ParseException e) {
			// This string has been checked and won't cause an exception
			e.printStackTrace();
		}
		try {
			storage = new RandomAccessReservationsStorage("ParkingSpotListTestSpotsFile");
		} catch (FileNotFoundException e) {
			ExceptionsLogger.logException("Could not open the files for RandomAccessUsersStorage");
			storage = null;
		}
		ids.add(1);
		ParkingSpotList psl = new ParkingSpotList(ids, storage);
		
		System.out.println("ParkingSpotList In Test reserveParkingSpot");
		assertEquals("ParkingSpotList In Test reserveParkingSpot: ID should not be found ",psl.reserveParkingSpot(0, "ohad", null),Reply.ID_NOT_FOUND);
		assertEquals("ParkingSpotList In Test reserveParkingSpot: Date should be EXPIRED ",psl.reserveParkingSpot(1, "ohad", dateOne),Reply.DATE_EXPIRED);
		assertEquals("ParkingSpotList In Test reserveParkingSpot: Spoot should be reserved",psl.reserveParkingSpot(1, "ohad", date),Reply.SUCCESS);
	}
	
	public void testfreeReservation() {
		before();
		ReservationsStorage storage;
		LinkedList<Integer> ids = new LinkedList<Integer>();

		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		Date date=new Date();
		try {
			date = format.parse("1-12-2019");
		} catch (ParseException e) {
			// This string has been checked and won't cause an exception
			e.printStackTrace();
		}
		try {
			storage = new RandomAccessReservationsStorage("ParkingSpotListTestSpotsFile");
		} catch (FileNotFoundException e) {
			ExceptionsLogger.logException("Could not open the files for RandomAccessUsersStorage");
			storage = null;
		}
		ids.add(1);
		ids.add(2);
		ParkingSpotList psl = new ParkingSpotList(ids, storage);
		System.out.println("ParkingSpotList In Test freeReservation");
		assertEquals("ParkingSpotList In Test freeReservation: Reservation FAILED ",psl.reserveParkingSpot(1, "ohad", date),Reply.SUCCESS);
		
		assertEquals("ParkingSpotList In Test freeReservation: ID should not be found ",psl.freeReservation(0),Reply.ID_NOT_FOUND);
		assertEquals("ParkingSpotList In Test reserveParkingSpot: Spot 1 should be reserved",psl.freeReservation(1),Reply.SUCCESS);
		assertEquals("ParkingSpotList In Test reserveParkingSpot: Spot 2 should be reserved",psl.freeReservation(2),Reply.SUCCESS);
	}
	
	public void testgetParkingSpot() {
		before();
		ReservationsStorage storage;
		LinkedList<Integer> ids = new LinkedList<Integer>();
		ParkingSpot spot = new ParkingSpot(123);
		try {
			storage = new RandomAccessReservationsStorage("ParkingSpotListTestSpotsFile");
		} catch (FileNotFoundException e) {
			ExceptionsLogger.logException("Could not open the files for RandomAccessUsersStorage");
			storage = null;
		}
		
		ids.add(123);
		ParkingSpotList psl = new ParkingSpotList(ids, storage);
		
		
		System.out.println("ParkingSpotList In Test getParkingSpot");
		assertSame(psl.getParkingSpot(123).getID(),spot.getID());
	}
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(DriverTest.class);
	}
	
	
	

}
