package com.sponberg.fluid.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sponberg.fluid.EventsManager;

public class EventsManagerTest {

	EventsManager eventsManager;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		eventsManager = new EventsManager();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddEventListener() {
		
		MockActionListener listener = new MockActionListener();
		MockActionListener listener2 = new MockActionListener();
		MockActionListener listener3 = new MockActionListener();
		MockActionListener listener4 = new MockActionListener();
		
		eventsManager.addEventListener(listener, new String[] { "root" });
		eventsManager.addEventListener(listener2, new String[] { "root", "next" });
		eventsManager.addEventListener(listener3, new String[] { "root", "next", "a" });
		eventsManager.addEventListener(listener4, new String[] { "root", "next", "b" });
		
		eventsManager.userTapped("root", null);
		eventsManager.userTapped("root", null);

		assertEquals(2, listener.userTapped);
		assertEquals(0, listener2.userTapped);
		assertEquals(0, listener3.userTapped);
		assertEquals(0, listener4.userTapped);
		
		eventsManager.userTapped("root.next", null);
		eventsManager.userTapped("root.next.a", null);
		
		assertEquals(2, listener.userTapped);
		assertEquals(1, listener2.userTapped);
		assertEquals(1, listener3.userTapped);
		assertEquals(0, listener4.userTapped);
		
		eventsManager.userTapped("root.next.a", null);
		eventsManager.userTapped("root.next.a", null);

		assertEquals(2, listener.userTapped);
		assertEquals(1, listener2.userTapped);
		assertEquals(3, listener3.userTapped);
		assertEquals(0, listener4.userTapped);
		
		eventsManager.userTapped("root.next.b", null);
		
		assertEquals(2, listener.userTapped);
		assertEquals(1, listener2.userTapped);
		assertEquals(3, listener3.userTapped);
		assertEquals(1, listener4.userTapped);

	}

}
