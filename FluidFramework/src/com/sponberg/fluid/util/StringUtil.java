package com.sponberg.fluid.util;


public class StringUtil {

	public static String capitalized(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}
	
	public static String underscoreDashToCamelCase(String s) {
		String[] parts = s.split("[_-]");
		StringBuilder builder = new StringBuilder();
		for (String part : parts) {
			builder.append(capitalized(part));
		}
		return builder.toString();
	}
	
	public static String underscoreToCamelCase(String s) {
		String[] parts = s.split("_");
		String camelCaseString = "";
		for (String part : parts) {
			camelCaseString = camelCaseString + StringUtil.toProperCase(part);
		}
		return camelCaseString;
	}

	public static String toProperCase(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}

	public static String processEscapes(String s) {
		return s.replaceAll("\\\\n", "\n");
	}
	
}
