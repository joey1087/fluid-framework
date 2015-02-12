package com.sponberg.fluid.datastore;

import java.util.ArrayList;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SQLParameterizedStatement {

	final String unboundSql;
	
	final ArrayList<Pair> updateParamsInOrder;
	
	final ArrayList<Object> whereParamsInOrder;
	
	public SQLParameterizedStatement(String unboundSql, ArrayList<Pair> updateParamsInOrder, ArrayList<Object> whereParamsInOrder) {
		this.unboundSql = unboundSql;
		this.updateParamsInOrder = updateParamsInOrder;
		this.whereParamsInOrder = whereParamsInOrder;
	}
	
	public boolean hasUpdateParams() {
		return updateParamsInOrder != null && updateParamsInOrder.size() > 0;
	}
	
	public static class Pair {
		
		final String key;
		
		final Object value;

		public Pair(String key, Object value) {
			this.key = key;
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public Object getValue() {
			return value;
		}
		
	}
	
}
