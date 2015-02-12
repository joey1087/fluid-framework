package com.sponberg.fluid.datastore;

import java.util.ArrayList;
import java.util.Map.Entry;

import lombok.ToString;

import com.sponberg.fluid.datastore.SQLParameterizedStatement.Pair;

@ToString
public class SQLUpdate<T extends SQLDataInput & SQLTable> implements SQLStatement {

	T object;
	
	SQLWhereClause whereClause = null;
	
	public SQLUpdate(T object) {
		this.object = object;
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
		
		ArrayList<Pair> params = new ArrayList<Pair>();
		ArrayList<Object> whereParams = null;
		
		StringBuilder builder = new StringBuilder();
		builder.append("update ");
		builder.append(object._getTableName());
		builder.append(" set ");
		
		boolean first = true;
		for (Entry<String, Object> entry : object._getData().entrySet()) {
			if (!first) {
				builder.append(", ");
			}
			first = false;
			builder.append(entry.getKey());
			builder.append(" = ?");
			params.add(new Pair(entry.getKey(), entry.getValue()));	
		}
		
		if (whereClause != null) {
			builder.append(" where ");
			builder.append(whereClause.getWhere());
			whereParams = whereClause.getParameters();
		}
		
		return new SQLParameterizedStatement(builder.toString(), params, whereParams);
	}

	public String getTable() {
		return object._getTableName();
	}
	
}
