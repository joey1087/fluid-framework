package com.sponberg.fluid.android.util;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;

public class AndroidDebugUtil {

	public static long getAvailableRamInMegs(Context context) {
		
		MemoryInfo mi = new MemoryInfo();
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		activityManager.getMemoryInfo(mi);
		long availableMegs = mi.availMem / 1048576L;
		return availableMegs;
	}
	
}
