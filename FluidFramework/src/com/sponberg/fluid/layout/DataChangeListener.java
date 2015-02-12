package com.sponberg.fluid.layout;

public interface DataChangeListener {
	
	public void dataChanged(String key, String...subKeys);
	
	public void dataRemoved(String key);
	
}