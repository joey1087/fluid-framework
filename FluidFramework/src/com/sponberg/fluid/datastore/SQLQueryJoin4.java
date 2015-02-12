package com.sponberg.fluid.datastore;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.sponberg.fluid.GlobalState;

@Getter
@Setter
@ToString
public class SQLQueryJoin4<	T extends SQLQueryResult, 
							T2 extends SQLQueryResult,
							T3 extends SQLQueryResult,
							T4 extends SQLQueryResult> extends SQLQueryJoinBase {

	SQLResultList<SQLQueryResultTuple4<T, T2, T3, T4>> results;
	
	public <Z extends SQLQueryResult & SQLTable, 
			Z2 extends SQLQueryResult & SQLTable,
			Z3 extends SQLQueryResult & SQLTable> 
				SQLQueryJoin4(
					Class<Z> queryResultClass, 
					Class<Z2> queryResultClass2,
					Class<Z3> queryResultClass3,
					Class<Z3> queryResultClass4,
					LinkedHashMap<String, ArrayList<String>> columnsByTableName) {

		super(columnsByTableName);
		
		this.results = new SQLResultList<>(this);
		
		resultClasses.add(queryResultClass);
		resultClasses.add(queryResultClass2);
		resultClasses.add(queryResultClass3);		
		resultClasses.add(queryResultClass4);		
	}

	SQLQueryResultTuple4<T, T2, T3, T4> tuple;
	
	@Override
	public void addResult() throws InstantiationException, IllegalAccessException {
		
		tuple = new SQLQueryResultTuple4<>();
		tuple.createInstance(0, getQueryResultClass(0)); // Non table query
		tuple.createInstance(1, getQueryResultClass(1));
		tuple.createInstance(2, getQueryResultClass(2));
		tuple.createInstance(3, getQueryResultClass(3));
		tuple.createInstance(4, getQueryResultClass(4));
		
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
