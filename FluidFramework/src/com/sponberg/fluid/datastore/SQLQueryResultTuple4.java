package com.sponberg.fluid.datastore;

import java.util.ArrayList;

public class SQLQueryResultTuple4<	T extends SQLQueryResult, 
									T2 extends SQLQueryResult,
									T3 extends SQLQueryResult,
									T4 extends SQLQueryResult> {

	ArrayList<SQLQueryResult> results = new ArrayList<>(4);

	public void createInstance(int index, Class<? extends SQLQueryResult> queryResultClass) throws InstantiationException, IllegalAccessException {
		results.add(index, queryResultClass.newInstance());
	}
	
	public SQLQueryResult getResult(int index) {
		return results.get(index);
	}
	
	public SQLQueryResultDefault getDefault() {
		return (SQLQueryResultDefault) results.get(0);
	}

	public T t1() {
		return (T) results.get(1);
	}

	public T2 t2() {
		return (T2) results.get(2);
	}
	
	public T3 t3() {
		return (T3) results.get(3);
	}
	
	public T4 t4() {
		return (T4) results.get(4);
	}
	
}
