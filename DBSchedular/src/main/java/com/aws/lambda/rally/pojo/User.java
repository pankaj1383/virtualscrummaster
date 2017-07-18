package com.aws.lambda.rally.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract interface User {
	
	public abstract String getDisplayName();

	public abstract void setDisplayName(String paramString);

	public abstract String getUserName();

	public abstract String get_referenceID();

	public abstract void set_referenceID(String paramString);

	public abstract void setUserName(String paramString);

	public abstract String getEmailAddress();

	public abstract void setEmailAddress(String paramString);

	public abstract String getFirstName();

	public abstract void setFirstName(String paramString);

	public abstract String getLastName();

	public abstract void setLastName(String paramString);

	public abstract String getMiddleName();

	public abstract void setMiddleName(String paramString);
}
