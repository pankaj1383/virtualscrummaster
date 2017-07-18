package com.aws.lambda.schedular;

import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.aws.lambda.rally.IScrumTool;
import com.aws.lambda.rally.RallyService;
import com.aws.lambda.rally.pojo.User;
import com.aws.lambda.rally.pojo.UsersResponse;
import com.aws.lambda.schedular.dynamodb.DBHelper;
import com.aws.lambda.schedular.dynamodb.DBHelperImpl;
import com.aws.lambda.schedular.dynamodb.pojo.DBUserInfo;
import com.aws.lambda.schedular.services.EmailService;
import com.aws.lambda.schedular.services.EmailServiceImpl;
import com.aws.lambda.schedular.utils.Logger;

public class DBSchedular implements RequestHandler<Object, String> {

	private DBHelper dbHelper = null;
	private EmailService emailService = null;

	private IScrumTool rallyService;

	private void init() {
		Logger.log("Initilializing lambda");

		rallyService = new RallyService();
		emailService = new EmailServiceImpl();
		dbHelper = new DBHelperImpl();
	}

	public String handleRequest(Object input, Context context) {

		init();

		Logger.log("Input: " + input);

		try {
			checkForNewUserOnRally();

			Logger.log("DBSchedular.handleRequest executed!");

			return "DBSchedular.handleRequest executed!";
		} catch (Exception e) {
			Logger.log("Exception occured in DBSchedular.handleRequest: " + e.getMessage());

			return "Exception occured in DBSchedular.handleRequest: " + e.getMessage();
		} finally {
			context.getLogger().log(
					"------------------------------------ [START] DBSchedular Logs [START] ------------------------------------\n");
			context.getLogger().log(Logger.getLogs());
			context.getLogger().log(
					"\n------------------------------------ [END] DBSchedular Logs [END]------------------------------------\n");

			Logger.clearLogs();
		}
	}

	private void checkForNewUserOnRally() throws Exception {
		Logger.log("Looking for new added customers on Rally...");

		Map<String, DBUserInfo> dynamoDBUsers = dbHelper.getAllUsers();

		Logger.log("dynamoDBUsers " + dynamoDBUsers.size());

		UsersResponse response = (UsersResponse) rallyService.getAllRallyUsers();

		List<User> rallyUsers = response.getUsers();

		if (rallyUsers != null) {

			for (User rallyUser : rallyUsers) {

				if ((dynamoDBUsers == null) || (!dynamoDBUsers.containsKey(rallyUser.getEmailAddress()))) {

					createNewUserInDynamoDB(rallyUser);

				} else {

					DBUserInfo existingUser = (DBUserInfo) dynamoDBUsers.get(rallyUser.getEmailAddress());

					if ((existingUser.getUserStatus() == null)
							|| (DBUserInfo.STATUS_USER_CREATED.equals(existingUser.getUserStatus()))) {
						try {
							existingUser.setActivationKey(generateActivationKey());

							boolean result = emailService.sendMailUsingSendGrid(existingUser);

							if (result) {
								existingUser.setUserStatus(DBUserInfo.STATUS_USER_READY_FOR_ACTIVATION);
								dbHelper.saveUser(existingUser);
							}
						} catch (Exception e) {
							Logger.log(

									"Exception occured in checkForNewUserOnRally while sending activation email to user: "
											+ e.getMessage());
						}
					}
				}
			}
		}

		Logger.log("Processing completed...");
	}

	private void createNewUserInDynamoDB(User rallyUser) {
		try {
			DBUserInfo dbUser = new DBUserInfo();

			dbUser.setActivationKey(generateActivationKey());
			dbUser.setEmail(rallyUser.getEmailAddress());
			dbUser.setUserStatus(DBUserInfo.STATUS_USER_CREATED);
			
			if (rallyUser.getDisplayName() != null) {
				dbUser.setName(rallyUser.getDisplayName());
			} else {
				dbUser.setName(rallyUser.getFirstName() + " " + rallyUser.getLastName());
			}

			boolean result = emailService.sendMailUsingSendGrid(dbUser);

			if (result) {
				dbUser.setUserStatus(DBUserInfo.STATUS_USER_READY_FOR_ACTIVATION);
			}

			dbHelper.saveUser(dbUser);
		} catch (Exception e) {
			Logger.log("Exception occured createUserInDynamoDB while adding new user: " + e.getMessage());
		}
	}

	private String generateActivationKey() {
		return "" + System.currentTimeMillis();
	}
}
