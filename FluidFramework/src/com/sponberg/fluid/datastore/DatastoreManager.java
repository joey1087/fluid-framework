package com.sponberg.fluid.datastore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;

import com.sponberg.fluid.ApplicationLoader;
import com.sponberg.fluid.FluidApp;
import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.SecurityService;
import com.sponberg.fluid.util.KeyValueList;
import com.sponberg.fluid.util.Logger;

public class DatastoreManager implements ApplicationLoader {

	KeyValueList settings;

	@Getter
	protected boolean enabled = false;

	@Getter
	protected String defaultDatabaseName;
	
	protected HashMap<String, Database> databases = new HashMap<>();
	
	// Can have 1 listener per toVersion, per database
	protected HashMap<String, HashMap<DatastoreVersion, UpgradeListener>> upgradeListeners = new HashMap<>();
	
	@Override
	public void load(FluidApp app) {

		settings = app.getSettings().get("datastore").get(0);
		if (!settings.getValue().equals("settings")) {
			throw new RuntimeException("Invalid settings under logging");
		}

		if (!settings.getValue("enabled").equalsIgnoreCase("true")) {
			return;
		}
		
		enabled = true;
		
		defaultDatabaseName = settings.getValue("default-database");
		
		populateDatabasesMap();
		
		if (app.getDatastoreService() == null) {
			throw new RuntimeException("DatastoreService not initialized");
		}

		for (Database database : databases.values()) {
			createOrUpdateDatabase(database, app);
		}
		
		upgradeListeners = null; // Don't need these anymore
	}

	protected void createOrUpdateDatabase(Database database, FluidApp app) throws DatastoreException {
		
		DatastoreService ds = app.getDatastoreService();

		try {
			
			populateVersionList(database);

			if (ds.doesBackupExist(database.getDatabaseName())) {
				// Something went wrong with the upgrade last time we tried
				ds.restoreBackup(database.getDatabaseName());
			}
			
			boolean databaseExists = false;
			
			if (!ds.doesDatabaseExist(database.getDatabaseName())) {
				databaseExists = ds.deployDatabaseFromBundle(database.getDatabaseName());
			}
			
			ds.openDatabase(database.getDatabaseName());

			// If there is no user salt, then this is the first time the app is running on this device.
			// The database could have been restored from a backup.
			// We won't have a user hash saved on the device, so we want to ignore a hash mistmatch,
			// and then save the row with the same value to put the correct hash
			boolean ignoreHashMismatch = false;
			SecurityService ss = GlobalState.fluidApp.getSecurityService();
			if (!ss.hasUserSalt()) {
				ignoreHashMismatch = true;
			}
			
			boolean databaseSchemaExists = databaseExists ? true : doesSchemaExist();
			
			if (!databaseSchemaExists) {

				// Start with a clean slate
				ds.closeDatabase();
				ds.deleteDatabase(database.getDatabaseName());
				ds.openDatabase(database.getDatabaseName());
				
				Logger.debug(this, "Database schema doesn't exist, creating database now: {}", database.getDatabaseName());
				createDatabase(database, app);
			}
			
			DatastoreVersion versionOfDatabase = getVersionOfDatabase(ignoreHashMismatch);
			
			if (databaseSchemaExists && ignoreHashMismatch) {
				// Set the correct hash
				setDatastoreVersion(versionOfDatabase);
			}
			
			Logger.debug(this, "Version of database on device is {}, current version is {}", versionOfDatabase, database.currentVersion);
			
			if (versionOfDatabase.compareTo(database.currentVersion) < 0) {
				upgradeDatabase(database, app, versionOfDatabase);
			}

			ds.closeDatabase();
			
		} catch (DatastoreException e) {
			Logger.error(this, e);
			throw new RuntimeException(e);
		}
		
	}
	
	private void createDatabase(Database database, FluidApp app) throws DatastoreException {
		
		DatastoreService ds = app.getDatastoreService();
		
		ds.startTransaction();
		
		DatastoreVersion firstVersion = database.versions.get(0);
		
		String fileSuffix = getVersionFileSuffix(firstVersion);

		String fileName = database.getSimpleName() + "_create" + fileSuffix;
		
		Logger.debug(this, "Opening {}", fileName);
		
		String createStatements = app.getResourceService().getResourceAsString(
				"sql", fileName);
		
		executeRawStatements(createStatements);
		
		// Create a table for use by fluid
		String create = "create table " + FluidDatastoreParameters.kFluidParameterTable + " ( " 
				+ FluidDatastoreParameters.kParameterKey + " text unique on conflict replace, " 
				+ FluidDatastoreParameters.kParameterValue + " text, "
				+ FluidDatastoreParameters.kParameterRowHash + " int);";
		ds.executeRawStatement(create);

		setDatastoreVersion(new DatastoreVersion(0,0));
		
		ds.commitTransaction();
		
		notifyUpgradeListener(database, ds, firstVersion);
		
		setDatastoreVersion(firstVersion);
	}

	private void notifyUpgradeListener(Database database, DatastoreService ds,
			DatastoreVersion toVersion) {
		
		UpgradeListener listener = null;
		HashMap<DatastoreVersion, UpgradeListener> map = upgradeListeners.get(database.getSimpleName());
		if (map != null) {
			listener = map.get(toVersion);
		}
		if (listener != null) {
			ds.closeDatabase();
			boolean success = listener.databaseWasUpgraded(toVersion);
			if (!success) {
				throw new DatastoreException("User aborted upgrade with listener on version " + toVersion);
			}
			ds.openDatabase(database.getDatabaseName());			
		}
	}

	private void upgradeDatabase(Database database, FluidApp app, DatastoreVersion fromVersion) throws DatastoreException {

		DatastoreService ds = app.getDatastoreService();

		ds.backupDatabase(database.getDatabaseName());
		
		try {
			upgradeDatabaseHelper(database, app, fromVersion);
			ds.deleteBackup(database.getDatabaseName());
		} catch (DatastoreException e) {
			ds.closeDatabase();
			ds.restoreBackup(database.getDatabaseName());
			throw e;
		}
		
	}
	
	private void upgradeDatabaseHelper(Database database, FluidApp app, DatastoreVersion fromVersion) throws DatastoreException {
		
		boolean performUpgrade = false;
		DatastoreVersion lastUpgrade = new DatastoreVersion(0, 0);
		for (DatastoreVersion v : database.versions) {
			
			if (v.compareTo(database.currentVersion) > 0) {
				throw new RuntimeException("Version can't be higher than current version " + v);
			}
			
			if (performUpgrade) {
				if (v.compareTo(fromVersion) <= 0) {
					throw new RuntimeException("Version to upgrade to must be higher than datastore version " + v);
				}
				if (v.compareTo(lastUpgrade) <= 0) {
					throw new RuntimeException("Version must be higher than last upgrade version " + v);
				}
				
				upgradeDatabaseVersion(database, app, v);
				lastUpgrade = v;
				
				if (v == database.currentVersion) {
					break;
				}
				
			} else if (fromVersion.equals(v)) {
				performUpgrade = true;
			}
		}
		
	}

	private void upgradeDatabaseVersion(Database database, FluidApp app, DatastoreVersion version) throws DatastoreException {
		
		Logger.debug(this, "Upgrading database {} to {}", database.getSimpleName(), version);
		
		String fileSuffix = getVersionFileSuffix(version);

		String createStatements = app.getResourceService().getResourceAsString(
				"sql", database.getSimpleName() + "_upgrade" + fileSuffix);

		DatastoreService ds = GlobalState.fluidApp.getDatastoreService();
		
		ds.startTransaction();		

		executeRawStatements(createStatements);
		
		ds.commitTransaction();	

		notifyUpgradeListener(database, ds, version);

		setDatastoreVersion(database.currentVersion);
		
	}

	private void executeRawStatements(String statements) throws DatastoreException {
		DatastoreService ds = GlobalState.fluidApp.getDatastoreService();
		for (String createStatement : getIndividualCreateStatements(statements)) {
			ds.executeRawStatement(createStatement);
		}
	}
	
	private void setDatastoreVersion(DatastoreVersion datastoreVersion) throws DatastoreException {
		String formattedNumber = formatVersionNumber(datastoreVersion);
		FluidDatastoreParameters.setValue(FluidDatastoreParameters.kDsVersion, formattedNumber);
	}
	
	private DatastoreVersion parseVersionNumber(String version) {
		
		String[] versionTokens = version.split("\\.");
		
		int majorVersion = Integer.parseInt(versionTokens[0]);
		int minorVersion = Integer.parseInt(versionTokens[1]);
		
		if (minorVersion > 99) {
			throw new RuntimeException("Minor version must be <= 99");
		}
		
		return new DatastoreVersion(majorVersion, minorVersion);
	}
	
	private String formatVersionNumber(DatastoreVersion version) {
		return String.format("%02d", version.getMajorVersion()) + "." + String.format("%02d", version.getMinorVersion());	
	}
	
	private String getVersionFileSuffix(DatastoreVersion version) {
		String versionPart = formatVersionNumber(version).replaceAll("\\.", "_");
		return "_" +  versionPart + ".sql";
	}

	private List<String> getIndividualCreateStatements(String createStatements) {

		// sqlite can only take 1 statement at a time. Parse all the statements
		// into individual statements.

		ArrayList<String> statements = new ArrayList<>();

		StringBuilder builder = new StringBuilder();

		Reader in = new BufferedReader(new StringReader(createStatements));

		boolean openSingleQuote = false;
		boolean openDoubleQuote = false;

		try {
			int c;
			while ((c = in.read()) != -1) {
				builder.append((char) c);
				if (c == ';' && !openSingleQuote && !openDoubleQuote) {
					statements.add(builder.toString().trim());
					builder = new StringBuilder();
					continue;
				}
				if (c == '\'' && !openDoubleQuote) {
					openSingleQuote = !openSingleQuote;
				} else if (c == '\"' && !openSingleQuote) {
					openDoubleQuote = !openDoubleQuote;
				}
			}
		} catch (IOException e) {
			Logger.error(this, e);
		}

		return statements;
	}
	
	private boolean doesSchemaExist() throws DatastoreException {
		
		DatastoreService ds = GlobalState.fluidApp.getDatastoreService();
		
		SQLQuery<SQLQueryResultDefault> query = new SQLQueryDefault("sqlite_master", "name");
		query.setWhere("{} = ? and {} = ?");
		query.getWhere().addStringParameter("type", "table");
		query.getWhere().addStringParameter("name", FluidDatastoreParameters.kFluidParameterTable);
		
		boolean hasTable = ds.query(query).hasNext();
		
		if (hasTable) {
			
			String dbVersion = FluidDatastoreParameters.getValue(FluidDatastoreParameters.kDsVersion);
			
			if (dbVersion == null || dbVersion.equals("00.00")) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}
	
	private DatastoreVersion getVersionOfDatabase(boolean ignoreHashMismatch) throws DatastoreException {
	
		DatastoreService ds = GlobalState.fluidApp.getDatastoreService();
		
		SQLQuery<SQLQueryResultDefault> query = new SQLQueryDefault("sqlite_master", "name");
		query.setWhere("{} = ? and {} = ?");
		query.getWhere().addStringParameter("type", "table");
		query.getWhere().addStringParameter("name", FluidDatastoreParameters.kFluidParameterTable);
		
		SQLResultList<SQLQueryResultDefault> list = ds.query(query);
		
		if (!list.hasNext()) {
			throw new RuntimeException("Database is missing internal fluid table");
		} else {
			
			String dbVersion = FluidDatastoreParameters.getValue(FluidDatastoreParameters.kDsVersion);
			if (dbVersion == null) {
				throw new RuntimeException("Missing dbVersion");
			}
			
			return this.parseVersionNumber(dbVersion);
		}		
	}

	private void populateVersionList(Database database) {
		
		String versionFileString = GlobalState.fluidApp.getResourceService()
				.getResourceAsString("generated", "datastoreVersions_" + database.getSimpleName() + ".txt");
		BufferedReader in = new BufferedReader(new StringReader(
				versionFileString));
		String line;
		try {
			while ((line = in.readLine()) != null) {
				database.versions.add(parseVersionNumber(line));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		database.currentVersion = database.versions.get(database.versions.size() - 1);
	}
	
	protected void populateDatabasesMap() {
		
		String versionFileString = GlobalState.fluidApp.getResourceService()
				.getResourceAsString("generated", "datastores.txt");
		BufferedReader in = new BufferedReader(new StringReader(
				versionFileString));
		String line;
		try {
			while ((line = in.readLine()) != null) {
				databases.put(line, new Database(line));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Database getDefaultDatabase() {
		return databases.get(defaultDatabaseName);
	}
	
	public Database getDatabase(String databaseName) {
		return databases.get(databaseName);
	}
	
	public Collection<Database> getDatabases() {
		return databases.values();
	}
	
	public void setUpgradeListener(DatastoreVersion toVersion, UpgradeListener listener) {
		String databaseSimpleName = listener.getDatastoreName();
		HashMap<DatastoreVersion, UpgradeListener> map = upgradeListeners.get(databaseSimpleName);
		if (map == null) {
			map = new HashMap<>();
			upgradeListeners.put(databaseSimpleName, map);
		}
		if (map.containsKey(toVersion)) {
			Logger.warn(this, "Setting the upgrade listener, but it was already set. Replacing it {} {}", databaseSimpleName, toVersion);
		}
		map.put(toVersion, listener);
	}
	
	public static class Database {
		
		ArrayList<DatastoreVersion> versions = new ArrayList<>();
		
		DatastoreVersion currentVersion;
		
		private final String name;
		
		public Database(String name) {
			this.name = name;
		}
		
		public String getDatabaseName() {
			return name + ".sqlite";
		}
		
		public String getSimpleName() {
			return name;
		}
		
	}
	
	@Override
	public String[] getSupportedPlatforms() {
		return null;
	}
}
