package com.sponberg.fluid.datastore;

public interface SQLExecutableQuery {

	public abstract SQLParameterizedStatement getParameterizedStatement();

	public abstract boolean isAllowRefresh();
	
	public abstract Integer getLimit();
	
	public abstract int getOffset();
	
	public abstract void setOffset(int offset);
	
	public abstract void stepQuery() throws DatastoreException;
	
	public abstract void addResult() throws InstantiationException,
			IllegalAccessException;

	public abstract void setNull(int columnIndex, String columnName);

	public abstract void setInteger(int columnIndex, String columnName,
			Integer value);

	public abstract void setDouble(int columnIndex, String columnName,
			Double value);

	public abstract void setString(int columnIndex, String columnName,
			String value);

	public abstract void setBinary(int columnIndex, String columnName,
			byte[] value);

}