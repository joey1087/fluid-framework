package com.sponberg.fluid.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sponberg.fluid.util.LRUCacheTree;

public class LRUCacheTreeTest {

	LRUCacheTree<String> cache;

	int limit = 5;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		cache = new LRUCacheTree<>(limit);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void defaultTest() {
		
		String[] keyChain;
		
		keyChain = new String[] { "one" };
		cache.put(keyChain, "a");
		
		keyChain = new String[] { "one", "two" };
		cache.put(keyChain, "b");
		
		keyChain = new String[] { "one", "two", "three" };
		cache.put(keyChain, "c");
		
		keyChain = new String[] { "one", "two", "three.2" };
		cache.put(keyChain, "d");
		
		keyChain = new String[] { "one", "two", "three.3" };
		cache.put(keyChain, "e");
		
		keyChain = new String[] { "one", "two", "three", "four" };
		cache.put(keyChain, "f");
		
		keyChain = new String[] { "one", "two.2" };
		cache.put(keyChain, "g");
		
		keyChain = new String[] { "one" };
		assertEquals("a", cache.get(keyChain));
		
		keyChain = new String[] { "one", "two" };
		assertEquals("b", cache.get(keyChain));
		
		keyChain = new String[] { "one", "two", "three" };
		assertEquals("c", cache.get(keyChain));

		keyChain = new String[] { "one", "two", "three.2" };
		assertEquals("d", cache.get(keyChain));
		
		keyChain = new String[] { "one", "two", "three.3" };
		assertEquals("e", cache.get(keyChain));
		
		keyChain = new String[] { "one", "two", "three", "four" };
		assertEquals("f", cache.get(keyChain));
		
		keyChain = new String[] { "one", "two", "three.2" };
		cache.remove(keyChain);
		assertNull(cache.get(keyChain));

		keyChain = new String[] { "one", "two", "three.3" };
		assertEquals("e", cache.get(keyChain));
		
		keyChain = new String[] { "one", "two" };
		cache.remove(keyChain);
		assertNull(cache.get(keyChain));

		keyChain = new String[] { "one", "two", "three" };
		assertNull(cache.get(keyChain));
		
		keyChain = new String[] { "one", "two", "three.3" };
		assertNull(cache.get(keyChain));
		
		keyChain = new String[] { "one", "two", "three", "four" };
		assertNull(cache.get(keyChain));

		keyChain = new String[] { "one" };
		assertEquals("a", cache.get(keyChain));
		
		keyChain = new String[] { "one", "two.2" };
		assertEquals("g", cache.get(keyChain));
	}

	@Test
	public void testLimit() {
		
		for (int index = 0; index <= limit; index++) {
			String[] keyChain = new String[1];
			keyChain[0] = "" + index;
			cache.put(keyChain, "" + index);
		}

		assertNull(cache.get(new String[] { "0" }));
		for (int index = 1; index <= limit; index++) {
			String[] keyChain = new String[1];
			keyChain[0] = "" + index;
			assertEquals("" + index, cache.get(keyChain));
		}
		
	}
	
}
