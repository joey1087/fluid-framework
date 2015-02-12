package com.sponberg.app.ui;

import com.sponberg.app.SampleApp;
import com.sponberg.app.ui.Screen.ScreenMeasure;
import com.sponberg.fluid.ApplicationLoader;
import com.sponberg.fluid.FluidApp;
import com.sponberg.fluid.layout.ActionListenerAdapter;
import com.sponberg.fluid.layout.WebviewActionListener;

public class Measure implements ApplicationLoader {

	@Override
	public void load(final FluidApp app) {
		app.addActionListener(Screen.Measure, ScreenMeasure.Start).
			listener(new ActionListenerAdapter() {
				@Override
				public void userTapped(EventInfo info) {
					((SampleApp) app).getMeasureManager().startMeasurement();
				}
			});
		
		app.addWebviewActionListener(Screen.Measure, ScreenMeasure.Graph, "startMeasurement").
			listener(new WebviewActionListener() {
				@Override
				public void actionPerformed(String userInfo) {
					((SampleApp) app).getMeasureManager().startMeasurement();
				}
			});
	}

	@Override
	public String[] getSupportedPlatforms() {
		return null;
	}

}
