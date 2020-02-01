package serverBL.authorizedUsersManagement;

import java.util.Date;

public class Driver extends User {
	private int cardKey;
	private boolean isPresent;
	private Date expirationDate;
	
	public Driver(String userName, String password, int permission, int cardKey, Date expirationDate) {
		super(userName, password, permission);
		this.cardKey = cardKey;
		this.isPresent = false;
		this.expirationDate = expirationDate;
	}
	
	public Driver(String userName, byte[] hashedPassword, int permission, int cardKey, Date expirationDate) {
		super(userName, hashedPassword, permission);
		this.cardKey = cardKey;
		this.isPresent = false;
		this.expirationDate = expirationDate;
	}

	public int getCardKey() {
		return cardKey;
	}

	public boolean isPresent() {
		return isPresent;
	}

	public void setPresent(boolean isPresent) {
		this.isPresent = isPresent;
	}

	public Date getExpirationDate() {
		if (expirationDate != null)
			return (Date)expirationDate.clone();
		return null;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!other.getClass().equals(getClass()))
			return false;
		Driver otherDriver = (Driver)other;
		if (!super.equals(otherDriver))
			return false;
		if (cardKey != otherDriver.cardKey)
			return false;
		if ((expirationDate == null && otherDriver.expirationDate != null) || expirationDate != null && otherDriver.expirationDate == null)
			return false;
		if (expirationDate == null && otherDriver.expirationDate == null)
			return true;
		return expirationDate.equals(otherDriver.expirationDate);
	}
	
}
