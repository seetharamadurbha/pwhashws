package com.durbha.jc.pwhasher.func;

import com.durbha.jc.pwhasher.Results;
import com.durbha.jc.pwhasher.http.HTTPConstants.CONTENT_TYPES;
import com.durbha.jc.pwhasher.http.HTTPConstants.RESPONSE_CODES;
import com.durbha.jc.pwhasher.http.HTTPRequest;
import com.durbha.jc.pwhasher.http.HTTPResponse;

/**
 * A class to handle /stats requests.
 * 
 * @author seetharama
 *
 */
public class StatsHandler implements RequestHandler {

	public StatsHandler() {
	}

	/**
	 * Returns a JSON of the current statistics as gotten from {@link Results}.
	 */
	@Override
	public HTTPResponse handle(HTTPRequest parsedRequest) {
		Results.AverageTimeSnapshot stats = Results.getAverageTimes();
		String responseJSON = "{\"total\":" + stats.getNumRequests() + ", \"average\":" + stats.getAverageTime() + "}";
		return new HTTPResponse(RESPONSE_CODES.OK, CONTENT_TYPES.TEXT, responseJSON);
	}

}
