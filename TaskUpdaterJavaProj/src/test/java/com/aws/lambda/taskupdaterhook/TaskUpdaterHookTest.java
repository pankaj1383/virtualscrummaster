package com.aws.lambda.taskupdaterhook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class TaskUpdaterHookTest {

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

	private void checkMatchedStrInOptions(String allOptionsStr, String inputTrans, String taskInfoSlotVal) {

		String selectedOptionId = "";
		String selectedOptionTxt = "";
		int matchedCount = 0;
		String[] options = allOptionsStr.split(";");
		for (String option : options) {
			if (null != option && option.contains(": ")) {
				String txt[] = option.split(": ");

				if (txt[1].contains(taskInfoSlotVal) || txt[0].contains(taskInfoSlotVal)) {
					selectedOptionId = txt[0];
					selectedOptionTxt = txt[1];
					matchedCount++;
				}

				// sb.append(txt[1]);
				// sb.append("\n");
				// sb.append("\n");
				// GenericAttachment genericAttachment =
				// buildGenericAttachmentFor(txt[0], txt[1]);
				// if (null != genericAttachment) {
				// attachments.add(genericAttachment);
				// }

				if (isExactMatchByRefId(txt, inputTrans)) {
					matchedCount = 1;
					selectedOptionId = txt[0];
					selectedOptionTxt = txt[1];
				}

			}
		}
	}

	public boolean isExactMatchByRefId(String[] txt, String inputVal) {
		boolean retVal = false;
		if (null != txt && txt.length > 0) {
			String refId = null;
			String taskRefId = txt[0];

			if (null != taskRefId && taskRefId.indexOf("/") != -1) {
				int lastIndex = taskRefId.lastIndexOf("/");
				if (taskRefId.length() > lastIndex + 1) {
					// baseTaskUrl = taskRefId.substring(0, lastIndex + 1);
					refId = taskRefId.substring(taskRefId.lastIndexOf("/") + 1);
				}
			}

			retVal = null != refId ? refId.equalsIgnoreCase(inputVal) : retVal;
		}
		return retVal;
	}

	@Test
	public void testTaskUpdaterHook() {
		TaskUpdaterHook handler = new TaskUpdaterHook();
		Context ctx = createContext();

		// test time method
		// String req = readFile();
		// String output = handler.getReadbleDurationFrom("PT2H");
		String taskRef = "https://rally1.rallydev.com/slm/webservice/v2.0/task/132284686564";
		String id = taskRef.substring(taskRef.lastIndexOf("/") + 1);
		String url = taskRef.substring(0, taskRef.lastIndexOf("/") + 1);

		String Str = "https://rally1.rallydev.com/slm/webservice/v2.0/task/134638180936: User registration Intent creation of DemoBot Alexa User story;https://rally1.rallydev.com/slm/webservice/v2.0/task/134638182208: User registration hook creation of DemoBot Alexa User story;";
		String inputTrans = "134638180936";

		checkMatchedStrInOptions(Str, inputTrans, "User");
		String splittedStr[] = Str.split(": ");

		boolean retVal = handler.isExactMatchByRefId(splittedStr, "134638177656");

		String allOptionsStr = "https://rally1.rallydev.com/slm/webservice/v2.0/task/134638177656: Task Intent creation of DemoBot AlexaUser story;https://rally1.rallydev.com/slm/webservice/v2.0/task/134638180936: User registration Intent creation of DemoBot AlexaUser story;";
		String taskInfo = "Intent Creation";

		boolean containsCheck = allOptionsStr.contains(taskInfo);
		boolean patternCHeck = Pattern.compile(Pattern.quote(taskInfo), Pattern.CASE_INSENSITIVE).matcher(allOptionsStr)
				.find();

		String readableVal = handler.getReadbleDurationFrom("PT14H70M");

		String testStr = "abcdf df";
		String[] spli = testStr.split(" ");

		String escapedKey = "\"" + testStr
				+ "\" Api key you provided isn't working. Please get the correct one from Rally. I will be waiting for it";

		int output = handler.getDurationInputType("P1D");

		String inProgStr = "In-Progress";
		String inProgStr1 = "In–Progress";

		boolean isMtached = handler.matchTaskStatus(inProgStr1, inProgStr);

		inProgStr = "Defined";
		inProgStr1 = "Defined";

		isMtached = handler.matchTaskStatus(inProgStr1, inProgStr);

		// inProgStr = inProgStr.replace("[^a-zA-Z]", "");
		// inProgStr1 = inProgStr1.replace("[^a-zA-Z]", "");
		// inProgStr = Regex.Replace(inProgStr, @"[^\w\d\s]", "");
		Pattern pt = Pattern.compile("[^a-zA-Z0-9]");
		Matcher match = pt.matcher(inProgStr);
		while (match.find()) {
			String s = match.group();
			inProgStr = inProgStr.replaceAll("\\" + s, "");
		}
		inProgStr1 = inProgStr1.replace("[^a-zA-Z0-9]", "");

		// TODO: validate output here if needed.
		Assert.assertEquals("Hello from Lambda!", output);

		// String req = readFile();
		// JsonUtility util = new JsonUtility();
		// try {
		// input = util.parseLexRequest(req);
		// LexResponse output = handler.handleRequest(input, ctx);
		//
		// // TODO: validate output here if needed.
		// Assert.assertEquals("Hello from Lambda!", output);
		// } catch (CJsonException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

	}

	private static String readFile() {

		final StringBuilder out = new StringBuilder();
		try {

			File inputFile = new File("reqJson");
			FileInputStream istream = new FileInputStream(inputFile);

			final int bufferSize = 1024;
			final char[] buffer = new char[bufferSize];

			Reader in = new InputStreamReader(istream, "UTF-8");
			for (;;) {
				int rsz = in.read(buffer, 0, buffer.length);
				if (rsz < 0)
					break;
				out.append(buffer, 0, rsz);
			}
			return out.toString();

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
