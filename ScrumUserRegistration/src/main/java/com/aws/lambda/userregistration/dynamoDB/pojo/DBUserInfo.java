package com.aws.lambda.userregistration.dynamoDB.pojo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "users")
public class DBUserInfo implements DBUser {

	public static final String NAME = "name";
	public static final String EMAIL = "email";
	public static final String ACTIVATION_KEY = "activationKey";
	public static final String RALLY_API_KEY = "rallyAPIKey";
	public static final String SOCIAL_ID = "socialId";
	public static final String USER_STATUS = "userStatus";

	public static final String STATUS_USER_CREATED = "0";
	public static final String STATUS_USER_READY_FOR_ACTIVATION = "1";
	public static final String STATUS_USER_ACTIVATED = "2";
	public static final String STATUS_USER_API_KEY_EXPIRED = "3";
	public static final String STATUS_USER_DEACTIVATED = "4";

	private String name;
	private String email;
	private String activationKey;
	private String rallyAPIKey;
	private String socialId;
	private String userStatus;

	@DynamoDBHashKey(attributeName = "email")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@DynamoDBAttribute(attributeName = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@DynamoDBAttribute(attributeName = "activationKey")
	public String getActivationKey() {
		return activationKey;
	}

	public void setActivationKey(String activationKey) {
		this.activationKey = activationKey;
	}

	@DynamoDBAttribute(attributeName = "rallyAPIKey")
	public String getRallyAPIKey() {
		return rallyAPIKey;
	}

	public void setRallyAPIKey(String rallyAPIKey) {
		this.rallyAPIKey = rallyAPIKey;
	}

	@DynamoDBAttribute(attributeName = "socialId")
	public String getSocialId() {
		return socialId;
	}

	public void setSocialId(String socialId) {
		this.socialId = socialId;
	}

	@DynamoDBAttribute(attributeName = "userStatus")
	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	/**
	 * performs check whether this user is active & ready to serve or not
	 * 
	 * @return
	 */
	public boolean isReadyToUse() {

		return STATUS_USER_ACTIVATED.equalsIgnoreCase(getUserStatus());
	}

	@Override
	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("\nEmail: " + email);
		strBuffer.append("\nActivationKey: " + activationKey);
		strBuffer.append("\nRallyAPIKey: " + rallyAPIKey);
		strBuffer.append("\nSocialId: " + socialId);
		strBuffer.append("\nUserStatus: " + userStatus);

		return strBuffer.toString();
	}
}
