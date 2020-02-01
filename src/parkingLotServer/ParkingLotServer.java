package parkingLotServer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import adminUI.AdminLoginFrame;
import adminUI.ExceptionsLogger;
import mockups.DriverIdentificationSystemAdapterMockup;
import mockups.DriverMockup;
import mockups.GateAdapterMockup;
import mockups.ParkingSpotSensorAdapterMockup;
import serverBL.authorizedUsersManagement.DriverIdentificationSystemAdapter;
import serverBL.authorizedUsersManagement.DriverRestrictedInterface;
import serverBL.authorizedUsersManagement.GateAdapter;
import serverBL.authorizedUsersManagement.RandomAccessUsersStorage;
import serverBL.authorizedUsersManagement.UserList;
import serverBL.authorizedUsersManagement.UserRestrictedInterface;
import serverBL.authorizedUsersManagement.UsersStorage;
import serverBL.messagePassing.MessagePassing;
import serverBL.parkingMonitoringAndManagement.ParkingSpotList;
import serverBL.parkingMonitoringAndManagement.ParkingSpotSensorAdapter;
import serverBL.parkingMonitoringAndManagement.RandomAccessReservationsStorage;
import serverBL.parkingMonitoringAndManagement.ReservationsStorage;


public class ParkingLotServer { // SINGLETON
	private static ParkingLotServer theParkingLotServer = null;
	public final String DRIVERS_FILE = "DriversFile";
	public final String USERS_FILE = "UsersFile";
	public final String RESERVATIONS_FILE = "ReservationsFile";
	public final int NUM_OF_PARKING_SPOTS = 66;
	private UserList userList;
	private ParkingSpotList parkingList;
	private UsersStorage uStorage;
	private ReservationsStorage rStorage;
	private MessagePassing mp;
	private DriverIdentificationSystemAdapter disAdapter;
	private GateAdapter entranceGate;
	private GateAdapter exitGate;
	private ParkingSpotSensorAdapter pssAdapter;
	
	private ParkingLotServer() {
		try {
			uStorage = new RandomAccessUsersStorage(DRIVERS_FILE, USERS_FILE);
		} catch (FileNotFoundException e) {
			ExceptionsLogger.logException("Could not open the files for RandomAccessUsersStorage");
			uStorage = null;
		}
		userList  = new UserList(uStorage);
		
		try {
			rStorage = new RandomAccessReservationsStorage(RESERVATIONS_FILE);
		} catch (FileNotFoundException e) {
			ExceptionsLogger.logException("Could not open the file for RandomAccessReservationsStorage");
			rStorage = null;
		}
		
		LinkedList<Integer> idList = new LinkedList<Integer>();
		for (int i = 1; i <= NUM_OF_PARKING_SPOTS; i++)
			idList.addLast(i);
		parkingList = new ParkingSpotList(idList, rStorage);
		
		initMockups();
		
		try {
			mp = new MessagePassing(userList, parkingList, entranceGate);
		} catch (IOException e) {
			ExceptionsLogger.logException(e.getMessage());
		}
		new AdminLoginFrame(userList, mp);
	}
	
	private void initMockups() {
		entranceGate = new GateAdapterMockup();
		exitGate = new GateAdapterMockup();
		disAdapter = new DriverIdentificationSystemAdapterMockup(userList, entranceGate, exitGate);
		pssAdapter = new ParkingSpotSensorAdapterMockup(parkingList);
	}
	
	public void startSimulation() {
		LinkedList<DriverRestrictedInterface> driversList = new LinkedList<DriverRestrictedInterface>();
		LinkedList<UserRestrictedInterface> users = userList.getAllUsers();
		for (UserRestrictedInterface user : users) // copy only the drivers
			if (user instanceof DriverRestrictedInterface)
				driversList.add((DriverRestrictedInterface)user);
		
		for (DriverRestrictedInterface driver : driversList)
			new DriverMockup(driver.getUserName(), driver.getCardKey(), disAdapter, pssAdapter, parkingList).start();
	}
	
	public static ParkingLotServer getInstance() {
		if (theParkingLotServer == null)
			theParkingLotServer = new ParkingLotServer();
		return theParkingLotServer;
	}
	

	public static void main(String[] args) {
		ParkingLotServer parkingLot = ParkingLotServer.getInstance();
		parkingLot.startSimulation();
	}

}
