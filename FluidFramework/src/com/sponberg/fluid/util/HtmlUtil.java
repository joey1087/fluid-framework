package com.sponberg.fluid.util;

public class HtmlUtil {

	public static String escapeSingleQuote(String string) {
		return string.replaceAll("'", "\\\\\"");
	}
	
	public static String escapeBackslashes(String string) {
		return string.replaceAll("\\\\", "\\\\\\\\");
	}

}
