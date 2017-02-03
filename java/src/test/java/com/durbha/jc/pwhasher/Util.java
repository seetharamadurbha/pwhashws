package com.durbha.jc.pwhasher;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import com.durbha.jc.pwhasher.http.HTTPReqParser;

public class Util {
	

	public static String getRespBody(InputStream inputStream) throws IOException {
		
        BufferedReader in = new BufferedReader(
                new InputStreamReader(inputStream));
        String responseLine = in.readLine();
        
        boolean foundBodyDelimiter = false, isFirstLine = true;
        String responseBody = null;
        while (responseLine != null) {
        	if (responseLine.length() == 0) {
        		foundBodyDelimiter = true;
        	} else {
            	if (foundBodyDelimiter) {
            		responseBody = responseLine;
            	} else {
            		if (isFirstLine) {
            			//status must be 200
            			System.out.println("First line is " + responseLine);
            			assertTrue("Response status is not 200", responseLine.contains("200"));
            			isFirstLine = false;
            		} else {
    	        		//ignore headers
            		}
            	}
        	}
            responseLine = in.readLine();
        }
		return responseBody;
	}
	
	public static void main(String[] args) throws Exception {
	}

	public static void mainHttpParser(String[] args) throws Exception {
		String inputLine = "GET /api/auth/status HTTP/1.1\r\n"
			+ "Host: r3byr2.com\r\n"
			+ "Connection: keep-alive\r\n"
			+ "Pragma: no-cache\r\n"
			+ "Cache-Control: no-cache\r\n"
			+ "Accept: */*\r\n"
			+ "X-Requested-With: XMLHttpRequest\r\n"
			+ "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.76 Safari/537.36\r\n"
			+ "Referer: https://r3byr2.com/\r\n"
			+ "Accept-Encoding: gzip, deflate, sdch, br\r\n"
			+ "Accept-Language: en-US,en;q=0.8\r\n"
			+ "Cookie: oscookie=a50e3a3a-b2ea-4826-907d-430c2ab3086d; linkedin_oauth_781h2mto88z2py=null; linkedin_oauth_781h2mto88z2py_crc=null; _ga=GA1.2.294613353.1483124391; _gat=1\r\n"
			+ "\r\n"
			+ "This is the message body";
		
		InputStream stringIS = new ByteArrayInputStream(inputLine.getBytes(StandardCharsets.UTF_8));
		/*
		BufferedReader buffReader = new BufferedReader(new InputStreamReader(stringIS, StandardCharsets.UTF_8));
		String line = buffReader.readLine();
		while (line != null) {
			System.out.println(line);
			line = buffReader.readLine();
		}
		buffReader.close();
		stringIS.close();
		*/
		
		HTTPReqParser parser = new HTTPReqParser(stringIS, 1000);
		parser.parseInputStream();
	}

}
