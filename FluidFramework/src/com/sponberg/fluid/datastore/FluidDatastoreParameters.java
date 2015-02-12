package com.sponberg.fluid.datastore;

import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.SecurityService;
import com.sponberg.fluid.SecurityService.PasswordProvider;
import com.sponberg.fluid.util.HmacUtil;

public class FluidDatastoreParameters {

	public static final String kDsVersion = "dsVersion";
	
	static final String kFluidParameterTable = "__fluid_ptable";
	
	static final String kParameterKey = "key";

	static final String kParameterValue = "value";

	static final String kParameterRowHash = "rh";

	public static void setValue(String key, String value) throws DatastoreException {
		
		int rowHash = getRowHash(key, value);
		
		DatastoreService ds = GlobalState.fluidApp.getDatastoreService();
		
		String statement = "insert into " + kFluidParameterTable + " ("
				+ kParameterKey + "," + kParameterValue + "," + kParameterRowHash + ") values ('"
				+ key + "', '" + value + "', '" + rowHash + "');";
		ds.executeRawStatement(statement);
	}

	private static int getRowHash(String key, String value) {
		SecurityService ss = GlobalState.fluidApp.getSecurityService();
		PasswordProvider pp = ss.getPasswordProvider();
		Integer rowHash = HmacUtil.hmacHashcode(key + value, 
				pp.getHmacKeyFluidDatastoreParameters() + "a1a2a3a4".replace("1",  "$"), 
				ss.getUserSalt());
		if (rowHash == null) {
			throw new DatastoreException("Row hash null");
		}
		return rowHash;
	}
	
	public static String getValue(String key) throws DatastoreException {
		return getValue(key, false);
	}
	
	public static String getValue(String key, boolean ignoreHashMismatch) throws DatastoreException {
		
		DatastoreService ds = GlobalState.fluidApp.getDatastoreService();
		
		SQLQuery<SQLQueryResultDefault> query = new SQLQueryDefault(kFluidParameterTable, kParameterValue, kParameterRowHash);
		query.setWhere("{} = ?");
		query.getWhere().addStringParameter(kParameterKey, key);
		
		SQLResultList<SQLQueryResultDefault> list = ds.query(query);
		if (!list.hasNext()) {
			return null;
		}
		
		SQLQueryResultDefault result = list.next();
		String value = result.getString(kParameterValue);
		Integer rowHash = result.getInteger(kParameterRowHash);
		if (rowHash == null) {
			throw new DatastoreException("No row hash");
		}
		
		int calculatedRowHash = getRowHash(key, value);
		
		if (rowHash != calculatedRowHash && !ignoreHashMismatch) {
			throw new DatastoreException("Row hash mismatch");
		}
		
		return value;
	}
	
}
