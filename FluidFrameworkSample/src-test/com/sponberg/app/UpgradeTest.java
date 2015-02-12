package com.sponberg.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sponberg.app.datastore.app.DSBook;
import com.sponberg.app.datastore.app.DSLibrary;
import com.sponberg.app.datastore.app.DSPhoto;
import com.sponberg.fluid.datastore.DatastoreException;
import com.sponberg.fluid.datastore.DatastoreTransaction;
import com.sponberg.fluid.datastore.DatastoreTransaction.QueryJoinBuilder;
import com.sponberg.fluid.datastore.DatastoreTransaction.QueryJoinBuilder3;
import com.sponberg.fluid.datastore.SQLQueryResultTuple;
import com.sponberg.fluid.datastore.SQLQueryResultTuple3;
import com.sponberg.fluid.datastore.SQLResultList;
import com.sponberg.fluid.layout.ViewBehaviorTable;
import com.sponberg.fluid.layout.ViewBehaviorWebView;
import com.sponberg.fluid.test.ClasspathUtil;
import com.sponberg.fluid.test.JavaDatastoreService;
import com.sponberg.fluid.test.MockDataChangeListener;
import com.sponberg.fluid.test.MockRealHttpService;
import com.sponberg.fluid.test.MockResourceService;
import com.sponberg.fluid.test.MockUIService;
import com.sponberg.fluid.util.Logger;

public class UpgradeTest {

	SampleApp app;
	
	@BeforeClass
	public static void onlyOnce() throws Exception {
		Logger.setLoggingLevel(Logger.LEVEL_DEBUG);
		ClasspathUtil.loadSqliteJar("../FluidFramework/");
	}
	
	@Before
	public void setUp() throws Exception {
		app = new SampleApp();
		app.setPlatform("mock");
		app.setHttpService(new MockRealHttpService());
		app.setResourceService(new MockResourceService());
		app.setUiService(new MockUIService());
		JavaDatastoreService service = new JavaDatastoreService();
		app.setDatastoreService(service);
		app.setBaseUnit(6.5d);
		app.start();
	}

	@Test
	public void test() {
		// delete datastore file
	}
}
