package com.durbha.jc.pwhasher.func;

import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.logging.Logger;

import com.durbha.jc.pwhasher.Constants;
import com.durbha.jc.pwhasher.Main;
import com.durbha.jc.pwhasher.Results;

public class HashingThread extends Thread {
	private Logger logger = Logger.getLogger(Constants.LOGGER_NAME);

	long sequenceNumber;
	String passwordToHash;
	
	public static final long MILLIS_TO_WAIT = 5 * 1000; //5 seconds
	
	public HashingThread(long sequenceNumber, String passwordToHash) {
		this.sequenceNumber = sequenceNumber;
		this.passwordToHash = passwordToHash;
	}

	@Override
	public void run() {
		//First wait 5 seconds
		try {
			Thread.sleep(MILLIS_TO_WAIT);
		} catch (InterruptedException e) {
			
		}
		if (Main.isAppStopped) { //If the app is terminating, no point in calculating the hashes
			return;
		}
		try {
			long startTime = Calendar.getInstance().getTimeInMillis();
			String generatedHash = HashUtil.hashPassword(passwordToHash);
			Results.storeHash(sequenceNumber, generatedHash);
			long finishTime = Calendar.getInstance().getTimeInMillis();
			
			int processTime = (int)(finishTime - startTime);
			logger.fine("Processing time is " + processTime);
			Results.updateAverageTimes(processTime);
			
		} catch (NoSuchAlgorithmException e) { //This should not happen, as availability of SHA-512 was already verified in the main class
			logger.severe("Could not get message digest for " + Constants.HASHING_ALGORITHM);
		}

	}


}
