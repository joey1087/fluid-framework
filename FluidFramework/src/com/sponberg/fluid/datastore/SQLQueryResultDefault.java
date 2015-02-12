package com.sponberg.fluid.datastore;

import java.util.HashMap;

import lombok.ToString;

@ToString
public class SQLQueryResultDefault implements SQLQueryResult {

	HashMap<String, Object> data = new HashMap<>();
	
	@Override
	public void _setString(String columnName, String value) {
		data.put(columnName, value);
	}

	public String getString(String columnName) {
		return (String) data.get(columnName);
	}
	
	@Override
	public void _setInteger(String columnName, Integer value) {
		data.put(columnName, value);
	}

	public Integer getInteger(String columnName) {
		return (Integer) data.get(columnName);
	}
	
	@Override
	public void _setDouble(String columnName, Double value) {
		data.put(columnName, value);
	}

	public Double getDouble(String columnName) {
		return (Double) data.get(columnName);
	}
	
	@Override
	public void _setNull(String columnName) {
		data.put(columnName, null);
	}

	@Override
	public void _setBlob(String columnName, byte[] value) {
		data.put(columnName, value);
	}
	
	public byte[] getBlob(String columnName) {
		return (byte[]) data.get(columnName);
	}

	public HashMap<String, Object> getData() {
		return data;
	}
	
}