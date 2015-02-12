package com.sponberg.fluid.datastore;

import java.util.HashMap;

public class SQLUtil {

	static HashMap<Class<?>, String> tableName = new HashMap<>();
	
	public static <T extends SQLTable> String getTableName(Class<T> queryResultClass) {
		
		if (queryResultClass == null) {
			return SQLQueryJoin.kNoTableName;
		}
		
		try {
			String name = tableName.get(queryResultClass);
			if (name == null) {
				name = queryResultClass.newInstance()._getTableName();
				tableName.put(queryResultClass, name);
			}
			return name;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}
	
}
