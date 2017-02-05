package com.durbha.jc.pwhasher.util;

/**
 * A custom exception class indicating that there is a failure to perform the operation.
 * 
 * <p>This exception must be thrown only after appropriate logging of the original exception condition.
 * 
 * <p>The recipient of this exception should not log the original exception again, but take an appropriate action following the failure to complete a particular operation.
 * 
 * @author seetharama
 *
 */
public class FailedOperationException extends Exception {
	private static final long serialVersionUID = 9084181916633807781L;

	public FailedOperationException() {
		// TODO Auto-generated constructor stub
	}

	public FailedOperationException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public FailedOperationException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public FailedOperationException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public FailedOperationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
