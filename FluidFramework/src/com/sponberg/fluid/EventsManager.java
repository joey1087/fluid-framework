package com.sponberg.fluid;

import java.util.ArrayList;
import java.util.HashMap;

import com.sponberg.fluid.layout.ActionListener;
import com.sponberg.fluid.layout.ActionListener.EventInfo;

public class EventsManager {

	EventListenerGroup rootEventListenerGroup = new EventListenerGroup();
	
	public void addEventListener(ActionListener listener, String... keyPath) {
		addEventListenerHelper(rootEventListenerGroup, keyPath, 0,
				listener);
	}
	
	public void userTapped(String keyPath, EventInfo eventInfo) {
		String[] tokens = getTokensFromPath(keyPath);
		EventListenerGroup group = getEventListenerGroup(rootEventListenerGroup, tokens, 0);
		if (group != null) {
			for (ActionListener listener : group.getListener()) {
				listener.userTapped(eventInfo);
			}
		}
	}
	
	public void userScrolledToBottom(String keyPath, EventInfo eventInfo) {
		String[] tokens = getTokensFromPath(keyPath);
		EventListenerGroup group = getEventListenerGroup(rootEventListenerGroup, tokens, 0);
		if (group != null) {
			for (ActionListener listener : group.getListener()) {
				listener.userScrolledToBottom(eventInfo);
			}
		}
	}
	
	public void userScrolled(String keyPath, float percentage) {
		String[] tokens = getTokensFromPath(keyPath);
		EventListenerGroup group = getEventListenerGroup(rootEventListenerGroup, tokens, 0);
		if (group != null) {
			for (ActionListener listener : group.getListener()) {
				listener.userScrolled(percentage);
			}
		}
	}
	
	public boolean isListeningForTapAt(String viewPath) {
		
		String[] tokens = getTokensFromPath(viewPath);
		EventListenerGroup group = getEventListenerGroup(rootEventListenerGroup, tokens, 0);
		return group != null && group.getListener().size() > 0;
	}
	
	public void userChangedValueTo(String keyPath, EventInfo eventInfo, Object value) {
		String[] tokens = getTokensFromPath(keyPath);
		EventListenerGroup group = getEventListenerGroup(rootEventListenerGroup, tokens, 0);
		if (group != null) {
			for (ActionListener listener : group.getListener()) {
				listener.userChangedValueTo(eventInfo, value);
			}
		}
	}
	
	public void userCancelled(String keyPath) {
		String[] tokens = getTokensFromPath(keyPath);
		EventListenerGroup group = getEventListenerGroup(rootEventListenerGroup, tokens, 0);
		if (group != null) {
			for (ActionListener listener : group.getListener()) {
				listener.userCancelled();
			}
		}
	}
	
	public EventListenerGroup getEventListenerGroup(EventListenerGroup group, String[] tokens, int tokenIndex) {
		String key = tokens[tokenIndex];
		EventListenerGroup nextGroup = group.get(key);
		if (nextGroup == null) {
			return null;
		}
		if (tokenIndex == tokens.length - 1) {
			return nextGroup;
		} else {
			return getEventListenerGroup(nextGroup, tokens, tokenIndex + 1);
		}
	}
	
	protected void addEventListenerHelper(EventListenerGroup group, String[] tokens, int tokenIndex, ActionListener listener) {
		String key = tokens[tokenIndex];
		EventListenerGroup nextGroup = group.get(key);
		if (nextGroup == null) {
			nextGroup = new EventListenerGroup();
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
	
	static class EventListenerGroup {

		HashMap<String, EventListenerGroup> listenerGroups = new HashMap<>();

		ArrayList<ActionListener> listeners = new ArrayList<>();

		public void addListener(ActionListener listener) {
			listeners.add(listener);
		}
		
		public ArrayList<ActionListener> getListener() {
			return listeners;
		}
		
		EventListenerGroup get(String key) {
			return listenerGroups.get(key);
		}
		
		void put(String key, EventListenerGroup group) {
			listenerGroups.put(key, group);
		}
		
	}
	
}
