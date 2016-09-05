package com.sponberg.fluid.tracking;

public interface ITrackingUtil {

	public void sendPageView(String page);
	
	public void sendEvent(String Category, String Action, String Label);
}
