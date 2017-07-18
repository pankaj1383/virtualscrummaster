package com.aws.lambda.userregistration.scrum;

import java.io.IOException;
import java.net.URISyntaxException;

import com.aws.lambda.userregistration.scrum.exception.ScrumException;
import com.aws.lambda.userregistration.scrum.request.TaskFetchRequest;
import com.aws.lambda.userregistration.scrum.request.TaskUpdateRequest;
import com.aws.lambda.userregistration.scrum.response.TaskUpdateResponse;
import com.aws.lambda.userregistration.scrum.response.TasksResponse;

/**
 * 
 * @author raja.pateriya
 * 
 */
public interface IScrumTool {
	/**
	 * 
	 * @param apiKey
	 * @return true if user is valid else false
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public boolean isValidUser(String apiKey) throws ScrumException;

	/**
	 * 
	 * @param apiKey
	 * @return User Array
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public BaseResponse getAllUsers(String apiKey) throws ScrumException;

	/**
	 * 
	 * @param userApiKey
	 * @param taskSearchKeyWord
	 * @param userStorySearchKeyword
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws ScrumException
	 */
	public TasksResponse getAllTasksForUser(TaskFetchRequest taskReq)
			throws ScrumException;

	/**
	 * @param apiKey
	 * @param ref
	 * @param taskresponse
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public TaskUpdateResponse updateTask(TaskUpdateRequest taskUpdateRequest)
			throws ScrumException;

}
