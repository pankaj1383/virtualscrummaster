package com.aws.lambda.userregistration.dynamoDB.pojo;

public interface DBUser {

	public String getEmail();

	public void setEmail(String email);

	public String getName();

	public void setName(String name);

	public String getActivationKey();

	public void setActivationKey(String activationKey);

	public String getRallyAPIKey();

	public void setRallyAPIKey(String rallyAPIKey);

	public String getSocialId();

	public void setSocialId(String socialId);

	public String getUserStatus();

	public void setUserStatus(String userStatus);

	public boolean isReadyToUse();
}
