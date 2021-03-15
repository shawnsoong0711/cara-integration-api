package com.slinfo.helper;

import java.io.UnsupportedEncodingException;

/**
 * Utility class that contains methods to encode URL components.
 *
 * @author Kelvin Tee
 */
public class URLEncoder {
	public static String encodeUsingUTF8(String s) {
		try {
			// application/x-www-form-urlencoded MIME format
			String htmlFormEncodedString = java.net.URLEncoder.encode(s, "UTF8");
			return htmlFormEncodedString.replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
}
