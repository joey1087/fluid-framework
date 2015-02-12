package com.sponberg.app.ui;

import com.sponberg.app.SampleApp;
import com.sponberg.app.datastore.app.DSUser;
import com.sponberg.app.ui.Screen.ScreenTableForm;
import com.sponberg.app.ui.Screen.TableLayoutSignupForm;
import com.sponberg.fluid.ApplicationLoader;
import com.sponberg.fluid.FluidApp;
import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.datastore.DatastoreTransaction;
import com.sponberg.fluid.datastore.SQLResultList;
import com.sponberg.fluid.layout.ActionListenerAdapter;
import com.sponberg.fluid.layout.ScreenListener;

public class TableForm implements ApplicationLoader, ScreenListener {

	@Override
	public void load(final FluidApp fluidApp) {
		
		final SampleApp app = (SampleApp) fluidApp;
		
		app.addScreenListener(Screen.TableForm, this);
		
		app.addActionListener(Screen.TableForm, 
								ScreenTableForm.SignupForm, 
								TableLayoutSignupForm.Where)
			.listener(new ActionListenerAdapter() {
				@Override
				public void userTapped(EventInfo info) {
					GlobalState.fluidApp.getUiService().pushLayout(Screen.SearchWhere);
				}
			});
		
		app.addActionListener(Screen.TableForm, 
								ScreenTableForm.SignupForm, 
								TableLayoutSignupForm.When)
			.listener(new ActionListenerAdapter() {
				@Override
				public void userTapped(EventInfo info) {
					GlobalState.fluidApp.getUiService().pushLayout(Screen.PickWhen);
				}
			});
		
		/*
		 * This is set automatically by textfield via datamodelmanager
		app.getViewManager().getLayout("_TableLayout.TableFormLayout.name")
		.getViewMap().get("input").addActionListener(
				new ActionListenerAdapter() {
					@Override
					public void userChangedValueTo(Object value) {
						app.getQuoteForm().getUser().setName(value.toString());
					}
				});*/
		
		app.addActionListener(Screen.TableForm, 
								ScreenTableForm.SignupForm, 
								TableLayoutSignupForm.Submit, 
								TableLayoutSignupForm.RowSubmit.Submit)
			.listener(new ActionListenerAdapter() {
				@Override
				public void userTapped(EventInfo info) {
					System.out.println("User tapped");
				}
			});		
	}

	@Override
	public void screenWillAppear() {
		
		DatastoreTransaction txn = new DatastoreTransaction();
		txn.start();
		SQLResultList<DSUser> result = txn.query(DSUser.class)
			.select(DSUser.email, DSUser.phone, DSUser.name)
			.limit(1).execute();
		if (result.size() > 0) {
			SampleApp.getApp().getQuoteForm().setUser(result.next());
		} else {
			txn.insert(new DSUser());
		}
		txn.commit();
	}

	@Override
	public void screenDidDisappear() {
		
		DatastoreTransaction txn = new DatastoreTransaction();
		txn.start();
		txn.update(SampleApp.getApp().getQuoteForm().getUser()).execute();
		txn.commit();
	}			

	@Override
	public void screenDidAppear() {
	}			

	@Override
	public void screenWasRemoved() {
	}		
	
	@Override
	public String[] getSupportedPlatforms() {
		return null;
	}

}
