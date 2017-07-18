package com.aws.lambda.userregistration.scrum;

import com.aws.lambda.userregistration.scrum.exception.ScrumException;

public class BaseRequest {

	private String apiKey;

	
	/**
	 * @throws ScrumException 
	 * 
	 */
	public BaseRequest(String apiKey) throws ScrumException {

		if (null == apiKey || apiKey.isEmpty()) {
			throw new ScrumException("API key is either empty or null");
		}	
	this.apiKey = apiKey;
	}
	
	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}


}
