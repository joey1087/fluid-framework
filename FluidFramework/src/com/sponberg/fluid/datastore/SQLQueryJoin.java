package com.sponberg.fluid.datastore;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import lombok.Getter;
import lombok.Setter;

import com.sponberg.fluid.GlobalState;

@Getter
@Setter
public class SQLQueryJoin<T extends SQLQueryResult, T2 extends SQLQueryResult> extends SQLQueryJoinBase {
	
	SQLResultList<SQLQueryResultTuple<T, T2>> results;

	public <Z extends SQLQueryResult & SQLTable, Z2 extends SQLQueryResult & SQLTable> SQLQueryJoin(
			Class<Z> queryResultClass, Class<Z2> queryResultClass2,
			LinkedHashMap<String, ArrayList<String>> columnsByTableName) {
		
		super(columnsByTableName);
		
		this.results = new SQLResultList<>(this);
		
		resultClasses.add(queryResultClass);
		resultClasses.add(queryResultClass2);
	}
	
	SQLQueryResultTuple<T, T2> tuple;
	
	@Override
	public void addResult() throws InstantiationException, IllegalAccessException {
		
		tuple = new SQLQueryResultTuple<>();
		tuple.createInstance(0, getQueryResultClass(0)); // Non table query
		tuple.createInstance(1, getQueryResultClass(1));
		tuple.createInstance(2, getQueryResultClass(2));
		
		results.add(tuple);
	}
	
	@Override
	protected SQLQueryResult getCurrentTupleResult(int resultIndex) {
		return tuple.getResult(resultIndex);
	}
	
	@Override
	public void stepQuery() throws DatastoreException {
		GlobalState.fluidApp.getDatastoreService().query(this);
	}

}
