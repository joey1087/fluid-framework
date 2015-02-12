package com.sponberg.fluid.test;

import com.sponberg.fluid.FluidApp;

public class MockApp extends FluidApp {

	public MockApp() {
		setPlatform("mock");
		setHttpService(new MockRealHttpService());
	}

	@Override
	protected void startApp() {
	}

	@Override
	protected void reStartApp() {
	}

}
