package com.aws.lambda.userregistration;

import java.util.Map;

import com.aws.lambda.userregistration.pojo.DialogAction;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LexResponse {

	private Map<String, String> sessionAttributes;
	private DialogAction dialogAction;

	public Map<String, String> getSessionAttributes() {
		return sessionAttributes;
	}

	public void setSessionAttributes(Map<String, String> sessionAttributes) {
		this.sessionAttributes = sessionAttributes;
	}

	public DialogAction getDialogAction() {
		return dialogAction;
	}

	public void setDialogAction(DialogAction dialogAction) {
		this.dialogAction = dialogAction;
	}
}
