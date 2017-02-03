package com.durbha.jc.pwhasher;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.InetAddress;

import static org.junit.Assert.*;

import com.durbha.jc.pwhasher.func.HashUtil;
import com.durbha.jc.pwhasher.func.HashingThread;
import com.durbha.jc.pwhasher.func.PWHashHandler;
import com.durbha.jc.pwhasher.http.HTTPConstants;
import com.durbha.jc.pwhasher.http.HTTPReqParser;
import com.durbha.jc.pwhasher.http.HTTPRequest;
import com.durbha.jc.pwhasher.util.FailedOperationException;
import com.durbha.jc.pwhasher.util.InvalidInputException;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Unit test for simple App.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AppTest
{
    final int numTestThreads = 20;
    final int numAppThreads = 10;
    final long millisToSleep = HashingThread.MILLIS_TO_WAIT + 1000;//Sleep 1 second more than actual thread
	private Logger logger = Logger.getLogger(Constants.LOGGER_NAME);
    
	String TEST_PASSWORD = "AngryMonkey";
	
	//Hash gotten from http://hash.online-convert.com/sha512-generator
	String CORRECT_HASH_FOR_TEST_PASSWORD = "a6306201dc431886db117dab3f14f78d234555b6e95c404ebc018d8915bd777d067519cb318460e0e94260c335b2988fd18a41ec1bb362444c9a48d0af74edac";
	
	public AppTest() {
		Results.setHashedPasswords(new ConcurrentHashMap<Long, String>(numAppThreads/2));
		logger.setLevel(Level.FINER);
	}
	
	@Test
	public void simpleHashCheck() {
		
		try {
			assertEquals("Hash is not correct!", CORRECT_HASH_FOR_TEST_PASSWORD, HashUtil.hashPassword(TEST_PASSWORD));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			assertTrue("Exception creating hash!", false);
		}
	}
	
	@Test
	public void testHTTPParsing() {
		String body = "password=AngryMonkey&password2=AngryMonkey2";
		String method = "POST";
		String uri = "/hash";
		String inputLine = method + " " + uri + " HTTP/1.1\r\n"
				+ "Connection: keep-alive\r\n"
				+ "Pragma: no-cache\r\n"
				+ "Cache-Control: no-cache\r\n"
				+ "Content-Type: " + HTTPConstants.CONTENT_TYPES.FORM.getTypeString() + "\r\n"
				+ "Content-Length: " + body.length() + "\r\n"
				+ "Accept: */*\r\n"
				+ "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.76 Safari/537.36\r\n"
				+ "Accept-Encoding: gzip, deflate, sdch, br\r\n"
				+ "Accept-Language: en-US,en;q=0.8\r\n"
				+ "\r\n"
				+ body;
			
			InputStream stringIS = new ByteArrayInputStream(inputLine.getBytes(StandardCharsets.UTF_8));
			
			HTTPReqParser parser = new HTTPReqParser(stringIS, 1000);
			HTTPRequest request = null;
			try {
				request = parser.parseInputStream();
			} catch (FailedOperationException e) {
			} catch (InvalidInputException e) {
			}
			
			assertNotNull("Exception parsing HTTP request", request);
			
			assertEquals("HTTP Method does not match", request.getReqMethod(), method);
			assertEquals("HTTP Path does not match", request.getReqURI(), uri);
			assertEquals("HTTP Body does not match", request.getReqBody(), body);
			assertEquals("HTTP Parameter does not match", request.getParameters().get("password2"), "AngryMonkey2");
		
	}
    
	private static Configuration getDefaultConfiguration(int numberOfThreads) {
		Configuration config = new Configuration();
		try {
			config.setIpToListenTo(InetAddress.getByName("localhost"));
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		}
		config.setMaxCapacity(100);
		config.setNumberOfThreads(numberOfThreads);
		config.setPortNumber(1234);
		return config;
	}
	
	
	@Test
	public void positiveTest01() {
		Main.isAppStopped = false;
		Configuration config = getDefaultConfiguration(1);
		assertNotNull("Configuration error!", config);
		Main appMain = new Main();
		appMain.startWithConfig(config);
		Socket socket = null;
        PrintWriter out = null;
        InputStream in = null;
		try {
			socket = new Socket(config.getIpToListenTo(), config.getPortNumber());
	        out = new PrintWriter(socket.getOutputStream(), true);
			String body = "password=" + TEST_PASSWORD;
			String method = "POST";
			String uri = "/hash";
			String httpContent = method + " " + uri + " HTTP/1.1\r\n"
					+ "Content-Type: " + HTTPConstants.CONTENT_TYPES.FORM.getTypeString() + "\r\n"
					+ "Content-Length: " + body.length() + "\r\n"
					+ "\r\n"
					+ body;
			out.print(httpContent);
			out.flush();
			
			in = socket.getInputStream();
			String responseBody = Util.getRespBody(in);
			
			long sequenceNumber = -1;
	        try {
	        	sequenceNumber = Long.parseLong(responseBody);
	        } catch (NumberFormatException e) {
	        	assertTrue("Response is a not a number", false);
	        }
	        
	        try {
		        out.close();
		        in.close();
		        socket.close();
	        } catch (IOException e) {
	        	
	        }
	        
	        //Get the resulting hash
	        try {
	        	Thread.sleep(millisToSleep);
	        } catch (InterruptedException e) {}
	        
			socket = new Socket(config.getIpToListenTo(), config.getPortNumber());
	        out = new PrintWriter(socket.getOutputStream(), true);
			method = "GET";
			uri = "/hash/" + sequenceNumber;
			httpContent = method + " " + uri + " HTTP/1.1\r\n"
					+ "\r\n";
			out.print(httpContent);
			out.flush();
			
			in = socket.getInputStream();
			responseBody = Util.getRespBody(in);
			
			assertEquals("Hashes do not match", responseBody, CORRECT_HASH_FOR_TEST_PASSWORD);
	        try {
		        out.close();
		        in.close();
		        socket.close();
	        } catch (IOException e) {
	        	
	        }
	        
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue("IO Exception!", false);
		} finally {
	        appMain.stopApp();
		}
	}
	
	@Test
	public void invalidInputNoMethod() {
		Configuration config = getDefaultConfiguration(1);
		assertNotNull("Configuration error!", config);
		Main appMain = new Main();
		appMain.startWithConfig(config);
		Socket socket = null;
        PrintWriter out = null;
		try {
			socket = new Socket(config.getIpToListenTo(), config.getPortNumber());
	        out = new PrintWriter(socket.getOutputStream(), true);
			String body = "password=" + TEST_PASSWORD;
			String httpContent = //method + " " + uri + " HTTP/1.1\r\n" +
					"Content-Type: " + HTTPConstants.CONTENT_TYPES.FORM.getTypeString() + "\r\n"
					+ "Content-Length: " + body.length() + "\r\n"
					+ "\r\n"
					+ body;
			out.print(httpContent);
			out.flush();
			
	        BufferedReader in = new BufferedReader(
	                new InputStreamReader(socket.getInputStream()));
	        String responseLine = in.readLine();
			assertFalse("Response status is not 200", responseLine.contains("200"));
	        
	        try {
		        out.close();
		        in.close();
		        socket.close();
	        } catch (IOException e) {
	        	
	        }
	        
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue("IO Exception!", false);
		} finally {
	        appMain.stopApp();
		}
	}
    
    @Test
    public void zConcurrency() throws InterruptedException { //It is called zConcurrency because we want this to be tested last!
    	Main.isAppStopped = false;//reset
    	final int numIterations = 2;
        final List<String> exceptions = Collections.synchronizedList(new ArrayList<String>());
        final ExecutorService threadPool = Executors.newFixedThreadPool(numTestThreads);
        final long maxTimeoutSeconds = ((long)(millisToSleep / 1000) + 1) * numIterations;
        try {
            final CountDownLatch allExecutorThreadsReady = new CountDownLatch(numTestThreads);
            final CountDownLatch afterInitBlocker = new CountDownLatch(1);
            final CountDownLatch allDone = new CountDownLatch(numTestThreads);
            for (int i=0;i<numTestThreads;i++) {
                threadPool.submit(new Runnable() {
                    public void run() {
                        allExecutorThreadsReady.countDown();
                        try {
                            afterInitBlocker.await();
                            //Actual test goes here
                            for (int count=0;count<numIterations;count++) {
                        		String passwordToHash = Calendar.getInstance().getTimeInMillis() + "";
                    			String correctHash = HashUtil.hashPassword(passwordToHash);
                    			long sequenceNumber = PWHashHandler.initHashing(passwordToHash);
                    			try {
                    				Thread.sleep(millisToSleep);
                    			} catch (InterruptedException e) {}
                    			String resultHash = PWHashHandler.getHashForSeqNumber(sequenceNumber);
                    			if (!correctHash.equals(resultHash)) {
                    				exceptions.add("Hashes do not match");
                    			}
                            }
                        } catch (final Throwable e) {
                        	System.out.println("Exception??");
                            exceptions.add(e.getMessage());
                        } finally {
                            allDone.countDown();
                        }
                    }
                });
            }
            // wait until all threads are ready
            assertTrue("Timeout initializing threads! Perform long lasting initializations before passing runnables to assertConcurrent", allExecutorThreadsReady.await(numTestThreads * 10, TimeUnit.MILLISECONDS));
            // start all test runners
            afterInitBlocker.countDown();
            assertTrue("Timeout! More than" + maxTimeoutSeconds + "seconds", allDone.await(maxTimeoutSeconds, TimeUnit.SECONDS));
        } finally {
            threadPool.shutdownNow();
        }
        assertTrue("Failed with exception(s)" + exceptions, exceptions.isEmpty());
    }
   }
