package com.aws.lambda.userregistration.pojo;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentIntent {

	public static final String CONFIRMATION_STATUS_NONE = "None";
	public static final String CONFIRMATION_STATUS_CONFIRMED = "Confirmed";
	public static final String CONFIRMATION_STATUS_DENIED = "Denied";

	private String name;
	private Map<String, String> slots;
	private String confirmationStatus;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getSlots() {
		return slots;
	}

	public void setSlots(Map<String, String> slots) {
		this.slots = slots;
	}

	public String getConfirmationStatus() {
		return confirmationStatus;
	}

	public void setConfirmationStatus(String confirmationStatus) {
		this.confirmationStatus = confirmationStatus;
	}

}
