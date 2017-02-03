package com.durbha.jc.pwhasher.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * A BufferedReader that prevents DoS attacks by providing bounds for line length and number of lines
 * Modified from the below source to reject a line that is more than given number of characters.
 * 
 * @see https://code.google.com/p/owasp-esapi-java/issues/attachmentText?id=183&aid=-7134623167843514645&name=BoundedBufferedReader.java
 * @see https://github.com/seantmalone/BoundedBufferedReader/blob/master/BoundedBufferedReader.java
 * @version 1.0
 */


public class BoundedBufferedReader extends BufferedReader {

	public BoundedBufferedReader(InputStreamReader reader) {
		super(reader);
	}

	public String readLine(int maxLength, boolean checkCRLF) throws IOException, InvalidInputException {

		int currentPos = 0;
		char[] data = new char[maxLength];
		final int CR = 13;
		final int LF = 10;
		int currentCharVal = super.read();

		// Read characters and add them to the data buffer until we hit the end
		// of a line or the end of the file.
		while ((currentCharVal != CR) && (currentCharVal != LF) && (currentCharVal >= 0)) {
			data[currentPos++] = (char) currentCharVal;
			// Check readerMaxLineLen limit
			if (currentPos < maxLength)
				currentCharVal = super.read();
			else
				break;
		}

		if (currentCharVal < 0) {
			// End of file
			if (currentPos > 0) {
				return (new String(data, 0, currentPos));
			} else {
				return null;
			}
		} else {
			if (checkCRLF && currentCharVal != CR) {
				throw new InvalidInputException("Did not find CRLF within the given " + maxLength + " character limit.");
			}
			// Remove newline characters from the buffer
			if (currentCharVal == CR) {
				// Check for LF and remove from buffer
				super.mark(1);
				if (super.read() != LF) {
					super.reset();
				}
			} else if (currentCharVal != LF) {
				// maxLength has been hit, but we still need to remove
				// newline characters.
				super.mark(1);
				int nextCharVal = super.read();
				if (nextCharVal == CR) {
					super.mark(1);
					if (super.read() != LF) {
						super.reset();
					}
				} else if (nextCharVal != LF) {
					super.reset();
				}
			}
			return (new String(data, 0, currentPos));
		}

	}
}