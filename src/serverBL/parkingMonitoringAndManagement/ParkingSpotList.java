package serverBL.parkingMonitoringAndManagement;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import adminUI.ExceptionsLogger;
import common.Reply;
import serverBL.messagePassing.MessagePassing;


public class ParkingSpotList {
	private HashMap<Integer, ParkingSpot> parkingSpots;
	private ReservationsStorage storage;
	private boolean noStorage;
	private MessagePassing mp;
	
	public ParkingSpotList(LinkedList<Integer> ids, ReservationsStorage storage) {
		this.mp = null;
		if (storage == null)
			noStorage = true;
		else
			noStorage = false;
		
		this.storage = storage;
		parkingSpots = new HashMap<Integer, ParkingSpot>();
		int id;
		while (!ids.isEmpty()) {
			id = ids.remove();
			parkingSpots.put(id, new ParkingSpot(id));
		}
		
		if (!noStorage)
			try {
				LinkedList<ParkingSpot> list = storage.loadAllReservations();
				ParkingSpot from, to;
				while (!list.isEmpty()) {
					from = list.remove();
					to = parkingSpots.get(from.getID());
					to.reserveSpot(from.getReservedFor(), from.getExpirationDate());
				}
			} catch (Exception e) {
				noStorage = true;
				ExceptionsLogger.logException(new Date() + ": storage exception during a call to loadAllReservations()");
			}
	}
	
	
	public synchronized Reply getParkingSpotStatus(int id) {
		ParkingSpot spot = parkingSpots.get(id);
		if (spot == null)
			return Reply.ID_NOT_FOUND;
		
		if (spot.isReserved() && spot.getExpirationDate() != null && spot.getExpirationDate().before(new Date())) { // if reservation expired
			if (!noStorage)
				try {
					storage.deleteReservation(spot);
				} catch (Exception e) {
					noStorage = true;
					ExceptionsLogger.logException(new Date() + ": storage exception during a call to deleteReservation()");		
				}
			
			spot.freeReservation();
			
			if (mp != null)
				mp.parkingSpotStatusUpdate(id, spot.isOccupied(), spot.isReserved(), spot.getReservedFor());
		}
		
		return Reply.SUCCESS;
	}
	
	
	public synchronized Reply reserveParkingSpot(int id, String reservedFor, Date expirationDate) {
		ParkingSpot spot = parkingSpots.get(id);
		if (spot == null)
			return Reply.ID_NOT_FOUND;
		
		if (expirationDate != null && expirationDate.before(new Date()))
			return Reply.DATE_EXPIRED;
		
		spot.reserveSpot(reservedFor, expirationDate);
		
		if (!noStorage)
			try {
				storage.saveReservation(spot);
			} catch (Exception e) {
				noStorage = true;
				ExceptionsLogger.logException(new Date() + ": storage exception during a call to saveReservation()");		
			}
		
		if (mp != null)
			mp.parkingSpotStatusUpdate(id, spot.isOccupied(), spot.isReserved(), spot.getReservedFor());
		
		return Reply.SUCCESS;
	}
	
	
	public synchronized Reply freeReservation(int id) {
		ParkingSpot spot = parkingSpots.get(id);
		if (spot == null)
			return Reply.ID_NOT_FOUND;
		
		if (!noStorage)
			try {
				storage.deleteReservation(spot);
			} catch (Exception e) {
				noStorage = true;
				ExceptionsLogger.logException(new Date() + ": storage exception during a call to deleteReservation()");		
			}
		
		spot.freeReservation();
		
		if (mp != null)
			mp.parkingSpotStatusUpdate(id, spot.isOccupied(), spot.isReserved(), spot.getReservedFor());
		
		return Reply.SUCCESS;
	}
	
	public ParkingSpot getParkingSpot(int id) {
		return parkingSpots.get(id);
	}
	
	public void setMessagePassing(MessagePassing mp) {
		this.mp = mp;
		
		Collection<ParkingSpot> collection = parkingSpots.values();
		for (ParkingSpot spot : collection)
			spot.setMessagePassing(mp);
	}
}
