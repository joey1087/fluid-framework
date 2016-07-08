package com.sponberg.fluid.datastore;

import java.util.HashMap;
import java.util.Map;

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
	
	public void _setData(HashMap<String, Object> data) {
		this._data = data;
	}
	
	public HashMap<String, Object> cloneData() {
		
		HashMap<String, Object> clonedData = new HashMap<>();
		
		for(Map.Entry<String, Object> entry : _data.entrySet()) {
			
			Object copiedObject = null;
			
			if(entry.getValue() instanceof String) {
				copiedObject = (String)entry.getValue(); 
			} else if (entry.getValue() instanceof Integer) {
				Integer in = (Integer)entry.getValue();
				copiedObject = new Integer(in.intValue());
			} else if (entry.getValue() instanceof Double) {
				Double number = (Double)entry.getValue();
				copiedObject = new Double(number.doubleValue());
			} else if (entry.getValue() instanceof byte[]) {
				byte[] array = (byte[])entry.getValue();
				copiedObject = array.clone();
			}
			
			clonedData.put(entry.getKey(), copiedObject);
		}
		
		return clonedData;
	}
}










