package com.durbha.jc.pwhasher.func;

import com.durbha.jc.pwhasher.Results;
import com.durbha.jc.pwhasher.http.HTTPConstants;
import com.durbha.jc.pwhasher.http.HTTPConstants.CONTENT_TYPES;
import com.durbha.jc.pwhasher.http.HTTPConstants.RESPONSE_CODES;
import com.durbha.jc.pwhasher.util.SequenceNumberGenerator;
import com.durbha.jc.pwhasher.http.HTTPRequest;
import com.durbha.jc.pwhasher.http.HTTPResponse;

/**
 * Request handler for /hash requests. It handles both the GET and POST.
 * 
 * @author seetharama
 *
 */
public class PWHashHandler implements RequestHandler {

	public PWHashHandler() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * The generic request handler. Essentially, looks at the request method (GET or POST) and calls the corresponding method.
	 * 
	 * @param parsedRequest Standardized HTTPRequest object
	 * @return httpResponse HTTPResponse that needs to be sent back
	 */
	@Override
	public HTTPResponse handle(HTTPRequest parsedRequest) {
		if (parsedRequest.getReqMethod().equals(HTTPConstants.METHOD_GET)) {
			return procGet(parsedRequest);
		} else if (parsedRequest.getReqMethod().equals(HTTPConstants.METHOD_POST)) {
			return procPost(parsedRequest);
		}
		
		//Method is not GET or POST
		return new HTTPResponse(RESPONSE_CODES.METHOD_NOT_ALLOWED, null, null);
	}

	/**
	 * Processes POST - that is, the request to compute the hash for the given password.
	 * <p><code>password</code> parameter is required.
	 * <p>If password is provided, then initiates the hashing process and returns the sequence number to client.
	 * 
	 * @param parsedRequest Standardized HTTPRequest object
	 * @return httpResponse HTTPResponse that needs to be sent back
	 */
	private HTTPResponse procPost(HTTPRequest parsedRequest) {
		String password = parsedRequest.getParameters().get("password");
		if (password == null || password.length() == 0) {
			return new HTTPResponse(RESPONSE_CODES.BAD_REQUEST, CONTENT_TYPES.TEXT, "Password parameter is missing.");
		}
		long sequenceNumber = initHashing(password);
		//Return the sequence number
		return new HTTPResponse(RESPONSE_CODES.OK, CONTENT_TYPES.TEXT, sequenceNumber + "");
	}
	
	/**
	 * Gets the next sequence number, and creates a separate thread that computes the hash.
	 * 
	 * @param passwordToHash Password to hash
	 * @return sequence number for this request
	 */
	public static long initHashing(String passwordToHash) {
		//Get the sequence number
		long sequenceNumber = SequenceNumberGenerator.getNextSequenceNumber();
		
		//Create the thread that will hash in the background
		HashingThread hashingThread = new HashingThread(sequenceNumber, passwordToHash);
		hashingThread.start();
		
		return sequenceNumber;
	}

	/**
	 * Processes GET request. It extracts the sequence number from the request URI, looks it up on the Results object. 
	 * <p>If found, it returns the computed hash as a string, else it returns an HTTP 404 error.
	 * 
	 * @param parsedRequest Standardized HTTPRequest object
	 * @return httpResponse HTTPResponse that needs to be sent back. Computed hash as a string, if the sequence number is found, otherwise a 404 HTTP Not Found error
	 */
	private HTTPResponse procGet(HTTPRequest parsedRequest) {
		String reqUri = parsedRequest.getReqURI();
		String[] pathParts = reqUri.split("/");
		if (pathParts.length != 2) {
			return new HTTPResponse(RESPONSE_CODES.NOT_FOUND, null, null);
		}
		String seqNumString = pathParts[1];
		long sequenceNumber = -1;
		try {
			sequenceNumber = Long.parseLong(seqNumString);
		} catch (NumberFormatException e) {
			return new HTTPResponse(RESPONSE_CODES.BAD_REQUEST, CONTENT_TYPES.TEXT, "Unrecognized request/invalid sequence number");
		}
		String pwHash = getHashForSeqNumber(sequenceNumber);
		if (pwHash == null) {
			return new HTTPResponse(RESPONSE_CODES.NOT_FOUND, null, null);
		} else {
			return new HTTPResponse(RESPONSE_CODES.OK, CONTENT_TYPES.TEXT, pwHash);
		}
	}
	
	public static String getHashForSeqNumber(long sequenceNumber) {
		return Results.getHash(sequenceNumber);
	}

}
