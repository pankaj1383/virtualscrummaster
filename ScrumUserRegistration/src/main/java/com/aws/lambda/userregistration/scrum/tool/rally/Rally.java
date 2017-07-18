package com.aws.lambda.userregistration.scrum.tool.rally;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import com.aws.lambda.userregistration.scrum.BaseResponse;
import com.aws.lambda.userregistration.scrum.IScrumTool;
import com.aws.lambda.userregistration.scrum.exception.ScrumException;
import com.aws.lambda.userregistration.scrum.pojo.Task;
import com.aws.lambda.userregistration.scrum.pojo.User;
import com.aws.lambda.userregistration.scrum.request.TaskFetchRequest;
import com.aws.lambda.userregistration.scrum.request.TaskUpdateRequest;
import com.aws.lambda.userregistration.scrum.response.TaskUpdateResponse;
import com.aws.lambda.userregistration.scrum.response.TasksResponse;
import com.aws.lambda.userregistration.scrum.response.UsersResponse;
import com.aws.lambda.userregistration.scrum.tool.rally.pojo.RallyTask;
import com.aws.lambda.userregistration.scrum.tool.rally.pojo.RallyUser;
import com.aws.lambda.userregistration.utils.Logger;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.GetRequest;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.request.UpdateRequest;
import com.rallydev.rest.response.GetResponse;
import com.rallydev.rest.response.QueryResponse;
import com.rallydev.rest.response.UpdateResponse;
import com.rallydev.rest.util.Fetch;
import com.rallydev.rest.util.QueryFilter;

public class Rally implements IScrumTool {
	private String host = "https://rally1.rallydev.com";
	private static final String USER_REQ_PARAM = "/User";
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
	private static final String QUERY_FILTER_ATRIBUTE_WORKPRODUCT_NAME_STRING = "WorkProduct.Name";
	private static final String QUERY_FILTER_ATRIBUTE_OWNER_EMAIL_STRING = "Owner.EmailAddress";
	private static final String QUERY_FILTER_OPERATOR_CONTAINS_STRING = "contains";
	private static final String QUERY_FILTER_ATRIBUTE_EQUALS_STRING = "=";
	private static final String FETCH_ACTUALS_STRING = "Actuals";
	private String FETCH_DONE_HR_STRING = "c_DoneHr";
	private String FETCH_ESTIMATE_STRING = "Estimate";
	private String FETCH_STATE_STRING = "State";
	private String FETCH_TODO_STRING = "ToDo";

	public boolean isValidUser(String apiKey) throws ScrumException {
		boolean isValidUser = false;
		RallyRestApi restApi = null;
		try {
			restApi = new RallyRestApi(new URI(host), apiKey);

			// No parameter available to verify user exist or not so making an
			// api call if user exis the block will get execute otherwise it
			// will throw exception with error message containg "401"
			GetRequest getUserReq = new GetRequest(USER_REQ_PARAM);
			restApi.get(getUserReq);
			isValidUser = true;

		} catch (URISyntaxException uriSyntaxException) {
			// TODO Auto-generated catch block
			uriSyntaxException.printStackTrace();
			Logger.log("uriSyntaxException  occured in getAllUsers of Class Rally" + uriSyntaxException);
			System.out.println("URI exception occured in isValidUser of Class Rally" + uriSyntaxException);
			throw new ScrumException("URI exception occured in isValidUser of Class Rally", uriSyntaxException);
		} catch (IOException ioE) {
			System.out.println("User is not authorized");
		} finally {
			if (null != restApi) {
				try {
					restApi.close();
				} catch (IOException ioe) {
					// Not throwing any exception from here as opertaion is
					// successful and closing is the problem
					Logger.log("uriSyntaxException  occured in fianlly of isValidUser of Class Rally" + ioe);
					System.out.println("URI exception occured in fianlly of isValidUser of Class Rally" + ioe);
				}
			}
		}

		return isValidUser;
	}

	public BaseResponse getAllUsers(String apiKey) throws ScrumException {
		RallyRestApi restApi = null;
		UsersResponse usersResponse = null;
		try {
			restApi = new RallyRestApi(new URI(host), apiKey);
			if (null != restApi) {
				QueryRequest userRequest = new QueryRequest(USER_QUERY_PARAM);
				// set items to be fetched from User details
				userRequest.setFetch(new Fetch(USERNAME_FETCH_STRING, FIRSTNAME_FETCH_STRING, MIDDLENAME_FETCH_STRING,
						LASTNAME_FETCH_STRING, EMAIL_FETCH_STRING, DISPLAYNAME_FETCH_STRING));

				QueryResponse userQueryResponse = restApi.query(userRequest);
				String[] userReqError = userQueryResponse.getErrors();
				if (null != userReqError && userReqError.length > 0) {
					UsersResponse userResp = new UsersResponse();
					userResp.setStatusCode(BaseResponse.STATUS_SCRUM_ERROR);
					userResp.setErrors(userReqError);
					return userResp;
				} else {
					JsonArray userJsonArray = userQueryResponse.getResults();
					// RallyUser[] userArray = new
					// Gson().fromJson(jsonarrayString.toString(),
					// RallyUser[].class);

					ObjectMapper mapper = new ObjectMapper();
					mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

					User[] userArray = mapper.readValue(userJsonArray.toString(), RallyUser[].class);
					usersResponse = new UsersResponse();
					usersResponse.setUsers(Arrays.asList(userArray));
					usersResponse.setStatusCode(UsersResponse.STATUS_OK);

					return usersResponse;
				}
			}
		} catch (IOException ioE) {
			if (ioE.getMessage().contains("401")) {
				BaseResponse baseResponse = new BaseResponse();
				baseResponse.setStatusCode(TasksResponse.STATUS_UNAUTHORIZED);
				return baseResponse;
			}
			ioE.printStackTrace();
			Logger.log("IOException exception occured in getAllUsers of Class Rally" + ioE);
			System.out.println("IOException exception occured in getAllUsers of Class Rally" + ioE);
			throw new ScrumException("IOException exception occured in getAllUsers of Class Rally", ioE);
		} catch (URISyntaxException uriE) {
			uriE.printStackTrace();
			Logger.log("URIException exception occured in getAllUsers of Class Rally" + uriE);
			System.out.println("URIException exception occured in getAllUsers of Class Rally" + uriE);
			throw new ScrumException("URIException exception occured in getAllUsers of Class Rally", uriE);

		} finally {
			if (null != restApi) {
				try {
					restApi.close();
				} catch (IOException ioe) {
					// Not throwing any exception from here as opertaion is
					// successful and closing is the problem
					Logger.log("uriSyntaxException  occured in fianlly of getAllUsers of Class Rally" + ioe);
					System.out.println("URI exception occured in fianlly of getAllUsers of Class Rally" + ioe);
				}
			}
		}
		return usersResponse;

	}

	public TaskUpdateResponse updateTask(TaskUpdateRequest taskUpdateReq) throws ScrumException {
		RallyRestApi restApi = null;
		String apiKey = taskUpdateReq.getApiKey();

		try {
			Task task = taskUpdateReq.getTaskToUpdate();
			restApi = new RallyRestApi(new URI(host), apiKey);
			JsonObject updatedTask = new JsonObject();
			// replace with task obj details
			if (null != task.getToDo()) {
				updatedTask.addProperty("ToDo", Double.parseDouble(task.getToDo().trim()));
			}
			if (null != task.getActuals()) {
				updatedTask.addProperty("Actuals", Double.parseDouble(task.getActuals().trim()));
			}
			if (null != task.getState()) {
				updatedTask.addProperty("State", task.getState());
			}

			Gson gson = new Gson();
			Logger.log("Update request input arg obj json= " + gson.toJson(taskUpdateReq));

			UpdateRequest updateTaskRequest = new UpdateRequest(taskUpdateReq.getTaskToUpdate().getReferenceID(),
					updatedTask);
			// Logger.log("Update rally request obj json= "
			// + gson.toJson(updateTaskRequest));

			// update the task
			UpdateResponse response = restApi.update(updateTaskRequest);
			String[] updateError = response.getErrors();

			if (null != updateError && updateError.length > 0) {
				TaskUpdateResponse taskUpdareResp = new TaskUpdateResponse();
				taskUpdareResp.setStatusCode(BaseResponse.STATUS_SCRUM_ERROR);
				taskUpdareResp.setErrors(updateError);
				return taskUpdareResp;
			} else {
				JsonObject responseObj = response.getObject();
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

				Task rallyTask = mapper.readValue(responseObj.toString(), RallyTask.class);
				// return status 200 is update opertaion is successful
				TaskUpdateResponse taskUpdareResp = new TaskUpdateResponse();
				taskUpdareResp.setStatusCode(TaskUpdateResponse.STATUS_OK);
				taskUpdareResp.setTaskUpdated(rallyTask);
				return taskUpdareResp;
			}
		} catch (IOException ioE) {
			TaskUpdateResponse taskUpdateResp = new TaskUpdateResponse();
			if (ioE.getMessage().contains("401")) {
				;
				taskUpdateResp.setStatusCode(TaskUpdateResponse.STATUS_UNAUTHORIZED);
				return taskUpdateResp;
			}
			ioE.printStackTrace();
			Logger.log("IOException exception occured in updateTask of Class Rally" + ioE);
			taskUpdateResp.setStatusCode(TasksResponse.STATUS_INTERNAL_SERVER_ERROR);

		} catch (URISyntaxException uriE) {
			uriE.printStackTrace();
			Logger.log("URIException exception occured in updateTask of Class Rally" + uriE);
			TaskUpdateResponse taskUpdateResp = new TaskUpdateResponse();
			taskUpdateResp.setStatusCode(TasksResponse.STATUS_INTERNAL_SERVER_ERROR);

		} catch (Exception exp) {
			Logger.log("URIException exception occured in updateTask of Class Rally" + exp);
			System.out.println("URIException exception occured in updateTask of Class Rally" + exp);
			throw new ScrumException("URIException exception occured in updateTask of Class Rally", exp);
		} finally {
			if (null != restApi) {
				try {
					restApi.close();
				} catch (IOException ioe) {
					// Not throwing any exception from here as opertaion is
					// successful and closing is the problem
					Logger.log("uriSyntaxException  occured in fianlly of updateTask of Class Rally" + ioe);
					System.out.println("URI exception occured in fianlly of updateTask of Class Rally" + ioe);
				}
			}
		}
		return null;

	}

	public TasksResponse getAllTasksForUser(TaskFetchRequest taskReq) throws ScrumException {

		String userApiKey = taskReq.getApiKey();
		String tasksearchKeyWord = taskReq.getSearchTaskKeyword();
		String userStorySearchKeyword = taskReq.getSearchUSKeyword();

		long startTime = System.currentTimeMillis();
		RallyRestApi restApi = null;
		try {
			if (!tasksearchKeyWord.isEmpty() && !userApiKey.isEmpty()) {

				restApi = new RallyRestApi(new URI(host), userApiKey);
				GetRequest getUserReq = new GetRequest(USER_REQ_PARAM);
				if (null != restApi) {
					GetResponse currUserDetailResponse = restApi.get(getUserReq);
					if (null != currUserDetailResponse) {
						JsonObject currUserDetailObj = currUserDetailResponse.getObject();
						// fetch email address of current user
						JsonElement currUserEmailJsonElem = currUserDetailObj.get(EMAIL_FETCH_STRING);
						if (null != currUserEmailJsonElem) {
							String currUserEmail = currUserEmailJsonElem.getAsString();

							QueryRequest taskRequest = new QueryRequest(TASK_QUERY_PARAM);

							// set items to fetch from tasks
							taskRequest.setFetch(new Fetch(FETCH_NAME_STRING, FETCH_FORMATTEDID_STRING,
									FETCH_WORKPRODUCT_STRING, FETCH_OWNER_STRING, FETCH_EMAIL_STRING,
									FETCH_ACTUALS_STRING, FETCH_DONE_HR_STRING, FETCH_ESTIMATE_STRING,
									FETCH_STATE_STRING, FETCH_TODO_STRING));

							// setting query filter to fetch task if contains
							// task search
							// keyword and user story serach keyword for current
							// user

							taskRequest.setQueryFilter(createFilterByTaskUSEmail(tasksearchKeyWord,
									userStorySearchKeyword, currUserEmail));
							QueryResponse taskRes = restApi.query(taskRequest);

							String[] taskReqError = taskRes.getErrors();
							if (null != taskReqError && taskReqError.length > 0) {
								TasksResponse taskResp = new TasksResponse();
								taskResp.setStatusCode(BaseResponse.STATUS_SCRUM_ERROR);
								taskResp.setErrors(taskReqError);
								return taskResp;
							} else {

								JsonArray taskJsonArray = taskRes.getResults();
								if (null != taskJsonArray) {

									ObjectMapper mapper = new ObjectMapper();
									mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

									Task[] taskArray = mapper.readValue(taskJsonArray.toString(), RallyTask[].class);

									TasksResponse tasksResponse = new TasksResponse();
									tasksResponse.setStatusCode(TasksResponse.STATUS_OK);
									tasksResponse.setTasks(Arrays.asList(taskArray));
									Logger.log("Time taken for all tasks in millisec= "
											+ (System.currentTimeMillis() - startTime));
									return tasksResponse;
								}
							}
						}
					}
				}
			}
		} catch (IOException ioE) {
			TasksResponse tasksResponse = new TasksResponse();
			if (ioE.getMessage().contains("401")) {

				tasksResponse.setStatusCode(TasksResponse.STATUS_UNAUTHORIZED);
				return tasksResponse;
			}
			ioE.printStackTrace();
			Logger.log("IOException exception occured in getAllTasksForUser of Class Rally" + ioE);
			tasksResponse.setStatusCode(TasksResponse.STATUS_INTERNAL_SERVER_ERROR);
			return tasksResponse;
		} catch (URISyntaxException uriE) {
			uriE.printStackTrace();
			Logger.log("URIException exception occured in getAllTasksForUser of Class Rally" + uriE);
			System.out.println("URIException exception occured in getAllTasksForUser of Class Rally" + uriE);
			TasksResponse tasksResponse = new TasksResponse();
			tasksResponse.setStatusCode(TasksResponse.STATUS_INTERNAL_SERVER_ERROR);
			return tasksResponse;

		} catch (Exception exp) {
			Logger.log("URIException exception occured in updateTask of Class Rally" + exp);
			System.out.println("URIException exception occured in updateTask of Class Rally" + exp);
			TasksResponse tasksResponse = new TasksResponse();
			tasksResponse.setStatusCode(TasksResponse.STATUS_INTERNAL_SERVER_ERROR);
			return tasksResponse;
		} finally {
			if (null != restApi) {
				try {
					restApi.close();
				} catch (IOException ioe) {
					// Not throwing any exception from here as opertaion is
					// successful and closing is the problem
					Logger.log("uriSyntaxException  occured in fianlly of getAllTasksForUser of Class Rally" + ioe);
					System.out.println("URI exception occured in fianlly of getAllTasksForUser of Class Rally" + ioe);
				}
			}
		}
		return null;

	}

	private QueryFilter createFilterByTaskUSEmail(String taskKeyword, String andUserStoryKeyword, String andEmail) {

		if (null == andUserStoryKeyword || andUserStoryKeyword.isEmpty()) {

			QueryFilter queryFilter = (new QueryFilter(QUERY_FILTER_ATRIBUTE_NAME_STRING,
					QUERY_FILTER_OPERATOR_CONTAINS_STRING, taskKeyword)
							.or(new QueryFilter(QUERY_FILTER_ATRIBUTE_FORMATEDID_STRING,
									QUERY_FILTER_OPERATOR_CONTAINS_STRING, taskKeyword)))
											.and(new QueryFilter(QUERY_FILTER_ATRIBUTE_OWNER_EMAIL_STRING,
													QUERY_FILTER_ATRIBUTE_EQUALS_STRING, andEmail));
			return queryFilter;

		}

		else {
			QueryFilter queryFilter = ((new QueryFilter(QUERY_FILTER_ATRIBUTE_NAME_STRING,
					QUERY_FILTER_OPERATOR_CONTAINS_STRING, taskKeyword)
							.and(new QueryFilter(QUERY_FILTER_ATRIBUTE_WORKPRODUCT_NAME_STRING,
									QUERY_FILTER_OPERATOR_CONTAINS_STRING, andUserStoryKeyword)))
											.or(new QueryFilter(QUERY_FILTER_ATRIBUTE_FORMATEDID_STRING,
													QUERY_FILTER_OPERATOR_CONTAINS_STRING, taskKeyword)
															.and(new QueryFilter(
																	QUERY_FILTER_ATRIBUTE_WORKPRODUCT_FORMATEDID_STRING,
																	QUERY_FILTER_OPERATOR_CONTAINS_STRING,
																	andUserStoryKeyword))))
																			.and(new QueryFilter(
																					QUERY_FILTER_ATRIBUTE_OWNER_EMAIL_STRING,
																					QUERY_FILTER_ATRIBUTE_EQUALS_STRING,
																					andEmail));

			return queryFilter;
		}

	}

	public static void main(String args[]) throws IOException, URISyntaxException, ScrumException {

		Rally rallyUtil = new Rally();
		TaskFetchRequest taskReq = new TaskFetchRequest("_Dl8a4ejcQ4WTcq4ExdxbvF3mb1d2eUsNo6v43LThc");
		taskReq.setSearchTaskKeyword("TFS");
		// TasksResponse re = rallyUtil.getAllTasksForUser(taskReq);

		TaskUpdateRequest taskUpdatedReq = new TaskUpdateRequest("_Dl8a4ejcQ4WTcq4ExdxbvF3mb1d2eUsNo6v43LThc");
		Task taskToUpdate = new RallyTask();
		taskToUpdate.setToDo("12.5");
		taskToUpdate.setActuals("6.5");
		taskToUpdate.setReferenceID("https://rally1.rallydev.com/slm/webservice/v2.0/task/132284686564");
		taskUpdatedReq.setTaskToUpdate(taskToUpdate);

		rallyUtil.updateTask(taskUpdatedReq);

	}

}