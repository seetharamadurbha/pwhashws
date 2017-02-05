package com.durbha.jc.pwhasher.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A utility class to generate a sequence number. 
 * This class allows the implementation to change without affecting the rest of the implementation.
 *  
 * <p>Current implementation will use AtomicLong.
 * 
 * @author seetharama
 *
 */
public class SequenceNumberGenerator {

	private static final AtomicLong sequenceNumber = new AtomicLong(1);

	public static long getNextSequenceNumber() {
		return sequenceNumber.getAndIncrement();
	}

}
