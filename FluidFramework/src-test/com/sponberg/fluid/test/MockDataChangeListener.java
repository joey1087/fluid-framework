package com.sponberg.fluid.test;

import com.sponberg.fluid.layout.DataChangeListener;

public class MockDataChangeListener implements DataChangeListener {
	
	public int count;

	public int countRemoved;
	
	@Override
	public void dataChanged(String key, String...subKeys) {
		count++;
	}

	@Override
	public void dataRemoved(String key) {
		countRemoved++;
	}

}
