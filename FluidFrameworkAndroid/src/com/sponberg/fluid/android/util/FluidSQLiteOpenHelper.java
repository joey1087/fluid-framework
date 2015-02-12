package com.sponberg.fluid.android.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class FluidSQLiteOpenHelper extends SQLiteOpenHelper {

	boolean databaseExists = true;
	
	public FluidSQLiteOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Do nothing. FluidFramework handles this.
		databaseExists = false;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Do nothing. FluidFramework handles this.
	}

	public boolean isDatabaseExists() {
		return databaseExists;
	}

}
