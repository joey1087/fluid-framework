package com.sponberg.fluid.test;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sponberg.fluid.layout.DataModelManager;

public class DataModelManagerTest {

	DataModelManager manager;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		manager = new DataModelManager();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void defaultTest() {
		
		MockDataChangeListener listener = new MockDataChangeListener();

		MockDataChangeListener listener2 = new MockDataChangeListener();

		manager.addDataChangeListener(null, "dt.time", "1", listener);
		manager.addDataChangeListener(null, "dt.two", "1", listener2);
		
		manager.dataDidChange("dt", "time");
		manager.dataDidChange("dt", "time");
		manager.dataDidChange("dt", "time");
		
		manager.dataDidChange("app.measureManager", "measureProgress");
		
		manager.getValue(null, "dt.time", null, null);
		
		assertEquals(3, listener.count);
		assertEquals(0, listener2.count);
		
		manager.dataDidChange("dt", "two");

		assertEquals(3, listener.count);
		assertEquals(1, listener2.count);

		manager.dataDidChange("dt.two");
		
		assertEquals(3, listener.count);
		assertEquals(2, listener2.count);
		
		manager.dataDidChange("dt", "time", "two");
		
		assertEquals(4, listener.count);
		assertEquals(3, listener2.count);
		
		manager.dataDidChange("dt", "not a key");
		
		assertEquals(4, listener.count);
		assertEquals(3, listener2.count);
		
		manager.dataDidChange("dt");

		assertEquals(5, listener.count);
		assertEquals(4, listener2.count);

	}

	@Test
	public void testEmbeded() {
		
		MockDataChangeListener listener = new MockDataChangeListener();

		MockDataChangeListener listener2 = new MockDataChangeListener();

		MockDataChangeListener listener3 = new MockDataChangeListener();

		MockDataChangeListener listener4 = new MockDataChangeListener();

		MockDataChangeListener listener5 = new MockDataChangeListener();

		manager.addDataChangeListener(null, "one", "id", listener);
		manager.addDataChangeListener(null, "one.two", "id", listener2);
		manager.addDataChangeListener(null, "one.two.three", "id", listener3);
		manager.addDataChangeListener(null, "one.two.three.four", "id", listener4);
		manager.addDataChangeListener(null, "one.two.three.four.five", "id", listener5);

		manager.dataDidChange("one");
		
		assertEquals(1, listener.count);
		assertEquals(1, listener2.count);
		assertEquals(1, listener3.count);
		assertEquals(1, listener4.count);
		assertEquals(1, listener5.count);
		
		manager.dataDidChange("one.two");
		
		assertEquals(1, listener.count);
		assertEquals(2, listener2.count);
		assertEquals(2, listener3.count);
		assertEquals(2, listener4.count);
		assertEquals(2, listener5.count);
		
		manager.dataDidChange("one", "two");
		
		assertEquals(1, listener.count);
		assertEquals(3, listener2.count);
		assertEquals(3, listener3.count);
		assertEquals(3, listener4.count);
		assertEquals(3, listener5.count);
		
		manager.dataDidChange("one.two.three");
		
		assertEquals(1, listener.count);
		assertEquals(3, listener2.count);
		assertEquals(4, listener3.count);
		assertEquals(4, listener4.count);
		assertEquals(4, listener5.count);
		
		manager.dataDidChange("one.two", "three");
		
		assertEquals(1, listener.count);
		assertEquals(3, listener2.count);
		assertEquals(5, listener3.count);
		assertEquals(5, listener4.count);
		assertEquals(5, listener5.count);
		
	}
	
}
