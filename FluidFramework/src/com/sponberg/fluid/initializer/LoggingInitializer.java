package com.sponberg.fluid.initializer;

import com.sponberg.fluid.ApplicationInitializer;
import com.sponberg.fluid.FluidApp;
import com.sponberg.fluid.util.KeyValueList;
import com.sponberg.fluid.util.Logger;

public class LoggingInitializer implements ApplicationInitializer {

	@Override
	public void initialize(FluidApp app) {
		
		KeyValueList kvl = app.getSettings().get("logging").get(0);
		if (!kvl.getValue().equals("settings")) {
			throw new RuntimeException("Invalid settings under logging");
		}
		
		String dateTimeFormat = kvl.getValue("date-time-format");
		Logger.setDateFormat(dateTimeFormat);
		
		String level = kvl.getValue("level");
		if (level.equalsIgnoreCase("debug")) {
			Logger.setLoggingLevel(Logger.LEVEL_DEBUG);
		} else if (level.equalsIgnoreCase("info")) {
			Logger.setLoggingLevel(Logger.LEVEL_INFO);
		} else if (level.equalsIgnoreCase("warn")) {
			Logger.setLoggingLevel(Logger.LEVEL_WARN);
		} else if (level.equalsIgnoreCase("error")) {
			Logger.setLoggingLevel(Logger.LEVEL_ERROR);
		} else if (level.equalsIgnoreCase("none")) {
			Logger.setLoggingLevel(Logger.LEVEL_NONE);
		} else {
			throw new RuntimeException("Invalid logging level " + level);
		}
		
		if (kvl.contains("includeClassName")) {
			Logger.setIncludeClassName(kvl.getValue("includeClassName").equalsIgnoreCase("true"));
		}

		if (kvl.contains("fullPackageName")) {
			Logger.setFullPackageName(kvl.getValue("fullPackageName").equalsIgnoreCase("true"));			
		}
		
		if (kvl.contains("includeMethodName")) {
			Logger.setIncludeMethodName(kvl.getValue("includeMethodName").equalsIgnoreCase("true"));			
		}
		
		if (kvl.contains("includeLineNumber")) {
			Logger.setIncludeLineNumber(kvl.getValue("includeLineNumber").equalsIgnoreCase("true"));			
		}
		
		Logger.setEnabled(true);
	}

	@Override
	public String[] getSupportedPlatforms() {
		return null;
	}
}
