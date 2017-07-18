package com.aws.lambda.userregistration.scrum.request;

import java.util.List;

import com.aws.lambda.userregistration.scrum.BaseRequest;
import com.aws.lambda.userregistration.scrum.exception.ScrumException;
import com.aws.lambda.userregistration.scrum.pojo.Task;

public class TaskFetchRequest extends BaseRequest {

	private String searchTaskKeyword;
	private String searchUSKeyword;
	

	public TaskFetchRequest(String apiKey) throws ScrumException {
		super(apiKey);
		

	}

	public String getSearchTaskKeyword() {
		return searchTaskKeyword;
	}

	public void setSearchTaskKeyword(String searchTaskKeyword) {
		this.searchTaskKeyword = searchTaskKeyword;
	}

	public String getSearchUSKeyword() {
		return searchUSKeyword;
	}

	public void setSearchUSKeyword(String searchUSKeyword) {
		this.searchUSKeyword = searchUSKeyword;
	}

	private List<Task> tasks;

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	

}
