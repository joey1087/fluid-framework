package com.sponberg.fluid.android;

import android.util.Log;

import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.LoggingService;

public class DefaultLoggingService implements LoggingService {

	private String tag;
	
	FluidFrameworkAndroidApp app;
	
	final private static int kLimit = 4000;
	
	public DefaultLoggingService() {
		tag = GlobalState.fluidApp.getClass().getSimpleName();
	}
	
	@Override
	public void logError(String msg) {
		if (msg.length() <= kLimit) {
			Log.e(tag, msg);
		} else {
			separateLog(msg, true);
		}
	}
	
	@Override
	public void logMessage(String msg) {
		if (msg.length() <= kLimit) {
			Log.i(tag, msg);
		} else {
			try {
			separateLog(msg, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void separateLog(String msg, boolean error) {
		
		int i = 0;
		while (i <= msg.length()) {
			
			int i2 = i + kLimit;
			
			if (i2 >= msg.length()) {
				i2 = msg.length();
			} else {
				int tmp = msg.lastIndexOf("\n", i2);
				i2 = (tmp <= i) ? i2 : tmp;
			}
			
			if (error)
				Log.e(tag, msg.substring(i, i2));
			else
				Log.i(tag, msg.substring(i, i2));
			
			i = i2 + 1; // eat newline
		}
	}

}
