package com.aws.lambda.rally;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import com.aws.lambda.rally.exception.ScrumException;
import com.aws.lambda.rally.pojo.BaseResponse;
import com.aws.lambda.rally.pojo.RallyUser;
import com.aws.lambda.rally.pojo.User;
import com.aws.lambda.rally.pojo.UsersResponse;
import com.aws.lambda.schedular.utils.Logger;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.GetRequest;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.response.QueryResponse;
import com.rallydev.rest.util.Fetch;

public class RallyService implements IScrumTool {

	private final String API_KEY = "_Dl8a4ejcQ4WTcq4ExdxbvF3mb1d2eUsNo6v43LThc";

	private final String host = "https://rally1.rallydev.com";
	
	private static final String USER_REQ_PARAM = "/User";
	private static final String USER_QUERY_PARAM = "User";
	private static final String USERNAME_FETCH_STRING = "UserName";
	private static final String DISPLAYNAME_FETCH_STRING = "DisplayName";
	private static final String EMAIL_FETCH_STRING = "EmailAddress";
	private static final String FIRSTNAME_FETCH_STRING = "FirstName";
	private static final String MIDDLENAME_FETCH_STRING = "MiddleName";
	private static final String LASTNAME_FETCH_STRING = "LastName";
	
	public boolean isValidUser(String apiKey) throws ScrumException {
		boolean isValidUser = false;
		RallyRestApi restApi = null;
		
		try {
			
			restApi = new RallyRestApi(new URI(host), apiKey);

			// No parameter available to verify user exist or not so making an
			// api call if user exis the block will get execute otherwise it
			// will throw exception with error message contain "401"
			
			GetRequest getUserReq = new GetRequest(USER_REQ_PARAM);
			restApi.get(getUserReq);
			isValidUser = true;

		} catch (URISyntaxException uriSyntaxException) {
			// TODO Auto-generated catch block
			uriSyntaxException.printStackTrace();
			Logger.log("uriSyntaxException  occured in getAllUsers of Class Rally" + uriSyntaxException);
			System.out.println("URI exception occured in isValidUser of Class Rally" + uriSyntaxException);
			throw new ScrumException("URI exception occured in isValidUser of Class Rally", uriSyntaxException);
		} catch (IOException ioE) {
			System.out.println("User is not authorized");
		} finally {
			if (null != restApi) {
				try {
					restApi.close();
				} catch (IOException ioe) {
					// Not throwing any exception from here as operation is
					// successful and closing is the problem
					Logger.log("uriSyntaxException  occured in fianlly of isValidUser of Class Rally" + ioe);
					System.out.println("URI exception occured in fianlly of isValidUser of Class Rally" + ioe);
				}
			}
		}

		return isValidUser;
	}

	@Override
	public BaseResponse getAllRallyUsers() throws ScrumException {

		RallyRestApi restApi = null;
		UsersResponse usersResponse = null;
		try {
			restApi = new RallyRestApi(new URI(host), API_KEY);
			if (null != restApi) {
				QueryRequest userRequest = new QueryRequest(USER_QUERY_PARAM);
				// set items to be fetched from User details
				userRequest.setFetch(new Fetch(USERNAME_FETCH_STRING, FIRSTNAME_FETCH_STRING, MIDDLENAME_FETCH_STRING,
						LASTNAME_FETCH_STRING, EMAIL_FETCH_STRING, DISPLAYNAME_FETCH_STRING));

				QueryResponse userQueryResponse = restApi.query(userRequest);
				String[] userReqError = userQueryResponse.getErrors();
				if (null != userReqError && userReqError.length > 0) {
					UsersResponse userResp = new UsersResponse();
					userResp.setStatusCode(BaseResponse.STATUS_SCRUM_ERROR);
					userResp.setErrors(userReqError);
					return userResp;
				} else {
					JsonArray userJsonArray = userQueryResponse.getResults();
					// RallyUser[] userArray = new
					// Gson().fromJson(jsonarrayString.toString(),
					// RallyUser[].class);

					ObjectMapper mapper = new ObjectMapper();
					mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

					User[] userArray = mapper.readValue(userJsonArray.toString(), RallyUser[].class);
					usersResponse = new UsersResponse();
					usersResponse.setUsers(Arrays.asList(userArray));
					usersResponse.setStatusCode(UsersResponse.STATUS_OK);

					return usersResponse;
				}
			}
		} catch (IOException ioE) {
			if (ioE.getMessage().contains("401")) {
				BaseResponse baseResponse = new BaseResponse();
				baseResponse.setStatusCode(BaseResponse.STATUS_UNAUTHORIZED);
				return baseResponse;
			}
			ioE.printStackTrace();
			Logger.log("IOException exception occured in getAllUsers of Class Rally" + ioE);
			System.out.println("IOException exception occured in getAllUsers of Class Rally" + ioE);
			throw new ScrumException("IOException exception occured in getAllUsers of Class Rally", ioE);
		} catch (URISyntaxException uriE) {
			uriE.printStackTrace();
			Logger.log("URIException exception occured in getAllUsers of Class Rally" + uriE);
			System.out.println("URIException exception occured in getAllUsers of Class Rally" + uriE);
			throw new ScrumException("URIException exception occured in getAllUsers of Class Rally", uriE);

		} finally {
			if (null != restApi) {
				try {
					restApi.close();
				} catch (IOException ioe) {
					// Not throwing any exception from here as operation is
					// successful and closing is the problem
					Logger.log("uriSyntaxException  occured in fianlly of getAllUsers of Class Rally" + ioe);
					System.out.println("URI exception occured in fianlly of getAllUsers of Class Rally" + ioe);
				}
			}
		}
		return usersResponse;

	}
}
