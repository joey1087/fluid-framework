package com.sponberg.fluid.tracking;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackingManager {

	public enum UtilType {
		GoogleAnalytics,
		Snowplow
	}
	
	Map<UtilType, ITrackingUtil> trackingUtilsMap =  new HashMap<>();
	
	public void addTrackingUtil(UtilType type, ITrackingUtil util) {
		
		trackingUtilsMap.put(type, util); 
	}
	
	public void setUserId(String userId) {
		
		for (UtilType type : trackingUtilsMap.keySet()) {
			ITrackingUtil util = trackingUtilsMap.get(type);
			util.setUserId(userId);
		}
	}
	
	/**
	 * Send page view to all util types
	 * @param page
	 */
	public void sendPageView(String page) {
		
		for (UtilType type : trackingUtilsMap.keySet()) {
			ITrackingUtil util = trackingUtilsMap.get(type);
			util.sendPageView(page);
		}
	}
	
	/**
	 * Send event to all util types
	 * @param Category
	 * @param Action
	 * @param Label
	 */
	public void sendEvent(String Category, String Action, String Label) {
		
		for (UtilType type : trackingUtilsMap.keySet()) {
			ITrackingUtil util = trackingUtilsMap.get(type);
			util.sendEvent(Category, Action, Label);
		}
	}
	
	/**
	 * TODO: Send page view to type
	 * @param page
	 * @param type
	 */
	public void sendPageView(String page, UtilType type) {
		
	}
	
	/**
	 * TODO: Send event to type
	 * @param Category
	 * @param Action
	 * @param Label
	 * @param type
	 */
	public void sendEvent(String Category, String Action, String Label, UtilType type) {
		
	}
	
	/**
	 * TODO: Send page view to types 
	 * @param page
	 * @param types
	 */
	public void sendPageView(String page, List<UtilType> types) {
		
	}
	
	/**
	 * TODO: Send event to types
	 * @param Category
	 * @param Action
	 * @param Label
	 * @param types
	 */
	public void sendEvent(String Category, String Action, String Label, List<UtilType> types) {
		
	}
}
