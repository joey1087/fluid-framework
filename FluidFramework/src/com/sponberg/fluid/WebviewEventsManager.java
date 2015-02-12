package com.sponberg.fluid;

import java.util.ArrayList;
import java.util.HashMap;

import com.sponberg.fluid.layout.WebviewActionListener;

public class WebviewEventsManager {

	WebviewEventListenerGroup rootEventListenerGroup = new WebviewEventListenerGroup();
	
	public void addEventListener(WebviewActionListener listener, String... keyPath) {
		addEventListenerHelper(rootEventListenerGroup, keyPath, 0,
				listener);
	}
	
	public void actionPerformed(String keyPath, String userInfo) {
		String[] tokens = getTokensFromPath(keyPath);
		WebviewEventListenerGroup group = getEventListenerGroup(rootEventListenerGroup, tokens, 0);
		if (group != null) {
			for (WebviewActionListener listener : group.getListener()) {
				listener.actionPerformed(userInfo);
			}
		}
	}
	
	public boolean isListeningForTapAt(String viewPath) {
		
		String[] tokens = getTokensFromPath(viewPath);
		WebviewEventListenerGroup group = getEventListenerGroup(rootEventListenerGroup, tokens, 0);
		return group != null && group.getListener().size() > 0;
	}
	
	public WebviewEventListenerGroup getEventListenerGroup(WebviewEventListenerGroup group, String[] tokens, int tokenIndex) {
		String key = tokens[tokenIndex];
		WebviewEventListenerGroup nextGroup = group.get(key);
		if (nextGroup == null) {
			return null;
		}
		if (tokenIndex == tokens.length - 1) {
			return nextGroup;
		} else {
			return getEventListenerGroup(nextGroup, tokens, tokenIndex + 1);
		}
	}
	
	protected void addEventListenerHelper(WebviewEventListenerGroup group, String[] tokens, int tokenIndex, WebviewActionListener listener) {
		String key = tokens[tokenIndex];
		WebviewEventListenerGroup nextGroup = group.get(key);
		if (nextGroup == null) {
			nextGroup = new WebviewEventListenerGroup();
			group.put(key, nextGroup);
		}
		if (tokenIndex == tokens.length - 1) {
			nextGroup.addListener(listener);
		} else {
			addEventListenerHelper(nextGroup, tokens, tokenIndex + 1, listener);
		}
	}
	
	// Listeners register for a generic view path control
	//   eg. Weather.Table.WeatherRow.DeleteButton
	// The actual view path with have the id of the object in the weather row
	//   eg. Weather.Table.WeatherRow|39348.DeleteButton
	// The actual id should be removed before finding the listener
	private String[] getTokensFromPath(String keyPath) {
		String[] tokens = keyPath.split("\\.");
		for (int i = 0; i < tokens.length; i++) {
			int index = tokens[i].indexOf("|");
			if (index != -1) {
				tokens[i] = tokens[i].substring(0, index);
			}
		}
		return tokens;
	}
	
	static class WebviewEventListenerGroup {

		HashMap<String, WebviewEventListenerGroup> listenerGroups = new HashMap<>();

		ArrayList<WebviewActionListener> listeners = new ArrayList<>();

		public void addListener(WebviewActionListener listener) {
			listeners.add(listener);
		}
		
		public ArrayList<WebviewActionListener> getListener() {
			return listeners;
		}
		
		WebviewEventListenerGroup get(String key) {
			return listenerGroups.get(key);
		}
		
		void put(String key, WebviewEventListenerGroup group) {
			listenerGroups.put(key, group);
		}
		
	}
	
}
