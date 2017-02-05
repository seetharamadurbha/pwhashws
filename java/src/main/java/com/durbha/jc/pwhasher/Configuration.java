package com.durbha.jc.pwhasher;

import java.net.InetAddress;

/**
 * Configuration of the application. Comes with default values.
 * 
 * @author seetharama
 *
 */
public class Configuration {

	/**
	 * Number of threads, that will process inbound requests. 
	 * Has a default value of 10.
	 */
	int numberOfThreads = 10;
	/**
	 * This is the size of the request buffer, this many requests will be buffered waiting for a thread to become available to process it.
	 */
	int maxCapacity = 100; // After this number, server hangs until at least one
							// thread completes
	/**
	 * Port to listen to
	 */
	int portNumber = 80;
	/**
	 * IP address to listen to
	 */
	InetAddress ipToListenTo = null;
	
	public int getNumberOfThreads() {
		return numberOfThreads;
	}
	public void setNumberOfThreads(int numberOfThreads) {
		this.numberOfThreads = numberOfThreads;
	}
	public int getMaxCapacity() {
		return maxCapacity;
	}
	public void setMaxCapacity(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}
	public int getPortNumber() {
		return portNumber;
	}
	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}
	public InetAddress getIpToListenTo() {
		return ipToListenTo;
	}
	public void setIpToListenTo(InetAddress ipToListenTo) {
		this.ipToListenTo = ipToListenTo;
	}

}
