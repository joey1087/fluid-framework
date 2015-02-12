package com.sponberg.app.ui;

import com.sponberg.app.datastore.DS;
import com.sponberg.app.datastore.postcodes.DSPostcode;
import com.sponberg.app.datastore.postcodes.DSSuburb;
import com.sponberg.app.ui.Screen.ScreenSeparateActivity;
import com.sponberg.fluid.ApplicationLoader;
import com.sponberg.fluid.FluidApp;
import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.datastore.DatastoreTransaction;
import com.sponberg.fluid.datastore.SQLResultList;
import com.sponberg.fluid.layout.ActionListenerAdapter;
import com.sponberg.fluid.util.Logger;

public class SeparateActivity implements ApplicationLoader {

	String inputName = "";
	
	@Override
	public void load(final FluidApp app) {
		
		app.addActionListener(Screen.SeparateActivity, ScreenSeparateActivity.Close)
			.listener(new ActionListenerAdapter() {
				@Override
				public void userTapped(EventInfo info) {
					GlobalState.fluidApp.getUiService().closeCurrentLayout();
				}
			});
		
		app.addActionListener(Screen.SeparateActivity, ScreenSeparateActivity.InputName)
			.listener(new ActionListenerAdapter() {
				@Override
				public void userChangedValueTo(EventInfo info, Object value) {
					Logger.debug(this, "Value changed to {}", value);
					
					inputName = value.toString();
					
					if (value.equals("")) {
						return;
					}
					
					// hstdbc remove suburb search
					
					DatastoreTransaction txn = new DatastoreTransaction(DS.postcodes);
					txn.start();
					
					long millis = System.currentTimeMillis();
					SQLResultList<DSPostcode> results = 
						txn.query(DSPostcode.class)
						.select(DSPostcode.id, DSPostcode.title)
						.where("{} like ?")
						.param(DSSuburb.title, "%" + value + "%")
						.execute();
	
					long dur = System.currentTimeMillis() - millis;
					
					Logger.debug(this, "Searching for postcodes, duration is " + dur + " found " + results.size());
					
					if (results.size() < 20) {
						for (DSPostcode postcode : results) {
							Logger.debug(this, postcode.getTitle());
						}
					}
					
					txn.rollback();
				}
			});
		
		app.addActionListener(Screen.SeparateActivity, ScreenSeparateActivity.Submit)
			.listener(new ActionListenerAdapter() {
				@Override
				public void userTapped(EventInfo info) {
									
					app.getDataModelManager().setDataModel("formName", inputName);
					app.getDataModelManager().dataDidChange("formName");
					
					Logger.debug(this, "input value {}", inputName);
				}
			});
	}

	@Override
	public String[] getSupportedPlatforms() {
		return null;
	}

}
