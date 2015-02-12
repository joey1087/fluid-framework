package com.sponberg.app.datastore;

import com.sponberg.app.datastore.app.DSBook;
import com.sponberg.app.datastore.app.DSPhoto;
import com.sponberg.fluid.datastore.DatastoreException;
import com.sponberg.fluid.datastore.DatastoreTransaction;
import com.sponberg.fluid.datastore.SQLResultList;

public class Datastore {

	public static SQLResultList<DSBook> getBookNamesWhereIdGreaterThan(int i) throws DatastoreException {
		
		DatastoreTransaction txn = new DatastoreTransaction();
		try {
			txn.start();
			
			return 
					txn.query(DSBook.class)
					.select(DSBook.name)
					.where("{} > ?")
					.param(DSBook.id, i)
					.execute();

		} finally {
			txn.rollback();
		}
	}

	public static void insertPhoto(DSPhoto photo) throws DatastoreException {
		
		DatastoreTransaction txn = new DatastoreTransaction();
		try {
			txn.start();			
			txn.insert(photo);
			txn.commit();
		} finally {
			txn.rollback();
		}
	}
	
}
