package com.sponberg.app.ui;

import com.sponberg.app.ui.Screen.ScreenRecursionA;
import com.sponberg.app.ui.Screen.ScreenRecursionB;
import com.sponberg.fluid.ApplicationLoader;
import com.sponberg.fluid.FluidApp;
import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.layout.ActionListenerAdapter;

public class RecursionScreens implements ApplicationLoader {

	@Override
	public void load(FluidApp app) {
		app.addActionListener(Screen.RecursionA, ScreenRecursionA.C).
			listener(new ActionListenerAdapter() {
				@Override
				public void userTapped(EventInfo info) {				
					GlobalState.fluidApp.getUiService().pushLayout(Screen.RecursionB);
				}
			});

		app.addActionListener(Screen.RecursionB, ScreenRecursionB.C)
			.listener(new ActionListenerAdapter() {
				@Override
				public void userTapped(EventInfo info) {				
					GlobalState.fluidApp.getUiService().pushLayout(Screen.RecursionA);
				}
			});
	}

	@Override
	public String[] getSupportedPlatforms() {
		return null;
	}

}
