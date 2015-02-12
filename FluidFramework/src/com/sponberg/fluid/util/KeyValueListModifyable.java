package com.sponberg.fluid.util;

public interface KeyValueListModifyable extends KeyValueList {

	public void add(String key, KeyValueList newKvl);
	
	public void remove(String key);
	
	public void removeByValue(String key, String value);
	
	public void setToValue(String key, KeyValueList newKvl);
	
}
