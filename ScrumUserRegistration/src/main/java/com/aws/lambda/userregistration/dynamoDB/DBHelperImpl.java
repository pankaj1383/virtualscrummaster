package com.aws.lambda.userregistration.dynamoDB;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.aws.lambda.userregistration.dynamoDB.pojo.DBUserInfo;
import com.aws.lambda.userregistration.utils.Logger;

/**
 * DB helper class which facilitates dynamo DB CRUD operations
 * 
 * @author pankaj.lole
 *
 */
public final class DBHelperImpl implements DBHelper {

	private AmazonDynamoDB ddb = null;
	private final String USER_TABLE = "users";

	/**
	 * 
	 * @return
	 */
	private AmazonDynamoDB getDBClient() throws Exception {
		if (ddb == null) {
			ddb = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
		}
		return ddb;
	}

	/**
	 * this method returns all the user from Dynamo DB table "users" who are
	 * ready to serve
	 * 
	 * @return
	 */
	public Map<String, DBUserInfo> getActiveUsers() throws Exception {
		return getDBUsers(true);
	}

	/**
	 * this method returns all the user from Dynamo DB table "users"
	 * 
	 * @return
	 */
	public Map<String, DBUserInfo> getAllUsers() throws Exception {
		return getDBUsers(false);
	}

	/**
	 * 
	 * @param onlyActiveUsers
	 * @return
	 * @throws Exception
	 */
	private Map<String, DBUserInfo> getDBUsers(boolean onlyActiveUsers) throws Exception {

		Logger.log("DBHelper.getAllUsers START");

		ScanRequest request = new ScanRequest();

		request.setTableName(USER_TABLE);
		ScanResult result = getDBClient().scan(request);

		List<Map<String, AttributeValue>> items = result.getItems();

		Map<String, DBUserInfo> users = null;

		if (null != items) {

			users = new HashMap<String, DBUserInfo>();

			for (Map<String, AttributeValue> map : items) {
				DBUserInfo user = getUserInfo(map);
				if (onlyActiveUsers) {
					if (user.isReadyToUse()) {
						users.put(user.getEmail(), user);
					}
				} else {
					users.put(user.getEmail(), user);
				}
			}
		}

		Logger.log("DBHelper.getAllUsers END");

		return users;
	}

	/**
	 * fetches the user from dynamo DB based upon given activationKey
	 * 
	 * @param value
	 * @return
	 */
	public DBUserInfo fetchUserByActivationKey(String activationKey) throws Exception {
		return searchUser(DBUserInfo.ACTIVATION_KEY, activationKey);
	}

	/**
	 * fetches the user from dynamo DB based upon given emailId
	 * 
	 * @param value
	 * @return
	 */
	public DBUserInfo fetchUserByEmailId(String emailId) throws Exception {
		return searchUser(DBUserInfo.EMAIL, emailId);
	}

	/**
	 * fetches the user from dynamo DB based upon given socialId
	 * 
	 * @param value
	 * @return
	 */
	public DBUserInfo fetchUserBySocialId(String socialId) throws Exception {
		return searchUser(DBUserInfo.SOCIAL_ID, socialId);
	}

	private DBUserInfo searchUser(String indexName, String value) throws Exception {

		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		eav.put(":val1", new AttributeValue().withS(value));

		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
				.withFilterExpression(indexName + " = :val1").withExpressionAttributeValues(eav);

		DynamoDBMapper mapper = new DynamoDBMapper(getDBClient());
		List<DBUserInfo> scanResult = mapper.scan(DBUserInfo.class, scanExpression);

		if (null != scanResult && scanResult.size() > 0) {
			return scanResult.get(0);
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param newUser
	 */
	public void saveUser(DBUserInfo user) throws Exception {

		if (!isValidUser(user)) {
			throw new IllegalArgumentException("Invalid user can't proceed..");
		}

		DBUserInfo existingUser = fetchUserByEmailId(user.getEmail());

		if (existingUser != null) {
			updateUser(user);
			return;
		}

		Map<String, AttributeValue> newItem = new HashMap<String, AttributeValue>();

		newItem.put(DBUserInfo.NAME, new AttributeValue().withS(user.getName()));

		if (user.getEmail() != null) {
			newItem.put(DBUserInfo.EMAIL, new AttributeValue().withS(user.getEmail()));
		}

		if (user.getActivationKey() != null) {
			newItem.put(DBUserInfo.ACTIVATION_KEY, new AttributeValue().withS(user.getActivationKey()));
		}

		if (user.getRallyAPIKey() != null) {
			newItem.put(DBUserInfo.RALLY_API_KEY, new AttributeValue().withS(user.getRallyAPIKey()));
		}

		if (user.getSocialId() != null) {
			newItem.put(DBUserInfo.SOCIAL_ID, new AttributeValue().withS(user.getSocialId()));
		}

		if (user.getUserStatus() != null) {
			newItem.put(DBUserInfo.USER_STATUS, new AttributeValue().withS(user.getUserStatus()));
		}

		getDBClient().putItem(new PutItemRequest().withTableName(USER_TABLE).withItem(newItem));
	}

	/**
	 * this method update the user information in Dynamo DB based upon hash key
	 * "email"
	 * 
	 * @param user
	 */
	private void updateUser(DBUserInfo user) throws Exception {

		if (!isValidUser(user)) {
			throw new IllegalArgumentException("Invalid user can't proceed..");
		}

		UpdateItemRequest update = new UpdateItemRequest();
		update.setTableName(USER_TABLE);

		Map<String, AttributeValue> keyToUpdate = new HashMap<String, AttributeValue>();
		keyToUpdate.put(DBUserInfo.EMAIL, new AttributeValue(user.getEmail()));
		update.setKey(keyToUpdate);

		Map<String, AttributeValueUpdate> items = new HashMap<String, AttributeValueUpdate>();

		if (user.getName() != null) {
			items.put(DBUserInfo.NAME,
					new AttributeValueUpdate().withValue(new AttributeValue().withS(user.getName())));
		}

		if (user.getActivationKey() != null) {
			items.put(DBUserInfo.ACTIVATION_KEY,
					new AttributeValueUpdate().withValue(new AttributeValue().withS(user.getActivationKey())));
		}

		if (user.getRallyAPIKey() != null) {
			items.put(DBUserInfo.RALLY_API_KEY,
					new AttributeValueUpdate().withValue(new AttributeValue().withS(user.getRallyAPIKey())));
		}

		if (user.getSocialId() != null) {
			items.put(DBUserInfo.SOCIAL_ID,
					new AttributeValueUpdate().withValue(new AttributeValue().withS(user.getSocialId())));
		}

		if (user.getUserStatus() != null) {
			items.put(DBUserInfo.USER_STATUS,
					new AttributeValueUpdate().withValue(new AttributeValue().withS(user.getUserStatus())));
		}

		update.setAttributeUpdates(items);

		getDBClient().updateItem(update);
	}

	/**
	 * deletes the user from dynamo DB
	 * 
	 * @param newUser
	 */
	public void deleteUser(DBUserInfo user) throws Exception {

		if (!isValidUser(user)) {
			throw new IllegalArgumentException("Invalid user can't proceed..");
		}

		deleteUserByEmail(user.getEmail());
	}

	/**
	 * deletes the user from dynamo DB via email Id
	 */
	public void deleteUserByEmail(String email) throws Exception {

		if (email == null || email.trim().length() == 0) {
			throw new IllegalArgumentException("Email cannot be null here..");
		}

		DeleteItemRequest delete = new DeleteItemRequest();
		delete.setTableName(USER_TABLE);

		Map<String, AttributeValue> keyToDelete = new HashMap<String, AttributeValue>();

		keyToDelete.put(DBUserInfo.EMAIL, new AttributeValue(email));
		delete.setKey(keyToDelete);

		getDBClient().deleteItem(delete);
	}

	/**
	 * 
	 * @param map
	 * @return
	 */
	private DBUserInfo getUserInfo(Map<String, AttributeValue> map) {

		DBUserInfo userInfo = null;

		if (map != null) {

			userInfo = new DBUserInfo();

			if (map.get(DBUserInfo.NAME) != null) {
				userInfo.setName(map.get(DBUserInfo.NAME).getS());
			}

			if (map.get(DBUserInfo.EMAIL) != null) {
				userInfo.setEmail(map.get(DBUserInfo.EMAIL).getS());
			}

			if (map.get(DBUserInfo.SOCIAL_ID) != null) {
				userInfo.setSocialId(map.get(DBUserInfo.SOCIAL_ID).getS());
			}

			if (map.get(DBUserInfo.ACTIVATION_KEY) != null) {
				userInfo.setActivationKey(map.get(DBUserInfo.ACTIVATION_KEY).getS());
			}

			if (map.get(DBUserInfo.RALLY_API_KEY) != null) {
				userInfo.setRallyAPIKey(map.get(DBUserInfo.RALLY_API_KEY).getS());
			}

			if (map.get(DBUserInfo.USER_STATUS) != null) {
				userInfo.setUserStatus(map.get(DBUserInfo.USER_STATUS).getS());
			}
		}

		return userInfo;
	}

	/**
	 * performs check whether this user is valid or not
	 * 
	 * @param user
	 * @return
	 */
	private boolean isValidUser(DBUserInfo user) {
		boolean isValid = true;

		if (user == null || user.getEmail() == null || user.getEmail().trim().length() == 0) {
			isValid = false;
		}

		return isValid;
	}
}
