package com.aws.lambda.taskupdaterhook.scrum.response;

import com.aws.lambda.taskupdaterhook.scrum.BaseResponse;
import com.aws.lambda.taskupdaterhook.scrum.pojo.Task;


public class TaskUpdateResponse extends BaseResponse {

	private Task taskUpdated;
	

	public Task getTaskUpdated() {
		return taskUpdated;
	}

	public void setTaskUpdated(Task taskUpdated) {
		this.taskUpdated = taskUpdated;
	}



}
