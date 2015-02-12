package com.sponberg.fluid.util;

import java.util.List;
import java.util.Set;

public interface KeyValueList {
	
	public boolean contains(String key);
	
	public List<? extends KeyValueList> get(String key);
	
	public KeyValueList getWithValue(String key, String value);
	
	public String getValue();
	
	public String getValue(String key);
	
	public List<String> getValues(String key);
	
	public Set<String> keys();
	
}
