package com.durbha.jc.pwhasher.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.durbha.jc.pwhasher.Constants;
import com.durbha.jc.pwhasher.http.HTTPConstants;
import com.durbha.jc.pwhasher.http.HTTPConstants.CONTENT_TYPES;
import com.durbha.jc.pwhasher.http.HTTPConstants.RESPONSE_CODES;

public class SocketResponder {
	private static Logger logger = Logger.getLogger(Constants.LOGGER_NAME);
	private static final String CR_LF = "\r\n";
	
	/**
	 * A static method that is handy to send out quick responses.
	 * Note: The socket WILL BE CLOSED after the response is sent, so no more data can be sent after this.
	 * 
	 * Follows the venerable Wikipedia (https://en.wikipedia.org/wiki/Hypertext_Transfer_Protocol#Response_message)
	 * [But, seriously, it follows https://tools.ietf.org/html/rfc7230#section-3.1. Wikipedia is only used when English is the prefered language.]
	 * 
	 * @param socket
	 * @param responseCode
	 * @param headers Optional
	 * @param responseBody Optional - if present, must be UTF-8 encoded version of the underlying binary data
	 * @throws FailedOperationException If the response could not be sent for any reason.
	 */
	public static void sendResponse(Socket socket, RESPONSE_CODES responseCode, 
			HashMap<String, String> headers, 
			CONTENT_TYPES contentType,
			String responseBody) {
		
		if ((responseBody != null && contentType == null) ||
		   (responseBody == null && contentType != null)) {
			logger.warning("Both responseBody and contentType must be present or both must be absent");
			//throw new FailedOperationException();
		} else {
			
			if (headers == null) {
				headers = new HashMap<String, String>();
			}
			Calendar cal = Calendar.getInstance();
			
			//Add ALWAYS used headers
			headers.put("Connection", "close"); //For this application, we do not support Keep-Alive for connections
			headers.put("Date", Constants.HTTP_DATE_FORMAT.format(cal.getTime()));
			
			//Add headers based on response
			if (contentType != null) {
				headers.put(HTTPConstants.HEADER_CONTENT_TYPE, contentType.getTypeString() + "; charset=UTF-8");
			}
			if (responseCode == RESPONSE_CODES.OK && responseBody != null) {
				headers.put(HTTPConstants.HEADER_CONTENT_LENGTH, responseBody.length() + "");
			}
			PrintWriter socketOut = null;
			try {
				socketOut = new PrintWriter(socket.getOutputStream(), true);
				
				//Status line
				socketOut.print("HTTP/1.1 " + responseCode.getResponseCode() + " ");
				if (responseCode == RESPONSE_CODES.OK) {
					socketOut.print(responseCode.getResponseString());
				} else {
					if (responseBody != null) {
						socketOut.print(responseBody);
					} else {
						socketOut.print(responseCode.getResponseString());
					}
				}
				socketOut.print(CR_LF);
				
				//Headers
				for (Map.Entry<String, String> header: headers.entrySet()) {
					socketOut.print(header.getKey() + ": " + header.getValue());
					socketOut.print(CR_LF);
				}
				
				//Two CRLFs after headers - one CRLF was already added in the header loop
				socketOut.print(CR_LF);
				
				if (responseCode == RESPONSE_CODES.OK && responseBody != null) {
					socketOut.print(responseBody);
				}
				
			} catch (IOException e) {
				logger.warning("Unable to send response to client: " + e.getMessage());
			} finally {
				if (socketOut != null) {
					socketOut.flush();
					socketOut.close();
				}
				try {
					socket.close();
				} catch (IOException e) {
					logger.warning("Error closing client socket: " + e.getMessage());
					//No further exception is thrown
				}
			}
		}
	}

}
