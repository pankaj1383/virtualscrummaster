package com.aws.lambda.taskupdaterhook.scrum.request;

import java.util.List;

import com.aws.lambda.taskupdaterhook.scrum.BaseResponse;
import com.aws.lambda.taskupdaterhook.scrum.pojo.User;


public class UsersRequest extends BaseResponse {

	private List<User> users;

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

}
