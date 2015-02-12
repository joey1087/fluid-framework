package com.sponberg.fluid.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.sponberg.fluid.datastore.DatastoreException;
import com.sponberg.fluid.datastore.DatastoreService;
import com.sponberg.fluid.datastore.SQLDataInput;
import com.sponberg.fluid.datastore.SQLExecutableQuery;
import com.sponberg.fluid.datastore.SQLInsert;
import com.sponberg.fluid.datastore.SQLParameterizedStatement;
import com.sponberg.fluid.datastore.SQLQuery;
import com.sponberg.fluid.datastore.SQLQueryJoin;
import com.sponberg.fluid.datastore.SQLQueryJoin3;
import com.sponberg.fluid.datastore.SQLQueryJoin4;
import com.sponberg.fluid.datastore.SQLQueryResult;
import com.sponberg.fluid.datastore.SQLQueryResultTuple;
import com.sponberg.fluid.datastore.SQLQueryResultTuple3;
import com.sponberg.fluid.datastore.SQLQueryResultTuple4;
import com.sponberg.fluid.datastore.SQLResultList;
import com.sponberg.fluid.datastore.SQLTable;
import com.sponberg.fluid.datastore.SQLUpdate;
import com.sponberg.fluid.util.Logger;
import com.sponberg.fluid.util.StreamUtil;

public class DefaultDatastoreService implements DatastoreService {

	SQLiteDatabase database;

	Context context;

	public DefaultDatastoreService(Context context) {
		this.context = context;
	}

	@Override
	public void closeDatabase() throws DatastoreException {
		if (database != null) {
			database.close();
		}
		database = null;
	}

	@Override
	public void commitTransaction() throws DatastoreException {
		try {
			database.setTransactionSuccessful();
		} catch (IllegalStateException e) {
			throw new DatastoreException("Unable to commit transaction");
		} finally {
			database.endTransaction();
		}
	}

	@Override
	public boolean deployDatabaseFromBundle(String name) {
		return false; // hstdbc implement
	}

	@Override
	public boolean doesDatabaseExist(String name) {
		File dbFile = new File(getPathToDatabase(name));
		if (!dbFile.exists()) {
			// There will be an error if the databases dir doesn't exist
			File parentDir = dbFile.getParentFile();
			parentDir.mkdirs();
		}
	    return dbFile.exists();
	}

	private String getPathToDatabase(String name) {
		return context.getDatabasePath(name).getAbsolutePath();
	}

	@Override
	public void executeRawStatement(String statement) throws DatastoreException {
		try {
			database.execSQL(statement);
		} catch (SQLException e) {
			Logger.warn(this, e);
			throw new DatastoreException("Unable to execute statement "
					+ statement);
		}
	}

	private void bindParameters(ContentValues cv, SQLParameterizedStatement statement) {
		for (SQLParameterizedStatement.Pair entry : statement.getUpdateParamsInOrder()) {
			if (entry.getValue() instanceof Integer) {
				cv.put(entry.getKey(), (Integer) entry.getValue());
			} else if (entry.getValue() instanceof Double) {
				cv.put(entry.getKey(), (Double) entry.getValue());
			} else if (entry.getValue() instanceof String) {
				cv.put(entry.getKey(), (String) entry.getValue());
			} else if (entry.getValue() instanceof byte[]) {
				cv.put(entry.getKey(), (byte[]) entry.getValue());
			}
		}
	}

	@Override
	public <T extends SQLDataInput & SQLTable> Long insert(SQLInsert<T> insert) throws DatastoreException {

		SQLParameterizedStatement pStatement = insert.getParameterizedStatement();

		ContentValues cv = null;
		if (pStatement.hasUpdateParams()) {
			cv = new ContentValues();
			bindParameters(cv, pStatement);
		}

		try {
			if (cv == null) {
				database.execSQL(pStatement.getUnboundSql());
				Cursor c = database.rawQuery("select last_insert_rowid()", null);
				if (c.moveToFirst()) {
					return c.getLong(0);
				}
				return -1l;
			} else {
				long id = database.insertOrThrow(insert.getTable(), null, cv);
				return id;
			}
		} catch (SQLiteException e) {
			Logger.warn(this, e);
			throw new DatastoreException("Unable to execute insert "
					+ insert.getSqlStatementUnbound());
		}
	}

	@Override
	public void openDatabase(String name) throws DatastoreException {
		try {
			database = SQLiteDatabase.openDatabase(getPathToDatabase(name), null,
					SQLiteDatabase.OPEN_READWRITE + SQLiteDatabase.CREATE_IF_NECESSARY);
		} catch (SQLiteException e) {
			Logger.warn(this, e);
			throw new DatastoreException("Unable to open database " + name);
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

		SQLParameterizedStatement pStatement = query.getParameterizedStatement();

		try {

			Cursor cursor = database.rawQuery(pStatement.getUnboundSql(), getParametersAsStringArray(pStatement.getWhereParamsInOrder()));

			while (cursor.moveToNext()) {

				query.addResult();

				for (int index = 0; index < cursor.getColumnCount(); index++) {
					String columnName = cursor.getColumnName(index);
					switch (cursor.getType(index)) {
					case Cursor.FIELD_TYPE_NULL:
						query.setNull(index, columnName);
						break;
					case Cursor.FIELD_TYPE_INTEGER:
						query.setInteger(index, columnName, cursor.getInt(index));
						break;
					case Cursor.FIELD_TYPE_FLOAT:
						query.setDouble(index, columnName, cursor.getDouble(index));
						break;
					case Cursor.FIELD_TYPE_STRING:
						query.setString(index, columnName, cursor.getString(index));
						break;
					case Cursor.FIELD_TYPE_BLOB:
						query.setBinary(index, columnName, cursor.getBlob(index));
						break;
					default:
						Logger.warn(this, "Unknown column type {}", cursor.getType(index));
					}
				}
			}

			cursor.close();

		} catch (SQLiteException | InstantiationException | IllegalAccessException e) {
			throw new DatastoreException("Unable to execute query "
					+ pStatement.getUnboundSql());
		}
	}

	@Override
	public void rollbackTransaction() {
		database.endTransaction();
	}

	@Override
	public void startTransaction() {
		database.beginTransaction();
	}

	@Override
	public <T extends SQLDataInput & SQLTable> void update(SQLUpdate<T> update) throws DatastoreException {

		SQLParameterizedStatement pStatement = update.getParameterizedStatement();

		ContentValues cv = new ContentValues();
		bindParameters(cv, pStatement);

		String where = null;
		Collection<Object> whereParams = null;
		if (update.getWhere() != null) {
			where = update.getWhere().getWhere();
			whereParams = update.getWhere().getParameters();
		}

		try {
			database.update(update.getTable(), cv, where, getParametersAsStringArray(whereParams));
		} catch (SQLiteException e) {
			throw new DatastoreException("Unable to execute update "
					+ pStatement.getUnboundSql());
		}
	}

	private String[] getParametersAsStringArray(Collection<Object> parameters) {
		if (parameters == null) {
			return new String[0];
		}
		int index = 0;
		String[] params = new String[parameters.size()];
		for (Object object : parameters) {
			params[index++] = object.toString();
		}
		return params;
	}

	@Override
	public void backupDatabase(String databaseName) throws DatastoreException {
		File existing = new File(this.getPathToDatabase(databaseName
				+ DatastoreService.backupSuffix));
		if (existing.exists()) {
			existing.delete();
		}
		try {
			StreamUtil.copyInputStream(new FileInputStream(
					getPathToDatabase(databaseName)),
					new FileOutputStream(getPathToDatabase(databaseName
							+ DatastoreService.backupSuffix)));
		} catch (IOException e) {
			throw new DatastoreException(e);
		}
	}

	@Override
	public void deleteBackup(String databaseName) throws DatastoreException {
		boolean delete = new File(getPathToDatabase(databaseName
				+ DatastoreService.backupSuffix)).delete();
		if (!delete) {
			throw new DatastoreException("Unable to delete backup");
		}
	}

	@Override
	public boolean doesBackupExist(String databaseName) {
		return this.doesDatabaseExist(databaseName + DatastoreService.backupSuffix);
	}

	@Override
	public void restoreBackup(String databaseName) throws DatastoreException {
		File existing = new File(this.getPathToDatabase(databaseName));
		if (existing.exists()) {
			existing.delete();
		}
		try {
			StreamUtil.copyInputStream(new FileInputStream(
					getPathToDatabase(databaseName
							+ DatastoreService.backupSuffix)),
					new FileOutputStream(getPathToDatabase(databaseName)));
			new File(getPathToDatabase(databaseName + DatastoreService.backupSuffix)).delete();
		} catch (IOException e) {
			throw new DatastoreException(e);
		}
	}

	@Override
	public void deleteDatabase(String databaseName) throws DatastoreException {

		File dbFile = new File(getPathToDatabase(databaseName));
		if (dbFile.exists()) {
			boolean delete = new File(getPathToDatabase(databaseName)).delete();
			if (!delete) {
				throw new DatastoreException("Unable to delete database");
			}
		}

		String journalName = databaseName + "-journal";
		dbFile = new File(getPathToDatabase(journalName));
		if (dbFile.exists()) {
			boolean delete = new File(getPathToDatabase(journalName)).delete();
			if (!delete) {
				throw new DatastoreException("Unable to delete database journal");
			}
		}
	}

}
