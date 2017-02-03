package com.durbha.jc.pwhasher.util;

import java.util.concurrent.atomic.AtomicLong;

public class SequenceNumberGenerator {

	private static final AtomicLong sequenceNumber = new AtomicLong(1);

	public static long getNextSequenceNumber() {
		return sequenceNumber.getAndIncrement();
	}

}
