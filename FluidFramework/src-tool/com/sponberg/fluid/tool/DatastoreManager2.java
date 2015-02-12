package com.sponberg.fluid.tool;

import com.sponberg.fluid.FluidApp;
import com.sponberg.fluid.datastore.DatastoreException;
import com.sponberg.fluid.datastore.DatastoreManager;

public class DatastoreManager2 extends DatastoreManager {

	public DatastoreManager2() {

		enabled = true;
		
		populateDatabasesMap();
	}
	
	@Override
	protected void createOrUpdateDatabase(Database database, FluidApp app)
			throws DatastoreException {
		super.createOrUpdateDatabase(database, app);
	}
	
}
