package com.sponberg.fluid.android.util;

import android.os.Build;

public class DeviceUtil {

	public static boolean isEmulator() {
		return Build.BRAND.equals("generic");
	}
	
}
