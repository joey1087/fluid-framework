package com.sponberg.fluid.util;

public class DebugUtil {

	public static void printCurrentThreadStackTrace(int limit) {
		
		printCurrentThreadStackTrace(limit, 2, false, null);
	}
	
	public static void printCurrentThreadStackTrace(int limit, int skip, boolean useSystemOut, String systemOutPrefix) {
		
		int actualLimit = limit + skip;
		int count = 0;
		for (StackTraceElement e : Thread.currentThread().getStackTrace()) {
		
			if (count < skip) {
				count++;
				continue;
			}
			
			String tab = (count == skip) ? "" : "\t";
			if (useSystemOut) {
				System.out.println((count - skip) + " " + tab + systemOutPrefix + e.toString());
			} else {
				Logger.debug(DebugUtil.class, "{} {}{}", (count - skip), tab, e.toString());
			}
			count++;
			if (count == actualLimit) {
				break;
			}
		}
	}
	
}
