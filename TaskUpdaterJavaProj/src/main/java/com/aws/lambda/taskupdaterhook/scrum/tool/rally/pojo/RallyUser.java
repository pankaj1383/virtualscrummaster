package com.aws.lambda.taskupdaterhook.scrum.tool.rally.pojo;

import com.aws.lambda.taskupdaterhook.scrum.pojo.User;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author raja.pateriya
 *
 */
public class RallyUser implements User{

	private String displayName;
	private String userName;
	private String emailAddress;
	private String firstName;
	private String lastName;
	private String middleName;
	private String _referenceID;
	
	
	@JsonProperty("DisplayName")
	public String getDisplayName() {
		return displayName;
	}
	@JsonProperty("DisplayName")
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	@JsonProperty("UserName")
	public String getUserName() {
		return userName;
	}
	
	@JsonProperty("UserName")
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@JsonProperty("EmailAddress")
	public String getEmailAddress() {
		return emailAddress;
	}
	
	@JsonProperty("EmailAddress")
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	@JsonProperty("FirstName")
	public String getFirstName() {
		return firstName;
	}
	
	@JsonProperty("FirstName")
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	@JsonProperty("LastName")
	public String getLastName() {
		return lastName;
	}
	
	@JsonProperty("LastName")
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	@JsonProperty("MiddleName")
	public String getMiddleName() {
		return middleName;
	}
	
	@JsonProperty("MiddleName")
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	
	@JsonProperty("_refObjecName")
	public String get_referenceID() {
		return _referenceID;
	}
	
	@JsonProperty("_refObjecName")
	public void set_referenceID(String _referenceID) {
		this._referenceID = _referenceID;
	}

	
	
	
}
