package com.aws.lambda.taskupdaterhook.scrum.response;

import java.util.List;

import com.aws.lambda.taskupdaterhook.scrum.BaseResponse;
import com.aws.lambda.taskupdaterhook.scrum.pojo.Task;


public class TasksResponse extends BaseResponse {

	private List<Task> tasks;

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

}
