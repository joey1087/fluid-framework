package com.sponberg.fluid.util;

import com.sponberg.fluid.GlobalState;

public class HmacUtil {

	public static String hmacBase64(String data, String key, String salt) {
		return GlobalState.fluidApp.getSecurityService().hmacBase64(data, key, salt);
	}
	
	public static Integer hmacHashcode(String data, String key, String salt) {
		String hmac = hmacBase64(data, key, salt);
		if (hmac == null) {
			return null;
		} else {
			return hmac.hashCode();
		}
	}

}
