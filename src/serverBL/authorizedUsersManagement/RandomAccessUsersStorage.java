package serverBL.authorizedUsersManagement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

public class RandomAccessUsersStorage implements UsersStorage {
	private File driversFile;
	private File usersFile;
	private HashMap<String, Long> usersOffsetsList;
	private HashMap<String, Long> driversOffsetsList;
	private LinkedList<Long> deletedUsersList;
	private LinkedList<Long> deletedDriversList;
	
	
	public RandomAccessUsersStorage(String driversFilename, String usersFilename) throws FileNotFoundException {
		driversFile = new File(driversFilename);
		if(!driversFile.exists() || driversFile.isDirectory()) { 
		    throw new FileNotFoundException();
		}
		usersFile = new File(usersFilename);
		if(!usersFile.exists() || usersFile.isDirectory()) { 
		    throw new FileNotFoundException();
		}
		usersOffsetsList = new HashMap<String, Long>();
		driversOffsetsList = new HashMap<String, Long>();
		deletedUsersList = new LinkedList<Long>();
		deletedDriversList = new LinkedList<Long>();
	}
	
	
	public LinkedList<User> loadAllUsers() throws Exception{
		
		FileToUserStruct readUser;
		long fileLength;
		long offset;
		LinkedList<User> usersList = new LinkedList<User>();
		
		//Adding users to linked list
		RandomAccessFile usersFileHandler = null;
		usersFileHandler = new RandomAccessFile(usersFile, "r");	
		
		fileLength = usersFileHandler.length();
			
		while (fileLength > (offset = usersFileHandler.getFilePointer())) {
			readUser = readUser(usersFileHandler);
			if (readUser.isDeleted)
				deletedUsersList.add(offset);
			else {
				usersList.add(new User(readUser.userName, readUser.password, readUser.permission));
				usersOffsetsList.put(readUser.userName, offset);
			}
		}
		usersFileHandler.close();
		
		//Adding Drivers to linked list
		RandomAccessFile driversFileHandler = null;
		driversFileHandler = new RandomAccessFile(driversFile, "r");
		
		fileLength = driversFileHandler.length();

		while (fileLength > (offset = driversFileHandler.getFilePointer())) {
			readUser = readDriver(driversFileHandler);
			if (readUser.isDeleted)
				deletedDriversList.add(offset);
			else {
				usersList.add(new Driver(readUser.userName, readUser.password, readUser.permission,readUser.cardKey,readUser.expirationDate));
				driversOffsetsList.put(readUser.userName, offset);
			}
		}	
		driversFileHandler.close();
			
		return usersList;
	}
	
	
	public void saveUser(User user) throws Exception {
	// assuming user is a NEW User which isn't already in the file!! 
		FileToUserStruct userAdaptor;
		RandomAccessFile file = null;
		long offset;
		
		if (user instanceof Driver) {
			file = new RandomAccessFile(driversFile, "rws");
			userAdaptor = new FileToUserStruct((Driver)user);
			
			if (!deletedDriversList.isEmpty()) {
				offset = deletedDriversList.getFirst();
				file.seek(offset);
				writeDriver(file, userAdaptor);
				deletedDriversList.removeFirst();
				driversOffsetsList.put(userAdaptor.userName, offset);
			}
			else {
				offset = file.length();
				file.seek(offset);
				writeDriver(file, userAdaptor);
				driversOffsetsList.put(userAdaptor.userName, offset);
			}	
		}
		else {
			file = new RandomAccessFile(usersFile, "rws");
			userAdaptor = new FileToUserStruct(user);
			
			if (!deletedUsersList.isEmpty()) {
				offset = deletedUsersList.getFirst();
				file.seek(offset);
				writeUser(file, userAdaptor);
				deletedUsersList.removeFirst();
				usersOffsetsList.put(userAdaptor.userName, offset);
			}
			else {
				offset = file.length();
				file.seek(offset);
				writeUser(file, userAdaptor);
				usersOffsetsList.put(userAdaptor.userName, offset);
			}
		}
		
		file.close();
	}
	
	
	public void deleteUser(User user) throws Exception {
		// assuming user is ALREADY in the file!!
		FileToUserStruct userAdaptor;
		RandomAccessFile file = null;
		long offset;
		
		if (user instanceof Driver) {
			file = new RandomAccessFile(driversFile, "rws");
			userAdaptor = new FileToUserStruct((Driver)user);
			userAdaptor.isDeleted = true;
			
			offset = driversOffsetsList.remove(userAdaptor.userName);
			file.seek(offset);
			writeDriver(file, userAdaptor);
			deletedDriversList.addLast(offset);
		}
		else {
			file = new RandomAccessFile(usersFile, "rws");
			userAdaptor = new FileToUserStruct(user);
			userAdaptor.isDeleted = true;
			
			offset = usersOffsetsList.remove(userAdaptor.userName);
			file.seek(offset);
			writeUser(file, userAdaptor);
			deletedUsersList.addLast(offset);
		}
		
		file.close();
	}
	
	
	public void modifyUser(User user) throws Exception{
		// assuming user is ALREADY in the file!!
		FileToUserStruct userAdaptor;
		RandomAccessFile file = null;
		long offset;
		
		if (user instanceof Driver) {
			file = new RandomAccessFile(driversFile, "rws");
			userAdaptor = new FileToUserStruct((Driver)user);
			
			offset = driversOffsetsList.get(userAdaptor.userName);
			file.seek(offset);
			writeDriver(file, userAdaptor);
		}
		else {
			file = new RandomAccessFile(usersFile, "rws");
			userAdaptor = new FileToUserStruct(user);
			
			offset = usersOffsetsList.get(userAdaptor.userName);
			file.seek(offset);
			writeUser(file, userAdaptor);
		}
		
		file.close();
	}
	
	private FileToUserStruct readUser(RandomAccessFile file) throws IOException {
		FileToUserStruct user = new FileToUserStruct();
		
		user.userName = (file.readUTF().split("\\$"))[0]; // removes the padding
		user.password = new byte[32]; // 256bit
		file.read(user.password);
		user.permission = file.readInt();
		user.isDeleted = file.readBoolean();
		
		return user;
	}
	
	
	private FileToUserStruct readDriver(RandomAccessFile file) throws IOException {
		FileToUserStruct driver = readUser(file);
		driver.cardKey = file.readInt();
		driver.isUnlimited = file.readBoolean();
		int day = file.readInt();
		int month = file.readInt();
		int year = file.readInt();
		if (driver.isUnlimited == true)
			driver.expirationDate = null;
		else {
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy"); // Because Date class is autistic
			try {
				driver.expirationDate = format.parse("" + day + "-" + month + "-" + year);
			} catch (ParseException e) {
				// This string has been checked and won't cause an exception
			}
		}
		return driver;
	}
	
	
	private void writeUser(RandomAccessFile file, FileToUserStruct user) throws IOException {
		// a userName has max 20 characters, but we need all of the records to have the same size so we pad 
		// the usernames with $ so they all have the same length
		file.writeUTF(user.userName + "$$$$$$$$$$$$$$$$$$$$".substring(user.userName.length()));
		file.write(user.password);
		file.writeInt(user.permission);
		file.writeBoolean(user.isDeleted);
	}
	
	
	private void writeDriver(RandomAccessFile file, FileToUserStruct driver) throws IOException {
		writeUser(file, driver);
		file.writeInt(driver.cardKey);
		file.writeBoolean(driver.isUnlimited);
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy"); // Because Date class is autistic
		String[] dateStrings = format.format(driver.expirationDate).split("-");
		file.writeInt(Integer.parseInt(dateStrings[0])); // write day
		file.writeInt(Integer.parseInt(dateStrings[1])); // write month
		file.writeInt(Integer.parseInt(dateStrings[2])); // write year
	}
	
	
	private class FileToUserStruct {
		public String userName;
		public byte[] password;
		public int permission;
		public boolean isDeleted;
		public int cardKey;
		public boolean isUnlimited;
		public Date expirationDate;
		
		public FileToUserStruct() {}
		
		public FileToUserStruct(User user) {
			userName = user.getUserName();
			password = user.getHashedPassword();
			permission = user.getPermission();
			isDeleted = false;
		}
		
		public FileToUserStruct(Driver driver) {
			this((User)driver);
			cardKey = driver.getCardKey();
			if (driver.getExpirationDate() == null) {
				isUnlimited = true;
				expirationDate = new Date();
			}
			else {
				isUnlimited = false;
				expirationDate = driver.getExpirationDate();
			}
		}
	}
}
