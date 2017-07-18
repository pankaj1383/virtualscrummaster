package com.aws.lambda.userregistration.scrum.request;

import com.aws.lambda.userregistration.scrum.BaseRequest;
import com.aws.lambda.userregistration.scrum.exception.ScrumException;
import com.aws.lambda.userregistration.scrum.pojo.Task;

public class TaskUpdateRequest extends BaseRequest {

	private Task taskToUpdate;


	/**
	 * @throws ScrumException
	 * 
	 */
	public TaskUpdateRequest(String apiKey) throws ScrumException {
		super(apiKey);
		// TODO Auto-generated constructor stub
	}

	public Task getTaskToUpdate() {
		return taskToUpdate;
	}

	public void setTaskToUpdate(Task taskToUpdate) {
		this.taskToUpdate = taskToUpdate;
	}


}
