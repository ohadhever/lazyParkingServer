package serverBL.authorizedUsersManagement;

import java.util.Date;

public abstract class DriverIdentificationSystemAdapter {
	private UserList userList;
	private GateAdapter entranceGate;
	private GateAdapter exitGate;
	
	public DriverIdentificationSystemAdapter(UserList userList, GateAdapter entranceGate, GateAdapter exitGate) {
		this.userList = userList;
		this.entranceGate = entranceGate;
		this.exitGate = exitGate;
	}
	
	public boolean driverVerification(String username, int cardKey) {
		UserRestrictedInterface user = userList.getUser(username);
		if (user == null || !(user instanceof DriverRestrictedInterface))
			return false;
		
		DriverRestrictedInterface driver = (DriverRestrictedInterface)user;
		if (driver.getCardKey() != cardKey || driver.isPresent() || (driver.getExpirationDate() != null && driver.getExpirationDate().before(new Date())))
			return false;
		
		entranceGate.openGate();
		driver.setPresent(true);
		return true;
	}
	
	public void driverExit(String username, int cardKey) {
		DriverRestrictedInterface driver = (DriverRestrictedInterface)userList.getUser(username);
		if (driver != null)
			driver.setPresent(false);
		
		exitGate.openGate();
	}

}
