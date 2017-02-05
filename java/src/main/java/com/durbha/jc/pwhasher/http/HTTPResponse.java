package com.durbha.jc.pwhasher.http;

import com.durbha.jc.pwhasher.func.RequestHandler;
import com.durbha.jc.pwhasher.http.HTTPConstants.CONTENT_TYPES;
import com.durbha.jc.pwhasher.http.HTTPConstants.RESPONSE_CODES;

/**
 * A standardized response, as coming back from a {@link RequestHandler} object to the {@link com.durbha.jc.pwhasher.SocketHandler}.
 * 
 * @author seetharama
 *
 */
public class HTTPResponse {

	/**
	 * The effective response code to be sent back to the client.
	 */
	RESPONSE_CODES responseCode;
	/**
	 * The response content type. If this is present, then {@link #responseBody} must be present too.
	 */
	CONTENT_TYPES contentType;
	/**
	 * The response body. If this is present, then {@link HTTPResponse#contentType} must be present too.
	 */
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
