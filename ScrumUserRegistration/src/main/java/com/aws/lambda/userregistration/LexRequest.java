package com.aws.lambda.userregistration;

import java.util.Map;

import com.aws.lambda.userregistration.pojo.Bot;
import com.aws.lambda.userregistration.pojo.CurrentIntent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LexRequest {

	public static final String INVOC_SOURCE_VALIDATION_DIALOG = "DialogCodeHook";
	public static final String INVOC_SOURCE_FULFILLMENT = "FulfillmentCodeHook";

	public static final String OUTPUT_DIALOG_MODE_TEXT = "Text";
	public static final String OUTPUT_DIALOG_MODE_VOICE = "Voice";

	private CurrentIntent currentIntent;
	private Bot bot;
	private Map<String, String> sessionAttributes;
	private String userId;
	private String inputTranscript;
	private String invocationSource;
	private String outputDialogMode;
	private String messageVersion;

	public CurrentIntent getCurrentIntent() {
		return currentIntent;
	}

	public void setCurrentIntent(CurrentIntent currentIntent) {
		this.currentIntent = currentIntent;
	}

	public Bot getBot() {
		return bot;
	}

	public void setBot(Bot bot) {
		this.bot = bot;
	}

	public Map<String, String> getSessionAttributes() {
		return sessionAttributes;
	}

	public void setSessionAttributes(Map<String, String> sessionAttributes) {
		this.sessionAttributes = sessionAttributes;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getInputTranscript() {
		return inputTranscript;
	}

	public void setInputTranscript(String inputTranscript) {
		this.inputTranscript = inputTranscript;
	}

	public String getInvocationSource() {
		return invocationSource;
	}

	public void setInvocationSource(String invocationSource) {
		this.invocationSource = invocationSource;
	}

	public String getOutputDialogMode() {
		return outputDialogMode;
	}

	public void setOutputDialogMode(String outputDialogMode) {
		this.outputDialogMode = outputDialogMode;
	}

	public String getMessageVersion() {
		return messageVersion;
	}

	public void setMessageVersion(String messageVersion) {
		this.messageVersion = messageVersion;
	}

}
