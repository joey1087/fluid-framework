package com.sponberg.fluid.manager;

import java.util.ArrayList;
import java.util.HashMap;

public class PushNotificationManager {

	ArrayList<PushNotificationListener> listeners = new ArrayList<>();
	
	public void addPushNotificationListener(PushNotificationListener listener) {
		
		listeners.add(listener);
	}
	
	public void didReceivePushNotification(HashMap<String, String> data) {
		
		for (PushNotificationListener l : listeners) {
			l.pushNotificationReceived(data);
		}
	}
	
	public interface PushNotificationListener {
		
		// Raw message will be provided, json will be provided if it was able to parse the message as json
		public void pushNotificationReceived(HashMap<String, String> data);
	}
	
}
