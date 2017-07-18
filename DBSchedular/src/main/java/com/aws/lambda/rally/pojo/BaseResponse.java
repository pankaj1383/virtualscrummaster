package com.aws.lambda.rally.pojo;

public class BaseResponse {

	public static final int STATUS_OK = 200;
	public static final int STATUS_UNAUTHORIZED = 401;
	public static final int STATUS_INTERNAL_SERVER_ERROR = 500;
	public static final int STATUS_SCRUM_ERROR = 406;
	private int statusCode;

	private String[] errors;

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String[] getErrors() {
		return errors;
	}

	public void setErrors(String[] errors) {
		this.errors = errors;
	}

}
