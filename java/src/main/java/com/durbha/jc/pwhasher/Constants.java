package com.durbha.jc.pwhasher;

import java.text.SimpleDateFormat;

public class Constants {

	public static final String LOGGER_NAME = "pwHasher";
	public static final String HASHING_ALGORITHM = "SHA-512";

	/*
	 * See https://tools.ietf.org/html/rfc7231#section-7.1.1.2
	 */
	public static final SimpleDateFormat HTTP_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
	
	public static final String URI_REGEX_POST_HASH = "hash";
	public static final String URI_REGEX_POST_STATS = "stats";

}
