package com.durbha.jc.pwhasher.func;

import java.nio.charset.StandardCharsets;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.durbha.jc.pwhasher.Constants;

public class HashUtil {
	
	public static String hashPassword(String passwordToHash) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance(Constants.HASHING_ALGORITHM);
		//md.update(PRESET_SALT);
		byte[] bytes = md.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}

}
