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
import com.sponberg.fluid.layout.Layout;
import com.sponberg.fluid.layout.ViewBehaviorTable;
import com.sponberg.fluid.layout.ViewBehaviorWebView;
import com.sponberg.fluid.test.ClasspathUtil;
import com.sponberg.fluid.test.JavaDatastoreService;
import com.sponberg.fluid.test.MockDataChangeListener;
import com.sponberg.fluid.test.MockRealHttpService;
import com.sponberg.fluid.test.MockResourceService;
import com.sponberg.fluid.test.MockSecurityService;
import com.sponberg.fluid.test.MockSystemService;
import com.sponberg.fluid.test.MockUIService;
import com.sponberg.fluid.util.Logger;

public class SampleAppTest {

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
		app.setSystemService(new MockSystemService());
		app.setSecurityService(new MockSecurityService());
		JavaDatastoreService service = new JavaDatastoreService();
		service.setInMemory(true);
		app.setDatastoreService(service);
		app.setBaseUnit(6.5d);
		app.start();
	}

	@Test
	public void test() {
		try {
			app.getScreen("Home").getLayout().getViews(false, 320d, 480d, "Home");
			app.getScreen("List").getLayout().getViews(false, 320d, 480d, "List");
			app.getScreen("Details").getLayout().getViews(false, 320d, 480d, "Details");			
			app.getScreen("Measure").getLayout().getViews(false, 320d, 480d, "Details.Measure");		
			app.getScreen("Embed").getLayout().getViews(false, 320d, 480d, "Embed");		
			app.getScreen("Animate").getLayout().getViews(false, 320d, 480d, "Details.Animate");
			app.getScreen("RecursionA").getLayout().getViews(false, 320d, 480d, "Details.RecursionA");
			app.getScreen("SeparateActivity").getLayout().getViews(false, 320d, 480d, "Details.SeparateActivity");
			app.getScreen("TableForm").getLayout().getViews(false, 320d, 480d, "Details.TableForm");

			Layout layout = app.getLayout("_TableLayout.TableFormLayout.what");
			layout.getViews(false, 320d, 480d, "TableLayout.what");
			
			app.getScreen("Measure").getLayout().getViews(false, 568d, 219d, "Details.Measure");		
			ViewBehaviorWebView v = (ViewBehaviorWebView) app.getScreen("Measure").getView("graph").getViewBehavior();
			
			ViewBehaviorTable beh = (ViewBehaviorTable) app.getScreen("List").getView("C").getViewBehavior();
			beh.getRowOrSectionAt(0);
			
		} catch (Exception e) {
			Logger.error(this, e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testDataChangeListener() {
		
		MockDataChangeListener listener = new MockDataChangeListener();

		MockDataChangeListener listener2 = new MockDataChangeListener();

		app.getDataModelManager().addDataChangeListener(null, "dt.time", "1", listener);
		app.getDataModelManager().addDataChangeListener(null, "dt.two", "1", listener2);
		
		app.getDataModelManager().dataDidChange("dt", "time");
		app.getDataModelManager().dataDidChange("dt", "time");
		app.getDataModelManager().dataDidChange("dt", "time");
		
		app.getDataModelManager().dataDidChange("app.measureManager", "measureProgress");
		
		app.getDataModelManager().getValue(null, "dt.time", null, null);
		
		assertTrue(listener.count == 3);
		assertTrue(listener2.count == 0);
		
		app.getDataModelManager().dataDidChange("dt", "two");

		assertTrue(listener.count == 3);
		assertTrue(listener2.count == 1);

		app.getDataModelManager().dataDidChange("dt.two");
		
		assertTrue(listener.count == 3);
		assertTrue(listener2.count == 2);
		
		app.getDataModelManager().dataDidChange("dt", "time", "two");
		
		assertTrue(listener.count == 4);
		assertTrue(listener2.count == 3);
		
		app.getDataModelManager().dataDidChange("dt", "not a key");
		
		assertTrue(listener.count == 4);
		assertTrue(listener2.count == 3);
		
		app.getDataModelManager().dataDidChange("dt");

		assertTrue(listener.count == 5);
		assertTrue(listener2.count == 4);
	}
	
	@Test
	public void testDataChangeListenerRoot() {
		
		MockDataChangeListener listener = new MockDataChangeListener();
		
		app.getDataModelManager().addDataChangeListener(null, "formName", "Root", listener);
		
		String value = app.getDataModelManager().getValue(null, "formName", "Hi, {0}", "?");
		assertEquals("?", value);

		app.getDataModelManager().setDataModel("formName", "Hans");
		
		app.getDataModelManager().dataDidChange("formName");
		app.getDataModelManager().dataDidChange("formName");
		
		assertTrue(listener.count == 2);
		
		value = app.getDataModelManager().getValue(null, "formName", "Hi, {0}", "?");
		assertEquals("Hi, Hans", value);
	}
	
	@Test
	public void testQueryJoin() {
		DatastoreTransaction txn = new DatastoreTransaction();
		QueryJoinBuilder<DSBook, DSLibrary> q = 
				txn.queryJoin(DSBook.class, DSLibrary.class)
				.select(DSBook.class, DSBook.name, DSBook.id)
				.select(DSLibrary.class, DSLibrary.name)
				.where("{} = {} and {} > ?")
				.param(DSBook.class, DSBook.libraryId, DSLibrary.class, DSLibrary.id)
				.param(DSBook.class, DSBook.id, 1);
		try {
			txn.start();
			SQLResultList<SQLQueryResultTuple<DSBook, DSLibrary>> results = q.execute();
			
			SQLQueryResultTuple<DSBook, DSLibrary> result = results.get(0);
			assertEquals(2, result.t1().getId().intValue());
			assertEquals("A bad story", result.t1().getName());
			assertEquals("Default Library", result.t2().getName());
			
			result = results.get(1);
			assertEquals(3, result.t1().getId().intValue());
			assertEquals("Hans's book", result.t1().getName());
			assertEquals("Default Library", result.t2().getName());
			
		} catch (DatastoreException e) {
			Logger.error(this, e);
		} finally {
			txn.rollback();
		}
	}
	
	@Test
	public void testQueryJoin3() {

		DSPhoto photo = new DSPhoto();
		photo.setData(new byte[] { 1 });
		
		DatastoreTransaction txn = new DatastoreTransaction();
		try {
			txn.start();
			txn.insert(photo);
			txn.commit();
		} catch (DatastoreException e) {
			Logger.error(this, e);
		} finally {
			txn.rollback();
		}
		
		txn = new DatastoreTransaction();
		QueryJoinBuilder3<DSBook, DSLibrary, DSPhoto> q = txn.queryJoin(
				DSBook.class, DSLibrary.class, DSPhoto.class).select(
				DSPhoto.class, DSPhoto.id, DSPhoto.data);
		try {
			txn.start();
			for (SQLQueryResultTuple3<DSBook, DSLibrary, DSPhoto> result : q
					.execute()) {
				assertNull(result.t1().getId());
				assertNull(result.t1().getName());
				assertNull(result.t2().getName());
				assertEquals(result.t3().getId().intValue(), 1);
				assertEquals(result.t3().getData()[0], 1);
			}
		} catch (DatastoreException e) {
			Logger.error(this, e);
		} finally {
			txn.rollback();
		}
	}
	
}
