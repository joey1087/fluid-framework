package com.sponberg.fluid.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import com.sponberg.fluid.datastore.DatastoreException;
import com.sponberg.fluid.datastore.DatastoreService;
import com.sponberg.fluid.datastore.SQLDataInput;
import com.sponberg.fluid.datastore.SQLExecutableQuery;
import com.sponberg.fluid.datastore.SQLInsert;
import com.sponberg.fluid.datastore.SQLParameterizedStatement;
import com.sponberg.fluid.datastore.SQLParameterizedStatement.Pair;
import com.sponberg.fluid.datastore.SQLQuery;
import com.sponberg.fluid.datastore.SQLQueryDefault;
import com.sponberg.fluid.datastore.SQLQueryJoin;
import com.sponberg.fluid.datastore.SQLQueryJoin3;
import com.sponberg.fluid.datastore.SQLQueryJoin4;
import com.sponberg.fluid.datastore.SQLQueryResult;
import com.sponberg.fluid.datastore.SQLQueryResultDefault;
import com.sponberg.fluid.datastore.SQLQueryResultTuple;
import com.sponberg.fluid.datastore.SQLQueryResultTuple3;
import com.sponberg.fluid.datastore.SQLQueryResultTuple4;
import com.sponberg.fluid.datastore.SQLResultList;
import com.sponberg.fluid.datastore.SQLTable;
import com.sponberg.fluid.datastore.SQLUpdate;
import com.sponberg.fluid.util.Logger;
import com.sponberg.fluid.util.StreamUtil;

public class JavaDatastoreService implements DatastoreService {

	Connection connection = null;

	boolean inMemory = false;
	
	public JavaDatastoreService() throws ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
	}

	private String getPathOfDatabase(String name) {
		
		if (inMemory)
			return ":memory:";
		
		return new File("test/" + name).getAbsolutePath();
	}

	@Override
	public boolean doesDatabaseExist(String databaseName) {
		
		if (inMemory) {
			try {
				openDatabase("");

				SQLQueryDefault q = new SQLQueryDefault("sqlite_master");
				q.setSelectStatement("count(name)");
				q.setWhere("{} = ?");
				q.getWhere().addStringParameter("type", "table");
				
				SQLResultList<SQLQueryResultDefault> results = query(q);
				if (!results.hasNext())
					return false;
				SQLQueryResultDefault result = (SQLQueryResultDefault) results.next();
				
				int numTables = result.getInteger("count(name)");
				return numTables > 1;
			} catch (DatastoreException e) {
				throw new RuntimeException(e);
			} finally {
				try {
					closeDatabase();
				} catch (DatastoreException e) {
					throw new RuntimeException(e);
				}
			}
		}
		
		File file = new File(getPathOfDatabase(databaseName));
		if (!file.exists()) {
			// There will be an error if the databases dir doesn't exist
			File parentDir = file.getParentFile();
			parentDir.mkdirs();
		}
		return file.exists();
	}

	
	
	@Override
	public boolean deployDatabaseFromBundle(String databaseName) {
		return false;
	}

	@Override
	public void openDatabase(String databaseName) throws DatastoreException {
		try {
			if (connection == null)
				connection = DriverManager.getConnection("jdbc:sqlite:" + getPathOfDatabase(databaseName));
		} catch (SQLException e) {
			throw new DatastoreException(e);
		}
	}

	@Override
	public void closeDatabase() throws DatastoreException {
		if (inMemory)
			return;
		try {
			if (connection != null) {
				connection.close();
				connection = null;
			}
		} catch (SQLException e) {
			throw new DatastoreException(e);
		}
	}

	public void forceClose() {
		try {
			connection.close();
		} catch (SQLException e) {
			throw new DatastoreException(e);
		}
	}
	
	@Override
	public void executeRawStatement(String s) throws DatastoreException {
		try {
			Statement statement = connection.createStatement();
			statement.execute(s);
			statement.close();
		} catch (SQLException e) {
			throw new DatastoreException(e);
		}
	}

	@Override
	public <T extends SQLQueryResult> SQLResultList<T> query(SQLQuery<T> query)
			throws DatastoreException {

		executeQuery(query);

		return query.getResults();
	}

	@Override
	public <T extends SQLQueryResult, T2 extends SQLQueryResult> SQLResultList<SQLQueryResultTuple<T, T2>> query(
			SQLQueryJoin<T, T2> query) throws DatastoreException {
		
		executeQuery(query);
		
		return query.getResults();
	}

	@Override
	public <T extends SQLQueryResult, 
			T2 extends SQLQueryResult,
			T3 extends SQLQueryResult> SQLResultList<SQLQueryResultTuple3<T, T2, T3>> query(
			SQLQueryJoin3<T, T2, T3> query) throws DatastoreException {
		
		executeQuery(query);
		
		return query.getResults();
	}

	@Override
	public <T extends SQLQueryResult, 
			T2 extends SQLQueryResult,
			T3 extends SQLQueryResult,
			T4 extends SQLQueryResult> SQLResultList<SQLQueryResultTuple4<T, T2, T3, T4>> query(
			SQLQueryJoin4<T, T2, T3, T4> query) throws DatastoreException {
		
		executeQuery(query);
		
		return query.getResults();
	}

	private void executeQuery(SQLExecutableQuery query) throws DatastoreException {
		try {
			
			SQLParameterizedStatement pStatement = query.getParameterizedStatement();
			
			//Logger.debug(this, "SQL query: " + pStatement.getUnboundSql());

			PreparedStatement statement = connection.prepareStatement(pStatement.getUnboundSql());
			bindParameters(statement, pStatement);
			
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {

				ResultSetMetaData meta = rs.getMetaData();
				
				query.addResult();
				
				for (int index = 1; index <= meta.getColumnCount(); index++) {

					String columnName = meta.getColumnName(index);
					
					switch (meta.getColumnType(index)) {
					case Types.NULL:
						query.setNull(index - 1, columnName);
						break;
					case Types.INTEGER:
						query.setInteger(index - 1, columnName, rs.getInt(index));
						break;
					case Types.DOUBLE:
						query.setDouble(index - 1, columnName, rs.getDouble(index));
						break;
					case Types.FLOAT:
						query.setDouble(index - 1, columnName, (double) rs.getFloat(index));
						break;
					case Types.REAL:
						query.setDouble(index - 1, columnName, rs.getDouble(index));
						break;
					case Types.VARCHAR:
						query.setString(index - 1, columnName, rs.getString(index));
						break;
					case Types.BLOB:
						query.setBinary(index - 1, columnName, rs.getBytes(index));
						break;
					default:
						Logger.warn(this, "Unknown column type {}",
								meta.getColumnType(index));
					}
				}
			}
			
			rs.close();
			statement.close();
		} catch (SQLException | InstantiationException | IllegalAccessException e) {
			throw new DatastoreException(e);
		}
	}

	private void bindParameters(PreparedStatement statement, SQLParameterizedStatement pStatement) {
		try {
			int index = 1;
			if (pStatement.getUpdateParamsInOrder() != null)
				for (Pair entry : pStatement.getUpdateParamsInOrder()) {
					if (entry.getValue() instanceof Integer) {
						statement.setInt(index++, (Integer) entry.getValue());
					} else if (entry.getValue() instanceof Double) {
						statement.setDouble(index++, (Double) entry.getValue());
					} else if (entry.getValue() instanceof String) {
						statement.setString(index++, (String) entry.getValue());
					} else if (entry.getValue() instanceof byte[]) {
						statement.setBytes(index++, (byte[]) entry.getValue());
					} else if (entry.getValue() == null) {
						statement.setNull(index++, Types.NULL);
					} else {
						throw new RuntimeException("Don't know how to set parameter " + entry.getKey() + " " + entry.getValue());
					}
				}
			if (pStatement.getWhereParamsInOrder() != null)
				for (Object o : pStatement.getWhereParamsInOrder()) {
					if (o instanceof Integer) {
						statement.setInt(index++, (Integer) o);
					} else if (o instanceof Double) {
						statement.setDouble(index++, (Double) o);
					} else if (o instanceof String) {
						statement.setString(index++, (String) o);
					} else if (o instanceof byte[]) {
						statement.setBytes(index++, (byte[]) o);
					} else {
						throw new RuntimeException("Don't know how to set where param " + o);
					}				
				}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T extends SQLDataInput & SQLTable> void update(SQLUpdate<T> update) throws DatastoreException {
		try {
			SQLParameterizedStatement pStatement = update.getParameterizedStatement();
			PreparedStatement statement = connection.prepareStatement(pStatement.getUnboundSql());
			bindParameters(statement, pStatement);
			
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			throw new DatastoreException(e);
		}
	}

	@Override
	public <T extends SQLDataInput & SQLTable> Long insert(SQLInsert<T> insert) throws DatastoreException {
		try {
			SQLParameterizedStatement pStatement = insert.getParameterizedStatement();
			PreparedStatement statement = connection.prepareStatement(pStatement.getUnboundSql());
			
			bindParameters(statement, pStatement);
			
			statement.executeUpdate();
			
			ResultSet tableKeys = statement.getGeneratedKeys();
			tableKeys.next();
			long id = tableKeys.getInt(1);
			tableKeys.close();
			statement.close();
			return id;
		} catch (SQLException e) {
			throw new DatastoreException(e);
		}
	}

	@Override
	public void startTransaction() {
		try {
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void commitTransaction() throws DatastoreException {
		try {
			connection.commit();
		} catch (SQLException e) {
			throw new DatastoreException(e);
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void rollbackTransaction() {
		try {
			connection.rollback();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public boolean isInMemory() {
		return inMemory;
	}

	public void setInMemory(boolean inMemory) {
		this.inMemory = inMemory;
	}

	@Override
	public boolean doesBackupExist(String databaseName) {
		if (inMemory)
			return false;
		return this.doesDatabaseExist(databaseName + DatastoreService.backupSuffix);
	}

	@Override
	public void restoreBackup(String databaseName) throws DatastoreException {
		if (inMemory)
			return;
		File existing = new File(this.getPathOfDatabase(databaseName));
		if (existing.exists())
			existing.delete();
		try {
			StreamUtil.copyInputStream(new FileInputStream(
					getPathOfDatabase(databaseName
							+ DatastoreService.backupSuffix)),
					new FileOutputStream(getPathOfDatabase(databaseName)));
			new File(getPathOfDatabase(databaseName + DatastoreService.backupSuffix)).delete();
		} catch (IOException e) {
			throw new DatastoreException(e);
		}
	}

	@Override
	public void backupDatabase(String databaseName) throws DatastoreException {
		if (inMemory)
			return;		
		File existing = new File(this.getPathOfDatabase(databaseName
				+ DatastoreService.backupSuffix));
		if (existing.exists())
			existing.delete();
		try {
			StreamUtil.copyInputStream(new FileInputStream(
					getPathOfDatabase(databaseName)),
					new FileOutputStream(getPathOfDatabase(databaseName
							+ DatastoreService.backupSuffix)));
		} catch (IOException e) {
			throw new DatastoreException(e);
		}
	}

	@Override
	public void deleteBackup(String databaseName) throws DatastoreException {
		if (inMemory)
			return;
		
		boolean delete = new File(getPathOfDatabase(databaseName + DatastoreService.backupSuffix))
				.delete();
		if (!delete)
			throw new DatastoreException("Unable to delete backup");
	}

	@Override
	public void deleteDatabase(String databaseName) throws DatastoreException {
	
		if (inMemory)
			return;		

		File dbFile = new File(getPathOfDatabase(databaseName));
		if (dbFile.exists()) {
			boolean delete = new File(getPathOfDatabase(databaseName)).delete();
			if (!delete)
				throw new DatastoreException("Unable to delete database");
		}

		String journalName = databaseName + "-journal";
		dbFile = new File(getPathOfDatabase(journalName));
		if (dbFile.exists()) {
			boolean delete = new File(getPathOfDatabase(journalName)).delete();
			if (!delete)
				throw new DatastoreException("Unable to delete database journal");
		}
	}
}
