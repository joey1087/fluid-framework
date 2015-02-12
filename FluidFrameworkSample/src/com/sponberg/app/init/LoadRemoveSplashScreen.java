package com.sponberg.app.init;

import com.sponberg.fluid.ApplicationLoader;
import com.sponberg.fluid.FluidApp;
import com.sponberg.fluid.GlobalState;


public class LoadRemoveSplashScreen implements ApplicationLoader {

	@Override
	public void load(FluidApp app) {
		GlobalState.fluidApp.getUiService().removeSplashScreen("Home", false);
	}

	@Override
	public String[] getSupportedPlatforms() {
		return null;
	}

}
