package com.sponberg.fluid.test;

import com.sponberg.fluid.sdk.GoogleAnalyticsService;
import com.sponberg.fluid.util.Logger;

public class MockGoogleAnalyticsService implements GoogleAnalyticsService {

	MockCounter sendScreenCounter = new MockCounter();
	
	MockCounter sendEventCounter = new MockCounter();
	
	@Override
	public void sendScreenView(String trackerName, String screenName) {
		
		Logger.debug(this, "sendScreenView {} {}", trackerName, screenName);
		
		sendScreenCounter.increment(trackerName, screenName);
	}

	@Override
	public void sendEvent(String tracker, String category, String action,
			String label) {
		
		Logger.debug(this, "sendEvent {} {} {} {}", tracker, category, action, label);
		
		sendEventCounter.increment(tracker, category, action, label);
	}

	public int getSendScreenViewCount(String trackerName, String screenName) {
		
		return sendScreenCounter.getCount(trackerName, screenName);
	}
	
	public int getSendEventCount(String tracker, String category, String action,
			String label) {
		
		return sendEventCounter.getCount(tracker, category, action, label);
	}
	
}
