package com.aws.lambda.taskupdaterhook.pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseCard {

	public static final String CONTENT_TYPE_LEX_CARD = "application/vnd.amazonaws.card.generic";
	private int version;
	private String contentType;
	private List<GenericAttachment> genericAttachments;
	private static int versionCount;

	public ResponseCard() {
		contentType = CONTENT_TYPE_LEX_CARD;
		versionCount++;
		version = versionCount;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public List<GenericAttachment> getGenericAttachments() {
		return genericAttachments;
	}

	public void setGenericAttachments(List<GenericAttachment> genericAttachments) {
		this.genericAttachments = genericAttachments;
	}

}
