package com.aws.lambda.userregistration.scrum.response;

import java.util.List;

import com.aws.lambda.userregistration.scrum.BaseResponse;
import com.aws.lambda.userregistration.scrum.pojo.Task;


public class TasksResponse extends BaseResponse {

	private List<Task> tasks;

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

}
