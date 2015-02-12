package com.sponberg.app;

import java.util.Date;

import com.sponberg.fluid.ApplicationInitializer;
import com.sponberg.fluid.FluidApp;
import com.sponberg.fluid.GlobalState;

public class DataModelInitializer implements ApplicationInitializer {

	@Override
	public void initialize(final FluidApp app) {
		
		app.setDataModel("app", app);

		app.setDataModel("dt", new DateTime());
		
		new Thread() {
			public void run() {
				try {
					while (true) {
						Thread.sleep(1000);
						GlobalState.fluidApp.getSystemService().runOnUiThread(new Runnable() {
							public void run() {
								app.getDataModelManager().dataDidChange("dt", "time");								
							}
						});
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
		
	}

	public static class DateTime {
		public Date getTime() {
			return new Date();
		}
	}

	@Override
	public String[] getSupportedPlatforms() {
		return null;
	}
}
