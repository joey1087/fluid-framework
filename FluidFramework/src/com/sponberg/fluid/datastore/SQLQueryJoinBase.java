package com.sponberg.fluid.datastore;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public abstract class SQLQueryJoinBase implements SQLStatement, SQLExecutableQuery {

	// Used for queries that aren't matched to a table,
	// such as 'count(*)'
	// This constant starts with a digit, which would not be a valid sqlite table name
	public static final String kNoTableName = "0 No Table Name";
	
	protected final LinkedHashMap<String, ArrayList<String>> columnsByTableName;
	
	protected SQLWhereClause whereClause = null;

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	protected String selectStatement = null;
	
	protected int offset = 0;
	
	boolean allowRefresh = true;
	
	protected Integer limit = null;

	protected String orderBy = null;
	
	protected ArrayList<Class<? extends SQLQueryResult>> resultClasses = new ArrayList<>();
	
	public SQLQueryJoinBase(LinkedHashMap<String, ArrayList<String>> columnsByTableName) {
		
		this.columnsByTableName = columnsByTableName;
		
		resultClasses.add(SQLQueryResultDefault.class);

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


	@Override
	public SQLParameterizedStatement getParameterizedStatement() {
		
		ArrayList<Object> whereParams = null;

		StringBuilder builder = new StringBuilder();
		builder.append("select ");
		
		if (selectStatement != null) {
			builder.append(selectStatement);
		} else {
			boolean first = true;
			for (String tableName : columnsByTableName.keySet()) {
				for (String column : columnsByTableName.get(tableName)) {
					if (!first) {
						builder.append(",");
					}
					first = false;
					if (!tableName.equals(SQLQueryJoin.kNoTableName)) {
						builder.append(tableName);
						builder.append(".");
					}
					builder.append(column);					
				}
			}
		}
	
		builder.append(" from ");
		
		boolean first = true;
		for (String tableName : columnsByTableName.keySet()) {
			if (tableName.equals(SQLQueryJoin.kNoTableName)) {
				continue;
			}
			if (!first) {
				builder.append(",");
			}
			first = false;
			builder.append(tableName);
		}

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

	public int getResultIndexForColumn(int columnIndex) {
		
		int paramCount = 0;
		int resultIndex = 0;
		for (Entry<String, ArrayList<String>> entry : columnsByTableName.entrySet()) {
			paramCount += entry.getValue().size();
			if (columnIndex < paramCount) {
				return resultIndex;
			}
			resultIndex++;
		}
		throw new RuntimeException("Column index does not exist " + columnIndex);
	}
	
	public Class<? extends SQLQueryResult> getQueryResultClass(int index) {
		return resultClasses.get(index);
	}
	
	@Override
	public void setNull(int columnIndex, String columnName) {		
		result(columnIndex)._setNull(columnName);
	}

	@Override
	public void setInteger(int columnIndex, String columnName, Integer value) {		
		result(columnIndex)._setInteger(columnName, value);
	}

	@Override
	public void setDouble(int columnIndex, String columnName, Double value) {	
		result(columnIndex)._setDouble(columnName, value);
	}

	@Override
	public void setString(int columnIndex, String columnName, String value) {		
		result(columnIndex)._setString(columnName, value);
	}

	@Override
	public void setBinary(int columnIndex, String columnName, byte[] value) {		
		result(columnIndex)._setBlob(columnName, value);
	}	
	
	protected abstract SQLQueryResult getCurrentTupleResult(int resultIndex);
	
	private SQLQueryResult result(int columnIndex) {
		int resultIndex = getResultIndexForColumn(columnIndex);
		SQLQueryResult result = getCurrentTupleResult(resultIndex);
		return result;
	}

}
