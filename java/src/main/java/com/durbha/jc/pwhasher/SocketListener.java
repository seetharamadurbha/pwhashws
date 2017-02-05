package com.durbha.jc.pwhasher;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

import com.durbha.jc.pwhasher.http.HTTPConstants.CONTENT_TYPES;
import com.durbha.jc.pwhasher.http.HTTPConstants.RESPONSE_CODES;
import com.durbha.jc.pwhasher.util.SocketResponder;
import com.durbha.jc.pwhasher.util.ThreadPool;

/**
 * This thread starts listening on a port. This thread helps the Main thread to continue listening on the console for termination command.
 * 
 * <p>Creates a thread pool, starts listening on the port, creates a SocketHandler for every incoming request, and triggers the thread pool to run that SocketHandler.
 * 
 * @author seetharama
 *
 */
public class SocketListener extends Thread {

	Configuration config = null;
	
	public SocketListener(Configuration config) {
		this.config = config;
	}
	private Logger logger = Logger.getLogger(Constants.LOGGER_NAME);
	
	boolean stopRequested = false;
	ThreadPool threadPool = null;
	ServerSocket serverSocket = null;

	@Override
	public void run() {

		this.threadPool = new ThreadPool(config.getNumberOfThreads(), config.getMaxCapacity()); // I personally hate parameters that are of same type in a sequence - can cause errors when changing variable names and/or not using appropriate variable names

		int socketBacklog = config.getMaxCapacity() * 2; // Let's not reject connections at the socket level, until there are requests that are double the size of the queue capacity
		
		try {
			serverSocket = new ServerSocket(config.getPortNumber(), socketBacklog, config.getIpToListenTo());
		} catch (IOException e) {
			this.logger.severe(String.format("Unable to listen on port " + config.getPortNumber()));
			System.exit(-1);
		}
		
		while(!stopRequested) {
			Socket socket = null;
			try {
				socket = serverSocket.accept();
			} catch (IOException e) {
				logger.severe("Unable to accept connections on the given IP Address and port: error: " + e.getMessage() + "\nShutting down!!");
				break;
			}
			try {
				SocketHandler requestHandler = new SocketHandler(socket);
				this.threadPool.execute(requestHandler);
			} catch (IllegalStateException e) {
				logger.warning("Failed to create a new thread to process request: error: " + e.getMessage());
				//This could be because a stop is requested, or just too many requests
				SocketResponder.sendResponse(socket, RESPONSE_CODES.SERVICE_NOT_AVAILABLE, null, CONTENT_TYPES.TEXT, "Service not available");
			}
		}
		
		
	}

	/**
	 * Closes the socket, and informs the thread pool to stop as well.
	 */
	public void requestStop() {
		logger.info("Stop requested...");
		this.stopRequested = true;
		try {
			serverSocket.close();
			logger.info("Closed server socket");
		} catch (IOException e) {
			//tolerate!
		}
		
		threadPool.stop();
		
	}

}
