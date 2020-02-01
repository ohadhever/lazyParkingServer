package serverBL.messagePassing;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import adminUI.ExceptionsLogger;
import common.Reply;
import common.ReplyMessage;
import common.RequestMessage;
import common.RequestType;
import serverBL.authorizedUsersManagement.DriverRestrictedInterface;
import serverBL.authorizedUsersManagement.GateAdapter;
import serverBL.authorizedUsersManagement.UserList;
import serverBL.authorizedUsersManagement.UserRestrictedInterface;
import serverBL.authorizedUsersManagement.UsernameAndCardKeyPair;
import serverBL.parkingMonitoringAndManagement.ParkingSpot;
import serverBL.parkingMonitoringAndManagement.ParkingSpotList;


public class WritingThread implements Runnable {
	public final int OPERATOR_PERMISSION = 2;
	public final int DRIVER_PERMISSION = 1;
	private MessagePassing server;
	private ObjectOutputStream oStream;
	private boolean stopRunning;
	private int permission;
	private Queue<RequestMessage> requestQueue;
	private Queue<ReplyMessage> replyQueue;
	private UserList userList;
	private ParkingSpotList parkingList;
	private GateAdapter gateAdapter;
	private String username;
	LinkedList<DriverRestrictedInterface> expiredDrivers;
	
	
	WritingThread(MessagePassing server, Socket socket, UserList userList, ParkingSpotList parkingList, GateAdapter gateAdapter) throws IOException {
		this.server = server;
		oStream = new ObjectOutputStream(socket.getOutputStream());
		stopRunning = false;
		this.userList = userList;
		this.parkingList = parkingList;
		requestQueue = new LinkedList<RequestMessage>();
		replyQueue = new LinkedList<ReplyMessage>();
		this.gateAdapter = gateAdapter;
		permission = 0;
		username = null;
		expiredDrivers = null;
	}
	
	private ReplyMessage login(RequestMessage request) {
		ReplyMessage reply = new ReplyMessage();
		reply.type = request.type;
		UserRestrictedInterface user = userList.getUser(request.stringField1);
		if (user == null) {
			reply.reply = Reply.USERNAME_NOT_FOUND;
			return reply;
		}
		int permission = user.checkPassword(request.stringField2);
		if (permission == 0) {
			reply.reply = Reply.WRONG_PW;
			return reply;
		}
		username = user.getUserName();
		this.permission = permission >= OPERATOR_PERMISSION ? OPERATOR_PERMISSION : permission;
		reply.reply = Reply.SUCCESS;
		reply.stringField = username;
		reply.intField = this.permission;
		return reply;
	}
	
	private ReplyMessage logout(RequestMessage request) {
		ReplyMessage reply = new ReplyMessage();
		reply.type = request.type;
		if (permission == 0) {
			reply.reply = Reply.INSUFFICIENT_PRIVILEDGE;
			return reply;
		}
		reply.reply = Reply.SUCCESS;
		reply.intField = 0;
		return reply;
	}
	
	private ReplyMessage openGate(RequestMessage request) {
		ReplyMessage reply = new ReplyMessage();
		reply.type = request.type;
		if (permission < OPERATOR_PERMISSION) {
			reply.reply = Reply.INSUFFICIENT_PRIVILEDGE;
			return reply;
		}
		reply.reply = Reply.SUCCESS;
		gateAdapter.openGate();
		return reply;
	}
	
	private ReplyMessage getParkingStatus(RequestMessage request) {
		ReplyMessage reply = new ReplyMessage();
		reply.type = request.type;
		if (permission == 0) {
			reply.reply = Reply.INSUFFICIENT_PRIVILEDGE;
			return reply;
		}
		if (parkingList.getParkingSpotStatus(request.intField) == Reply.ID_NOT_FOUND) {
			reply.reply = Reply.ID_NOT_FOUND;
			return reply;
		}
		ParkingSpot spot = parkingList.getParkingSpot(request.intField);
		reply.intField = spot.getID();
		reply.stringField = spot.getReservedFor();
		reply.boolField1 = spot.isOccupied();
		reply.boolField2 = spot.isReserved();
		reply.reply = Reply.SUCCESS;
		return reply;
	}
	
	private ReplyMessage changePW(RequestMessage request) {
		ReplyMessage reply = new ReplyMessage();
		reply.type = request.type;
		if (permission == 0) {
			reply.reply = Reply.INSUFFICIENT_PRIVILEDGE;
			return reply;
		}
		UserRestrictedInterface user = userList.getUser(request.stringField1);
		if (user == null) {
			reply.reply = Reply.USERNAME_NOT_FOUND;
			return reply;
		}
		if (request.stringField1 != null && request.stringField2 != null && (username.equals(request.stringField1) || permission > user.getPermission())) {
			userList.changePassword(request.stringField1, request.stringField2);
			reply.reply = Reply.SUCCESS;
			return reply;
		}
		reply.reply = Reply.INSUFFICIENT_PRIVILEDGE;
		return reply;
	}
	
	private ReplyMessage reserveParkingSpot(RequestMessage request) {
		ReplyMessage reply = new ReplyMessage();
		reply.type = request.type;
		if (permission < OPERATOR_PERMISSION) {
			reply.reply = Reply.INSUFFICIENT_PRIVILEDGE;
			return reply;
		}
		reply.reply = parkingList.reserveParkingSpot(request.intField, request.stringField1, request.dateField);
		return reply;
	}
	
	private ReplyMessage addDriver(RequestMessage request) {
		ReplyMessage reply = new ReplyMessage();
		reply.type = request.type;
		if (permission < OPERATOR_PERMISSION) {
			reply.reply = Reply.INSUFFICIENT_PRIVILEDGE;
			return reply;
		}
		if (request.dateField != null && request.dateField.before(new Date())) {
			reply.reply = Reply.DATE_EXPIRED;
			return reply;
		}
		String[] name = request.stringField1.split(" ");
		UsernameAndCardKeyPair pair = userList.addDriver(name[0], name[1], request.stringField2, DRIVER_PERMISSION, request.dateField);
		reply.reply = Reply.SUCCESS;
		reply.intField = pair.cardKey;
		reply.stringField = pair.username;
		return reply;
	}
	
	private ReplyMessage removeDriver(RequestMessage request) {
		ReplyMessage reply = new ReplyMessage();
		reply.type = request.type;
		if (permission < OPERATOR_PERMISSION) {
			reply.reply = Reply.INSUFFICIENT_PRIVILEDGE;
			return reply;
		}
		reply.reply = userList.removeDriver(request.stringField1);
		return reply;
	}
	
	private ReplyMessage cancelReservation(RequestMessage request) {
		ReplyMessage reply = new ReplyMessage();
		reply.type = request.type;
		if (permission < OPERATOR_PERMISSION) {
			reply.reply = Reply.INSUFFICIENT_PRIVILEDGE;
			return reply;
		}
		reply.reply = parkingList.freeReservation(request.intField);
		return reply;
	}
	
	private ReplyMessage changeExpiration(RequestMessage request) {
		ReplyMessage reply = new ReplyMessage();
		reply.type = request.type;
		if (permission < OPERATOR_PERMISSION) {
			reply.reply = Reply.INSUFFICIENT_PRIVILEDGE;
			return reply;
		}
		reply.reply = userList.changeExpirationDate(request.stringField1, request.dateField);
		return reply;
	}
	
	private ReplyMessage getNextExpiredUser(RequestMessage request) {
		ReplyMessage reply = new ReplyMessage();
		reply.type = request.type;
		if (permission < OPERATOR_PERMISSION) {
			reply.reply = Reply.INSUFFICIENT_PRIVILEDGE;
			return reply;
		}
		
		LinkedList<UserRestrictedInterface> users;
		if (expiredDrivers == null) {
			expiredDrivers = new LinkedList<DriverRestrictedInterface>();
			users = userList.getAllUsers();
			for (UserRestrictedInterface user : users) // copy only the expired drivers
				if (user instanceof DriverRestrictedInterface && ((DriverRestrictedInterface)user).getExpirationDate() != null && ((DriverRestrictedInterface)user).getExpirationDate().before(new Date()))
					expiredDrivers.add((DriverRestrictedInterface)user);	
		}
		if (expiredDrivers.isEmpty()) {
			expiredDrivers = null;
			reply.reply = Reply.USERNAME_NOT_FOUND; // signal there are no more expired drivers
			return reply;
		}
		reply.reply = Reply.SUCCESS;
		DriverRestrictedInterface driver = expiredDrivers.remove();
		reply.stringField = driver.getUserName();
		reply.dateField = driver.getExpirationDate();
		return reply;
	}
	
	void synchronizedAddToReplyQueue(ReplyMessage message) {
		synchronized(replyQueue) {
			replyQueue.add(message);
		}
		
		synchronized(this) {
			notify();
		}
	}
	
	void synchronizedAddToRequestQueue(RequestMessage message) {
		synchronized(requestQueue) {
			requestQueue.add(message);
		}
		
		synchronized(this) {
			notify();
		}
	}
	
	@Override
	public void run() {
		ReplyMessage reply;
		RequestMessage request;
		
		while (!stopRunning) {
			if (!replyQueue.isEmpty()) {
				synchronized(replyQueue) {
					reply = replyQueue.poll();
				}
				
				try {
					oStream.reset();
					oStream.writeObject(reply);
				} catch (IOException e) {
					stopRunning = true;
					ExceptionsLogger.logException(e.getMessage());
				}
			} else if (!requestQueue.isEmpty()) {
				synchronized(requestQueue) {
					request = requestQueue.poll();
				}
				reply = handleMessage(request);
				
				try {
					oStream.reset();
					oStream.writeObject(reply);
					if (reply.type == RequestType.LOGOUT) {
						oStream.flush();
						stopRunning = true;
					}
				} catch (IOException e) {
					stopRunning = true;
					ExceptionsLogger.logException(e.getMessage());
				}
			} else {
				synchronized(this) {
					/*
					 * This looks redundant, but there is a tiny chance 
					 * that the thread will wait forever without it.
					 */
					if (!stopRunning && replyQueue.isEmpty() && requestQueue.isEmpty()) { 
						try {
							wait();
						} catch (InterruptedException e) {
							ExceptionsLogger.logException(e.getMessage());
						}
					}
				}
			}
		}
		
		try {
			oStream.close(); // This will also close the socket (which will close the input stream).
			server.removeWritingThread(this);
		} catch (IOException e) {
			ExceptionsLogger.logException(e.getMessage());
		}
	}
	
	synchronized void disconnect() {
		stopRunning = true;
		notify();
	}
	
	int getPermission() {
		return permission;
	}
	
	private ReplyMessage handleMessage(RequestMessage request) {
		switch (request.type) {
		case LOGIN:
			return login(request);
		case LOGOUT:
			return logout(request);
		case OPEN_GATE:
			return openGate(request);
		case GET_PARKING_STATUS:
			return getParkingStatus(request);
		case CHANGE_PW:
			return changePW(request);
		case RESERVE_PARKING_SPOT:
			return reserveParkingSpot(request);
		case ADD_DRIVER:
			return addDriver(request);
		case REMOVE_DRIVER:
			return removeDriver(request);
		case CANCEL_RESERVATION:
			return cancelReservation(request);
		case CHANGE_EXPIRATION:
			return changeExpiration(request);
		case GET_NEXT_EXPIRED:
			return getNextExpiredUser(request);
		default:
			return null; // should never get here
		}
	}

}
