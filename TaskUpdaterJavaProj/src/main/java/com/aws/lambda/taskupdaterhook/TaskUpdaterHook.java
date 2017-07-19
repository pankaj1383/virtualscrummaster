package com.aws.lambda.taskupdaterhook;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.aws.lambda.taskupdaterhook.dynamoDB.DBHelper;
import com.aws.lambda.taskupdaterhook.dynamoDB.DBHelperImpl;
import com.aws.lambda.taskupdaterhook.dynamoDB.pojo.DBUserInfo;
import com.aws.lambda.taskupdaterhook.exceptions.CJsonException;
import com.aws.lambda.taskupdaterhook.pojo.Button;
import com.aws.lambda.taskupdaterhook.pojo.CurrentIntent;
import com.aws.lambda.taskupdaterhook.pojo.DialogAction;
import com.aws.lambda.taskupdaterhook.pojo.GenericAttachment;
import com.aws.lambda.taskupdaterhook.pojo.Message;
import com.aws.lambda.taskupdaterhook.pojo.ResponseCard;
import com.aws.lambda.taskupdaterhook.scrum.BaseResponse;
import com.aws.lambda.taskupdaterhook.scrum.IScrumTool;
import com.aws.lambda.taskupdaterhook.scrum.exception.ScrumException;
import com.aws.lambda.taskupdaterhook.scrum.pojo.Task;
import com.aws.lambda.taskupdaterhook.scrum.pojo.UserStory;
import com.aws.lambda.taskupdaterhook.scrum.request.TaskFetchRequest;
import com.aws.lambda.taskupdaterhook.scrum.request.TaskUpdateRequest;
import com.aws.lambda.taskupdaterhook.scrum.response.TaskUpdateResponse;
import com.aws.lambda.taskupdaterhook.scrum.response.TasksResponse;
import com.aws.lambda.taskupdaterhook.scrum.tool.rally.Rally;
import com.aws.lambda.taskupdaterhook.scrum.tool.rally.pojo.RallyTask;
import com.aws.lambda.taskupdaterhook.utils.JsonUtility;
import com.aws.lambda.taskupdaterhook.utils.Logger;

public class TaskUpdaterHook implements RequestHandler<LexRequest, LexResponse> {

	private static final String TASK_INFO_KEY = "taskInfo";
	private static final String TASK_INFO_OPTIONS_KEY = "taskInfoOptions";
	private static final String TASK_ID_KEY = "taskID";
	private static final String EFFORT_DURATION_KEY = "effortDuration";
	private static final String EFFORT_REMAINING_KEY = "effortRemaining";
	private static final String TASK_STATUS_KEY = "taskStatus";

	private static final String TASK_OLD_TODO_KEY = "TASK_OLD_TODO_KEY";
	private static final String TASK_OLD_ACTUALS_KEY = "TASK_OLD_ACTUALS_KEY";

	private static final String[] TASK_STATUS_OPTIONS = { "In-Progress", "Completed", "Defined" };

	private static int DURATION_TYPE_VALID_HOUR = 1;
	private static int DURATION_TYPE_INVALID_INPUT = 2;
	private static int DURATION_TYPE_INVALID_RANGE = 3;
	private static int DURATION_TYPE_INVALID_NO_TIME = 4;

	private IScrumTool scrumInterface;
	private String apiKey;

	private static final String ACTIVATION_KEY = "activationId";
	private static final String API_KEY = "apiKey";
	private static final String USER_REG_INTENT = "UserIntroduction";

	private static final String CONFIRMATION_MSG = "Are you sure you want to log %1$s of effort for %2$s task and with current status as %3$s?";

	private static final String TODO_STR = "ToDo: %1$s Hours";
	private static final String ACTUALS_STR = "Actual: %1$s Hours";

	private String userName;

	private DBHelper dbHelper;

	private void init() {
		scrumInterface = new Rally();
	}

	@Override
	public LexResponse handleRequest(LexRequest input, Context context) {
		LexResponse response = null;

		try {
			Logger.log("---- TaskUpdaterHook AWS Java Lambda execution starts ----");
			init();

			String reqJsonStr = getRequestObjJson(input);
			Logger.log("in handleRequest method, request Json new impl: " + reqJsonStr);
			//
			// if (true) {
			// LexResponse resp = getLexResponseForDelegate(null, (LexRequest)
			// input);
			// Map<String, String> sessionVals = ((LexRequest)
			// input).getSessionAttributes();
			// if (null == sessionVals) {
			// sessionVals = new HashMap<String, String>();
			// }
			// String oldVal = sessionVals.get("testSessVal");
			// int oldIntVal = null == oldVal ? 0 : Integer.parseInt(oldVal);
			// oldIntVal++;
			// sessionVals.put("testSessVal", "" + oldIntVal);
			// resp.setSessionAttributes(sessionVals);
			//
			// return resp;
			// }

			Logger.log("lex requ obj= " + input);
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
			Logger.log("---- TaskUpdaterHook AWS Java Lambda execution ends ----");

			String responseJsonStr = getResponseObjJson(response);
			Logger.log("in handleRequest method, response Json new impl: " + responseJsonStr);

		} catch (Exception e) {

			Logger.log("Unhandled exception: " + e);

			context.getLogger().log("Logger size " + Logger.getSize());
			context.getLogger().log("Unhandled exception: " + e);

		} finally {

			context.getLogger().log(Logger.getLogs());
			Logger.clearLogs();
		}

		return response;
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

	private LexResponse buildRespForFulfillment(LexRequest requestObject) {
		Logger.log("in buildRespForFulfillment method");

		LexResponse response = new LexResponse();
		DialogAction dialogAction = new DialogAction();
		response.setDialogAction(dialogAction);

		response.setSessionAttributes(new HashMap<String, String>());

		dialogAction.setType(DialogAction.DIALOG_TYPE_CLOSE);
		Message msg = new Message();
		msg.setContentType(Message.CONTENT_TYPE_TEXT);
		dialogAction.setMessage(msg);

		if (validMatchForFulfillment(requestObject)) {
			// makeAPI Call
			Task t = new RallyTask();
			// TODO Remove Response to request class
			TaskUpdateResponse taskUpdateResponse = null;
			try {
				TaskUpdateRequest taskRequestObj = new TaskUpdateRequest(apiKey);
				taskRequestObj.setTaskToUpdate(populateTaskToUpdate(t, requestObject));

				TaskUpdateResponse baseTaskUpdateResponse = scrumInterface.updateTask(taskRequestObj);
				taskUpdateResponse = baseTaskUpdateResponse;
			} catch (ScrumException e) {
				Logger.log("error while updating rally task in buildRespForFulfillment method= " + e);
			}

			if (null != taskUpdateResponse && taskUpdateResponse.getStatusCode() == BaseResponse.STATUS_OK) {
				msg.setContent("Wow, I have just modified your scrum task "
						+ taskUpdateResponse.getTaskUpdated().getName() + " with " + t.getState() + " as status!");
				dialogAction.setFulfillmentState(DialogAction.FULFILLMENT_STATE_FULFILLED);
			} else {
				msg.setContent(
						"Oh sorry, I could not update your scrum task due to some technical reason. I am feeling bad about it. Please try later");
				dialogAction.setFulfillmentState(DialogAction.FULFILLMENT_STATE_FAILED);
			}
		} else {
			msg.setContent("Oh sorry, I could not update your scrum task with your changed inputs");
			dialogAction.setFulfillmentState(DialogAction.FULFILLMENT_STATE_FAILED);
		}

		return response;
	}

	private LexResponse buildRespForConfirmation(LexRequest requestObject) {
		Logger.log("in buildRespForConfirmation method");
		LexResponse response = new LexResponse();
		response.setSessionAttributes(requestObject.getSessionAttributes());
		DialogAction dialogAction = new DialogAction();
		response.setDialogAction(dialogAction);
		dialogAction.setType(DialogAction.DIALOG_TYPE_CONFIRM_INTENT);
		Message msg = new Message();
		String task = "";
		String effortInvested = "";
		String status = "";
		if (null != requestObject.getSessionAttributes()) {
			task = requestObject.getSessionAttributes().get(TASK_INFO_KEY);
			effortInvested = requestObject.getSessionAttributes().get(EFFORT_DURATION_KEY);
			status = requestObject.getSessionAttributes().get(TASK_STATUS_KEY);
		}
		String msgTxt = String.format(CONFIRMATION_MSG, effortInvested, task, status);
		msg.setContentType(Message.CONTENT_TYPE_TEXT);
		msg.setContent(msgTxt);
		dialogAction.setMessage(msg);
		// dialogAction.setIntentName(requestObject.getCurrentIntent().getName());
		dialogAction.setSlots(requestObject.getCurrentIntent().getSlots());
		dialogAction.setIntentName(requestObject.getCurrentIntent().getName());

		return response;
	}

	private Task populateTaskToUpdate(Task t, LexRequest requestObject) {

		Map<String, String> sessionAttrs = requestObject.getSessionAttributes();
		if (null != sessionAttrs) {
			String taskId = sessionAttrs.get(TASK_ID_KEY);
			if (null != taskId) {
				t.setReferenceID(taskId);
			}

			String taskStatus = sessionAttrs.get(TASK_STATUS_KEY);
			if (null != taskStatus) {
				t.setState(taskStatus);
			}

			String effortInvested = sessionAttrs.get(EFFORT_DURATION_KEY);
			if (null != effortInvested) {
				t.setActuals(effortInvested);
			}

			String effortRemaining = sessionAttrs.get(EFFORT_REMAINING_KEY);
			if (null != effortRemaining) {
				t.setToDo(effortRemaining);
			}
		}

		return t;
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

	private DBHelper getDBHelper() {
		if (null == dbHelper) {
			dbHelper = new DBHelperImpl();
		}

		return dbHelper;
	}

	private LexResponse getUserRegIntentResponse(String message, LexRequest reqObject) {

		// Not sure if elicitIntent will work here so trying ElicitSlot 1st as
		// it ask for intentName & slot
		LexResponse response = getLexResponseForElicitSlot(ACTIVATION_KEY, message, reqObject, USER_REG_INTENT);

		// Map<String, String> sessionAttrs = reqObject.getSessionAttributes();
		// Map<String, String> sessionAttrs = response.getSessionAttributes();
		// sessionAttrs.put(USER_SOCIAL_ID, reqObject.getUserId());
		// response.setSessionAttributes(sessionAttrs);

		DialogAction dialogAction = new DialogAction();
		response.setDialogAction(dialogAction);

		dialogAction.setType(DialogAction.DIALOG_TYPE_ELICIT_SLOT);
		if (null != message) {
			Message msgObj = new Message();
			msgObj.setContentType(Message.CONTENT_TYPE_TEXT);
			msgObj.setContent(message);
			dialogAction.setMessage(msgObj);
		}

		dialogAction.setSlots(reqObject.getCurrentIntent().getSlots());

		return response;
	}

	private LexResponse buildRespForValidation(LexRequest requestObject) {
		Logger.log("in buildRespForValidation method");
		LexResponse response = null;

		DBUserInfo dbUserObj = getUserBySocialId(requestObject);
		String activationStatus = null != dbUserObj ? dbUserObj.getUserStatus() : "Unknown";
		userName = dbUserObj != null ? dbUserObj.getName() : null;

		if (null == dbUserObj || activationStatus.equalsIgnoreCase(DBUserInfo.STATUS_USER_READY_FOR_ACTIVATION)) {
			// if user null or state is ready for activation then prompt user
			// registration activation
			String message = "Hey, great to see you. I would love to assist you. May I know your activation number?";
			response = getLexResponseForElicitSlot(ACTIVATION_KEY, message, requestObject, USER_REG_INTENT);
			return response;

		} else if (activationStatus.equalsIgnoreCase(DBUserInfo.STATUS_USER_API_KEY_EXPIRED)) {
			// if user != null and state api expiry the prompt userReg api key
			response = getLexResponseForElicitSlot(API_KEY, null, requestObject, USER_REG_INTENT);
			return response;

		} else if (null != requestObject.getCurrentIntent().getSlots()
				|| requestObject.getCurrentIntent().getSlots().size() > 0) {
			apiKey = dbUserObj.getRallyAPIKey();
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
		Map<String, String> reqSlots = requestObject.getCurrentIntent().getSlots();

		String sessionTaskId = null != reqSessionAttributes ? reqSessionAttributes.get(TASK_ID_KEY) : null;
		if (null == sessionTaskId) {
			return false;
		}

		String slotTaskEffortInvested = null != reqSlots ? reqSlots.get(EFFORT_DURATION_KEY) : null;
		String slotTaskEffortInvestedStr = getReadbleDurationFrom(slotTaskEffortInvested);
		String sessionTaskEffortInvested = null != reqSessionAttributes ? reqSessionAttributes.get(EFFORT_DURATION_KEY)
				: "";
		if (null != sessionTaskEffortInvested
				&& !sessionTaskEffortInvested.equalsIgnoreCase(slotTaskEffortInvestedStr)) {
			return false;
		}

		String slotTaskStatus = null != reqSlots ? reqSlots.get(TASK_STATUS_KEY) : null;
		String sessionTaskStatus = null != reqSessionAttributes ? reqSessionAttributes.get(TASK_STATUS_KEY) : "";
		if (null != sessionTaskStatus && !sessionTaskStatus.equalsIgnoreCase(slotTaskStatus)) {
			return false;
		}

		return retVal;
	}

	private LexResponse validateSlotsInput(LexRequest requestObject) {

		Logger.log("in validateSlotsInput method");

		LexResponse response = null;

		response = validateTaskInfoSlotInput(requestObject);
		// it indicates task Info slot needs validation and hence raised
		// elicitslot response. So raise immediately!
		if (null != response) {
			return response;
		}

		response = validateEffortInvestedSlotInput(requestObject);
		// it indicates effort invested slot needs validation and hence raised
		// elicitslot response. So raise immediately!
		if (null != response) {
			return response;
		}

		response = validateEffortRemainingSlotInput(requestObject);
		// it indicates effort remaining slot needs validation and hence raised
		// elicitslot response. So raise immediately!
		if (null != response) {
			return response;
		}

		response = validateTaskStatusSlotInput(requestObject);
		// it indicates task status slot needs validation and hence raised
		// elicitslot response. So raise immediately!
		if (null != response) {
			return response;
		}

		// if it reaches here then create delegate response and return
		response = getLexResponseForDelegate(null, requestObject);
		return response;
	}

	private LexResponse getLexResponseForDelegate(String message, LexRequest reqObject) {
		Logger.log("in getLexResponseForDelegate method of TaskUpdaterHook class");
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
			if (intentName.equalsIgnoreCase(USER_REG_INTENT)) {
				Map<String, String> sessionAttr = new HashMap<String, String>();
				response.setSessionAttributes(sessionAttr);

				Map<String, String> slots = new HashMap<String, String>();
				slots.put(ACTIVATION_KEY, null);
				slots.put(API_KEY, null);
				dialogAction.setSlots(slots);
			}
		}

		return response;
	}

	private LexResponse validateTaskInfoSlotInput(LexRequest requestObject) {
		Logger.log("in validateTaskInfoSlotInput method");

		LexResponse response = null;

		Map<String, String> reqSessionAttributes = requestObject.getSessionAttributes();
		Map<String, String> reqSlots = requestObject.getCurrentIntent().getSlots();

		String taskInfoSlotVal = null != reqSlots ? reqSlots.get(TASK_INFO_KEY) : null;
		String taskInfoSessionVal = null != reqSessionAttributes && reqSessionAttributes.containsKey(TASK_INFO_KEY)
				? reqSessionAttributes.get(TASK_INFO_KEY) : "";
		String allOptionsStr = null != reqSessionAttributes ? reqSessionAttributes.get(TASK_INFO_OPTIONS_KEY) : null;

		if (null != taskInfoSlotVal && null != allOptionsStr) {

			ResponseCard respCard = new ResponseCard();
			List<GenericAttachment> attachments = new ArrayList<GenericAttachment>();
			respCard.setGenericAttachments(attachments);

			boolean taskInfoSlotContains = Pattern.compile(Pattern.quote(taskInfoSlotVal), Pattern.CASE_INSENSITIVE)
					.matcher(allOptionsStr).find();
			if (!taskInfoSlotContains) {

				String[] options = allOptionsStr.split(";");
				StringBuffer sb = new StringBuffer("Which task from these?");
				sb.append("\n");
				sb.append("\n");
				for (String option : options) {
					if (null != option && option.contains(": ")) {
						String txt[] = option.split(": ");
						// sb.append(txt[1]);
						// sb.append("\n");
						// sb.append("\n");

						GenericAttachment genericAttachment = buildGenericAttachmentFor(txt[0], txt[1]);
						if (null != genericAttachment) {
							attachments.add(genericAttachment);
						}
					}

				}

				response = getLexResponseForElicitSlot(TASK_INFO_KEY, sb.toString(), requestObject);
				if (attachments.size() > 0) {
					response.getDialogAction().setResponseCard(respCard);
				}
				return response;
				// 2. If allOptionsStr !contains taskInfoSlotVal
				// a. Get pair of each option key-val from allOptionsStr
				// b. Prompt via elicitSlot for task & present all options to
				// user
				// response =
				// getLexResponseForElicitSlot(TASK_INFO_KEY,prepareTaskInfoOptions(allOptionsStr),requestObject);
				// return response;
			} else {

				StringBuffer sb = new StringBuffer(
						"Multiple match again. Please be precise to tell which task from these?");
				sb.append("\n");
				sb.append("\n");
				String selectedOptionId = null;
				String selectedOptionTxt = null;
				String[] options = allOptionsStr.split(";");
				int matchedCount = 0;
				for (String option : options) {
					if (null != option && option.contains(": ")) {
						String txt[] = option.split(": ");

						if (txt[1].contains(taskInfoSlotVal) || txt[0].contains(taskInfoSlotVal)) {
							selectedOptionId = txt[0];
							selectedOptionTxt = txt[1];
							matchedCount++;
						}

						// sb.append(txt[1]);
						// sb.append("\n");
						// sb.append("\n");
						GenericAttachment genericAttachment = buildGenericAttachmentFor(txt[0], txt[1]);
						if (null != genericAttachment) {
							attachments.add(genericAttachment);
						}

						if (isExactMatchByRefId(txt, requestObject.getInputTranscript())) {
							matchedCount = 1;
							selectedOptionId = txt[0];
							selectedOptionTxt = txt[1];
							break;
						}

					}
				}

				if (matchedCount == 1 && null != selectedOptionId && null != selectedOptionTxt) {
					if (null == reqSessionAttributes) {
						reqSessionAttributes = new HashMap<String, String>();
					}
					reqSessionAttributes.put(TASK_INFO_KEY, selectedOptionTxt);
					reqSlots.put(TASK_INFO_KEY, selectedOptionTxt);
					reqSessionAttributes.put(TASK_ID_KEY, selectedOptionId);
					reqSessionAttributes.remove(TASK_INFO_OPTIONS_KEY);

					response = getLexResponseForDelegate(null, requestObject);
					return response;
				} else {
					response = getLexResponseForElicitSlot(TASK_INFO_KEY, sb.toString(), requestObject);
					if (attachments.size() > 0) {
						response.getDialogAction().setResponseCard(respCard);
					}
					return response;
				}
				// 1. If allOptionsStr contains taskInfoSlotVal
				// a. Get pair of each option key-val from allOptionsStr and
				// check
				// which option pair matches with taskInfoSlotVal
				// b. Set selected option val as sessionAttr val for
				// TASK_INFO_KEY
				// c. Set selected option key as taskId in sessionAttr
				// d. Remove entry for TASK_INFO_OPTIONS_KEY from session attr
			}

		} else if (null != taskInfoSlotVal && !taskInfoSlotVal.equalsIgnoreCase(taskInfoSessionVal)) {

			// TasksResponse tasksResponse =
			// scrumInterface.fetchTasksForDescription(taskInfoSlotVal);
			TasksResponse tasksResponse = null;

			try {

				TaskFetchRequest taskReq = new TaskFetchRequest(apiKey);
				taskReq.setSearchTaskKeyword(taskInfoSlotVal);
				tasksResponse = scrumInterface.getAllTasksForUser(taskReq);
			} catch (ScrumException e) {
				Logger.log("error while fetching all tasks from rally in validateTaskInfoSlotInput method= " + e);
				e.printStackTrace();
			}
			if (null != tasksResponse && tasksResponse.getStatusCode() == BaseResponse.STATUS_OK) {
				// TODO Remove type casting and return specific Response
				List<Task> tasksFound = tasksResponse.getTasks();
				if (null != tasksFound) {
					if (tasksFound.size() < 1) {

						String message = "Oops " + userName + ", I couldn't find any task with provided description.";
						response = getLexResponseForElicitSlot(TASK_INFO_KEY, message, requestObject);
						return response;
					} else if (tasksFound.size() == 1) {
						Task task = tasksFound.get(0);
						if (null != task) {
							if (null == reqSessionAttributes) {
								reqSessionAttributes = new HashMap<String, String>();
							}
							reqSessionAttributes.put(TASK_INFO_KEY, taskInfoSlotVal);
							reqSessionAttributes.put(TASK_ID_KEY, task.getReferenceID());
						}
					} else if (tasksFound.size() > 1 && tasksFound.size() <= 10) {
						String message = "Oh " + userName + ", I found quite a few tasks. Which one from these?";
						StringBuffer sb = new StringBuffer(message);
						sb.append("\n");
						sb.append("\n");
						StringBuffer sessionVal = new StringBuffer();
						ResponseCard respCard = new ResponseCard();
						List<GenericAttachment> attachments = new ArrayList<GenericAttachment>();
						respCard.setGenericAttachments(attachments);

						for (Task taskObj : tasksFound) {

							GenericAttachment genericAttachment = buildGenericAttachmentFor(taskObj);
							if (null != genericAttachment) {
								attachments.add(genericAttachment);
							}

							UserStory userStory = taskObj.getUserStory();
							String userStoryName = "";
							if (null != userStory) {
								userStoryName = userStory.getName();
							}
							String txt = taskObj.getName() + " of " + userStoryName + " User story";
							// sb.append(txt);
							// sb.append("\n");
							// sb.append("\n");
							sessionVal.append(taskObj.getReferenceID() + ": " + txt);
							sessionVal.append(";");
						}

						if (null == reqSessionAttributes) {
							reqSessionAttributes = new HashMap<String, String>();
						}
						reqSessionAttributes.put(TASK_INFO_OPTIONS_KEY, sessionVal.toString());
						response = getLexResponseForElicitSlot(TASK_INFO_KEY, sb.toString(), requestObject);
						if (attachments.size() > 0) {
							response.getDialogAction().setResponseCard(respCard);
						}

						return response;
						// 2b. if tasks list returns > 1 result then form string
						// with task
						// descp & user story name for all options and call
						// elicitslot for
						// task and present all options to user; Also add
						// sessionAtr for all
						// options with TASK_INFO_OPTIONS_KEY as key and String
						// composed of
						// all options with "taskID_vals":"task+US Decp" and
						// separated by ;
						// response =
						// getLexResponseForElicitSlot(TASK_INFO_KEY,prepareTaskInfoOptions(allOptionsStr),requestObject);
						// return response;
					} else if (tasksFound.size() > 10) {
						response = getLexResponseForElicitSlot(TASK_INFO_KEY,
								userName + ", please refine your search. I found too many results with description you have provided",
								requestObject);
						return response;
					}
				} else {
					// if of null != tasksFound
					response = getLexResponseForElicitSlot(TASK_INFO_KEY,
							"Oops, I couldn't find any task with provided description.", requestObject);
					return response;
				}
			} else if (null != tasksResponse && tasksResponse.getStatusCode() == BaseResponse.STATUS_UNAUTHORIZED) {
				// handle api key expired case
				String message = "Looks like you have changed the api key. What is the key now?";
				response = getLexResponseForElicitSlot(API_KEY, message, requestObject, USER_REG_INTENT);
				return response;
			} else {
				// TODO
				response = new LexResponse();
				DialogAction dialogAction = new DialogAction();
				response.setDialogAction(dialogAction);
				response.setSessionAttributes(new HashMap<String, String>());

				dialogAction.setSlots(reqSlots);

				dialogAction.setType(DialogAction.DIALOG_TYPE_CLOSE);
				Message msg = new Message();
				msg.setContentType(Message.CONTENT_TYPE_TEXT);
				dialogAction.setMessage(msg);

				msg.setContent(
						"Oh sorry, I could not read your tasks at this time. I am feeling bad. Please try later");
				dialogAction.setFulfillmentState(DialogAction.FULFILLMENT_STATE_FAILED);

				return response;

				// Send close delegate with please try after sometime
				// API couldn't return task from rally
				// How to handle this?
			}

		}

		return response;
	}

	public boolean isExactMatchByRefId(String[] txt, String inputVal) {
		boolean retVal = false;
		if (null != txt && txt.length > 0) {
			String refId = null;
			String taskRefId = txt[0];

			if (null != taskRefId && taskRefId.indexOf("/") != -1) {
				int lastIndex = taskRefId.lastIndexOf("/");
				if (taskRefId.length() > lastIndex + 1) {
					// baseTaskUrl = taskRefId.substring(0, lastIndex + 1);
					refId = taskRefId.substring(taskRefId.lastIndexOf("/") + 1);
				}
			}

			retVal = null != refId ? refId.equalsIgnoreCase(inputVal) : retVal;
		}
		return retVal;
	}

	private GenericAttachment buildGenericAttachmentFor(String taskUrl, String taskDescp) {
		GenericAttachment genericAttachment = null;

		String baseTaskUrl = null;
		String refId = null;
		String taskRefId = taskUrl;

		if (null != taskRefId && taskRefId.indexOf("/") != -1) {
			int lastIndex = taskRefId.lastIndexOf("/");
			if (taskRefId.length() > lastIndex + 1) {
				baseTaskUrl = taskRefId.substring(0, lastIndex + 1);
				refId = taskRefId.substring(taskRefId.lastIndexOf("/") + 1);
				// TASK_BASE_URL_KEY
			}
		}

		if (null != taskDescp && null != refId) {
			genericAttachment = new GenericAttachment();
			genericAttachment.setTitle("Is this the task?");
			genericAttachment.setSubTitle("Task: " + taskDescp);
			List<Button> buttons = new ArrayList<Button>();
			Button button = new Button();
			button.setText("Select this!");
			button.setValue(refId);
			buttons.add(button);
			genericAttachment.setButtons(buttons);
		}

		return genericAttachment;
	}

	private GenericAttachment buildGenericAttachmentFor(Task taskObj) {
		GenericAttachment genericAttachment = null;

		String baseTaskUrl = null;
		String refId = null;
		String taskRefId = null != taskObj.getReferenceID() ? taskObj.getReferenceID() : null;

		if (null != taskRefId && taskRefId.indexOf("/") != -1) {
			int lastIndex = taskRefId.lastIndexOf("/");
			if (taskRefId.length() > lastIndex + 1) {
				baseTaskUrl = taskRefId.substring(0, lastIndex + 1);
				refId = taskRefId.substring(taskRefId.lastIndexOf("/") + 1);
				// TASK_BASE_URL_KEY
			}
		}

		UserStory userStory = taskObj.getUserStory();
		String userStoryName = null;
		if (null != userStory) {
			userStoryName = userStory.getName();
		}

		if (null != userStoryName && null != taskObj.getName() && null != refId) {
			genericAttachment = new GenericAttachment();
			genericAttachment.setTitle("User Story: " + userStoryName);
			StringBuffer sb = new StringBuffer();
			sb.append("Task: " + taskObj.getName());
			if (null != taskObj.getActuals() && null != taskObj.getToDo()) {
				sb.append("\n");
				sb.append("\n");
				sb.append(String.format(ACTUALS_STR, taskObj.getActuals()));
				sb.append("\n");
				sb.append(String.format(TODO_STR, taskObj.getToDo()));
			}
			// TODO_STR
			genericAttachment.setSubTitle(sb.toString());
			List<Button> buttons = new ArrayList<Button>();
			Button button = new Button();
			button.setText("Select this!");
			button.setValue(refId);
			buttons.add(button);
			genericAttachment.setButtons(buttons);
		}

		return genericAttachment;
	}

	private LexResponse validateTaskStatusSlotInput(LexRequest requestObject) {
		Logger.log("in validateTaskStatusSlotInput method");

		LexResponse response = null;

		Map<String, String> reqSessionAttributes = requestObject.getSessionAttributes();
		Map<String, String> reqSlots = requestObject.getCurrentIntent().getSlots();

		// check for task status vals
		String taskStatusVal = null != reqSlots ? reqSlots.get(TASK_STATUS_KEY) : null;
		String taskStatusSessionVal = null != reqSessionAttributes && reqSessionAttributes.containsKey(TASK_STATUS_KEY)
				? reqSessionAttributes.get(TASK_STATUS_KEY) : "";
		// if (null != taskStatusVal &&
		// !taskStatusVal.equalsIgnoreCase(taskStatusSessionVal)) {
		if (null != taskStatusVal && !matchTaskStatus(taskStatusVal, taskStatusSessionVal)) {

			// 1. make API call to get all task status

			// 2a. If taskStatusVal matches fetched task status val then set
			// this as task status in slot & session
			boolean statusSet = false;
			for (String statusOption : TASK_STATUS_OPTIONS) {
				// if (statusOption.equalsIgnoreCase(taskStatusVal)) {
				if (matchTaskStatus(statusOption, taskStatusVal)) {
					if (null == reqSessionAttributes) {
						reqSessionAttributes = new HashMap<String, String>();
					}
					reqSessionAttributes.put(TASK_STATUS_KEY, statusOption);
					statusSet = true;
					break;
				}
			}

			if (!statusSet) {
				StringBuffer sb = new StringBuffer(
						taskStatusVal + " isn't a right status. Choose one from the following, ");
				sb.append("\n");
				// sb.append("\n");
				// for (String statusOption : TASK_STATUS_OPTIONS) {
				// sb.append(statusOption + ", ");
				// sb.append("\n");
				// sb.append("\n");
				// }

				response = getLexResponseForElicitSlot(TASK_STATUS_KEY, sb.toString(), requestObject);
				return response;
			}

			// 2b. If taskStatusVal doesn't match then present all options to
			// user and send elicitslot
			// response =
			// getLexResponseForElicitSlot(TASK_STATUS_KEY,prepareTaskStatusOptions(allStatusStr),requestObject);
			// return response;
		}

		return response;
	}

	private LexResponse validateEffortInvestedSlotInput(LexRequest requestObject) {
		Logger.log("in validateEffortInvestedSlotInput method");

		LexResponse response = null;

		Map<String, String> reqSessionAttributes = requestObject.getSessionAttributes();
		Map<String, String> reqSlots = requestObject.getCurrentIntent().getSlots();
		// update effortDuration in session if changed
		String effortDurSessionVal = null != reqSessionAttributes
				&& reqSessionAttributes.containsKey(EFFORT_DURATION_KEY) ? reqSessionAttributes.get(EFFORT_DURATION_KEY)
						: "";
		String effortDurVal = null != reqSlots ? reqSlots.get(EFFORT_DURATION_KEY) : null;
		int durationInputType = getDurationInputType(effortDurVal);
		String effortDurReadableVal = null;
		if (durationInputType == DURATION_TYPE_VALID_HOUR) {
			effortDurReadableVal = getReadbleDurationFrom(effortDurVal);
		}

		if (null != effortDurReadableVal && !effortDurReadableVal.equalsIgnoreCase(effortDurSessionVal)) {

			if (durationInputType == DURATION_TYPE_VALID_HOUR) {
				// convert to readble hour -> effortDurReadableVal
				// update slot & Sessionattr val with latest effortDurVal
				if (null == reqSessionAttributes) {
					reqSessionAttributes = new HashMap<String, String>();
				}
				// if (null == reqSlots) {
				// reqSlots = new HashMap<String, String>();
				// }
				// reqSlots.put(EFFORT_DURATION_KEY, effortDurReadableVal);
				reqSessionAttributes.put(EFFORT_DURATION_KEY, effortDurReadableVal);

			}
		} else if (null == effortDurReadableVal && null != effortDurVal && effortDurVal.length() > 0) {

			if (durationInputType == DURATION_TYPE_INVALID_RANGE) {
				// return prompt of invalid range
				response = getLexResponseForElicitSlot(EFFORT_DURATION_KEY,
						"I only understand values in hour or minute", requestObject);
				return response;
			} else if (durationInputType == DURATION_TYPE_INVALID_INPUT) {
				response = getLexResponseForElicitSlot(EFFORT_DURATION_KEY,
						"I couldn't get what you are saying. Please let me know time you invested", requestObject);
				return response;
			} else if (durationInputType == DURATION_TYPE_INVALID_NO_TIME) {
				response = getLexResponseForElicitSlot(EFFORT_DURATION_KEY,
						"Please let me know the effort in time and not in day, week or month", requestObject);
				return response;
			}
		}

		return response;
	}

	private LexResponse validateEffortRemainingSlotInput(LexRequest requestObject) {
		Logger.log("in validateEffortRemainingSlotInput method");

		LexResponse response = null;

		Map<String, String> reqSessionAttributes = requestObject.getSessionAttributes();
		Map<String, String> reqSlots = requestObject.getCurrentIntent().getSlots();
		// update effortDuration in session if changed
		String effortDurSessionVal = null != reqSessionAttributes
				&& reqSessionAttributes.containsKey(EFFORT_REMAINING_KEY)
						? reqSessionAttributes.get(EFFORT_REMAINING_KEY) : "";
		String effortDurVal = null != reqSlots ? reqSlots.get(EFFORT_REMAINING_KEY) : null;
		int durationInputType = getDurationInputType(effortDurVal);
		String effortDurReadableVal = null;
		if (durationInputType == DURATION_TYPE_VALID_HOUR) {
			effortDurReadableVal = getReadbleDurationFrom(effortDurVal);

		}

		if (null != effortDurReadableVal && !effortDurReadableVal.equalsIgnoreCase(effortDurSessionVal)) {

			if (durationInputType == DURATION_TYPE_VALID_HOUR) {
				// convert to readble hour -> effortDurReadableVal
				// update slot & Sessionattr val with latest effortDurVal
				if (null == reqSessionAttributes) {
					reqSessionAttributes = new HashMap<String, String>();
				}
				// if (null == reqSlots) {
				// reqSlots = new HashMap<String, String>();
				// }
				// reqSlots.put(EFFORT_REMAINING_KEY, effortDurReadableVal);
				reqSessionAttributes.put(EFFORT_REMAINING_KEY, effortDurReadableVal);

			}
		} else if (null == effortDurReadableVal && null != effortDurVal && effortDurVal.length() > 0) {

			if (durationInputType == DURATION_TYPE_INVALID_RANGE) {
				// return prompt of invalid range
				response = getLexResponseForElicitSlot(EFFORT_REMAINING_KEY,
						"I only understand values in hour or minute", requestObject);
				return response;
			} else if (durationInputType == DURATION_TYPE_INVALID_INPUT) {
				response = getLexResponseForElicitSlot(EFFORT_REMAINING_KEY,
						"I couldn't get what you are saying. Please let me know time you invested", requestObject);
				return response;
			} else if (durationInputType == DURATION_TYPE_INVALID_NO_TIME) {
				response = getLexResponseForElicitSlot(EFFORT_REMAINING_KEY,
						"Please let me know the effort in time and not in day, week or month", requestObject);
				return response;
			}
		}

		return response;
	}

	public String getReadbleDurationFrom(String periodStr) {
		if (null == periodStr) {
			return "";
		}
		String retVal = "";
		int pIndex = periodStr.indexOf("P");
		int tIndex = periodStr.indexOf("T");
		int hIndex = periodStr.indexOf("H");
		int mIndex = periodStr.indexOf("M");
		float hours = 0;
		float minutes = 0;
		float convertedMinutestoHour = 0;
		if (tIndex >= 0 && hIndex > 0) {
			hours = Float.parseFloat(periodStr.substring(tIndex + 1, hIndex));
		}

		if ((hIndex >= 0 || tIndex >= 0) && mIndex > 0) {
			if (hIndex >= 0) {
				minutes = Float.parseFloat(periodStr.substring(hIndex + 1, mIndex));
			} else if (tIndex >= 0) {
				minutes = Float.parseFloat(periodStr.substring(tIndex + 1, mIndex));
			}
			convertedMinutestoHour = minutes / 60f;
		}

		float total = hours + convertedMinutestoHour;
		DecimalFormat numberFormat = new DecimalFormat("#.00");
		retVal = numberFormat.format(total);
		return retVal;
	}

	public int getDurationInputType(String effortDurVal) {
		// PnYnMnDTnHnMnS
		if (null != effortDurVal && effortDurVal.length() > 0) {
			int pIndex = effortDurVal.indexOf("P");
			int tIndex = effortDurVal.indexOf("T");
			if (pIndex == -1) {
				return DURATION_TYPE_INVALID_INPUT;
			} else if (tIndex == -1) {
				return DURATION_TYPE_INVALID_NO_TIME;
			} else if (pIndex != -1 && tIndex != -1 && tIndex - pIndex > 1) {
				return DURATION_TYPE_INVALID_RANGE;
			}
		} else {
			return DURATION_TYPE_INVALID_INPUT;
		}

		return DURATION_TYPE_VALID_HOUR;
	}

	public boolean matchTaskStatus(String val1, String val2) {
		boolean isAMatch = false;

		String option1 = val1;
		String option2 = val2;

		option1 = filterSpecialChar(option1);
		option2 = filterSpecialChar(option2);

		isAMatch = null != option1 && option1.equalsIgnoreCase(option2);

		return isAMatch;

	}

	private String filterSpecialChar(String inputStr) {
		String retVal = inputStr;

		Pattern pt = Pattern.compile("[^a-zA-Z0-9]");
		Matcher match = pt.matcher(retVal);
		while (match.find()) {
			String s = match.group();
			retVal = retVal.replaceAll("\\" + s, "");
		}

		return retVal;
	}
}
