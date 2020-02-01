package serverBL.authorizedUsersManagement;


public class UsernameGenerator {
	private String firstName;
	private String lastName;
	/* 
	 * rule1: lastName + firstName[0..idx],  0 <= idx < firstName.length - 1  
	 * rule2: firstName + lastName[0..idx],  0 <= idx < lastName.length - 1
	 * rule3: lastName[0..idx] + firstName,  0 <= idx < lastName.length - 1
	 * rule4: firstName[0..idx] + lastName,  0 <= idx < firstName.length - 1
	 */
	private int lastNameIdx;
	private int firstNameIdx;
	private int rule;
	private int suffix;
	
	public UsernameGenerator(String firstName, String lastName) {
		this.firstName = firstName.toLowerCase();
		this.lastName = lastName.toLowerCase();
		lastNameIdx = 0;
		firstNameIdx = 0;
		rule = 0;
		suffix = 0;
	}
	
	public String getNextUsername() {
		String username = null;
		
		if (firstNameIdx >= firstName.length() - 1 && lastNameIdx >= lastName.length() - 1) {
			suffix++;
			firstNameIdx = 0;
			lastNameIdx = 0;
		}
		if (firstNameIdx >= firstName.length() - 1 && (rule == 0 || rule == 3)) 
			rule = 1;
		if (lastNameIdx >= lastName.length() - 1 && (rule == 1 || rule == 2)) 
			rule = 3;
		
		
		switch (rule) {
		case 0:
			username = lastName + firstName.substring(0, firstNameIdx + 1);
			break;
		case 1:
			username =  firstName + lastName.substring(0, lastNameIdx + 1);
			break;
		case 2:
			username = lastName.substring(0, lastNameIdx + 1) + firstName;
			lastNameIdx++;
			break;
		case 3:
			username = firstName.substring(0, firstNameIdx + 1) + lastName;
			firstNameIdx++;
			break;
		}
		
		rule++;
		if (rule == 4)
			rule = 0;
		
		if (suffix > 0)
			username = username + suffix;
		return username;
	}
	
}
