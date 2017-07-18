package com.aws.lambda.userregistration.utils;

public final class Logger {

	private static StringBuilder eventLogs = null;

	/**
	 * 
	 * @param log
	 */
	public static void log(String log) {
		if (null == eventLogs) {
			eventLogs = new StringBuilder();
		}

		if (null != log) {
			eventLogs.append("\n");
			eventLogs.append(log);
		}
	}

	/**
	 * 
	 * @return
	 */
	public static String getLogs() {
		if (null != eventLogs) {
			return eventLogs.toString();
		}
		return null;
	}

	/**
	* 
	*/
	public static void clearLogs() {
		if (null != eventLogs) {
			eventLogs.delete(0, eventLogs.length());
		}
		eventLogs = null;
	}

	public static int getSize() {
		if (null != eventLogs) {
			return eventLogs.length();
		}
		return 0;

	}
}
