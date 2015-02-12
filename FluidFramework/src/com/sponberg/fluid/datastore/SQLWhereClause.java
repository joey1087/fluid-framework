package com.sponberg.fluid.datastore;

import java.util.ArrayList;

import lombok.Getter;
import lombok.ToString;

import org.slf4j.helpers.MessageFormatter;

@Getter
@ToString
public class SQLWhereClause {

	final String where;
	
	ArrayList<String> paramNames = new ArrayList<>();
	
	ArrayList<Object> parameters = new ArrayList<>();

	public SQLWhereClause(String where) {
		this.where = where;
	}
	
	public SQLWhereClause(String where, ArrayList<String> paramNames, ArrayList<Object> params) {
		this.where = where;
		this.paramNames = paramNames;
		this.parameters = params;
	}
	
	public String getWhere() {
		
		for (int index = 0; index < parameters.size(); index++) {
			if (parameters.get(index) == null) {
				throw new RuntimeException("Parameter value not be null. Instead of '{} = ?' use '{} is null'. Possibly for " + paramNames.get(index));
			}
		}
		
		String where = MessageFormatter.arrayFormat(this.where, paramNames.toArray()).getMessage();
		return where;
	}

	public void addStringParameter(String name, String value) {
		paramNames.add(name);
		parameters.add(value);
	}
	
	public void addIntegerParameter(String name, Integer value) {
		paramNames.add(name);
		parameters.add(value);
	}
	
	public void addDoubleParameter(String name, Double value) {
		paramNames.add(name);
		parameters.add(value);
	}

	public void addBlobParameter(String name, byte[] value) {
		paramNames.add(name);
		parameters.add(value);
	}

	public void addNameParameter(String name, String name2) {
		paramNames.add(name);
		paramNames.add(name2);
	}
	
}
