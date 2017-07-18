package com.aws.lambda.taskupdaterhook.utils;

import java.io.IOException;

import com.aws.lambda.taskupdaterhook.LexRequest;
import com.aws.lambda.taskupdaterhook.LexResponse;
import com.aws.lambda.taskupdaterhook.exceptions.CJsonException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtility {

	public JsonUtility() {
	}

	public <T extends LexRequest> T parseJsonRequest(Object jsonRequestStr, Class<T> type) throws CJsonException {

		Logger.log("in parseJsonRequest method of JsonUtility class");
		ObjectMapper objectMapper = new ObjectMapper();

		T requestObject = null;
		try {
			requestObject = type.cast(objectMapper.readValue(jsonRequestStr.toString().getBytes(), type));
		} catch (JsonParseException e) {
			throw new CJsonException(e.getMessage(), e);
		} catch (JsonMappingException e) {
			throw new CJsonException(e.getMessage(), e);
		} catch (IOException e) {
			Logger.log("exception in parseJsonRequest method of JsonUtility class= " + e);
			throw new CJsonException(e.getMessage(), e);
		}

		return requestObject;

	}

	public LexRequest parseLexRequest(String jsonRequestStr) throws CJsonException {

		Logger.log("in parseLexRequest method of JsonUtility class");
		ObjectMapper objectMapper = new ObjectMapper();

		LexRequest requestObject = null;
		try {
			requestObject = objectMapper.readValue(jsonRequestStr.toString().getBytes(), LexRequest.class);
		} catch (JsonParseException e) {
			throw new CJsonException(e.getMessage(), e);
		} catch (JsonMappingException e) {
			throw new CJsonException(e.getMessage(), e);
		} catch (IOException e) {
			Logger.log("exception in parseJsonRequest method of JsonUtility class= " + e);
			throw new CJsonException(e.getMessage(), e);
		}

		return requestObject;

	}

	public <T> String getJsonStr(T bean) throws CJsonException {

		Logger.log("in getJsonStr method of JsonUtility class");
		ObjectMapper objectMapper = new ObjectMapper();

		String jsonReq = null;
		try {
			jsonReq = objectMapper.writeValueAsString(bean);
		} catch (JsonProcessingException e) {
			Logger.log("exception in getJsonStr method of JsonUtility class= " + e);
			throw new CJsonException(e.getMessage(), e);
		}

		return jsonReq;
	}

	public String getJsonFromRequest(LexRequest bean) throws CJsonException {

		Logger.log("in getJsonFromRequest method of JsonUtility class");
		ObjectMapper objectMapper = new ObjectMapper();

		String jsonReq = null;
		try {
			jsonReq = objectMapper.writeValueAsString(bean);
		} catch (JsonProcessingException e) {
			Logger.log("exception in getJsonFromRequest method of JsonUtility class= " + e);
			throw new CJsonException(e.getMessage(), e);
		}

		return jsonReq;
	}

	public String getJsonFromRespone(LexResponse bean) throws CJsonException {

		Logger.log("in getJsonFromRespone method of JsonUtility class");
		ObjectMapper objectMapper = new ObjectMapper();

		String jsonReq = null;
		try {
			jsonReq = objectMapper.writeValueAsString(bean);
		} catch (JsonProcessingException e) {
			Logger.log("exception in getJsonFromRespone method of JsonUtility class= " + e);
			throw new CJsonException(e.getMessage(), e);
		}

		return jsonReq;
	}
}
