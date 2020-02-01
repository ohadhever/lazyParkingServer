package serverBL.messagePassing;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import adminUI.ExceptionsLogger;
import common.RequestMessage;
import common.RequestType;


public class ReadingThread implements Runnable {
	private ObjectInputStream iStream;
	private boolean stopRunning;
	private WritingThread wThread;
	
	ReadingThread(MessagePassing server, Socket socket, 
			WritingThread wThread) throws IOException {
		
		iStream = new ObjectInputStream(socket.getInputStream());
		stopRunning = false;
		this.wThread = wThread;
	}
	
	public void run() {
		RequestMessage request = null;
		while (!stopRunning) {
			try {
				request = (RequestMessage) iStream.readObject();
				wThread.synchronizedAddToRequestQueue(request);
				if (request.type == RequestType.LOGOUT)
					stopRunning = true;
			} catch (Exception e) {
				stopRunning = true;
				ExceptionsLogger.logException(e.getMessage());
			}
		}
	}

}
