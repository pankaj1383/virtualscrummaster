package com.aws.lambda.rally.pojo;

import java.util.List;

public class UsersResponse extends BaseResponse {
	
	private List<User> users;

	public UsersResponse() {
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
}
