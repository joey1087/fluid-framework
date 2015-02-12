package com.sponberg.app.datastore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sponberg.app.SampleApp;
import com.sponberg.app.datastore.app.DSBook;
import com.sponberg.app.datastore.postcodes.DSPostcode;
import com.sponberg.fluid.datastore.DatastoreTransaction;
import com.sponberg.fluid.datastore.SQLResultList;
import com.sponberg.fluid.test.ClasspathUtil;
import com.sponberg.fluid.test.JavaDatastoreService;
import com.sponberg.fluid.test.MockRealHttpService;
import com.sponberg.fluid.test.MockResourceService;
import com.sponberg.fluid.test.MockUIService;
import com.sponberg.fluid.util.Logger;

public class MultipleDatastoreTest {

	SampleApp app;

	@BeforeClass
	public static void onlyOnce() throws Exception {
		ClasspathUtil.loadSqliteJar("../FluidFramework/");
	}
	
	@Before
	public void setUp() throws Exception {
		
		if (new File("test/app.sqlite").exists()) {
			assertTrue(new File("test/app.sqlite").delete());
		}
		if (new File("test/suburbs.sqlite").exists()) {
			assertTrue(new File("test/suburbs.sqlite").delete());
		}
		
		app = new SampleApp();
		app.setPlatform("mock");
		app.setHttpService(new MockRealHttpService());
		app.setResourceService(new MockResourceService());
		app.setUiService(new MockUIService());
		JavaDatastoreService service = new JavaDatastoreService();
		app.setDatastoreService(service);
		app.setBaseUnit(6.5d);

		app.start();
		
		Logger.setEnabled(true);
		Logger.setLoggingLevel(Logger.LEVEL_DEBUG);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testQueryClassOfT() throws Exception {
		
		DatastoreTransaction txn = new DatastoreTransaction(DS.postcodes);
		txn.start();
		SQLResultList<DSPostcode> results =
				txn.query(DSPostcode.class)
				.select(DSPostcode.id, DSPostcode.title)
				.where("{} like ?")
				.param(DSPostcode.title, "%F%")
				.execute();
		assertEquals(1128, results.size());
		
		DSPostcode postcode =
				txn.query(DSPostcode.class)
				.select(DSPostcode.id, DSPostcode.title)
				.where("{} like ?")
				.param(DSPostcode.title, "%2094%")
				.execute().next();
		assertEquals("2094 - Fairlight", postcode.getTitle());
		
		txn.rollback();
		
		txn = new DatastoreTransaction();
		txn.start();
		DSBook book =
				txn.query(DSBook.class)
				.select(DSBook.numPages)
				.where("{} like ?")
				.param(DSBook.name, "%good%")
				.execute().next();
		
		assertEquals(101, book.getNumPages().intValue());
		txn.rollback();
	}

}
