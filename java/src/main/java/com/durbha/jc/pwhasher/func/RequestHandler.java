package com.durbha.jc.pwhasher.func;

import com.durbha.jc.pwhasher.http.HTTPRequest;
import com.durbha.jc.pwhasher.http.HTTPResponse;

public interface RequestHandler {

	public HTTPResponse handle(HTTPRequest parsedRequest);
	
}
