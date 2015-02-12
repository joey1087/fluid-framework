package com.sponberg.fluid.android.util;

public interface DataChangeRunnable {

	public void run(String key, String...subKeys);
	
	public void runRemove(String key);
	
}
