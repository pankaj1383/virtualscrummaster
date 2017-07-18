package com.aws.lambda.taskupdaterhook.scrum.tool.versionone;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import com.amazonaws.services.lambda.runtime.Context;
import com.aws.lambda.taskupdaterhook.scrum.BaseResponse;
import com.aws.lambda.taskupdaterhook.scrum.IScrumTool;
import com.aws.lambda.taskupdaterhook.scrum.exception.ScrumException;
import com.aws.lambda.taskupdaterhook.scrum.request.TaskFetchRequest;
import com.aws.lambda.taskupdaterhook.scrum.request.TaskUpdateRequest;
import com.aws.lambda.taskupdaterhook.scrum.response.TaskUpdateResponse;
import com.aws.lambda.taskupdaterhook.scrum.response.TasksResponse;
import com.aws.lambda.taskupdaterhook.scrum.tool.rally.pojo.RallyTask;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.request.UpdateRequest;
import com.rallydev.rest.response.UpdateResponse;
import com.rallydev.rest.util.Fetch;
import com.rallydev.rest.util.QueryFilter;
import com.versionone.Oid;
import com.versionone.apiclient.Asset;
import com.versionone.apiclient.AttributeSelection;
import com.versionone.apiclient.Query;
import com.versionone.apiclient.Services;
import com.versionone.apiclient.V1Connector;
import com.versionone.apiclient.exceptions.V1Exception;
import com.versionone.apiclient.interfaces.IAssetType;
import com.versionone.apiclient.interfaces.IAttributeDefinition;
import com.versionone.apiclient.interfaces.IServices;
import com.versionone.apiclient.services.QueryFind;
import com.versionone.apiclient.services.QueryResult;

public class VersionOne implements IScrumTool {
	private String host = "https://www53.v1host.com/MobilityTeam";
	private static final String USER_REQ_PARAM = "/User";
	private static final String USER_AGENT_HEADER_APPNAME = "AppName";
	private static final String USER_AGENT_HEADER_VERSION = "1.0";
	private static final String USER_QUERY_PARAM = "User";
	private static final String TASK_QUERY_PARAM = "Tasks";
	private static final String USERNAME_FETCH_STRING = "UserName";
	private static final String DISPLAYNAME_FETCH_STRING = "DisplayName";
	private static final String EMAIL_FETCH_STRING = "EmailAddress";
	private static final String FIRSTNAME_FETCH_STRING = "FirstName";
	private static final String MIDDLENAME_FETCH_STRING = "MiddleName";
	private static final String LASTNAME_FETCH_STRING = "LastName";
	private static final String FETCH_NAME_STRING = "Name";
	private static final String FETCH_FORMATTEDID_STRING = "FormattedID";
	private static final String FETCH_WORKPRODUCT_STRING = "WorkProduct";
	private static final String FETCH_OWNER_STRING = "Owner";
	private static final String FETCH_EMAIL_STRING = "Email";
	private static final String QUERY_FILTER_ATRIBUTE_NAME_STRING = "Name";
	private static final String QUERY_FILTER_ATRIBUTE_FORMATEDID_STRING = "FormattedID";
	private static final String QUERY_FILTER_ATRIBUTE_WORKPRODUCT_FORMATEDID_STRING = "WorkProduct.FormattedID";
	private static final String ATTRIBUTE_PARENT_NAME_STRING = "Parent.Name";
	private static final String ATTRIBUTE_ESTIMATE_STRING = "Estimate";
	private static final String ATTRIBUTE_OWNER_EMAIL_STRING = "Owners.Email";
	private static final String ATTRIBUTE_TASK_STATE_STRING = "AssetState";
	private static final String ATTRIBUTE_ACTUALS_STRING = "Actuals";
	private static final String ATTRIBUTE_TODO_STRING = "ToDo";
	private static final String ATTRIBUTE_ESTIMATED_DONE_STRING = "EstimatedDone";
	private static final String QUERY_FILTER_ATRIBUTE_OWNER_EMAIL_STRING = "Owner.EmailAddress";
	private static final String QUERY_FILTER_OPERATOR_CONTAINS_STRING = "contains";
	private static final String QUERY_FILTER_ATRIBUTE_EQUALS_STRING = "=";

	private Context context;

	// 11.qXLghjmul0ITSNOV3FShMM40+Xw=
	@Override
	public boolean isValidUser(String apiKey) throws ScrumException {
		boolean isValidUser = false;
		try {

			V1Connector connector = V1Connector.withInstanceUrl(host)
					.withUserAgentHeader(USER_AGENT_HEADER_APPNAME, USER_AGENT_HEADER_VERSION).withAccessToken(apiKey)
					.build();

			IServices services = new Services(connector);

			Oid oid = services.getLoggedIn();
			if (null != oid) {
				isValidUser = true;
			}

		} catch (IOException ioE) {
			context.getLogger().log("IOException  occured in isValidUser of Class VersionOne" + ioE);
			System.out.println("IOException occured in isValidUser of Class VersionOne" + ioE);
			throw new ScrumException("IOException exception occured in isValidUser of Class VersionOne", ioE);
		} catch (V1Exception v1e) {
			// if(v1e.getMessage().contains("401")) {
			//
			// }
			context.getLogger().log("V1Exception  occured in isValidUser of Class VersionOne" + v1e);
			System.out.println("V1Exception occured in isValidUser of Class VersionOne" + v1e);
			throw new ScrumException("V1Exception exception occured in isValidUser of Class VersionOne", v1e);
		} finally {
		}

		return isValidUser;
	}

	// @Override
	// public BaseResponse getAllUsers(String apiKey) throws ScrumException {
	// RallyRestApi restApi = null;
	// try {
	// return null
	// }
	// } catch (IOException ioE) {
	// ioE.printStackTrace();
	// context.getLogger().log(
	// "IOException exception occured in getAllUsers of Class Rally"
	// + ioE);
	// System.out
	// .println("IOException exception occured in getAllUsers of Class Rally"
	// + ioE);
	// throw new ScrumException(
	// "IOException exception occured in getAllUsers of Class Rally",
	// ioE);
	// } catch (URISyntaxException uriE) {
	// uriE.printStackTrace();
	// context.getLogger().log(
	// "URIException exception occured in getAllUsers of Class Rally"
	// + uriE);
	// System.out
	// .println("URIException exception occured in getAllUsers of Class Rally"
	// + uriE);
	// throw new ScrumException(
	// "URIException exception occured in getAllUsers of Class Rally",
	// uriE);
	//
	// } finally {
	// if (null != restApi) {
	// try {
	// restApi.close();
	// } catch (IOException ioe) {
	// // Not throwing any exception from here as opertaion is
	// // successful and closing is the problem
	// context.getLogger().log(
	// "uriSyntaxException occured in fianlly of getAllUsers of Class Rally"
	// + ioe);
	// System.out
	// .println("URI exception occured in fianlly of isValidUser of Class Rally"
	// + ioe);
	// }
	// }
	// }
	// return null;
	//
	// }
	//
	// @Override
	// public void updateTask(String apiKey, String ref,
	// TaskUpdateRequest taskUpdateResponse) throws ScrumException {
	// RallyRestApi restApi = null;
	// try {
	// RallyTask task = taskUpdateResponse.getTaskToUpdate();
	// restApi = new RallyRestApi(new URI(host), apiKey);
	// JsonObject updatedTask = new JsonObject();
	// // replace with task obj details
	// if (null != task.getToDo()) {
	// updatedTask.addProperty("ToDo", task.getToDo());
	// }
	// if (null != task.getEstimate()) {
	// updatedTask.addProperty("Actuals", task.getEstimate());
	// }
	// if (null != task.getState()) {
	// updatedTask.addProperty("State", task.getState());
	// }
	//
	// UpdateRequest updateTaskRequest = new UpdateRequest(ref,
	// updatedTask);
	// // update the task
	// restApi.update(updateTaskRequest);
	// } catch (IOException ioE) {
	// ioE.printStackTrace();
	// context.getLogger().log(
	// "IOException exception occured in updateTask of Class Rally"
	// + ioE);
	// System.out
	// .println("IOException exception occured in updateTask of Class Rally"
	// + ioE);
	// throw new ScrumException(
	// "IOException exception occured in updateTask of Class Rally",
	// ioE);
	// } catch (URISyntaxException uriE) {
	// uriE.printStackTrace();
	// context.getLogger().log(
	// "URIException exception occured in updateTask of Class Rally"
	// + uriE);
	// System.out
	// .println("URIException exception occured in updateTask of Class Rally"
	// + uriE);
	// throw new ScrumException(
	// "URIException exception occured in updateTask of Class Rally",
	// uriE);
	//
	// } finally {
	// if (null != restApi) {
	// try {
	// restApi.close();
	// } catch (IOException ioe) {
	// // Not throwing any exception from here as opertaion is
	// // successful and closing is the problem
	// context.getLogger().log(
	// "uriSyntaxException occured in fianlly of getAllUsers of Class Rally"
	// + ioe);
	// System.out
	// .println("URI exception occured in fianlly of isValidUser of Class Rally"
	// + ioe);
	// }
	// }
	// }
	//
	// }
	//
	
	public BaseResponse getAllTasksForUser(String userApiKey, String tasksearchKeyWord, String userStorySearchKeyword)
			throws ScrumException {

		RallyRestApi restApi = null;
		try {
			if (!tasksearchKeyWord.isEmpty() || !userApiKey.isEmpty()) {

				V1Connector connector = V1Connector.withInstanceUrl(host)
						.withUserAgentHeader(USER_AGENT_HEADER_APPNAME, USER_AGENT_HEADER_VERSION)
						.withAccessToken("1.qXLghjmul0ITSNOV3FShMM40+Xw=").build();
				IServices services = new Services(connector);
				// code to get currnet Member
				IAssetType memberAssetType = services.getMeta().getAssetType("Member");
				IAttributeDefinition memberNameAttribute = memberAssetType.getAttributeDefinition("Email");
				Query memberQuery = new Query(memberAssetType);
				memberQuery.getSelection().add(memberNameAttribute);
				QueryResult memberResult = services.retrieve(memberQuery);
				Asset[] member = memberResult.getAssets();
				Object memberEmail = member[0].getAttribute(memberNameAttribute).getValue();

				// Oid oid = services.getLoggedIn();
				IAssetType taskType = services.getMeta().getAssetType(TASK_QUERY_PARAM);
				IAttributeDefinition nameAttribute = taskType.getAttributeDefinition(FETCH_NAME_STRING);
				IAttributeDefinition estimateAttribute = taskType.getAttributeDefinition(ATTRIBUTE_ESTIMATE_STRING);
				IAttributeDefinition parentAttribute = taskType.getAttributeDefinition(ATTRIBUTE_PARENT_NAME_STRING);
				IAttributeDefinition ownerEmailAttribute = taskType
						.getAttributeDefinition(ATTRIBUTE_OWNER_EMAIL_STRING);
				IAttributeDefinition taskStateAttribute = taskType.getAttributeDefinition(ATTRIBUTE_TASK_STATE_STRING);
				IAttributeDefinition actualsAttribute = taskType.getAttributeDefinition(ATTRIBUTE_ACTUALS_STRING);
				IAttributeDefinition estimatedDoneAttribute = taskType
						.getAttributeDefinition(ATTRIBUTE_ESTIMATED_DONE_STRING);
				IAttributeDefinition todoAttribute = taskType.getAttributeDefinition(ATTRIBUTE_TODO_STRING);
				Query query = new Query(taskType);

				query.getSelection().add(nameAttribute);
				query.getSelection().add(estimateAttribute);
				query.getSelection().add(parentAttribute);
				query.getSelection().add(ownerEmailAttribute);
				query.getSelection().add(taskStateAttribute);
				query.getSelection().add(actualsAttribute);
				query.getSelection().add(estimatedDoneAttribute);
				query.getSelection().add(todoAttribute);
				AttributeSelection taskattributeSelection = new AttributeSelection();
				taskattributeSelection.add(nameAttribute);
				AttributeSelection usAttributeSelection = new AttributeSelection();
				usAttributeSelection.add(parentAttribute);
				AttributeSelection emailAttributeSelection = new AttributeSelection();
				emailAttributeSelection.add(ownerEmailAttribute);
				// Search for task based on keyword
				query.setFind(new QueryFind(tasksearchKeyWord, taskattributeSelection));
				query.setFind(new QueryFind(userStorySearchKeyword, usAttributeSelection));
				query.setFind(new QueryFind(memberEmail.toString(), emailAttributeSelection));
				QueryResult result = services.retrieve(query);
				Asset[] task = result.getAssets();
				Object taskName = task[0].getAttribute(ownerEmailAttribute).getValue();

			}
		} catch (IOException ioE) {
			ioE.printStackTrace();
			context.getLogger().log("IOException exception occured in getAllTasksForUser of Class Rally" + ioE);
			System.out.println("IOException exception occured in getAllUsers of Class Rally" + ioE);
			throw new ScrumException("IOException exception occured in getAllUsers of Class Rally", ioE);
		} catch (V1Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (null != restApi) {
				try {
					restApi.close();
				} catch (IOException ioe) {
					// Not throwing any exception from here as opertaion is
					// successful and closing is the problem
					context.getLogger()
							.log("uriSyntaxException  occured in fianlly of getAllUsers of Class Rally" + ioe);
					System.out.println("URI exception occured in fianlly of isValidUser of Class Rally" + ioe);
				}
			}
		}
		return null;

	}

	public static void main(String args[]) throws IOException, URISyntaxException, ScrumException {

		String host = "https://rally1.rallydev.com";
		// String username = "raja.pateriya@impetus.co.in";
		// String password = "impetus123";
		String applicationName = "Find Iterations by Release Dates and Stories";
		VersionOne rallyUtil = new VersionOne();
		// rallyUtil.isValidUser("");
		// rallyUtil.getAllTasksForUser("11.qXLghjmul0ITSNOV3FShMM40+Xw=",
		// "TFS",
		// "TFS");
		rallyUtil.checkCode("PT120M");
		RallyRestApi restApi = null;

		// _OMd81n5QyWzL7UPAg6roNhsD7vN0g7slgTFCm8OOo
		// https://rally1.rallydev.com/login.

		try {
			restApi = new RallyRestApi(new URI(host), "_Dl8a4ejcQ4WTcq4ExdxbvF3mb1d2eUsNo6v43LThc");
			// RallyRestApi restApi2 = new RallyRestApi(new URI(host),
			// "_OMd81n5QyWzL7UPAg6roNhsD7vN0g7slgTFCm8OOasdasdo");
			restApi.setApplicationName(applicationName);
			// restApi.setProxy(new URI("https://rally1.rallydev.com"));

			System.out.println(restApi.getWsapiVersion());
			QueryRequest taskRequest = new QueryRequest("Tasks");
			taskRequest.setFetch(new Fetch("Name", "FormattedID", "WorkProduct", "Owner", "Email"));
			// taskRequest.setQueryFilter(new QueryFilter("Name", "contains",
			// "and").and( new QueryFilter("WorkProduct.Name", "contains",
			// "to")));
			taskRequest.setQueryFilter(new QueryFilter("Owner.EmailAddress", "contains", "Raja"));
			JsonArray tasks = restApi.query(taskRequest).getResults();
			RallyTask[] userArray = new Gson().fromJson(tasks.toString(), RallyTask[].class);
			JsonObject updatedTask = new JsonObject();
			updatedTask.addProperty("ToDo", "18");
			updatedTask.addProperty("Actuals", "6");

			JsonObject taskJsonObj = tasks.get(0).getAsJsonObject();
			String taskRefrence = taskJsonObj.get("_ref").getAsString();
			UpdateRequest updateTaskRequest = new UpdateRequest(taskRefrence, updatedTask);
			UpdateResponse updateTaskResponse = restApi.update(updateTaskRequest);

			// String ref = Ref.getRelativeRef(existUserStoryJsonObject
			// .get("_ref").getAsString());
			// Request getRequest = new GetRequest(ref);
			// GetResponse getResponse = restApi.get((GetRequest) getRequest);
			// JsonObject obj = getResponse.getObject();
			// String state = obj.get("ScheduleState").getAsString();
			// System.out.println("\nExistinge definition : " + state);
			//
			// // Update userstory/defect details
			// // Here example to update state of user story as In-Progress
			// System.out.println("\nUpdatingstory/defect state...");
			// JsonObject updatedDefect = new JsonObject();
			// updatedDefect.addProperty("ScheduleState", "In-Progress");
			// UpdateRequest updateRequest = new UpdateRequest(ref,
			// updatedDefect);
			// UpdateResponse updateResponse = restApi.update(updateRequest);
			// obj = updateResponse.getObject();
			//
			// System.out.println("existUserStoryRef : "
			// + getResponse.getObject().toString());

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			restApi.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.aws.lambda.taskupdaterhook.scrum.tool.IScrumTool#getAllUsers(java
	 * .lang.String)
	 */
	@Override
	public BaseResponse getAllUsers(String apiKey) throws ScrumException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.aws.lambda.taskupdaterhook.scrum.tool.IScrumTool#updateTask(java.
	 * lang.String, java.lang.String,
	 * com.aws.lambda.taskupdaterhook.scrum.tool.TaskUpdateResponse)
	 */
	
	public BaseResponse updateTask(String apiKey, String ref, TaskUpdateResponse taskUpdateResponse)
			throws ScrumException {
		return taskUpdateResponse;
		// TODO Auto-generated method stub

	}

	public String checkCode(String effortDurVal) {
		int hours = 0;
		Duration xmlDuration;
		try {
			xmlDuration = DatatypeFactory.newInstance().newDuration(effortDurVal);

			hours = xmlDuration.getHours();

			int minutes = xmlDuration.getMinutes();

			if (minutes > 0) {

				int addOnMins = minutes % 60;
				float addOnHour = addOnMins / 60.0f;

				float hoursValue = minutes / 60;
				hoursValue = hoursValue + addOnHour;

				float convertedHours = hours + hoursValue;
				String effortDurReadableVal = "" + convertedHours;
				System.out.println("values is " + effortDurReadableVal);
				return effortDurReadableVal;
			}
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "" + hours;
	}

	@Override
	public TasksResponse getAllTasksForUser(TaskFetchRequest taskReq) throws ScrumException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TaskUpdateResponse updateTask(TaskUpdateRequest taskUpdateRequest) throws ScrumException {
		// TODO Auto-generated method stub
		return null;
	}

}