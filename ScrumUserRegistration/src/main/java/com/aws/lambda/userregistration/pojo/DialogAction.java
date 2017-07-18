package com.aws.lambda.userregistration.pojo;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DialogAction {

	public static final String FULFILLMENT_STATE_FULFILLED = "Fulfilled";
	public static final String FULFILLMENT_STATE_FAILED = "Failed";

	public static final String DIALOG_TYPE_CLOSE = "Close";
	public static final String DIALOG_TYPE_CONFIRM_INTENT = "ConfirmIntent";
	public static final String DIALOG_TYPE_DELEGATE = "Delegate";
	public static final String DIALOG_TYPE_ELICIT_INTENT = "ElicitIntent";
	public static final String DIALOG_TYPE_ELICIT_SLOT = "ElicitSlot";

	private String type;
	private String fulfillmentState;
	private Message message;
	private String intentName;
	private Map<String, String> slots;
	private String slotToElicit;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFulfillmentState() {
		return fulfillmentState;
	}

	public void setFulfillmentState(String fulfillmentState) {
		this.fulfillmentState = fulfillmentState;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public String getIntentName() {
		return intentName;
	}

	public void setIntentName(String intentName) {
		this.intentName = intentName;
	}

	public Map<String, String> getSlots() {
		return slots;
	}

	public void setSlots(Map<String, String> slots) {
		this.slots = slots;
	}

	public String getSlotToElicit() {
		return slotToElicit;
	}

	public void setSlotToElicit(String slotToElicit) {
		this.slotToElicit = slotToElicit;
	}

}
