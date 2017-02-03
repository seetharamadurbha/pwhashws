package com.durbha.jc.pwhasher;

import java.net.InetAddress;

public class Configuration {

	int numberOfThreads = 10;
	int maxCapacity = 100; // After this number, server hangs until at least one
							// thread completes
	int portNumber = 80;
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
