package com.sponberg.fluid.datastore;

public interface SQLQueryResult {
	
	public void _setString(String columnName, String value);
	
	public void _setInteger(String columnName, Integer value);
	
	public void _setDouble(String columnName, Double value);
	
	public void _setNull(String columnName);
	
	public void _setBlob(String columnName, byte[] value);
	
}