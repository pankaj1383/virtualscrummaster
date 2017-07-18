package com.aws.lambda.taskupdaterhook.scrum.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 
 * @author raja.pateriya
 * 
 */
@JsonInclude(Include.NON_NULL)
public interface User {

	public String getDisplayName();

	public void setDisplayName(String displayName);

	public String getUserName();

	public String get_referenceID();

	public void set_referenceID(String _referenceID);

	public void setUserName(String userName);

	public String getEmailAddress();

	public void setEmailAddress(String emailAddress);

	public String getFirstName();

	public void setFirstName(String firstName);

	public String getLastName();

	public void setLastName(String lastName);

	public String getMiddleName();

	public void setMiddleName(String middleName);

}
