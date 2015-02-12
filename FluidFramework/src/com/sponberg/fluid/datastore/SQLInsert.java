package com.sponberg.fluid.datastore;

import java.util.ArrayList;
import java.util.Map.Entry;

import lombok.Getter;
import lombok.ToString;

import com.sponberg.fluid.datastore.SQLParameterizedStatement.Pair;

@Getter
@ToString
public class SQLInsert<T extends SQLDataInput & SQLTable> implements SQLStatement {

	T object;
	
	public SQLInsert(T object) {
		this.object = object;
	}
	
	@Override
	public SQLParameterizedStatement getParameterizedStatement() {
		
		ArrayList<Pair> params = new ArrayList<Pair>();
		
		StringBuilder builder = new StringBuilder();
		builder.append("insert into ");
		builder.append(object._getTableName());
		
		if (object._getData().entrySet().size() > 0) {
		
			builder.append(" (");
			
			boolean first = true;
			for (Entry<String, Object> entry : object._getData().entrySet()) {
				if (!first) {
					builder.append(", ");
				}
				first = false;
				builder.append(entry.getKey());
				params.add(new Pair(entry.getKey(), entry.getValue()));
				
			}
			
			builder.append(") values (");
			
			first = true;
			for (int i = 0; i < object._getData().size(); i++) {
				if (!first) {
					builder.append(", ?");
				} else {
					builder.append("?");
				}
				first = false;
			}
			builder.append(")");
		
		} else {
			builder.append(" default values");
		}
		
		return new SQLParameterizedStatement(builder.toString(), params, null);
	}
	
	public String getSqlStatementUnbound() {
		
		StringBuilder builder = new StringBuilder();
		builder.append("insert into ");
		builder.append(object._getTableName());
		builder.append(" (");
		
		boolean first = true;
		for (String key : object._getData().keySet()) {
			if (!first) {
				builder.append(", ");
			}
			first = false;
			builder.append(key);
		}
		builder.append(") values (");
		
		first = true;
		for (int i = 0; i < object._getData().size(); i++) {
			if (!first) {
				builder.append(", ?");
			} else {
				builder.append("?");
			}
			first = false;
		}
		builder.append(")");
		
		return builder.toString();
	}
	
	public String getTable() {
		return object._getTableName();
	}
	
}
