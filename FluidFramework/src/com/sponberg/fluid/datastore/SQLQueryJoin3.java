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
public class SQLQueryJoin3<	T extends SQLQueryResult, 
							T2 extends SQLQueryResult,
							T3 extends SQLQueryResult> extends SQLQueryJoinBase {

	SQLResultList<SQLQueryResultTuple3<T, T2, T3>> results;
	
	public <Z extends SQLQueryResult & SQLTable, 
			Z2 extends SQLQueryResult & SQLTable,
			Z3 extends SQLQueryResult & SQLTable> 
				SQLQueryJoin3(
					Class<Z> queryResultClass, 
					Class<Z2> queryResultClass2,
					Class<Z3> queryResultClass3,
					LinkedHashMap<String, ArrayList<String>> columnsByTableName) {

		super(columnsByTableName);
		
		this.results = new SQLResultList<>(this);
		
		resultClasses.add(queryResultClass);
		resultClasses.add(queryResultClass2);
		resultClasses.add(queryResultClass3);		
	}

	SQLQueryResultTuple3<T, T2, T3> tuple;
	
	@Override
	public void addResult() throws InstantiationException, IllegalAccessException {
		
		tuple = new SQLQueryResultTuple3<>();
		tuple.createInstance(0, getQueryResultClass(0)); // Non table query
		tuple.createInstance(1, getQueryResultClass(1));
		tuple.createInstance(2, getQueryResultClass(2));
		tuple.createInstance(3, getQueryResultClass(3));
		
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
