package com.sponberg.app.ui;

import com.sponberg.app.datastore.Datastore;
import com.sponberg.app.datastore.app.DSBook;
import com.sponberg.app.ui.Screen.ScreenHome;
import com.sponberg.fluid.ApplicationLoader;
import com.sponberg.fluid.FluidApp;
import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.datastore.DatastoreException;
import com.sponberg.fluid.layout.ActionListenerAdapter;
import com.sponberg.fluid.util.Logger;

public class Home implements ApplicationLoader {

	@Override
	public void load(FluidApp app) {
		
		app.addActionListener(Screen.Home, ScreenHome.C).listener(new ActionListenerAdapter() {
			@Override
			public void userTapped(EventInfo info) {
				GlobalState.fluidApp.getUiService().pushLayout(Screen.Details);
			}
		});

		app.addActionListener(Screen.Home, ScreenHome.C).listener(new ActionListenerAdapter() {
			@Override
			public void userTapped(EventInfo info) {
				try {
					for (DSBook book : Datastore.getBookNamesWhereIdGreaterThan(1)) {
						Logger.debug(this, "Found book {}", book.getName());
					}
				} catch (DatastoreException e) {
					Logger.error(this, e);
				}
			}
		});
		
		app.addActionListener(Screen.Home, ScreenHome.D).listener(new ActionListenerAdapter() {
			@Override
			public void userTapped(EventInfo info) {				
				GlobalState.fluidApp.getUiService().showAlert("Clicky", "You clicked D. Good for you.");
			}
		});
		
		app.addActionListener(Screen.Home, ScreenHome.Rec).listener(new ActionListenerAdapter() {
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
