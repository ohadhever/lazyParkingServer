package serverBL.parkingMonitoringAndManagement;

import java.util.Date;

import serverBL.messagePassing.MessagePassing;

public class ParkingSpot {
	private int id;
	private boolean isOccupied;
	private boolean isReserved;
	private Date expirationDate;
	private String reservedFor;
	private MessagePassing mp;
	
	public ParkingSpot(int id) {
		this.id = id;
		this.mp = null;
		isOccupied = false;
		isReserved = false;
		expirationDate = null;
		reservedFor = null;
	}
	
	public boolean isOccupied() {
		return isOccupied;
	}
	
	public void setOccupied(boolean isOccupied) {
		this.isOccupied = isOccupied;
		
		if (mp != null)
			mp.parkingSpotStatusUpdate(id, isOccupied, isReserved, reservedFor);
	}
	
	public void reserveSpot(String reservedFor, Date expirationDate) {
		isReserved = true;
		this.reservedFor = reservedFor;
		this.expirationDate = expirationDate;
	}
	
	public void freeReservation() {
		isReserved = false;
		reservedFor = null;
		expirationDate = null;
	}
	
	public boolean isReserved() {
		return isReserved;
	}
	
	public Date getExpirationDate() {
		if (!isReserved)
			return null;
		return expirationDate;
	}
	
	public String getReservedFor() {
		if (!isReserved)
			return null;
		return reservedFor;
	}
	
	public int getID() {
		return id;
	}
	
	public void setMessagePassing(MessagePassing mp) {
		this.mp = mp;
	}
}
