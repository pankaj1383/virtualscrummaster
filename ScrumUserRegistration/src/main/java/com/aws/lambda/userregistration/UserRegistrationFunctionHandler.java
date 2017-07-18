package com.aws.lambda.userregistration;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.aws.lambda.userregistration.dynamoDB.DBHelper;
import com.aws.lambda.userregistration.dynamoDB.DBHelperImpl;
import com.aws.lambda.userregistration.dynamoDB.pojo.DBUserInfo;
import com.aws.lambda.userregistration.exception.CJsonException;
import com.aws.lambda.userregistration.pojo.CurrentIntent;
import com.aws.lambda.userregistration.pojo.DialogAction;
import com.aws.lambda.userregistration.pojo.Message;
import com.aws.lambda.userregistration.scrum.IScrumTool;
import com.aws.lambda.userregistration.scrum.exception.ScrumException;
import com.aws.lambda.userregistration.scrum.tool.rally.Rally;
import com.aws.lambda.userregistration.utils.JsonUtility;
import com.aws.lambda.userregistration.utils.Logger;

public class UserRegistrationFunctionHandler implements RequestHandler<LexRequest, LexResponse> {

	private static final String ACTIVATION_KEY = "activationId";
	private static final String API_KEY = "apiKey";

	private static final String TASK_INFO_KEY = "taskInfo";
	private static final String EFFORT_DURATION_KEY = "effortDuration";
	private static final String EFFORT_REMAINING_KEY = "effortRemaining";
	private static final String TASK_STATUS_KEY = "taskStatus";

	private static final String TASK_UPDATER_INTENT = "UpdateTask";

	private DBHelper dbHelper;

	private static final String TASK_UPDATE_QUERY = "Hello %1$s, Do you want to update task efforts?";

	private static final String CONFIRMATION_MSG = "We are about to link your social account with the scrum tool api key you provided. Do you want to proceed?";
	private IScrumTool scrumInterface;

	@Override
	public LexResponse handleRequest(LexRequest input, Context context) {
		LexResponse response = null;

		try {
			Logger.log("---- UserRegistrationFunctionHandler AWS Java Lambda execution starts ----");

			scrumInterface = new Rally();

			String reqJsonStr = getRequestObjJson(input);
			Logger.log("in handleRequest method, request Json new impl: " + reqJsonStr);

			LexRequest requestObject = null;

			if (input instanceof LexRequest) {
				requestObject = (LexRequest) input;

				if (LexRequest.INVOC_SOURCE_FULFILLMENT.equalsIgnoreCase(requestObject.getInvocationSource())) {
					response = buildRespForFulfillment(requestObject);
				} else if (LexRequest.INVOC_SOURCE_VALIDATION_DIALOG
						.equalsIgnoreCase(requestObject.getInvocationSource())) {
					if (CurrentIntent.CONFIRMATION_STATUS_NONE
							.equalsIgnoreCase(requestObject.getCurrentIntent().getConfirmationStatus())) {

						Map<String, String> sessionAttrs = requestObject.getSessionAttributes();
						if (null == sessionAttrs) {
							sessionAttrs = new HashMap<String, String>();
							requestObject.setSessionAttributes(sessionAttrs);
						}

						response = buildRespForValidation(requestObject);
					} else if (CurrentIntent.CONFIRMATION_STATUS_CONFIRMED
							.equalsIgnoreCase(requestObject.getCurrentIntent().getConfirmationStatus())
							|| CurrentIntent.CONFIRMATION_STATUS_DENIED
									.equalsIgnoreCase(requestObject.getCurrentIntent().getConfirmationStatus())) {
						response = buildRespForConfirmation(requestObject);
					}
				}
			}
			// TODO: implement your handler

			String responseJsonStr = getResponseObjJson(response);
			Logger.log("in handleRequest method, response Json new impl: " + responseJsonStr);

		} catch (Exception e) {

			Logger.log("Unhandled exception: " + e);

		} finally {

			Logger.log("---- UserRegistrationFunctionHandler AWS Java Lambda execution ends ----");
			context.getLogger().log(Logger.getLogs());
			Logger.clearLogs();
		}

		return response;
	}

	private LexResponse getTaskUpdaterIntentResponse(String message, LexRequest reqObject) {

		// Not sure if elicitIntent will work here so trying ElicitSlot 1st as
		// it ask for intentName & slot
		LexResponse response = getLexResponseForElicitSlot(TASK_INFO_KEY, message, reqObject, TASK_UPDATER_INTENT);

		// Map<String, String> sessionAttrs = reqObject.getSessionAttributes();
		// Map<String, String> sessionAttrs = response.getSessionAttributes();
		// sessionAttrs.put(USER_SOCIAL_ID, reqObject.getUserId());
		// response.setSessionAttributes(sessionAttrs);

		// DialogAction dialogAction = new DialogAction();
		// response.setDialogAction(dialogAction);
		//
		// dialogAction.setType(DialogAction.DIALOG_TYPE_ELICIT_SLOT);
		// if (null != message) {
		// Message msgObj = new Message();
		// msgObj.setContentType(Message.CONTENT_TYPE_TEXT);
		// msgObj.setContent(message);
		// dialogAction.setMessage(msgObj);
		// }
		//
		// dialogAction.setSlots(reqObject.getCurrentIntent().getSlots());

		return response;
	}

	private DBUserInfo getUserBySocialId(LexRequest request) {
		DBUserInfo dbUser = null;
		DBHelper dbHelperObj = getDBHelper();
		try {
			dbUser = dbHelperObj.fetchUserBySocialId(request.getUserId());
		} catch (Exception e) {
			Logger.log("error in getUserBySocialId method: " + e);
		}
		return dbUser;
	}

	private String getRequestObjJson(LexRequest input) {

		JsonUtility jsonUtil = new JsonUtility();
		String retVal = "";

		try {
			retVal = jsonUtil.getJsonFromRequest(input);
		} catch (CJsonException e) {
			Logger.log("error in getRequestObjJson method: " + e);
		}

		return retVal;
	}

	private String getResponseObjJson(LexResponse input) {

		JsonUtility jsonUtil = new JsonUtility();
		String retVal = "";

		try {
			retVal = jsonUtil.getJsonFromRespone(input);
		} catch (CJsonException e) {
			Logger.log("error in getResponseObjJson method: " + e);
		}

		return retVal;
	}

	private DBHelper getDBHelper() {
		if (null == dbHelper) {
			dbHelper = new DBHelperImpl();
		}

		return dbHelper;
	}

	private LexResponse buildRespForFulfillment(LexRequest requestObject) {
		Logger.log("in buildRespForFulfillment method");
		// 1. update dynamodb user object found by activationId with fbId,
		// apiKey and mark status as active
		// 2. set fb id against USER_SOCIAL_ID in sessionAttributes
		// 3. ElicitIntent TaskUpdater

		LexResponse response = new LexResponse();
		DialogAction dialogAction = new DialogAction();
		response.setDialogAction(dialogAction);
		response.setSessionAttributes(new HashMap<String, String>());

		dialogAction.setType(DialogAction.DIALOG_TYPE_CLOSE);
		Message msg = new Message();
		msg.setContentType(Message.CONTENT_TYPE_TEXT);
		dialogAction.setMessage(msg);

		if (validMatchForFulfillment(requestObject)) {
			// update db user
			DBUserInfo dbUser = null;
			try {
				dbUser = getDBHelper()
						.fetchUserByActivationKey(requestObject.getSessionAttributes().get(ACTIVATION_KEY));
			} catch (Exception e) {
				Logger.log("Error fetching User by activatin key in buildRespForFulfillment method= " + e);
			}

			if (null != dbUser) {
				dbUser.setSocialId(requestObject.getUserId());
				dbUser.setRallyAPIKey(requestObject.getSessionAttributes().get(API_KEY));
				dbUser.setUserStatus(DBUserInfo.STATUS_USER_ACTIVATED);
				try {
					getDBHelper().saveUser(dbUser);
					msg.setContent("Great, I just enrolled you! Do you want me to update your task?");
					dialogAction.setFulfillmentState(DialogAction.FULFILLMENT_STATE_FULFILLED);
				} catch (Exception e) {
					msg.setContent(
							"Oh sorry, I could not enroll you at this moment. I am feeling bad about it. Please connect back after sometime");
					dialogAction.setFulfillmentState(DialogAction.FULFILLMENT_STATE_FULFILLED);
					Logger.log("Error updating User by activatin key in buildRespForFulfillment method= " + e);
				}

			} else {
				msg.setContent(
						"Oh sorry, I could not enroll you at this moment. I am feeling bad about it. Please connect back after sometime");
				dialogAction.setFulfillmentState(DialogAction.FULFILLMENT_STATE_FAILED);
			}
		} else {
			msg.setContent("I could not enroll you with your changed inputs");
			dialogAction.setFulfillmentState(DialogAction.FULFILLMENT_STATE_FAILED);
		}

		return response;
	}

	private LexResponse buildRespForConfirmation(LexRequest requestObject) {
		// 1. form confirmation string and prompt for confirmation

		Logger.log("in buildRespForConfirmation method");
		LexResponse response = new LexResponse();
		response.setSessionAttributes(requestObject.getSessionAttributes());
		DialogAction dialogAction = new DialogAction();
		response.setDialogAction(dialogAction);
		dialogAction.setType(DialogAction.DIALOG_TYPE_CONFIRM_INTENT);
		Message msg = new Message();

		msg.setContentType(Message.CONTENT_TYPE_TEXT);
		msg.setContent(CONFIRMATION_MSG);
		dialogAction.setMessage(msg);
		// dialogAction.setIntentName(requestObject.getCurrentIntent().getName());
		dialogAction.setSlots(requestObject.getCurrentIntent().getSlots());
		dialogAction.setIntentName(requestObject.getCurrentIntent().getName());

		return response;
	}

	private LexResponse buildRespForValidation(LexRequest requestObject) {
		Logger.log("in buildRespForValidation method");
		LexResponse response = null;

		DBUserInfo dbUserObj = getUserBySocialId(requestObject);
		String activationStatus = null != dbUserObj ? dbUserObj.getUserStatus() : "Unknown";
		String dbActivationKey = null != dbUserObj ? dbUserObj.getActivationKey() : null;

		// if already active user then prompt for tasks
		if (activationStatus.equalsIgnoreCase(DBUserInfo.STATUS_USER_ACTIVATED)) {

			String msg = String.format(TASK_UPDATE_QUERY, dbUserObj.getName());

			response = getTaskUpdaterIntentResponse(msg, requestObject);

		} else if (activationStatus.equalsIgnoreCase(DBUserInfo.STATUS_USER_API_KEY_EXPIRED)
				|| null != requestObject.getCurrentIntent().getSlots()
				|| requestObject.getCurrentIntent().getSlots().size() > 0) {

			// set activation key in slot & session as this is one odd case when
			// api key got expired
			if (null != dbActivationKey && activationStatus.equalsIgnoreCase(DBUserInfo.STATUS_USER_API_KEY_EXPIRED)) {

				Map<String, String> slotsMap = requestObject.getCurrentIntent().getSlots();
				if (null == slotsMap) {
					slotsMap = new HashMap<String, String>();
					requestObject.getCurrentIntent().setSlots(slotsMap);
				}
				slotsMap.put(ACTIVATION_KEY, dbActivationKey);

				Map<String, String> sessionMap = requestObject.getSessionAttributes();
				if (null == sessionMap) {
					sessionMap = new HashMap<String, String>();
					requestObject.setSessionAttributes(sessionMap);
				}
				sessionMap.put(ACTIVATION_KEY, dbActivationKey);
			}

			response = validateSlotsInput(requestObject);
		} else {
			response = new LexResponse();
			DialogAction dialogAction = new DialogAction();
			response.setDialogAction(dialogAction);
			dialogAction.setType(DialogAction.DIALOG_TYPE_ELICIT_INTENT);
			response.setSessionAttributes(requestObject.getSessionAttributes());
		}

		return response;
	}

	private boolean validMatchForFulfillment(LexRequest requestObject) {

		boolean retVal = true;
		Map<String, String> reqSessionAttributes = requestObject.getSessionAttributes();

		String sessionActivationId = null != reqSessionAttributes ? reqSessionAttributes.get(ACTIVATION_KEY) : null;
		if (null == sessionActivationId) {
			return false;
		}

		String sessionApiKey = null != reqSessionAttributes ? reqSessionAttributes.get(API_KEY) : null;
		if (null == sessionApiKey) {
			return false;
		}

		return retVal;
	}

	private LexResponse validateSlotsInput(LexRequest requestObject) {

		Logger.log("in validateSlotsInput method");

		LexResponse response = null;

		response = validateActivationKeySlotInput(requestObject);
		// it indicates task Info slot needs validation and hence raised
		// elicitslot response. So raise immediately!
		if (null != response) {
			return response;
		}

		response = validateApiKeySlotInput(requestObject);
		// it indicates task status slot needs validation and hence raised
		// elicitslot response. So raise immediately!
		if (null != response) {
			return response;
		}

		// if it reaches here then create delegate response and return
		response = getLexResponseForDelegate(null, requestObject);
		return response;
	}

	private LexResponse getLexResponseForClose(String message, LexRequest reqObject, boolean isFulfilled) {
		LexResponse response = new LexResponse();
		DialogAction dialogAction = new DialogAction();
		response.setDialogAction(dialogAction);
		response.setSessionAttributes(new HashMap<String, String>());

		dialogAction.setType(DialogAction.DIALOG_TYPE_CLOSE);
		if (null != message) {
			Message msg = new Message();
			msg.setContentType(Message.CONTENT_TYPE_TEXT);
			msg.setContent(message);
			dialogAction.setMessage(msg);
		}

		String fulfillmentStr = isFulfilled ? DialogAction.FULFILLMENT_STATE_FULFILLED
				: DialogAction.FULFILLMENT_STATE_FAILED;
		dialogAction.setFulfillmentState(fulfillmentStr);

		return response;
	}

	private LexResponse getLexResponseForDelegate(String message, LexRequest reqObject) {
		Logger.log("in getLexResponseForDelegate method of UserRegistrationFunctionHandler class");
		LexResponse response = new LexResponse();
		response.setSessionAttributes(reqObject.getSessionAttributes());
		DialogAction dialogAction = new DialogAction();
		response.setDialogAction(dialogAction);

		dialogAction.setType(DialogAction.DIALOG_TYPE_DELEGATE);
		if (null != message) {
			Message msgObj = new Message();
			msgObj.setContentType(Message.CONTENT_TYPE_TEXT);
			msgObj.setContent(message);
			dialogAction.setMessage(msgObj);
		}
		dialogAction.setSlots(reqObject.getCurrentIntent().getSlots());

		return response;
	}

	private LexResponse getLexResponseForElicitSlot(String slotToElicit, String message, LexRequest reqObject) {
		return getLexResponseForElicitSlot(slotToElicit, message, reqObject, null);
	}

	private LexResponse getLexResponseForElicitSlot(String slotToElicit, String message, LexRequest reqObject,
			String intentName) {
		LexResponse response = new LexResponse();

		DialogAction dialogAction = new DialogAction();
		response.setDialogAction(dialogAction);

		dialogAction.setType(DialogAction.DIALOG_TYPE_ELICIT_SLOT);
		dialogAction.setSlotToElicit(slotToElicit);

		if (null != message) {
			Message msgObj = new Message();
			msgObj.setContentType(Message.CONTENT_TYPE_TEXT);
			msgObj.setContent(message);
			dialogAction.setMessage(msgObj);
		}

		if (null == intentName) {
			dialogAction.setIntentName(reqObject.getCurrentIntent().getName());
			dialogAction.setSlots(reqObject.getCurrentIntent().getSlots());
			response.setSessionAttributes(reqObject.getSessionAttributes());
		} else {
			dialogAction.setIntentName(intentName);
			if (intentName.equalsIgnoreCase(TASK_UPDATER_INTENT)) {

				Map<String, String> sessionAttr = new HashMap<String, String>();
				response.setSessionAttributes(sessionAttr);

				Map<String, String> slots = new HashMap<String, String>();
				slots.put(TASK_INFO_KEY, null);
				slots.put(TASK_STATUS_KEY, null);
				slots.put(EFFORT_DURATION_KEY, null);
				slots.put(EFFORT_REMAINING_KEY, null);
				response.getDialogAction().setSlots(slots);
			}
		}

		return response;
	}

	private LexResponse validateActivationKeySlotInput(LexRequest requestObject) {
		Logger.log("in validateActivationKeySlotInput method");
		// 1. query dynamodb with activation key
		// 1a. If no user object is found then return prompt we don't know u
		// 1b. If user found but inactive state, then keep activation Id
		// in session key & then delegate
		// 1c. If user found and also in active state, then put fbId in session
		// and ElicitIntent

		LexResponse response = null;

		Map<String, String> reqSessionAttributes = requestObject.getSessionAttributes();
		Map<String, String> reqSlots = requestObject.getCurrentIntent().getSlots();

		String activationSlotVal = null != reqSlots ? reqSlots.get(ACTIVATION_KEY) : null;
		String activationSessionVal = null != reqSessionAttributes && reqSessionAttributes.containsKey(ACTIVATION_KEY)
				? reqSessionAttributes.get(ACTIVATION_KEY) : "";

		if (null == activationSlotVal) {
			// elicit slot for activation and return response
			// flow shouldn't go ahead until user supply activation key
			response = getLexResponseForElicitSlot(ACTIVATION_KEY,
					"Welcome! I am unable to recognize you. Please let me know your activation number", requestObject);
			return response;
		} else if (null != activationSlotVal && !activationSlotVal.equalsIgnoreCase(activationSessionVal)) {

			DBUserInfo dbUser = null;
			try {
				dbUser = getDBHelper().fetchUserByActivationKey(activationSlotVal);
			} catch (Exception e) {
				Logger.log("Error fetching User by activatin key in validateActivationKeySlotInput method= " + e);
			}
			if (null == dbUser) {
				// If no user object is found then return prompt we haven't
				// recognized u. Ask ur admin to add you to scrum tool or try
				// after sometime if you were recently added, DialogAction
				// should be closed
				response = getLexResponseForElicitSlot(ACTIVATION_KEY,
						"Oops, I couldn't find your activation number: " + activationSlotVal
								+ ". Ask your scrum tool admin to add you to scrum or try after a while if you were recently added to scrum tool. I look forward to assist you soon",
						requestObject);
				return response;
				// response = getLexResponseForClose(
				// "Oops, I couldn't find your activation number: " +
				// activationSlotVal
				// + ". Ask your scrum tool admin to add you to scrum or try
				// after a while if you were recently added to scrum tool. I
				// look forward to assist you soon",
				// requestObject, false);

			} else if (null != dbUser
					&& DBUserInfo.STATUS_USER_READY_FOR_ACTIVATION.equalsIgnoreCase(dbUser.getUserStatus())) {
				// If user found but inactive state, then keep activation Id
				// in session key
				if (null == reqSessionAttributes) {
					reqSessionAttributes = new HashMap<String, String>();
				}
				reqSessionAttributes.put(ACTIVATION_KEY, activationSlotVal);
			} else if (null != dbUser && DBUserInfo.STATUS_USER_ACTIVATED.equalsIgnoreCase(dbUser.getUserStatus())) {
				String dbSocialId = dbUser.getSocialId();
				String reqSocialId = requestObject.getUserId();
				if (!reqSocialId.equalsIgnoreCase(dbSocialId)) {
					// already used activation id is being again tried from
					// different channels
					response = getLexResponseForElicitSlot(ACTIVATION_KEY,
							"Oh I am amused, activation number: \"" + activationSlotVal
									+ "\" you provided is already in use. Please provide valid activation number",
							requestObject);
					return response;
				}
				// If user found and also in active state, then put fbId in
				// session
				// and ElicitIntent
				String msg = String.format(TASK_UPDATE_QUERY, dbUser.getName());

				response = getTaskUpdaterIntentResponse(msg, requestObject);

				return response;
			}

		}

		return response;
	}

	private LexResponse validateApiKeySlotInput(LexRequest requestObject) {
		Logger.log("in validateApiKeySlotInput method");
		// 1. Query User obj from DynamoDB by activationKey
		// 1a. If user not found with activation then elicitSlot Activation
		// 1b. If user found with activation then
		// 1b1. Check if apiKey is valid by making Rally call
		// 1b1a. If valid then apiKey in sessionAttr and delegate
		// 1b12. If not valid elicitSlot for apiKey

		LexResponse response = null;

		Map<String, String> reqSessionAttributes = requestObject.getSessionAttributes();
		Map<String, String> reqSlots = requestObject.getCurrentIntent().getSlots();

		// check for task status vals
		String apiKeySlotVal = null != reqSlots ? reqSlots.get(API_KEY) : null;
		String apiKeySessionVal = null != reqSessionAttributes && reqSessionAttributes.containsKey(API_KEY)
				? reqSessionAttributes.get(API_KEY) : "";

		// String activationKeySessionVal = null != reqSessionAttributes ?
		// reqSessionAttributes.get(ACTIVATION_KEY) : null;

		// DBUserInfo dbUser = null;
		// if (null != activationKeySessionVal) {
		// try {
		// dbUser =
		// getDBHelper().fetchUserByActivationKey(activationKeySessionVal);
		// } catch (Exception e) {
		// Logger.log("Error fetching User by activation key in
		// validateApiKeySlotInput method= " + e);
		// }
		//
		// if (null == dbUser) {
		// // elicit slot for activation key and return response
		// }
		// } else {
		// // elicit slot for activation key and return response
		// }

		if (null != apiKeySlotVal && !apiKeySlotVal.equalsIgnoreCase(apiKeySessionVal)) {

			// Check api Key is valid or not by making rally call
			String apiSplittedBySpace[] = apiKeySlotVal.split(" ");
			if (null != apiSplittedBySpace && apiSplittedBySpace.length > 1) {

				// elicit slot for api key and return response
				response = getLexResponseForElicitSlot(API_KEY,
						"\"" + apiKeySlotVal
								+ "\" isn't correct. Api key is usually a single word. Please enter correct value",
						requestObject);
				return response;
			}

			if (isValidScrumApiKey(apiKeySlotVal)) {
				if (null == reqSessionAttributes) {
					reqSessionAttributes = new HashMap<String, String>();
				}
				reqSessionAttributes.put(API_KEY, apiKeySlotVal);
			} else {
				// elicit slot for api key and return response
				response = getLexResponseForElicitSlot(API_KEY,
						"\"" + apiKeySlotVal
								+ "\" Api key you provided isn't working. Please get the correct one from Rally",
						requestObject);
				return response;
			}

		}

		return response;
	}

	public boolean isValidScrumApiKey(String apiKey) {
		boolean retVal = false;
		try {
			retVal = scrumInterface.isValidUser(apiKey);
		} catch (ScrumException e) {
			Logger.log("Error verifying api key in isValidScrumApiKey method= " + e);
		}
		return retVal;
	}

}
