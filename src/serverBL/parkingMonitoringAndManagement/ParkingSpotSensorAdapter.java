package serverBL.parkingMonitoringAndManagement;

public abstract class ParkingSpotSensorAdapter {
	protected ParkingSpotList parkingSpots; 
	
	public ParkingSpotSensorAdapter(ParkingSpotList parkingSpots) {
		this.parkingSpots = parkingSpots;
		pollSensors();
	}
	
	public void setOccupied(int id) {
		parkingSpots.getParkingSpot(id).setOccupied(true);
	}
	
	public void setFree(int id) {
		parkingSpots.getParkingSpot(id).setOccupied(false);
	}
	
	public abstract void pollSensors();
	
	

}
