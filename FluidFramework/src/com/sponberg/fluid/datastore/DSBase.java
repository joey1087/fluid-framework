package com.sponberg.fluid.datastore;

import java.util.HashMap;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public abstract class DSBase implements SQLQueryResult, SQLDataInput {

	protected HashMap<String, Object> _data = new HashMap<>();

	@Override
	public void _setString(String columnName, String value) {
		_data.put(columnName, value);
	}
	
	@Override
	public void _setInteger(String columnName, Integer value) {
		_data.put(columnName, value);
	}
	
	@Override
	public void _setDouble(String columnName, Double value) {
		_data.put(columnName, value);
	}
	
	@Override
	public void _setNull(String columnName) {
		_data.put(columnName, null);
	}

	@Override
	public void _setBlob(String columnName, byte[] value) {
		_data.put(columnName, value);
	}

	@Override
	public HashMap<String, Object> _getData() {
		return _data;
	}
	
}
