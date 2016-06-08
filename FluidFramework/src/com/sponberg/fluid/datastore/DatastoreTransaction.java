package com.sponberg.fluid.datastore;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.locks.ReentrantLock;

import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.datastore.DatastoreManager.Database;

public class DatastoreTransaction {

	DatastoreService ds;
	
	boolean started = false;
	
	boolean committed = false;
	
	boolean rolledBack = false;
	
	Database database;
	
	static final ReentrantLock lock = new ReentrantLock();
	
	public DatastoreTransaction() {
		this(GlobalState.fluidApp.getDatastoreManager().getDefaultDatabase().getSimpleName());
	}

	public DatastoreTransaction(String databaseName) {
		ds = GlobalState.fluidApp.getDatastoreService();
		this.database = GlobalState.fluidApp.getDatastoreManager().getDatabase(databaseName);
	}
	
	public void start() throws DatastoreException {
		if (started) {
			throw new DatastoreException("Invalid state when start called");
		}
		
		lock.lock();
		
		ds.openDatabase(database.getDatabaseName());
		ds.startTransaction();
		started = true;
	}
	
	public void commit() throws DatastoreException {
		if (!started || committed || rolledBack) {
			throw new DatastoreException("Invalid state when commit called");
		}
		ds.commitTransaction();
		ds.closeDatabase();
		committed = true;
		
		lock.unlock();
	}
	
	public void rollback() {
		if (!started || committed || rolledBack) {
			return;
		}
		ds.rollbackTransaction();
		try {
			ds.closeDatabase();
		} catch (DatastoreException e) {
		}
		rolledBack = true;
		
		lock.unlock();
	}
	
	public void executeRawStatement(String statement) throws DatastoreException {
		if (!started) {
			throw new DatastoreException("Invalid state, transaction not started");
		}
		ds.executeRawStatement(statement);
	}
	
	public <T extends SQLQueryResult> SQLResultList<T> query(SQLQuery<T> query) throws DatastoreException {
		if (!started) {
			throw new DatastoreException("Invalid state, transaction not started");
		}
		return ds.query(query);
	}
	
	public <T extends SQLQueryResult & SQLTable> QueryBuilder<T> query(Class<T> queryResultClass) {
		if (!started) {
			throw new DatastoreException("Invalid state, transaction not started");
		}
		return new QueryBuilder<T>(queryResultClass);
	}
	
	public <T extends SQLQueryResult & SQLTable, T2 extends SQLQueryResult & SQLTable> QueryJoinBuilder<T, T2> queryJoin(
			Class<T> queryResultClass, Class<T2> queryResultClass2) {
		if (!started) {
			throw new DatastoreException("Invalid state, transaction not started");
		}
		return new QueryJoinBuilder<T, T2>(queryResultClass, queryResultClass2);
	}
	
	public <T extends SQLQueryResult & SQLTable, 
			T2 extends SQLQueryResult & SQLTable,
			T3 extends SQLQueryResult & SQLTable> 
			QueryJoinBuilder3<T, T2, T3> queryJoin(
											Class<T> queryResultClass, 
											Class<T2> queryResultClass2,
											Class<T3> queryResultClass3) {
		if (!started) {
			throw new DatastoreException("Invalid state, transaction not started");
		}
		return new QueryJoinBuilder3<T, T2, T3>(queryResultClass, queryResultClass2, queryResultClass3);
	}
	
	public <T extends SQLQueryResult & SQLTable> QueryFunctionBuilder queryFunction(String function, Class<T> queryResultClass) {
		if (!started) {
			throw new DatastoreException("Invalid state, transaction not started");
		}
		return new QueryFunctionBuilder(function, SQLUtil.getTableName(queryResultClass));
	}
	
	public <T extends SQLDataInput & SQLTable> UpdateBuilder<T> update(T object) throws DatastoreException {
		if (!started) {
			throw new DatastoreException("Invalid state, transaction not started");
		}
		return new UpdateBuilder<T>(object);
	}

	public <T extends SQLDataInput & SQLTable> Long insert(T object) throws DatastoreException {
		SQLInsert<T> insert = new SQLInsert<>(object);
		if (!started) {
			throw new DatastoreException("Invalid state, transaction not started");
		}
		return ds.insert(insert);
	}
	
	public class UpdateBuilder<T extends SQLDataInput & SQLTable> {
		
		T object;
		
		String where;
		
		ArrayList<String> paramNames = new ArrayList<>();
		
		ArrayList<Object> paramValues = new ArrayList<>();
		
		public UpdateBuilder(T object) {
			this.object = object;
		}
		
		public UpdateBuilder<T> where(String where) {
			this.where = where;
			return this;
		}
		
		public UpdateBuilder<T> param(String name, Object value) {
			if (where == null) {
				throw new RuntimeException("Can't set a where param until where has been defined");
			}
			if (value == null) {
				throw new RuntimeException("Where parameter value can't be null. Instead of '= ?' use 'is null'");
			}
			paramNames.add(name);
			paramValues.add(value);
			return this;
		}
		
		public void execute() throws DatastoreException {
			if (object._getData().size() == 0) {
				return;
			}
			SQLUpdate<T> update = new SQLUpdate<>(object);
			if (where != null) {
				update.setWhereClause(new SQLWhereClause(where, paramNames, paramValues));
			}
			ds.update(update);
		}
		
	}
	
	public class QueryBuilderBase {

		String where;
		
		ArrayList<String> paramNames = new ArrayList<>();
		
		ArrayList<Object> paramValues = new ArrayList<>();
		
		int offset = 0;
		
		Integer limit = null;
		
		boolean allowRefresh = true;
		
		String orderBy = null;
		
		String groupBy = null;
	}
	
	public class QueryBuilder<T extends SQLQueryResult & SQLTable> extends QueryBuilderBase {
		
		Class<T> queryResultClass;
		
		String[] columns;
		
		public QueryBuilder(Class<T> queryResultClass) {
			this.queryResultClass = queryResultClass;
		}

		public QueryBuilder<T> select(String... columns) {
			this.columns = columns;
			for (String c : columns) {
				if (c.contains(",")) {
					throw new RuntimeException("Column name (or function) must not contain ','");
				}
			}
			return this;
		}
		
		public QueryBuilder<T> selectColumns(String[] columns) {
			this.columns = columns;
			
			return this;
		}
		
		public QueryBuilder<T> where(String where) {
			this.where = where;
			return this;
		}
		
		public QueryBuilder<T> param(String name, Object value) {
			if (value == null) {
				throw new RuntimeException("Parameter value can't be null. Instead of '{} = ?' use '{} is null'. For: " + name);
			}
			paramNames.add(name);
			paramValues.add(value);
			return this;
		}
		
		public QueryBuilder<T> offset(int offset) {
			this.offset = offset;
			return this;
		}
		
		public QueryBuilder<T> limit(int limit) {
			this.limit = limit;
			return this;
		}
		
		public QueryBuilder<T> allowRefresh(boolean allowRefresh) {
			this.allowRefresh = allowRefresh;
			return this;
		}
		
		public QueryBuilder<T> orderBy(String orderBy) {
			this.orderBy = orderBy;
			return this;
		}
		
		public QueryBuilder<T> groupBy(String groupBy) {
			this.groupBy = groupBy;
			return this;
		}
		
		public SQLResultList<T> execute() throws DatastoreException {
			
			if (where == null && paramNames.size() > 0) {
				throw new RuntimeException("Can't set params without a where clause");
			}
			
			SQLQuery<T> query = new SQLQuery<>(queryResultClass, columns);
			if (where != null) {
				query.setWhereClause(new SQLWhereClause(where, paramNames, paramValues));
			}
			query.setOffset(offset);
			query.setLimit(limit);
			query.setAllowRefresh(allowRefresh);
			query.setOrderBy(orderBy);
			query.setGroupBy(groupBy);
			return ds.query(query);
		}
		
	}
	
	public class QueryFunctionBuilder extends QueryBuilderBase {
		
		String aggregateFunction;
		
		String table;
		
		public QueryFunctionBuilder(String aggregateFunction, String table) {
			this.aggregateFunction = aggregateFunction;
			this.table = table;
		}

		public QueryFunctionBuilder where(String where) {
			this.where = where;
			return this;
		}
		
		public QueryFunctionBuilder param(String name, Object value) {
			if (where == null) {
				throw new RuntimeException("Can't set a where param until where has been defined");
			}
			if (value == null) {
				throw new RuntimeException("Where parameter value can't be null. Instead of '= ?' use 'is null'");
			}
			paramNames.add(name);
			paramValues.add(value);
			return this;
		}
		
		public QueryFunctionBuilder offset(int offset) {
			this.offset = offset;
			return this;
		}
		
		public QueryFunctionBuilder limit(int limit) {
			this.limit = limit;
			return this;
		}
		
		public SQLResultList<SQLQueryResultDefault> execute() throws DatastoreException {
			SQLQuery<SQLQueryResultDefault> query = new SQLQuery<>(table, SQLQueryResultDefault.class, new String[] { aggregateFunction });
			if (where != null) {
				query.setWhereClause(new SQLWhereClause(where, paramNames, paramValues));
			}
			query.setOffset(offset);
			query.setLimit(limit);
			return ds.query(query);
		}
		
	}
	
	public class QueryJoinBuilder<T extends SQLQueryResult & SQLTable, T2 extends SQLQueryResult & SQLTable> extends QueryBuilderBase {
		
		Class<T> queryResultClass;
		
		Class<T2> queryResultClass2;
		
		LinkedHashMap<String, ArrayList<String>> columnsByTablename = new LinkedHashMap<>();
		
		public QueryJoinBuilder(Class<T> queryResultClass, Class<T2> queryResultClass2) {
			this.queryResultClass = queryResultClass;
			this.queryResultClass2 = queryResultClass2;
			
			columnsByTablename.put(SQLQueryJoin.kNoTableName, new ArrayList<String>());
			columnsByTablename.put(SQLUtil.getTableName(queryResultClass), new ArrayList<String>());
			columnsByTablename.put(SQLUtil.getTableName(queryResultClass2), new ArrayList<String>());
		}

		public QueryJoinBuilder<T, T2> select(Class<? extends SQLTable> queryResultClass, String... columns) {
			
			String tableName = SQLUtil.getTableName(queryResultClass);
			
			for (String c : columns) {
				if (c.contains(",")) {
					throw new RuntimeException("Column name (or function) must not contain ','");
				}
				columnsByTablename.get(tableName).add(c);
			}
			
			return this;
		}
		
		public QueryJoinBuilder<T, T2> where(String where) {
			this.where = where;
			return this;
		}
		
		public QueryJoinBuilder<T, T2> param(Class<? extends SQLTable> queryResultClass, String name, Object value) {
			if (where == null) {
				throw new RuntimeException("Can't set a where param until where has been defined");
			}
			if (value == null) {
				throw new RuntimeException("Where parameter value can't be null. Instead of '= ?' use 'is null'");
			}
			String tableName = SQLUtil.getTableName(queryResultClass);
			paramNames.add(tableName + "." + name);
			paramValues.add(value);
			return this;
		}
		
		public QueryJoinBuilder<T, T2> param(
				Class<? extends SQLTable> queryResultClass, String name,
				Class<? extends SQLTable> queryResultClass2, String name2) {
			if (where == null) {
				throw new RuntimeException("Can't set a where param until where has been defined");
			}
			String tableName = SQLUtil.getTableName(queryResultClass);
			String tableName2 = SQLUtil.getTableName(queryResultClass2);
			paramNames.add(tableName + "." + name);
			paramNames.add(tableName2 + "." + name2);
			return this;
		}
		
		public QueryJoinBuilder<T, T2> offset(int offset) {
			this.offset = offset;
			return this;
		}
		
		public QueryJoinBuilder<T, T2> limit(int limit) {
			this.limit = limit;
			return this;
		}

		public QueryJoinBuilder<T, T2> orderBy(String orderBy) {
			this.orderBy = orderBy;
			return this;
		}

		public SQLResultList<SQLQueryResultTuple<T, T2>> execute() throws DatastoreException {
			SQLQueryJoin<T, T2> query = new SQLQueryJoin<>(queryResultClass, queryResultClass2, columnsByTablename);
			if (where != null) {
				query.setWhereClause(new SQLWhereClause(where, paramNames, paramValues));
			}
			query.setOffset(offset);
			query.setLimit(limit);
			query.setOrderBy(orderBy);
			return ds.query(query);
		}
		
	}
	
	public class QueryJoinBuilder3<	T extends SQLQueryResult & SQLTable, 
									T2 extends SQLQueryResult & SQLTable,
									T3 extends SQLQueryResult & SQLTable> extends QueryBuilderBase {
		
		Class<T> queryResultClass;
		
		Class<T2> queryResultClass2;
		
		Class<T3> queryResultClass3;
		
		LinkedHashMap<String, ArrayList<String>> columnsByTablename = new LinkedHashMap<>();
		
		public QueryJoinBuilder3(Class<T> queryResultClass, Class<T2> queryResultClass2, Class<T3> queryResultClass3) {
			this.queryResultClass = queryResultClass;
			this.queryResultClass2 = queryResultClass2;
			this.queryResultClass3 = queryResultClass3;
			
			columnsByTablename.put(SQLQueryJoin.kNoTableName, new ArrayList<String>());
			columnsByTablename.put(SQLUtil.getTableName(queryResultClass), new ArrayList<String>());
			columnsByTablename.put(SQLUtil.getTableName(queryResultClass2), new ArrayList<String>());
			columnsByTablename.put(SQLUtil.getTableName(queryResultClass3), new ArrayList<String>());
		}

		public QueryJoinBuilder3<T, T2, T3> select(Class<? extends SQLTable> queryResultClass, String... columns) {
			
			String tableName = SQLUtil.getTableName(queryResultClass);
			
			for (String c : columns) {
				if (c.contains(",")) {
					throw new RuntimeException("Column name (or function) must not contain ','");
				}
				columnsByTablename.get(tableName).add(c);
			}
			
			return this;
		}
		
		public QueryJoinBuilder3<T, T2, T3> where(String where) {
			this.where = where;
			return this;
		}
		
		public QueryJoinBuilder3<T, T2, T3> param(Class<? extends SQLTable> queryResultClass, String name, Object value) {
			if (where == null) {
				throw new RuntimeException("Can't set a where param until where has been defined");
			}
			if (value == null) {
				throw new RuntimeException("Where parameter value can't be null. Instead of '= ?' use 'is null'");
			}
			String tableName = SQLUtil.getTableName(queryResultClass);
			paramNames.add(tableName + "." + name);
			paramValues.add(value);
			return this;
		}
		
		public QueryJoinBuilder3<T, T2, T3> param(
				Class<? extends SQLTable> queryResultClass, String name,
				Class<? extends SQLTable> queryResultClass2, String name2) {
			if (where == null) {
				throw new RuntimeException("Can't set a where param until where has been defined");
			}
			String tableName = SQLUtil.getTableName(queryResultClass);
			String tableName2 = SQLUtil.getTableName(queryResultClass2);
			paramNames.add(tableName + "." + name);
			paramNames.add(tableName2 + "." + name2);
			return this;
		}
		
		public QueryJoinBuilder3<T, T2, T3> offset(int offset) {
			this.offset = offset;
			return this;
		}
		
		public QueryJoinBuilder3<T, T2, T3> limit(int limit) {
			this.limit = limit;
			return this;
		}

		public QueryJoinBuilder3<T, T2, T3> orderBy(String orderBy) {
			this.orderBy = orderBy;
			return this;
		}

		public SQLResultList<SQLQueryResultTuple3<T, T2, T3>> execute() throws DatastoreException {
			SQLQueryJoin3<T, T2, T3> query = new SQLQueryJoin3<>(queryResultClass, queryResultClass2, queryResultClass3, columnsByTablename);
			if (where != null) {
				query.setWhereClause(new SQLWhereClause(where, paramNames, paramValues));
			}
			query.setOffset(offset);
			query.setLimit(limit);
			query.setOrderBy(orderBy);
			return ds.query(query);
		}
		
	}
	
}

