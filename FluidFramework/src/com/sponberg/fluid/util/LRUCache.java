package com.sponberg.fluid.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class LRUCache<K, V> extends LinkedHashMap<K, V> {

	private final int maxEntries;

	ArrayList<RemovedListener<K, V>> removedListeners = new ArrayList<>();
	
	public LRUCache(int maxEntries) {
		super(8, .75f, true);
		this.maxEntries = maxEntries;
	}
	
	@Override
	protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
		boolean shouldRemove = size() > maxEntries;
		if (shouldRemove) {
			for (RemovedListener<K, V> l : removedListeners) {
				l.entryWasRemoved(eldest.getKey(), eldest.getValue());
			}
		}
	   return size() > maxEntries;
	}
	
	public void addRemovedListener(RemovedListener<K, V> listener) {
		removedListeners.add(listener);
	}
	
	public static interface RemovedListener<K, V> {
		public void entryWasRemoved(K key, V entry);
	}
	
}
