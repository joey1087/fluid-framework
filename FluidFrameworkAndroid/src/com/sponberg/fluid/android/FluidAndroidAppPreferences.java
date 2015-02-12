package com.sponberg.fluid.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.sponberg.fluid.GlobalState;

public class FluidAndroidAppPreferences {

	private static final String PROPERTY_REG_ID = "regId";
	private static final String PROPERTY_OLD_REG_ID = "oldRegId";
	private static final String PROPERTY_APP_VERSION = "appVersion";

	public static SharedPreferences getSharedPreferences(Context context) {
	    return context.getSharedPreferences(GlobalState.fluidApp.getClass().getName(), Context.MODE_PRIVATE);
	}
	
	private static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}
	
	public static void storeRegistrationId(Context context, String regId) {
		
		String oldRegId = getRegistrationId(context);
		
	    final SharedPreferences prefs = getSharedPreferences(context);
	    int appVersion = getAppVersion(context);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(PROPERTY_REG_ID, regId);
	    editor.putInt(PROPERTY_APP_VERSION, appVersion);
	    if (oldRegId != null)
	    	editor.putString(PROPERTY_OLD_REG_ID, oldRegId);
	    editor.commit();
	}
	
	public static String getRegistrationId(Context context) {
		
	    final SharedPreferences prefs = getSharedPreferences(context);
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    if (registrationId.isEmpty()) {
	        return "";
	    }
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing regID is not guaranteed to work with the new
	    // app version.
	    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    
	    if (registeredVersion != getAppVersion(context)) {
	        return "";
	    }
	    
	    return registrationId;
	}
	
}
