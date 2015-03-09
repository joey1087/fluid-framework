package com.sponberg.fluid.android;

public interface DataChangeRunnable {

	public void run(String key, String...subKeys);
	
	public void runRemove(String key);
	
}
