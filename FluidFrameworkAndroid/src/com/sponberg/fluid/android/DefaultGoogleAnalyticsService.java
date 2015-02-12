package com.sponberg.fluid.android;

import java.util.HashMap;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.sdk.GoogleAnalyticsService;
import com.sponberg.fluid.util.Logger;

public class DefaultGoogleAnalyticsService implements GoogleAnalyticsService {

	HashMap<String, Tracker> mTrackers = new HashMap<String, Tracker>();
	
	Context context;
	
	public DefaultGoogleAnalyticsService(Context context) {
		this.context = context;
	}
	
	synchronized Tracker getTracker(String trackerId) {
		
		if (!mTrackers.containsKey(trackerId)) {

			GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
			Tracker t = analytics.newTracker(trackerId);
			mTrackers.put(trackerId, t);
			
			t.enableAutoActivityTracking(false);
			
			String setting = GlobalState.fluidApp.getSetting("google-analytics", "settings", "session-timeout");
			if (setting != null) {
				t.setSessionTimeout(Integer.parseInt(setting));				
			}

			setting = GlobalState.fluidApp.getSetting("google-analytics", "settings", "log-level");
			if (setting != null) {
				GoogleAnalytics.getInstance(context).getLogger().setLogLevel(Integer.parseInt(setting));				
			}
			
			setting = GlobalState.fluidApp.getSetting("google-analytics", "settings", "dispatch-period-in-seconds");
			if (setting != null) {
				GoogleAnalytics.getInstance(context).setLocalDispatchPeriod(Integer.parseInt(setting));
			}
			
			setting = GlobalState.fluidApp.getSetting("google-analytics", "settings", "dry-run");
			if (setting != null && setting.equals("true")) {
				GoogleAnalytics.getInstance(context).setDryRun(true);				
			}
		}
		return mTrackers.get(trackerId);
	  }
	
	@Override
	public void sendScreenView(String tracker, String screen) {
	
		Logger.debug(this, "sendScreenView {} {}", tracker, screen);
		
        Tracker t = getTracker(tracker);
        t.setScreenName(screen);
        t.send(new HitBuilders.AppViewBuilder().build());		
	}

	
	@Override
	public void sendEvent(String tracker, String category, String action, String label) {
	
		Logger.debug(this, "sendEvent {} {} {} {}", tracker, category, action, label);
		
        Tracker t = getTracker(tracker);
        t.send(new HitBuilders.EventBuilder()
        			.setCategory(category)
        			.setAction(action)
        			.setLabel(label)
        			.build());		
	}
}
