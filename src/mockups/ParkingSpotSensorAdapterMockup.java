package mockups;

import serverBL.parkingMonitoringAndManagement.ParkingSpotList;
import serverBL.parkingMonitoringAndManagement.ParkingSpotSensorAdapter;

public class ParkingSpotSensorAdapterMockup extends ParkingSpotSensorAdapter {

	public ParkingSpotSensorAdapterMockup(ParkingSpotList parkingSpots) {
		super(parkingSpots);
	}

	@Override
	public void pollSensors() {
		/* DO NOTHING */
	}

}
