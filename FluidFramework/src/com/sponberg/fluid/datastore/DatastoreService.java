package com.sponberg.fluid.datastore;

public interface DatastoreService {

	public static final String backupSuffix = ".bak";
	
	public boolean doesDatabaseExist(String databaseName);
	
	public boolean doesBackupExist(String databaseName);
	
	public void restoreBackup(String databaseName) throws DatastoreException;
	
	public void backupDatabase(String databaseName) throws DatastoreException;
	
	public void deleteBackup(String databaseName) throws DatastoreException;
	
	// Returns true if a file exists in the bundle and it was deployed
	public boolean deployDatabaseFromBundle(String databaseName);

	// Open will create if not there
	public void openDatabase(String databaseName) throws DatastoreException;
	
	public void closeDatabase() throws DatastoreException;

	public void deleteDatabase(String databaseName) throws DatastoreException;
	
	// For use on create or update database
	public void executeRawStatement(String statement) throws DatastoreException;
	
	// Query with parameters
	public <T extends SQLQueryResult> SQLResultList<T> query(SQLQuery<T> query) throws DatastoreException;
	
	 // Query join with parameters
	public <T extends SQLQueryResult, T2 extends SQLQueryResult> 
			SQLResultList<SQLQueryResultTuple<T, T2>> query(SQLQueryJoin<T, T2> query) throws DatastoreException;
	
	public <T extends SQLQueryResult, T2 extends SQLQueryResult, T3 extends SQLQueryResult> 
			SQLResultList<SQLQueryResultTuple3<T, T2, T3>> query(SQLQueryJoin3<T, T2, T3> query) throws DatastoreException;
	
	public <T extends SQLQueryResult, T2 extends SQLQueryResult, T3 extends SQLQueryResult, T4 extends SQLQueryResult> 
			SQLResultList<SQLQueryResultTuple4<T, T2, T3, T4>> query(SQLQueryJoin4<T, T2, T3, T4> query) throws DatastoreException;

	public <T extends SQLDataInput & SQLTable> void update(SQLUpdate<T> update) throws DatastoreException;
	
	// returns auto id value
	public <T extends SQLDataInput & SQLTable> Long insert(SQLInsert<T> insert) throws DatastoreException;
	
	public void startTransaction();
	
	public void commitTransaction() throws DatastoreException;
	
	public void rollbackTransaction();
	
}
