package com.sponberg.fluid.datastore;

public class DatastoreException extends RuntimeException {
	
	public DatastoreException(String message) {
		super(message);
	}
	
	public DatastoreException(Exception e) {
		super(e);
	}
	
}