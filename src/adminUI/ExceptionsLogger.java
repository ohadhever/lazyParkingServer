package adminUI;

import java.util.LinkedList;
import java.util.Queue;

public class ExceptionsLogger {
	private static AdminUIFrame ui = null;
	private static Queue<String> exceptionsQueue = new LinkedList<String>();
	
	static void adminLoggedIn(AdminUIFrame ui) {
		ExceptionsLogger.ui = ui;
		synchronized(exceptionsQueue) {
			while (!exceptionsQueue.isEmpty())
				ui.displayException(exceptionsQueue.poll());
		}
	}
	
	public static void logException(String exceptionMsg) {
		if (ui == null)
			synchronized(exceptionsQueue) {
				exceptionsQueue.add(exceptionMsg);
			}
		else
			synchronized(ui) {
				ui.displayException(exceptionMsg);
			}
	}
	
	static void adminLoggedOut() {
		ui = null;
	}
}
