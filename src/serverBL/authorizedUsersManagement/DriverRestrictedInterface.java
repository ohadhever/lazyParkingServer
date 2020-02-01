package serverBL.authorizedUsersManagement;

import java.util.Date;

public class DriverRestrictedInterface extends UserRestrictedInterface {
	private Driver driver;
	
	public DriverRestrictedInterface(Driver driver) throws IllegalArgumentException {
		super(driver);
		if (driver == null)
			throw new IllegalArgumentException("driver must not be null");
		this.driver = driver;
	}
	
	public int getCardKey() {
		return driver.getCardKey();
	}

	public boolean isPresent() {
		return driver.isPresent();
	}

	public void setPresent(boolean isPresent) {
		driver.setPresent(isPresent);;
	}

	public Date getExpirationDate() {
		return driver.getExpirationDate();
	}
}
