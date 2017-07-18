package com.aws.lambda.taskupdaterhook.scrum.request;

import com.aws.lambda.taskupdaterhook.scrum.BaseRequest;
import com.aws.lambda.taskupdaterhook.scrum.exception.ScrumException;
import com.aws.lambda.taskupdaterhook.scrum.pojo.Task;

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
