package com.sponberg.fluid.datastore;

import com.sponberg.fluid.FluidApp;

public class MockDatastoreManager extends DatastoreManager {

	Database database;
	
	public MockDatastoreManager() {
		database = new Database(":memory:");
		
		databases.put(":memory:", new Database(":memory"){
			public String getDatabaseName() {
				return ":memory:";
			}
		});
	}
	
	@Override
	public void load(FluidApp app) {		
	}

	public Database getDefaultDatabase() {
		return database;
	}
	
	public Database getDatabase(String databaseName) {
		return databases.get(databaseName);
	}
	
}
