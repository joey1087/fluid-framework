package com.sponberg.app.ui;

import com.sponberg.app.SampleApp;
import com.sponberg.app.ui.Screen.ScreenSearchWhere;
import com.sponberg.fluid.FluidApp;
import com.sponberg.fluid.Platforms;
import com.sponberg.fluid.layout.ActionListenerAdapter;
import com.sponberg.fluid.layout.MenuButtonItem;

public class SearchWhereAndroid extends SearchWhere {

	@Override
	public void load(final FluidApp fApp) {

		this.app = (SampleApp) fApp;
		
		MenuButtonItem item = new MenuButtonItem(MenuButtonItem.SystemItemSearch, "Search", null, MenuButtonItem.ActionFlavorSearch);
		item.setProperty("queryHint", "2000 - Sydney");
		item.setProperty("textColor", "255,255,255");
		item.addActionListener(new ActionListenerAdapter() {
			@Override
			public void userChangedValueTo(EventInfo info, Object value) {
				userSearchedFor(value);
			}
		});
		app.getScreen(Screen.SearchWhere).getNavigationMenuItems().add(item);
		
		/*
		app.addActionListener("SearchWhere", "search").listener(new ActionListenerAdapter() {
			@Override
			public void userChangedValueTo(Object value) {
				userSearchedFor(value);
			}
			@Override
			public void userCancelled() {
				userCanceledSearch();
			}
		});*/
		
		app.addActionListener(Screen.SearchWhere, ScreenSearchWhere.Results)
			.listener(new ActionListenerAdapter() {
				@Override
				public void userTapped(EventInfo info) {
					userTappedSearchResult(info.getUserInfo());
				}
			});
	}

	@Override
	public String[] getSupportedPlatforms() {
		return new String[] { Platforms.Android };
	}

}
