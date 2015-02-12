package com.sponberg.fluid.datastore;

import java.util.ArrayList;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.sponberg.fluid.GlobalState;

@Getter
@Setter
@ToString
public class SQLQuery<T extends SQLQueryResult> implements SQLStatement, SQLExecutableQuery {

	String tableName;
	
	final String[] selectColumns;
	
	SQLWhereClause whereClause = null;

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	String selectStatement = null;
	
	int offset = 0;
	
	boolean allowRefresh = true;
	
	Integer limit = null;
	
	Class<T> queryResultClass;
	
	SQLResultList<T> results;
	
	String orderBy;
	
	public <Z extends SQLQueryResult & SQLTable> SQLQuery(Class<Z> queryResultClass, String... selectColumns) {
		this.tableName = SQLUtil.getTableName(queryResultClass);
		this.selectColumns = selectColumns;
		this.queryResultClass = (Class<T>) queryResultClass;
		this.results = new SQLResultList<>(this);
	}
	
	protected SQLQuery(String tableName, Class<T> queryResultClass, String... selectColumns) {
		this.tableName = tableName;
		this.selectColumns = selectColumns;
		this.queryResultClass = queryResultClass;
		this.results = new SQLResultList<>(this);
	}
	
	protected void setWhereClause(SQLWhereClause whereClause) {
		this.whereClause = whereClause;
	}
	
	public void setWhere(String where) {
		whereClause = new SQLWhereClause(where);
	}
	
	public SQLWhereClause getWhere() {
		return whereClause;
	}

	public void setSelectStatement(String selectStatement) {
		if (selectColumns != null && selectColumns.length > 0) {
			throw new RuntimeException("May not call setSelectStatement if selectColumns was used during construction");
		}
		this.selectStatement = selectStatement;
	}
	
	@Override
	public SQLParameterizedStatement getParameterizedStatement() {
		
		if (selectColumns == null) {
			throw new RuntimeException("At least one column must be selected.");
		}
		
		ArrayList<Object> whereParams = null;

		StringBuilder builder = new StringBuilder();
		builder.append("select ");
		
		if (selectStatement != null) {
			builder.append(selectStatement);
		} else {
			boolean first = true;
			for (String column : selectColumns) {
				if (!first) {
					builder.append(",");
				}
				first = false;
				builder.append(column);
			}
		}
		builder.append(" from ");
		builder.append(tableName);

		if (whereClause != null) {
			builder.append(" where ");
			builder.append(whereClause.getWhere());
			whereParams = whereClause.getParameters();
		}
		
		if (orderBy != null) {
			builder.append(" ");			
			builder.append(orderBy);			
		}
		
		if (limit != null) {
			builder.append(" limit ");
			builder.append(limit);
			builder.append(" offset ");
			builder.append(offset);
		}
		
		return new SQLParameterizedStatement(builder.toString(), null, whereParams);
	}

	T result;
	
	@Override
	public void addResult() throws InstantiationException, IllegalAccessException {
		
		result = getQueryResultClass().newInstance();
		results.add(result);
	}
	
	@Override
	public void setNull(int columnIndex, String columnName) {		
		result._setNull(columnName);
	}

	@Override
	public void setInteger(int columnIndex, String columnName, Integer value) {		
		result._setInteger(columnName, value);
	}

	@Override
	public void setDouble(int columnIndex, String columnName, Double value) {		
		result._setDouble(columnName, value);
	}

	@Override
	public void setString(int columnIndex, String columnName, String value) {		
		result._setString(columnName, value);
	}

	@Override
	public void setBinary(int columnIndex, String columnName, byte[] value) {		
		result._setBlob(columnName, value);
	}	

	@Override
	public void stepQuery() throws DatastoreException {
		GlobalState.fluidApp.getDatastoreService().query(this);
	}

}
