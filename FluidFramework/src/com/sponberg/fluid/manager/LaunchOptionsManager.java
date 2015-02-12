package com.sponberg.fluid.manager;

import java.util.HashMap;

public class LaunchOptionsManager {

	HashMap<String, String> pushNotification = new HashMap<>();
	
	public void setPushNotification(HashMap<String, String> pushNotification) {
		this.pushNotification = pushNotification;
	}

	public HashMap<String, String> getPushNotification() {
		return pushNotification;
	}

	public void clearPushNotification() {
		this.pushNotification.clear();
	}
	
}
