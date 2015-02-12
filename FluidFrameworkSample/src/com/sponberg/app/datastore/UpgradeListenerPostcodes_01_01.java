package com.sponberg.app.datastore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import com.sponberg.app.datastore.postcodes.DSPostcode;
import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.datastore.DatastoreException;
import com.sponberg.fluid.datastore.DatastoreTransaction;
import com.sponberg.fluid.datastore.DatastoreVersion;
import com.sponberg.fluid.datastore.UpgradeListener;
import com.sponberg.fluid.util.CsvReader;
import com.sponberg.fluid.util.CsvReader.Row;
import com.sponberg.fluid.util.Logger;

public class UpgradeListenerPostcodes_01_01 implements UpgradeListener {

	static final String datastoreName = DS.postcodes;

	@Override
	public boolean databaseWasUpgraded(DatastoreVersion version)
			throws DatastoreException {
		
		String csv = GlobalState.fluidApp.getResourceService().getResourceAsString("csv", "postcodes.csv");
		
		CsvReader suburbs;
		try {
			suburbs = new CsvReader(new BufferedReader(new StringReader(csv)), ',');
		} catch (IOException e) {
			Logger.error(this, e);
			return false;
		}
		
		DatastoreTransaction txn = new DatastoreTransaction(DS.postcodes);
		
		txn.start();

		for (Row row : suburbs.getRows()) {
			
			DSPostcode postcode = new DSPostcode();
			postcode.setId(Integer.parseInt(row.get("id")));
			postcode.setCode(row.get("code"));
			postcode.setTitle(row.get("title"));
			
			txn.insert(postcode);
		}

		txn.commit();
		
		Logger.debug(this, " Upgrading postcodes to 1.1, inserted postcodes from csv");
		
		return true;
	}

	@Override
	public String getDatastoreName() {
		return datastoreName;
	}

}
