package com.sponberg.app.ui;

import com.sponberg.app.SampleApp;
import com.sponberg.app.form.SignupForm;
import com.sponberg.app.form.WhenOption;
import com.sponberg.app.ui.Screen.ScreenPickWhen;
import com.sponberg.fluid.ApplicationLoader;
import com.sponberg.fluid.FluidApp;
import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.layout.ActionListenerAdapter;

public class PickWhen implements ApplicationLoader {

	@Override
	public void load(final FluidApp fApp) {

		fApp.addActionListener(Screen.PickWhen, ScreenPickWhen.Options)
			.listener(new ActionListenerAdapter() {
				@Override
				public void userTapped(EventInfo info) {
					Long whenId = (Long) info.getUserInfo();
					userPicked(whenId);
				}
			});
	}
	
	protected void userPicked(long whenId) {
		SampleApp app = (SampleApp) GlobalState.fluidApp;
		WhenOption when = SignupForm.whenOptions.getById(whenId);
		app.getQuoteForm().setWhen(when);
		app.getDataModelManager().dataDidChange("app.quoteForm.when");
		GlobalState.fluidApp.getUiService().popLayout();
	}

	@Override
	public String[] getSupportedPlatforms() {
		return null;
	}

}
