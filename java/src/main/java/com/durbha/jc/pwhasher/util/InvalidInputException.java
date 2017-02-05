package com.durbha.jc.pwhasher.util;

/**
 * An exception that denotes that the input parameters are not valid.
 * 
 * @author seetharama
 *
 */
public class InvalidInputException extends Exception {

	private static final long serialVersionUID = -1124862960272695532L;

	public InvalidInputException() {
		// TODO Auto-generated constructor stub
	}

	public InvalidInputException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public InvalidInputException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public InvalidInputException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public InvalidInputException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
