package com.aws.lambda.userregistration.scrum.response;

import com.aws.lambda.userregistration.scrum.BaseResponse;
import com.aws.lambda.userregistration.scrum.pojo.Task;


public class TaskUpdateResponse extends BaseResponse {

	private Task taskUpdated;
	

	public Task getTaskUpdated() {
		return taskUpdated;
	}

	public void setTaskUpdated(Task taskUpdated) {
		this.taskUpdated = taskUpdated;
	}



}
