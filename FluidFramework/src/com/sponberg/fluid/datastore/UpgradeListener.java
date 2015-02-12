package com.sponberg.fluid.datastore;

public interface UpgradeListener {

	// Returns true if successful. If false, the database upgrade is aborted.
	public boolean databaseWasUpgraded(DatastoreVersion version) throws DatastoreException;
	
	public String getDatastoreName();
	
}
