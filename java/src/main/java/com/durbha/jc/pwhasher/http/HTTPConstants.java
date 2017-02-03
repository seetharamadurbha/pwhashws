package com.durbha.jc.pwhasher.http;

public class HTTPConstants {

	public static final String METHOD_GET = "GET";
	public static final String METHOD_POST = "POST";
	
	public static final String HEADER_CONTENT_LENGTH = "Content-Length";
	public static final String HEADER_CONTENT_TYPE = "Content-Type";

	
	public enum RESPONSE_CODES {
		OK (200, "OK"),
		CREATED (201, "Created"),
		BAD_REQUEST (400, "Bad Request"),
		NOT_FOUND (404, "Not Found"),
		METHOD_NOT_ALLOWED (405, "Method Not Allowed"),
		GONE (410, "Gone"),
		SERVER_ERROR (500, "Internal Server Error"),
		SERVICE_NOT_AVAILABLE (503, "Service Not Available");
		
		private int responseCode;
		private String responseString;
		
		RESPONSE_CODES(int code, String string) {
			this.responseCode = code;
			this.responseString = string;
		}
		public int getResponseCode() {
			return this.responseCode;
		}
		public String getResponseString() {
			return this.responseString;
		}
	}
	
	public enum CONTENT_TYPES {
		TEXT ("text/plain"),
		FORM ("application/x-www-form-urlencoded");
		
		private String typeString;
		
		CONTENT_TYPES(String string) {
			this.typeString = string;
		}
		public String getTypeString() {
			return this.typeString;
		}
	}

}
