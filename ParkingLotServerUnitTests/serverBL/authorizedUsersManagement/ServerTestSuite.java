package serverBL.authorizedUsersManagement;

import junit.framework.TestSuite;
import serverBL.parkingMonitoringAndManagement.ParkingSpotListTest;
import serverBL.parkingMonitoringAndManagement.ParkingSpotTest;

public class ServerTestSuite {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}
	public static TestSuite suite() {
		
		TestSuite suite = new TestSuite();
		
		suite.addTestSuite(DriverTest.class);
		suite.addTestSuite(UserListTest.class);
		suite.addTestSuite(UserTest.class);
		suite.addTestSuite(RandomAccessUsersStorageUnitTest.class);
		suite.addTestSuite(UserRestrictedInterfaceTest.class);
		suite.addTestSuite(DriverRestrictedInterfaceTest.class);
		suite.addTestSuite(ParkingSpotListTest.class);
		suite.addTestSuite(ParkingSpotTest.class);
		
		System.out.println("About to perform "+ suite.countTestCases() + " tests");
		
		return suite;
	}
}