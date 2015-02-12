package com.sponberg.fluid.datastore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.datastore.DatastoreTransaction.QueryBuilder;
import com.sponberg.fluid.datastore.DatastoreTransaction.QueryJoinBuilder;
import com.sponberg.fluid.datastore.DatastoreTransaction.QueryJoinBuilder3;
import com.sponberg.fluid.test.ClasspathUtil;
import com.sponberg.fluid.test.JavaDatastoreService;
import com.sponberg.fluid.test.MockApp;
import com.sponberg.fluid.util.KVLReader;
import com.sponberg.fluid.util.Logger;

public class DatastoreTest {

	@Before
	public void setUp() throws Exception {

		Logger.setEnabled(true);
		Logger.setLoggingLevel(Logger.LEVEL_DEBUG);
		
		ClasspathUtil.loadSqliteJar();
		
		GlobalState.fluidApp = new MockApp();
		
		JavaDatastoreService service = new JavaDatastoreService();
		service.setInMemory(true);
		GlobalState.fluidApp.setDatastoreService(service);
		
		String settingsString = 
				"datastore:" + "\n" +
				"	settings" + "\n" +
				"		enabled:" + "\n" +
				"			true" + "\n" +
				"		database-name:" + "\n" +
				"			app.sqlite";
		KVLReader settings = new KVLReader(settingsString);
		GlobalState.fluidApp.setSettings(settings);
		
		GlobalState.fluidApp.setDatastoreManager(new MockDatastoreManager());
		
		String createLibrary = 
			"create table library (" +
				"id 						integer 	primary key autoincrement," +
				"name					text		not null," +
				"address				text		not null" +
			");";

		String createBook = 
			"create table book (" + 
				"id 						integer 	primary key autoincrement," +
				"library_id				integer		not null," +
				"name					text		not null," +
				"num_pages				integer		not null," +
				"price					real		not null" +
			");";
			
		String createPhoto =
			"create table photo (" + 
					"id 						integer 	primary key autoincrement," +
					"data					blob		not null" +
				");";
		
		DatastoreTransaction txn = new DatastoreTransaction();
		txn.start();
		txn.executeRawStatement(createLibrary);
		txn.executeRawStatement(createBook);
		txn.executeRawStatement(createPhoto);
		
		DSLibrary library = new DSLibrary();
		library.setName("Default Library");
		library.setAddress("12345 Some St");
		long libraryId = txn.insert(library);
		
		library = new DSLibrary();
		library.setName("Another Library");
		library.setAddress("999 Ave");
		txn.insert(library);
		
		DSPhoto photo = new DSPhoto();
		photo.setData(new byte[] { 1 });
		txn.insert(photo);
		
		for (int index = 0; index < 1000; index++) {
			DSBook book = new DSBook();
			book.setLibraryId((int) libraryId);
			book.setName("Story " + index);
			book.setNumPages(index);
			book.setPrice(index * 1.1);
			txn.insert(book);
		}
		
		txn.commit();
	}

	@Test
	public void testQueryJoin() throws Exception {
	
		DatastoreTransaction txn = new DatastoreTransaction();
		txn.start();
		
		QueryJoinBuilder<DSBook, DSLibrary> q = 
				txn.queryJoin(DSBook.class, DSLibrary.class)
					.select(DSBook.class, DSBook.name, DSBook.id)
					.select(DSLibrary.class, DSLibrary.name)
					.where("{} = {}")
					.param(DSBook.class, DSBook.libraryId, DSLibrary.class, DSLibrary.id);
		
		SQLResultList<SQLQueryResultTuple<DSBook, DSLibrary>> results = q.execute();
		
		assertEquals(1000, results.size());
		
		SQLQueryResultTuple<DSBook, DSLibrary> result = results.get(0);
		assertEquals(1, result.t1().getId().intValue());
		assertEquals("Story 0", result.t1().getName());
		assertEquals("Default Library", result.t2().getName());
		
		result = results.get(7);
		assertEquals("Story 7", result.t1().getName());
		assertEquals("Default Library", result.t2().getName());
		
		txn.rollback();
	}

	@Test
	public void testQueryJoinWithNonTableSelect() throws Exception {
	
		DatastoreTransaction txn = new DatastoreTransaction();
		txn.start();
		
		QueryJoinBuilder<DSBook, DSLibrary> q = 
				txn.queryJoin(DSBook.class, DSLibrary.class)
					.select(null, "count(*)")
					.where("{} = {}")
					.param(DSBook.class, DSBook.libraryId, DSLibrary.class, DSLibrary.id);
		
		SQLResultList<SQLQueryResultTuple<DSBook, DSLibrary>> results = q.execute();
		
		assertEquals(1, results.size());
		
		assertEquals(1000, results.get(0).getDefault().getInteger("count(*)").intValue());
		
		txn.rollback();
	}
	
	@Test
	public void testQueryJoin3() {

		DatastoreTransaction txn = new DatastoreTransaction();
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
	
	@Test
	public void testQuery() throws Exception {
		
		DatastoreTransaction txn = new DatastoreTransaction();
		txn.start();

		QueryBuilder<DSBook> q = 
				txn.query(DSBook.class)
				.select(DSBook.name)
				.where("{} > ?")
				.param(DSBook.id, 3);

		DSBook book = q.execute().get(45);
		
		assertEquals("Story 48", book.getName());
		
		txn.rollback();
	}
	
	@Test
	public void testQueryNonTable() throws Exception {
		
		DatastoreTransaction txn = new DatastoreTransaction();
		txn.start();

		SQLQueryResultDefault result = 
				txn.queryFunction("count(*)", DSBook.class)
				.where("{} > ?")
				.param(DSBook.id, 5)
				.execute()
				.get(0);
		
		assertEquals(995, result.getInteger("count(*)").intValue());
		
		txn.rollback();
	}
	
	@Test
	public void testLimitQuery() throws Exception {
		
		DatastoreTransaction txn = new DatastoreTransaction();
		txn.start();
		
		SQLResultList<DSBook> results =
				txn.query(DSBook.class)
				.select(DSBook.id)
				.limit(10)
				.offset(10)
				.execute();

		assertEquals(11, results.get(0).getId().intValue());
		
		assertEquals(10, results.size());
		
		for (int index = 0; index < 10; index++)
			results.next();
		
		assertEquals(0, results.size());
		
		int count = 0;
		while (results.hasNext()) {
			count++;
			results.next();
		}
		
		assertEquals(980, count);
		
		txn.rollback();
	}
	
}
