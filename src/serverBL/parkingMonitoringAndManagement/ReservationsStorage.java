package serverBL.parkingMonitoringAndManagement;

import java.util.LinkedList;


public interface ReservationsStorage {
	public LinkedList<ParkingSpot> loadAllReservations() throws Exception;
	
	public void saveReservation(ParkingSpot spot) throws Exception;
	
	public void deleteReservation(ParkingSpot spot) throws Exception;
}
