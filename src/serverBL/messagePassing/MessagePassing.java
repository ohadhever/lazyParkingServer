package serverBL.messagePassing;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import adminUI.ExceptionsLogger;
import common.Reply;
import common.ReplyMessage;
import common.RequestType;
import serverBL.authorizedUsersManagement.GateAdapter;
import serverBL.authorizedUsersManagement.UserList;
import serverBL.parkingMonitoringAndManagement.ParkingSpotList;



public class MessagePassing extends Thread implements Runnable {
	/*
	 * The classes that are common to both the server and the client:
	 * RequestMessage, ReplyMessage, RequestType (Enum), Reply (Enum)
	 * are found in the common package
	 */
	public static final int SERVER_PORT = 7000;
	private ServerSocket serverSocket;
	private Vector<WritingThread> writingThreads;
	private boolean stopRunning;
	private UserList userList;
	private ParkingSpotList parkingList;
	private GateAdapter gateAddapter;
	
	public MessagePassing(UserList userList, ParkingSpotList parkingList, GateAdapter gateAddapter) throws IOException {
		serverSocket = new ServerSocket(SERVER_PORT);
		writingThreads = new Vector<WritingThread>();
		stopRunning = false;
		this.parkingList = parkingList;
		this.userList = userList;
		this.gateAddapter = gateAddapter;
		parkingList.setMessagePassing(this);
		start();
	}
	
	@Override
	public void run() {
		while (!stopRunning) {
			try {
				Socket socket = serverSocket.accept();
				WritingThread wThread = new WritingThread(this, socket, userList, parkingList, gateAddapter);
				ReadingThread rThread = new ReadingThread(this, socket, wThread);
				Thread t1 = new Thread(wThread);
				Thread t2 = new Thread(rThread);
				
				writingThreads.addElement(wThread);
				
				t1.start();
				t2.start();
				
			} catch (IOException e) {
				stopRunning = true;
				ExceptionsLogger.logException(e.getMessage());
			}
		}
	}
	
	void removeWritingThread(WritingThread wThread) {
		writingThreads.remove(wThread);
	}
	
	public void disconnect() {
		for (WritingThread wThread : writingThreads) {
			wThread.disconnect();
		}
		stopRunning = true;
		try {
			serverSocket.close();
		} catch (IOException e) {
			ExceptionsLogger.logException(e.getMessage());
		}
	}
	
	public void parkingSpotStatusUpdate(int id, boolean isOccupied, boolean isReserved, String reservedFor) {
		ReplyMessage reply = new ReplyMessage();
		reply.type = RequestType.GET_PARKING_STATUS;
		reply.reply = Reply.SUCCESS;
		reply.intField = id;
		reply.stringField = reservedFor;
		reply.boolField1 = isOccupied;
		reply.boolField2 = isReserved;
		for (WritingThread wThread : writingThreads)
			if (wThread.getPermission() > 0)
				wThread.synchronizedAddToReplyQueue(reply);
	}

}
