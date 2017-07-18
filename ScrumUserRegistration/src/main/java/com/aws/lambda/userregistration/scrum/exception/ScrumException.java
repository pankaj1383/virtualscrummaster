/**
 * 
 */
package com.aws.lambda.userregistration.scrum.exception;

/**
 * @author raja.pateriya
 *
 *
 *  Class for all Scrum tool exceptions
 */
public class ScrumException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Create with just a message.  Used when the exception is internal.
	 * 
	 * @param message - String
	 */
	public ScrumException(String message) {
		super(message);
	}

	/**
	 * Create with a message and nested exception.  Used when an exception is being re-thrown
	 * 
	 * @param message - String
	 * @param innerException - Exception
	 */
	public ScrumException(String message, Exception innerException) {
		super(message, innerException);
	}
}