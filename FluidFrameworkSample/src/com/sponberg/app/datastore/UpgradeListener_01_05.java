package com.sponberg.app.datastore;

import com.sponberg.fluid.datastore.DSBase;
import com.sponberg.fluid.datastore.DatastoreException;
import com.sponberg.fluid.datastore.DatastoreTransaction;
import com.sponberg.fluid.datastore.DatastoreVersion;
import com.sponberg.fluid.datastore.SQLTable;
import com.sponberg.fluid.datastore.UpgradeListener;
import com.sponberg.fluid.util.Logger;

public class UpgradeListener_01_05 implements UpgradeListener {

	static final String datastoreName = DS.app;
	
	@Override
	public boolean databaseWasUpgraded(DatastoreVersion version)
			throws DatastoreException {
		
		DatastoreTransaction txn = new DatastoreTransaction();
		
		txn.start();
		
		for (DSLibraryOld library : txn.query(DSLibraryOld.class)
				.select(DSLibraryOld.id, DSLibraryOld.name, DSLibraryOld.address).execute()) {
			
			DSLibraryNew copy = new DSLibraryNew();
			copy.setId(library.getId());
			copy.setName(library.getName());
			copy.setAddress1(library.getAddress());
			
			txn.insert(copy);
		}

		txn.commit();
		
		Logger.debug(this, " Upgrading to 1.5, Library table copied");
		
		return true;
	}

	public static class DSLibraryOld extends DSBase implements SQLTable {

		// Table name

		public static final String _table = "library";

		// Table fields

		public static final String id = "id";
		public static final String name = "name";
		public static final String address = "address";

		// Methods

		public void setId(Integer id) {
			this._data.put(DSLibraryOld.id, id);
		}

		public Integer getId() {
			 return (Integer) _data.get(DSLibraryOld.id);
		}

		public void setName(String name) {
			this._data.put(DSLibraryOld.name, name);
		}

		public String getName() {
			 return (String) _data.get(DSLibraryOld.name);
		}

		public void setAddress(String address1) {
			this._data.put(DSLibraryOld.address, address1);
		}

		public String getAddress() {
			 return (String) _data.get(DSLibraryOld.address);
		}

		public String _getTableName() {
			return DSLibraryOld._table;
		}

	}

	public static class DSLibraryNew extends DSBase implements SQLTable {

		// Table name

		public static final String _table = "library2";

		// Table fields

		public static final String id = "id";
		public static final String name = "name";
		public static final String address1 = "address1";
		public static final String address2 = "address2";

		// Methods

		public void setId(Integer id) {
			this._data.put(DSLibraryNew.id, id);
		}

		public Integer getId() {
			 return (Integer) _data.get(DSLibraryNew.id);
		}

		public void setName(String name) {
			this._data.put(DSLibraryNew.name, name);
		}

		public String getName() {
			 return (String) _data.get(DSLibraryNew.name);
		}

		public void setAddress1(String address1) {
			this._data.put(DSLibraryNew.address1, address1);
		}

		public String getAddress1() {
			 return (String) _data.get(DSLibraryNew.address1);
		}

		public void setAddress2(String address2) {
			this._data.put(DSLibraryNew.address2, address2);
		}

		public String getAddress2() {
			 return (String) _data.get(DSLibraryNew.address2);
		}

		public String _getTableName() {
			return DSLibraryNew._table;
		}

	}

	@Override
	public String getDatastoreName() {
		return datastoreName;
	}
	
}
