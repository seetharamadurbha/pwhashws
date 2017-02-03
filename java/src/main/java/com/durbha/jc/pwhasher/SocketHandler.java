package com.durbha.jc.pwhasher;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;
import com.durbha.jc.pwhasher.func.PWHashHandler;
import com.durbha.jc.pwhasher.func.RequestHandler;
import com.durbha.jc.pwhasher.func.StatsHandler;
import com.durbha.jc.pwhasher.util.FailedOperationException;
import com.durbha.jc.pwhasher.http.HTTPConstants;
import com.durbha.jc.pwhasher.http.HTTPReqParser;
import com.durbha.jc.pwhasher.http.HTTPRequest;
import com.durbha.jc.pwhasher.http.HTTPResponse;
import com.durbha.jc.pwhasher.util.InvalidInputException;
import com.durbha.jc.pwhasher.util.SocketResponder;
import com.durbha.jc.pwhasher.http.HTTPConstants.RESPONSE_CODES;

/**
 * Handles the incoming requests. This is not really a thread, by itself - but is supposed to be run by a thread inside a thread pool.
 * The incoming socket connection is to be handed over to this class immediately after a connection is made.
 * 
 * @author seetharama
 *
 */
public class SocketHandler implements Runnable {
	private static final int MAX_CONTENT_LENGTH = 5000;//Some arbitrary number for now.
	
	private Socket socket = null;
	private Logger logger = Logger.getLogger(Constants.LOGGER_NAME);
	private HTTPRequest request = null;
	RequestHandler reqHandler = null;
	
	public SocketHandler(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			parseInputStream(socket);
		} catch (FailedOperationException e) {
			logger.warning("Processing of input request failed!");
			SocketResponder.sendResponse(socket, RESPONSE_CODES.SERVER_ERROR, null, null, null);
			return;
		} catch (InvalidInputException e) {
			logger.warning("Processing of input request failed - invalid input received!");
			SocketResponder.sendResponse(socket, RESPONSE_CODES.BAD_REQUEST, null, null, null);
			return;
		} finally {
		}
		
		//Alright, this.reqParser should have all the request information
		//Some validations first
		validateRequest();
		
		//If the request handler is successfully identified
		if (this.reqHandler != null) {
			HTTPResponse response = this.reqHandler.handle(this.request);
			SocketResponder.sendResponse(socket, response.getResponseCode(), null, response.getContentType(), response.getResponseBody());
		}
	}
	
	private void validateRequest() {
		if (this.request.getReqMethod() != null) {
			this.request.setReqMethod(this.request.getReqMethod().toUpperCase());
		}
		if (this.request.getReqMethod() == null ||
				(!HTTPConstants.METHOD_GET.equals(this.request.getReqMethod()) && !HTTPConstants.METHOD_POST.equals(this.request.getReqMethod())
						)) {
			logger.warning("HTTP request method not present, or it was not equal to GET or POST: " + this.request.getReqMethod());
			SocketResponder.sendResponse(socket, RESPONSE_CODES.BAD_REQUEST, null, null, null);
			return;
		}
		
		if (this.request.getReqURI() == null || this.request.getReqURI().length() == 0) {
			logger.warning("HTTP request URI not present");
			SocketResponder.sendResponse(socket, RESPONSE_CODES.BAD_REQUEST, null, null, null);
			return;
		} else {
			//It should be one of the allowed
			if (this.request.getReqURI().startsWith("http")) {
				try {
					URI uri = new URI(this.request.getReqURI());
					this.request.setReqURI(uri.getPath());
				} catch (URISyntaxException e) {
					logger.warning("HTTP request URI not recognized");
					SocketResponder.sendResponse(socket, RESPONSE_CODES.BAD_REQUEST, null, null, null);
					return;
				}
			}
			if (this.request.getReqURI().startsWith("/")) { //to handle situations where the URI may not have a preceding '/'
				this.request.setReqURI(this.request.getReqURI().substring(1));
			}
			if (this.request.getReqURI().startsWith(Constants.URI_REGEX_POST_HASH)) {
				logger.finer("Request URI matches " + Constants.URI_REGEX_POST_HASH);
				this.reqHandler = new PWHashHandler();
			} else if (this.request.getReqURI().startsWith(Constants.URI_REGEX_POST_STATS)) {
				if ("GET".equalsIgnoreCase(this.request.getReqMethod())) {
					logger.finer("Request URI matches GET Stats");
					this.reqHandler = new StatsHandler();
				}
			}
			if (this.reqHandler == null) {
				logger.warning("HTTP request URI does not match application URIs.");
				SocketResponder.sendResponse(socket, RESPONSE_CODES.NOT_FOUND, null, null, null);
				return;
			}
		}
	}
	
	/**
	 * Parse a socket into data that is processable
	 * @throws InvalidInputException 
	 */
	private void parseInputStream(Socket socket) throws FailedOperationException, InvalidInputException {
		try {
			HTTPReqParser reqParser = new HTTPReqParser(socket.getInputStream(), MAX_CONTENT_LENGTH);
			this.request = reqParser.parseInputStream();
		} catch (IOException e) {
			logger.warning("Unable to get input stream from socket: " + e.getMessage());
			throw new FailedOperationException();
		}
	}

}
