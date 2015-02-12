package com.sponberg.fluid.datastore;

import lombok.ToString;

@ToString
public class SQLQueryRaw extends SQLQueryDefault {

	Class<? extends SQLQueryResult> queryResultClass = SQLQueryResultDefault.class;

	String rawSqlCommand;
	
	public SQLQueryRaw(String rawSqlCommand) {
		super((String) null);
		this.rawSqlCommand = rawSqlCommand;
	}

	@Override
	public void setWhere(String where) {
		throw new RuntimeException("Not implemented");
	}
	
	@Override
	public SQLWhereClause getWhere() {
		return null;
	}

	@Override
	public void setSelectStatement(String selectStatement) {
		throw new RuntimeException("Not implemented");
	}
	
	@Override
	public SQLParameterizedStatement getParameterizedStatement() {
		return new SQLParameterizedStatement(this.rawSqlCommand, null, null);
	}

}
