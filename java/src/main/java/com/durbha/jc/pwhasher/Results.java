package com.durbha.jc.pwhasher;

import java.util.concurrent.ConcurrentHashMap;

/**
 * An object that holds the results - the computed hashes and the stats.
 * 
 * @author seetharama
 *
 */
public class Results {

	/*
	 * This is initialized in the main class based on the number of threads
	 */
	private static ConcurrentHashMap<Long, String> hashedPasswords = null;
	private static long numRequestsSoFar = 0;
	private static int averageProcessingTime = 0;
	private static long totalProcessingTime = 0;
	
	/**
	 * Updates average times.
	 * <p>It is synchronized so multiple threads cannot overwrite each others' compute times.
	 * 
	 * @param processTime The time it took the thread to compute the hash
	 */
	public static synchronized void updateAverageTimes(int processTime) {
		
		totalProcessingTime += processTime;
		numRequestsSoFar++;
		averageProcessingTime = (int) (totalProcessingTime / numRequestsSoFar);
		
	}
	
	/**
	 * An object that is used to hold both the number of requests and the average time.
	 * <p>It is used to return the statistics.
	 * 
	 * @author seetharama
	 *
	 */
	public static class AverageTimeSnapshot {
		long numRequests=0;
		int averageTime=0;
		AverageTimeSnapshot(long numRequests, int averageTime) {
			this.numRequests = numRequests;
			this.averageTime = averageTime;
		}
		public long getNumRequests() {
			return numRequests;
		}
		public int getAverageTime() {
			return averageTime;
		}
		
	}
	
	/**
	 * This method is synchronized so that the snapshot we get is accurate in terms of total requests so far and the average time.
	 * The synchronization ensures that updates to either value do not take place while the values are being returned.
	 * 
	 * @return A snapshot of the number of requests and corresponding average times
	 */
	public static synchronized AverageTimeSnapshot getAverageTimes() {
		
		return new AverageTimeSnapshot(numRequestsSoFar, averageProcessingTime);
		
	}
	
	public static int getAveragreTime() {
		return averageProcessingTime;
	}
	
	public static void storeHash(long sequenceNumber, String hash) {
		hashedPasswords.put(sequenceNumber, hash);
	}
	
	public static String getHash(long sequenceNumber) {
		return hashedPasswords.get(sequenceNumber);
	}

	public static ConcurrentHashMap<Long, String> getHashedPasswords() {
		return hashedPasswords;
	}

	public static void setHashedPasswords(ConcurrentHashMap<Long, String> hashedPasswords) {
		Results.hashedPasswords = hashedPasswords;
	}
	
}
