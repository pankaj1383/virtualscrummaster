package com.aws.lambda.taskupdaterhook.scrum.tool.versionone.pojo;
/**
 * 
 * @author raja.pateriya
 *
 */
public class V1User {

	private String DisplayName;
	private String UserName;
	private String EmailAddress;
	private String FirstName;
	private String LastName;
	private String MiddleName;
	private String _refObjectName;
	public String get_refObjectName() {
		return _refObjectName;
	}
	public void set_refObjectName(String _refObjectName) {
		this._refObjectName = _refObjectName;
	}
	public String getDisplayName() {
		return DisplayName;
	}
	public void setDisplayName(String displayName) {
		DisplayName = displayName;
	}
	public String getUserName() {
		return UserName;
	}
	public void setUserName(String userName) {
		UserName = userName;
	}
	public String getEmailAddress() {
		return EmailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		EmailAddress = emailAddress;
	}
	public String getFirstName() {
		return FirstName;
	}
	public void setFirstName(String firstName) {
		FirstName = firstName;
	}
	public String getLastName() {
		return LastName;
	}
	public void setLastName(String lastName) {
		LastName = lastName;
	}
	public String getMiddleName() {
		return MiddleName;
	}
	public void setMiddleName(String middleName) {
		MiddleName = middleName;
	}
	
	
}
