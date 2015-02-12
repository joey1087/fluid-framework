package com.sponberg.fluid.sdk;

public interface GoogleAnalyticsService extends ExternalSDK {

	public void sendScreenView(String trackerName, String screenName);

	public void sendEvent(String tracker, String category, String action, String label);
	
}
