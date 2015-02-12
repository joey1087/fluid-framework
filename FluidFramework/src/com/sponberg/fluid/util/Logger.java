package com.sponberg.fluid.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.helpers.MessageFormatter;

import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.LoggingService;

public class Logger {

	public static final int LEVEL_DEBUG = 0;
	public static final int LEVEL_INFO = 1;
	public static final int LEVEL_WARN = 2;
	public static final int LEVEL_ERROR = 3;
	public static final int LEVEL_NONE = 4;
	
	static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	static int loggingLevel = LEVEL_NONE;
	
	static boolean enabled = false;
	
	static boolean includeClassName = true;
	
	static boolean fullPackageName = false;
	
	static boolean includeMethodName = true;
	
	static boolean includeLineNumber = false;
	
	public static void debug(Object thisClass, String msg, Object... params) {
		log(LEVEL_DEBUG, thisClass.getClass(), msg, params);
	}
	
	public static void debug(Object thisClass, Throwable t) {
		ByteArrayOutputStream ostr = new ByteArrayOutputStream();
		t.printStackTrace(new PrintStream(ostr));
		log(LEVEL_DEBUG, thisClass.getClass(), new String(ostr.toByteArray()));
	}

	public static void info(Object thisClass, String msg, Object... params) {
		log(LEVEL_INFO, thisClass.getClass(), msg, params);
	}
	
	public static void info(Object thisClass, Throwable t) {
		ByteArrayOutputStream ostr = new ByteArrayOutputStream();
		t.printStackTrace(new PrintStream(ostr));
		log(LEVEL_INFO, thisClass.getClass(), new String(ostr.toByteArray()));
	}

	public static void warn(Object thisClass, String msg, Object... params) {
		log(LEVEL_WARN, thisClass.getClass(), msg, params);
	}
	
	public static void warn(Object thisClass, Throwable t) {
		ByteArrayOutputStream ostr = new ByteArrayOutputStream();
		t.printStackTrace(new PrintStream(ostr));
		log(LEVEL_WARN, thisClass.getClass(), new String(ostr.toByteArray()));
	}
	
	public static void error(Object thisClass, String msg, Object... params) {
		log(LEVEL_ERROR, thisClass.getClass(), msg, params);
	}
	
	public static void error(Object thisClass, Throwable t) {
		ByteArrayOutputStream ostr = new ByteArrayOutputStream();
		t.printStackTrace(new PrintStream(ostr));
		log(LEVEL_ERROR, thisClass.getClass(), new String(ostr.toByteArray()));
	}
	
	private static void log(final int level, final Class<?> clazz, final String baseMessage, final Object... params) {
		
		if (level < loggingLevel || !enabled || GlobalState.fluidApp.getLoggingService() == null) {
			return;
		}
		
		if (!GlobalState.fluidApp.getSystemService().isOnUiThread()) {
			Runnable r = new Runnable() {
				@Override
				public void run() {
					Logger.log(level, clazz, baseMessage, params);
				}
			};
			GlobalState.fluidApp.getSystemService().runOnUiThread(r);
			return;
		}
		
		String formattedMsg = getMessage(level, clazz, baseMessage, params);
		if (level >= LEVEL_ERROR) {
			GlobalState.fluidApp.getLoggingService().logError(formattedMsg);	
		} else {
			GlobalState.fluidApp.getLoggingService().logMessage(formattedMsg);				
		}
	}
	
	private static String getMessage(int level, Class<?> clazz, String baseMessage, Object... params) {
		
		int stackTraceIndex = 4; // method->info->log->getMessage->getStackTrace
		
		StringBuilder builder = new StringBuilder();
		
		builder.append(dateFormat.format(new Date()));
		
		switch (level) {
		case LEVEL_DEBUG:
			builder.append(" [DEBUG] ");
			break;
		case LEVEL_INFO:
			builder.append(" [INFO] ");
			break;
		case LEVEL_WARN:
			builder.append(" [WARN] ");
			break;
		case LEVEL_ERROR:
			builder.append(" [ERROR] ");
			break;			
		}
		
		StackTraceElement stackTraceElement = null;
		for (int index = stackTraceIndex; index < Thread.currentThread().getStackTrace().length; index++) {
			stackTraceElement = Thread.currentThread().getStackTrace()[index];
			if (!stackTraceElement.getClassName().equals(Logger.class.getName())) {
				break;
			}
		}
		
		if (includeClassName && stackTraceElement == null) {
			builder.append("[?] ");
		} else if (includeClassName && stackTraceElement != null) {
			builder.append("[");
		
			if (fullPackageName) {
				builder.append(clazz.getName());
			} else {
				builder.append(clazz.getSimpleName());
			}
		
			if (includeMethodName) {
				builder.append(" ");
				builder.append(stackTraceElement.getMethodName());
			}
			
			if (includeLineNumber && stackTraceElement.getLineNumber() != -1) {
				builder.append(" ");
				builder.append(stackTraceElement.getLineNumber());
			}
			
			builder.append("] ");
		}
		
		builder.append(MessageFormatter.arrayFormat(baseMessage, params).getMessage());
		
		return builder.toString();
	}

	public static void setDateFormat(String dateFormat) {
		Logger.dateFormat = new SimpleDateFormat(dateFormat);
	}

	public static boolean isEnabled() {
		return enabled;
	}

	public static void setEnabled(boolean enabled) {
		Logger.enabled = enabled;
	}

	public static int getLoggingLevel() {
		return loggingLevel;
	}

	public static void setLoggingLevel(int loggingLevel) {
		Logger.loggingLevel = loggingLevel;
	}

	public static boolean isIncludeClassName() {
		return includeClassName;
	}

	public static void setIncludeClassName(boolean includeClassName) {
		Logger.includeClassName = includeClassName;
	}

	public static boolean isFullPackageName() {
		return fullPackageName;
	}

	public static void setFullPackageName(boolean fullPackageName) {
		Logger.fullPackageName = fullPackageName;
	}

	public static boolean isIncludeMethodName() {
		return includeMethodName;
	}

	public static void setIncludeMethodName(boolean includeMethodName) {
		Logger.includeMethodName = includeMethodName;
	}

	public static boolean isIncludeLineNumber() {
		return includeLineNumber;
	}

	public static void setIncludeLineNumber(boolean includeLineNumber) {
		Logger.includeLineNumber = includeLineNumber;
	}

	public static class SimpleLoggingService implements LoggingService {

		@Override
		public void logMessage(String message) {
			System.out.println(message);
		}

		@Override
		public void logError(String message) {
			System.err.println(message);
		}
		
	}
	
}
