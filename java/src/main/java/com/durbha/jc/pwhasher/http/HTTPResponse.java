package com.durbha.jc.pwhasher.http;

import com.durbha.jc.pwhasher.http.HTTPConstants.CONTENT_TYPES;
import com.durbha.jc.pwhasher.http.HTTPConstants.RESPONSE_CODES;

public class HTTPResponse {

	RESPONSE_CODES responseCode;
	CONTENT_TYPES contentType;
	String responseBody;
	
	public HTTPResponse(RESPONSE_CODES responseCode,
		CONTENT_TYPES contentType,
		String responseBody) {
		this.responseCode = responseCode;
		this.contentType = contentType;
		this.responseBody = responseBody;
	}

	public RESPONSE_CODES getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(RESPONSE_CODES responseCode) {
		this.responseCode = responseCode;
	}

	public CONTENT_TYPES getContentType() {
		return contentType;
	}

	public void setContentType(CONTENT_TYPES contentType) {
		this.contentType = contentType;
	}

	public String getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}

}
