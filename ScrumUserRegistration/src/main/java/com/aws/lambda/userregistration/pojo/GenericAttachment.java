package com.aws.lambda.userregistration.pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GenericAttachment {

	private String title;
	private String subTitle;
	private String imageUrl;
	private String attachmentLinkUrl;
	private List<Button> buttons;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getAttachmentLinkUrl() {
		return attachmentLinkUrl;
	}

	public void setAttachmentLinkUrl(String attachmentLinkUrl) {
		this.attachmentLinkUrl = attachmentLinkUrl;
	}

	public List<Button> getButtons() {
		return buttons;
	}

	public void setButtons(List<Button> buttons) {
		this.buttons = buttons;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

}
