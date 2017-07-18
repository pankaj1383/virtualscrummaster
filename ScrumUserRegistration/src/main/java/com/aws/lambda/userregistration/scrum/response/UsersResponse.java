package com.aws.lambda.userregistration.scrum.response;

import java.util.List;

import com.aws.lambda.userregistration.scrum.BaseResponse;
import com.aws.lambda.userregistration.scrum.pojo.User;


public class UsersResponse extends BaseResponse {

	private List<User> users;

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

}
