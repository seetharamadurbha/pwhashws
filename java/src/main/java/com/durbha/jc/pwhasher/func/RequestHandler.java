package com.durbha.jc.pwhasher.func;

import com.durbha.jc.pwhasher.http.HTTPRequest;
import com.durbha.jc.pwhasher.http.HTTPResponse;

/**
 * An interface to standardize handlers for processing incoming requests.
 * 
 * @author seetharama
 *
 */
public interface RequestHandler {

	/**
	 * Called to process the incoming request.
	 * 
	 * @param parsedRequest Standardized HTTPRequest object
	 * @return A standardized HTTP response object
	 */
	public HTTPResponse handle(HTTPRequest parsedRequest);
	
}
