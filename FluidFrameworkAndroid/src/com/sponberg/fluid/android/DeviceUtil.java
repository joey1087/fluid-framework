package com.sponberg.fluid.android;

import android.os.Build;

public class DeviceUtil {

	public static boolean isEmulator() {
		return Build.BRAND.equals("generic");
	}
	
}
