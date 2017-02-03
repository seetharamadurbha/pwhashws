package com.durbha.jc.pwhasher;

import java.io.Console;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/*
 * Password Hasher - main class that is run from the command line.
 * 
 * Its purpose is to initialize the app and start the port listener.
 * 
 * Command line options supported are
 * 
 * -maxThreads: Number of threads to initialize (default: 10)
 * -maxRequests: Number of requests to queue (after that, new requests will be rejected with Service Not Available error. (default: 100)
 * -port: Port number to listen to (default: 80)
 * -ipAddress: IP Address to listen to (default: gotten from resolving hostname)
 * 
 */
public class Main {
	public static void main(String[] args) {
		Main hasher = new Main();
		hasher.init(args);
	}

	private static final String PARAMETER_NUM_THREADS = "-numThreads";
	private static final String PARAMETER_MAX_REQUESTS = "-maxRequests";
	private static final String PARAMETER_PORT = "-port";
	private static final String PARAMETER_IPADDRESS = "-ipAddress";

	private Logger logger = Logger.getLogger(Constants.LOGGER_NAME);
	public static boolean isAppStopped = false;
	
	SocketListener socketThread = null;

	private void init(String[] args) {
		
		//Initialize the configuration
		Configuration config = null;
		try {
			config = parseArgs(args);
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
			System.out.println("Usage: java " + Main.class.getName() + " " 
					+ PARAMETER_NUM_THREADS + " {{Number of threads to use}} " + PARAMETER_MAX_REQUESTS + " {{Max number of requests to queue}}"
					+ PARAMETER_PORT + " {{portNumber}} " + PARAMETER_IPADDRESS + " {{IP Address to listen to}}"
									);
			System.exit(-1);
		}
		
		System.out.println("Starting with configuration: "
				+ "\n\tportNumber: " + config.getPortNumber()
				+ "\n\tipAddress: " + config.getIpToListenTo()
				+ "\n\tnumThreads: " + config.getNumberOfThreads()
				+ "\n\tmaxRequests: " + config.getMaxCapacity()
				);
		
		logger.info("Log level is " + logger.getParent().getLevel());
		
		startWithConfig(config);
		
		//Now look for commands to terminate
		Console console = System.console();
		while (true) {
			String command = console.readLine("Enter exit to terminate: ");
			if (command.equalsIgnoreCase("exit")) {
				System.out.println("Terminating....");
				stopApp();
				break;
			} else {
				if (command.length() > 0) {
					System.out.println("Unknown command '" + command + "'");
				}
			}
		}
		
		System.exit(0);
	}
	
	public void stopApp() {
		isAppStopped = true; //This is to inform the HashingThread threads
		this.socketThread.requestStop();
	}
	
	public void startWithConfig(Configuration config) {
		
		//Create the concurrent hashmap store for password hashes - make concurrency same as number of threads
		Results.setHashedPasswords(new ConcurrentHashMap<Long, String>(config.getNumberOfThreads()));
		
		//Make sure that SHA-512 is available
		try {
			MessageDigest.getInstance(Constants.HASHING_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			logger.severe(Constants.HASHING_ALGORITHM + " is not available! Terminating.");
			System.exit(-1);
		}


		this.socketThread = new SocketListener(config);
		
		this.socketThread.start(); //Start listening on the port
		
		logger.info("App ready");
		
	}

	private Configuration parseArgs(String[] args) {
		Configuration config = new Configuration();
		if (args != null && args.length > 0) {
			for (int index = 0; index < args.length; index++) {
				switch (args[index]) {
				case PARAMETER_NUM_THREADS:
					if (index >= args.length) { //We reached the end of parameters
						throw new IllegalArgumentException(PARAMETER_NUM_THREADS + " must provide a value");
					}
					index++;
					try {
						config.setNumberOfThreads(Integer.parseInt(args[index]));
					} catch (NumberFormatException e) {
						throw new IllegalArgumentException(PARAMETER_NUM_THREADS + " must provide a numeric value (" + args[index] + " is not a number).");
					}
					break;
				case PARAMETER_MAX_REQUESTS:
					if (index >= args.length) { //We reached the end of parameters
						throw new IllegalArgumentException(PARAMETER_MAX_REQUESTS + " must provide a value");
					}
					index++;
					try {
						config.setMaxCapacity(Integer.parseInt(args[index]));
					} catch (NumberFormatException e) {
						throw new IllegalArgumentException(PARAMETER_MAX_REQUESTS + " must provide a numeric value (" + args[index] + " is not a number).");
					}
					break;
				case PARAMETER_PORT:
					if (index >= args.length) { //We reached the end of parameters
						throw new IllegalArgumentException(PARAMETER_PORT + " must provide a value");
					}
					index++;
					try {
						config.setPortNumber(Integer.parseInt(args[index]));
					} catch (NumberFormatException e) {
						throw new IllegalArgumentException(PARAMETER_PORT + " must provide a numeric value (" + args[index] + " is not a number).");
					}
					break;
				case PARAMETER_IPADDRESS:
					if (index >= args.length) { //We reached the end of parameters
						throw new IllegalArgumentException(PARAMETER_IPADDRESS + " must provide a value");
					}
					index++;
					try {
						String ipAddress = args[index];
						config.setIpToListenTo(InetAddress.getByName(ipAddress));
					} catch (UnknownHostException e) {
						throw new IllegalArgumentException(PARAMETER_PORT + " must provide a valid IP Address (" + args[index] + " is not an IP Address).");
					}
					break;
				default:
					throw new IllegalArgumentException("Unknown parameter - " + args[index]);
				}
			}
		}
		if (config.getIpToListenTo() == null) {
			try {
				config.setIpToListenTo(InetAddress.getLocalHost());
			} catch (UnknownHostException e) {
				throw new IllegalArgumentException("No IP address was specified in parameters, and could not resolve local host's IP to listen to. Specify IP address to listen to using "
								+ PARAMETER_IPADDRESS + " parameter.");
			}
		}
		return config;
	}
}
