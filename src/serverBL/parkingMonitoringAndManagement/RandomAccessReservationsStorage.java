package serverBL.parkingMonitoringAndManagement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

public class RandomAccessReservationsStorage implements ReservationsStorage {
	private File reservationsFile;
	private HashMap<Integer, Long> reservationsOffsetsList;
	private LinkedList<Long> deletedReservationsList;
	
	
	public RandomAccessReservationsStorage(String reservationsFilename) throws FileNotFoundException {
		reservationsFile = new File(reservationsFilename);
		if(!reservationsFile.exists() || reservationsFile.isDirectory()) { 
		    throw new FileNotFoundException();
		}
		reservationsOffsetsList = new HashMap<Integer, Long>();
		deletedReservationsList = new LinkedList<Long>();
	}
	
	
	@Override
	public LinkedList<ParkingSpot> loadAllReservations() throws Exception {
		FileToReservationStruct readReservation;
		long fileLength;
		long offset;
		LinkedList<ParkingSpot> reservationsList = new LinkedList<ParkingSpot>();
		
		//Adding reservations to linked list
		RandomAccessFile reservationsFileHandler = null;
		reservationsFileHandler = new RandomAccessFile(reservationsFile, "r");	
		
		fileLength = reservationsFileHandler.length();
			
		while (fileLength > (offset = reservationsFileHandler.getFilePointer())) {
			readReservation = readReservation(reservationsFileHandler);
			if (readReservation.isDeleted)
				deletedReservationsList.add(offset);
			else {
				ParkingSpot tmp = new ParkingSpot(readReservation.id);
				tmp.reserveSpot(readReservation.reservedFor, readReservation.expirationDate);
				reservationsList.add(tmp);
				reservationsOffsetsList.put(readReservation.id, offset);
			}
		}
		reservationsFileHandler.close();
			
		return reservationsList;
	}
	
	@Override
	public synchronized void saveReservation(ParkingSpot spot) throws Exception { 
		FileToReservationStruct reservation;
		RandomAccessFile file = null;
		long offset;
		
		file = new RandomAccessFile(reservationsFile, "rws");
		reservation = new FileToReservationStruct(spot);
			
		if (!deletedReservationsList.isEmpty()) {
			offset = deletedReservationsList.getFirst();
			file.seek(offset);
			writeReservation(file, reservation);
			deletedReservationsList.removeFirst();
			reservationsOffsetsList.put(reservation.id, offset);
		}
		else {
			offset = file.length();
			file.seek(offset);
			writeReservation(file, reservation);
			reservationsOffsetsList.put(reservation.id, offset);
		}	
		
		file.close();
	}
	
	@Override
	public synchronized void deleteReservation(ParkingSpot spot) throws Exception {
		FileToReservationStruct reservation;
		RandomAccessFile file = null;
		Long offset;
		reservation = new FileToReservationStruct(spot);
		if ((offset = reservationsOffsetsList.remove(reservation.id)) == null)
			return;
		
		file = new RandomAccessFile(reservationsFile, "rws");
		reservation.isDeleted = true;
		file.seek(offset);
		writeReservation(file, reservation);
		deletedReservationsList.addLast(offset);
		
		file.close();
	}
	
	
	private FileToReservationStruct readReservation(RandomAccessFile file) throws IOException {
		FileToReservationStruct reservation = new FileToReservationStruct();
		
		reservation.id = file.readInt();
		reservation.reservedFor = (file.readUTF().split("\\$"))[0]; // removes the padding
		reservation.isUnlimited = file.readBoolean();
		int day = file.readInt();
		int month = file.readInt();
		int year = file.readInt();
		if (reservation.isUnlimited == true)
			reservation.expirationDate = null;
		else {
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy"); // Because Date class is autistic
			try {
				reservation.expirationDate = format.parse("" + day + "-" + month + "-" + year);
			} catch (ParseException e) {
				// This string has been checked and won't cause an exception
			}
		}
		reservation.isDeleted = file.readBoolean();
		
		return reservation;
	}
	
	
	private void writeReservation(RandomAccessFile file, FileToReservationStruct reservation) throws IOException {
		// a reservedFor has max 20 characters, but we need all of the records to have the same size so we pad 
		// the reservedFor-s with $ so they all have the same length
		file.writeInt(reservation.id);
		file.writeUTF(reservation.reservedFor + "$$$$$$$$$$$$$$$$$$$$".substring(reservation.reservedFor.length()));
		file.writeBoolean(reservation.isUnlimited);
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy"); // Because Date class is autistic
		String[] dateStrings = format.format(reservation.expirationDate).split("-");
		file.writeInt(Integer.parseInt(dateStrings[0])); // write day
		file.writeInt(Integer.parseInt(dateStrings[1])); // write month
		file.writeInt(Integer.parseInt(dateStrings[2])); // write year
		file.writeBoolean(reservation.isDeleted);
	}
	
	
	private class FileToReservationStruct {
		public int id;
		public String reservedFor;
		public boolean isUnlimited;
		public Date expirationDate;
		public boolean isDeleted;
		
		
		public FileToReservationStruct() { }
		
		public FileToReservationStruct(ParkingSpot spot) {
			id = spot.getID();
			reservedFor = spot.getReservedFor();
			isDeleted = false;
			if (spot.getExpirationDate() == null) {
				isUnlimited = true;
				expirationDate = new Date();
			}
			else {
				isUnlimited = false;
				expirationDate = spot.getExpirationDate();
			}
		}
	}
}
