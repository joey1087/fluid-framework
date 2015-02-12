package com.sponberg.app.manager;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sponberg.app.MockApp;
import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.layout.DataModelManager;

public class MeasureManagerTest {

	MeasureManager measureManager;
	
	@Before
	public void setUp() throws Exception {
		GlobalState.fluidApp = new MockApp();
		GlobalState.fluidApp.setDataModelManager(new DataModelManager());
		measureManager = new MeasureManager();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetMeasureProgress() throws InterruptedException {
		measureManager.durationInSeconds = 2;
		measureManager.initialDelayMillis = 100;
		assertEquals(new Integer(0), measureManager.getMeasureProgress());
		int durationInSeconds = measureManager.getDurationInSeconds();
		measureManager.startMeasurement();
		Thread.sleep(measureManager.getInitialDelayMillis() + durationInSeconds * 1000 + 100);
		assertEquals(new Integer(100), measureManager.getMeasureProgress());
	}

}
