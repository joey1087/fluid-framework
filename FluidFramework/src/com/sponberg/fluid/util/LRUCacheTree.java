package com.sponberg.fluid.util;

import java.util.HashSet;


public class LRUCacheTree<V> {

	private final int maxEntries;

	LRUCache<String, V> cache;
	
	LRUCache<String, LRUCacheTree<V>> subCache;
	
	HashSet<String> dontCacheChainsStartingWith = new HashSet<>();
	
	public LRUCacheTree(int maxEntries) {
		cache = new LRUCache<>(maxEntries);
		subCache = new LRUCache<>(maxEntries);
		this.maxEntries = maxEntries;
	}
	
	public void dontCacheChainStartingWith(String key) {
		dontCacheChainsStartingWith.add(key);
	}
	
	public V get(String[] keyChain) {
		return getHelper(keyChain, 0);
	}
	
	protected V getHelper(String[] keyChain, int index) {
		if (index == keyChain.length - 1) {
			return cache.get(keyChain[index]);
		} else {
			LRUCacheTree<V> tree = subCache.get(keyChain[index]);
			if (tree == null) {
				return null;
			} else {
				return tree.getHelper(keyChain, index + 1);
			}
		}
	}
	
	public void put(String[] keyChain, V value) {
		if (dontCacheChainsStartingWith.contains(keyChain[0])) {
			return;
		}
		putHelper(keyChain, 0, value);
	}
	
	protected void putHelper(String[] keyChain, int index, V value) {
		if (index == keyChain.length - 1) {
			cache.put(keyChain[index], value);
		} else {
			LRUCacheTree<V> tree = subCache.get(keyChain[index]);
			if (tree == null) {
				tree = new LRUCacheTree<>(maxEntries);
				subCache.put(keyChain[index], tree);
			}
			tree.putHelper(keyChain, index + 1, value);
		}
	}
	
	public void remove(String[] keyChain) {
		removeHelper(keyChain, 0);
	}
	
	protected void removeHelper(String[] keyChain, int index) {
		if (index == keyChain.length - 1) {
			cache.remove(keyChain[index]);
			subCache.remove(keyChain[index]);
		} else {
			LRUCacheTree<V> tree = subCache.get(keyChain[index]);
			if (tree == null) {
				return;
			} else {
				tree.removeHelper(keyChain, index + 1);
			}
		}
	}
	
}
