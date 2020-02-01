package mockups;

import adminUI.ExceptionsLogger;
import serverBL.authorizedUsersManagement.DriverIdentificationSystemAdapter;
import serverBL.parkingMonitoringAndManagement.ParkingSpotList;
import serverBL.parkingMonitoringAndManagement.ParkingSpotSensorAdapter;

public class DriverMockup extends Thread {
	public static final int MIN_DELAY = 10000; // in milliseconds
	public static final int MAX_DELAY = 100000; // in milliseconds
	public static final int NUM_OF_PARKING_SPOTS = 66;
	public static final double FAST_ACTIONS_FACTOR = 0.1;
	private String username;
	private int cardKey;
	private DriverIdentificationSystemAdapter cardReader;
	private ParkingSpotSensorAdapter sensorsHub;
	private ParkingSpotList parkingList;
	
	public DriverMockup(String username, int cardKey, DriverIdentificationSystemAdapter cardReader, ParkingSpotSensorAdapter sensorsHub, ParkingSpotList parkingList) {
		this.username = username;
		this.cardKey = cardKey;
		this.cardReader = cardReader;
		this.sensorsHub = sensorsHub;
		this.parkingList = parkingList;
	}
	
	private void delay(double factor) {
		try {
			sleep((long)(((MAX_DELAY  + 1 - MIN_DELAY) * Math.random() + MIN_DELAY) * factor));
		} catch (InterruptedException e) {
			ExceptionsLogger.logException(e.getMessage());
		}
	}
	
	@Override
	public void run() {
		while (true) {
			delay(1.0); // the time while outside the parking lot
			if (!cardReader.driverVerification(username, cardKey))
				return;
			delay(FAST_ACTIONS_FACTOR); // the time it takes to find a parking spot
			int id;
			do // find a parking spot that isn't occupied
				id = (int)(NUM_OF_PARKING_SPOTS * Math.random() + 1);
			while (parkingList.getParkingSpot(id).isOccupied());
			sensorsHub.setOccupied(id);
			delay(1.0); // the time while parking
			sensorsHub.setFree(id);
			delay(FAST_ACTIONS_FACTOR); // the time it takes to get out of the parking lot
			cardReader.driverExit(username, cardKey);
		}
	}
}
