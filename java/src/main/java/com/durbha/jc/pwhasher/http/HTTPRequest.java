package com.durbha.jc.pwhasher.http;

import java.util.HashMap;

public class HTTPRequest {
	
	HashMap<String, String> reqHeaders = new HashMap<String, String>();
	HashMap<String, String> parameters = new HashMap<String, String>();
	String reqMethod, reqURI, reqBody;

	public HTTPRequest() {
		// TODO Auto-generated constructor stub
	}

	public HashMap<String, String> getReqHeaders() {
		return reqHeaders;
	}

	public void setReqHeaders(HashMap<String, String> reqHeaders) {
		this.reqHeaders = reqHeaders;
	}

	public String getReqMethod() {
		return reqMethod;
	}

	public void setReqMethod(String reqMethod) {
		this.reqMethod = reqMethod;
	}

	public String getReqURI() {
		return reqURI;
	}

	public void setReqURI(String reqURI) {
		this.reqURI = reqURI;
	}

	public String getReqBody() {
		return reqBody;
	}

	public void setReqBody(String reqBody) {
		this.reqBody = reqBody;
	}

	public HashMap<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(HashMap<String, String> parameters) {
		this.parameters = parameters;
	}

}
