package com.sponberg.fluid.datastore;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SQLQueryDefault extends SQLQuery<SQLQueryResultDefault> {

	public SQLQueryDefault(String tableName, String... selectColumns) {
		super(tableName, SQLQueryResultDefault.class, selectColumns);
	}

}
