package com.durbha.jc.pwhasher.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import com.durbha.jc.pwhasher.Constants;
import com.durbha.jc.pwhasher.util.BoundedBufferedReader;
import com.durbha.jc.pwhasher.util.FailedOperationException;
import com.durbha.jc.pwhasher.util.InvalidInputException;

/**
 * Parses the incoming socket connection, per HTTP specification(s).
 * 
 * <p>It also enforces certain limitations on the request. For example, a header line cannot be more than {@value #MAX_CHARS_IN_REQUEST_LINE}.
 * <p>Another check is on the content length - maximum content length supported is 5000 by default.
 * 
 * @author seetharama
 *
 */
public class HTTPReqParser {
	private Logger logger = Logger.getLogger(Constants.LOGGER_NAME);
	
	/**
	 * Maximum characters in the header - {@value}
	 */
	private static final int MAX_CHARS_IN_REQUEST_LINE = 300;
	private static final int CONTENT_LENGTH_NOT_GIVEN = -1;
	
	int contentLength = CONTENT_LENGTH_NOT_GIVEN;
	
	/**
	 * Maximum length of content (applies to a POST) - default is 5000
	 */
	int maxContentLength = 5000;//default
	
	boolean isFormURLEncoded = false;
	
	InputStream inStream = null;
	
	enum STATUS {
		REQUEST_LINE, HEADERS, BODY
	}

	/**
	 * Constructor with the Input stream to process and the max content length that is allowed.
	 * 
	 * @param inStream Input stream to process
	 * @param maxContentLength Maximum content length to process
	 */
	public HTTPReqParser(InputStream inStream, int maxContentLength) {
		this.inStream = inStream;
		this.maxContentLength = maxContentLength;
	}

	
	/**
	 * Process the input stream to extract HTTP request method, URI, message body and other headers.
	 * 
	 * <p>Processed values are used to create the {@link HTTPRequest} object.
	 * 
	 * @return Parsed values populated into a {@link HTTPRequest} object
	 * @throws InvalidInputException If any of the input validations fail
	 */
	public HTTPRequest parseInputStream() throws InvalidInputException {
		
		HTTPRequest request = new HTTPRequest();
		
		BoundedBufferedReader bufferedReader = null;
		
		bufferedReader = new BoundedBufferedReader(
				new InputStreamReader(this.inStream, StandardCharsets.UTF_8));
		STATUS currentStatus = STATUS.REQUEST_LINE;
		boolean processMore = true;
		try {
			while (processMore) { //Don't worry, we will have break at the end of the loop
				try {
					String line = bufferedReader.readLine(MAX_CHARS_IN_REQUEST_LINE, currentStatus != STATUS.BODY);
					if (line == null) {
						logger.finer("End of input stream reached...quitting");
						break;//We are done!
					} else if (line.length() == 0) {
						logger.finer("Found break after headers");
						currentStatus = STATUS.BODY;
					}
					switch (currentStatus) {
					case REQUEST_LINE:
						//Process request line - it is of format GET URL HTTP/1.1
						//See https://tools.ietf.org/html/rfc7230#section-3.1
						//request-line   = method<<space>>request-target<<space>>HTTP-version
						StringTokenizer reqTokens = new StringTokenizer(line, " ");
						request.setReqMethod(reqTokens.nextToken());
						request.setReqURI(reqTokens.nextToken());
						currentStatus = STATUS.HEADERS;
						break;
					case HEADERS:
						//Process header line - HeaderName: HeaderValue
						StringTokenizer headerTokens = new StringTokenizer(line, ":");
						String headerName = headerTokens.nextToken().trim();
						String headerValue = headerTokens.nextToken().trim();
						if (HTTPConstants.HEADER_CONTENT_LENGTH.equalsIgnoreCase(headerName)) {
							try {
								this.contentLength = Integer.parseInt(headerValue);
								if (this.contentLength > this.maxContentLength) {
									logger.warning("Received content length of " + this.contentLength + " is greater than max allowed: " + this.maxContentLength);
									throw new InvalidInputException();
								}
							} catch (NumberFormatException e) {
								logger.warning("Invalid value for Content-Length header: " + headerValue);
								throw new InvalidInputException();
							}
						}
						if (headerName.equalsIgnoreCase(HTTPConstants.HEADER_CONTENT_TYPE) && headerValue.equalsIgnoreCase(HTTPConstants.CONTENT_TYPES.FORM.getTypeString())) {
							isFormURLEncoded = true;
						}
						request.getReqHeaders().put(headerName, headerValue);
						break;
					case BODY:
						if (this.contentLength == CONTENT_LENGTH_NOT_GIVEN) {
							//If no content-length was given, then this is it! No more to process
							processMore = false;
						} else {
							//Read the rest of the input stream as body
							char[] bodyChars = new char[this.contentLength];
							logger.finer("Going to read rest of input stream as request body");
							int charsRead = bufferedReader.read(bodyChars);
							if (charsRead != this.contentLength) {
								logger.warning("Number of characters in request body less than specified in Content-Length header");
							}
							request.setReqBody(new String(bodyChars));
							processMore = false;
						}
					}
				} catch (IOException e) {
					logger.warning("Unable to read from input stream: " + e.getMessage());
					break;
				}
				
			}
			
			if (isFormURLEncoded && request.getReqBody() != null) {
				String postBody = request.getReqBody().startsWith("?") ? request.getReqBody().substring(1) : request.getReqBody();
				//parse request body into parameters
			    String[] pairs = postBody.split("&");
			    for (String pair : pairs) {
			        int idx = pair.indexOf("=");
			        try {
						request.getParameters().put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						logger.warning("Could not decode form parameter in the request");
						throw new InvalidInputException();
					}
			    }			
			}
		} finally {
			/*
			 * Do not close these, if you do, then the underlying socket is closed too :(
			try {
				bufferedReader.close();
				this.inStream.close();
			} catch (IOException e) {
				logger.warning("Error closing the request input stream: " + e.getMessage());
			}
			*/
		}
		//Close the input stream after finishing the parsing
		
		return request;
	}

}
