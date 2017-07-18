package com.aws.lambda.taskupdaterhook.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {

	public static final String CONTENT_TYPE_TEXT = "PlainText";
	public static final String CONTENT_TYPE_SSML = "SSML";

	private String contentType;
	private String content;

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
