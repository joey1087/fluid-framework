package com.sponberg.fluid.datastore;


public interface SQLStatementQuery extends SQLStatement {

	public void setBlobParameter(String column, byte[] value);

	public void setNullParameter(String column);

	public void setDoubleParameter(String column, Double value);

	public void setIntegerParameter(String column, Integer value);

	public void setStringParameter(String column, String value);

}