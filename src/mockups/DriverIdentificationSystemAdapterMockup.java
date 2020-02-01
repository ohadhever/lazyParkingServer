package mockups;

import serverBL.authorizedUsersManagement.DriverIdentificationSystemAdapter;
import serverBL.authorizedUsersManagement.GateAdapter;
import serverBL.authorizedUsersManagement.UserList;

public class DriverIdentificationSystemAdapterMockup extends DriverIdentificationSystemAdapter {

	public DriverIdentificationSystemAdapterMockup(UserList userList, GateAdapter entranceGate, GateAdapter exitGate) {
		super(userList, entranceGate, exitGate);
	}

}
