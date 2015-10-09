package org.bajnarola.utils;

import java.util.Random;

public class RandomString {
	private static final int DEFAULT_RANDOM_STRING_SIZE = 10;

	public static String generateAsciiString(int size) {
		int i;
		char c;
		String s = new String();
		Random r = new Random();
		
		for (i=0; i<size; i++) {
			c = (char)(r.nextInt(25) + 97);
			s += c;
		}
		
		return s;
		
	}
	public static String generateAsciiString() {
		return RandomString.generateAsciiString(DEFAULT_RANDOM_STRING_SIZE);
	}
}
