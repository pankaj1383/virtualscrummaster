package com.aws.lambda.userregistration.demo;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;
import com.aws.lambda.userregistration.LexRequest;
import com.aws.lambda.userregistration.LexResponse;
import com.aws.lambda.userregistration.UserRegistrationFunctionHandler;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class UserRegistrationFunctionHandlerTest {

	private static LexRequest input;

	@BeforeClass
	public static void createInput() throws IOException {
		// TODO: set up your sample input object here.
		input = null;
	}

	private Context createContext() {
		TestContext ctx = new TestContext();

		// TODO: customize your context here if needed.
		ctx.setFunctionName("Your Function Name");

		return ctx;
	}

	@Test
	public void testUserRegistrationFunctionHandler() {
		UserRegistrationFunctionHandler handler = new UserRegistrationFunctionHandler();
		Context ctx = createContext();

		LexResponse output = handler.handleRequest(input, ctx);

		// TODO: validate output here if needed.
		Assert.assertEquals("Hello from Lambda!", output);
	}
}
