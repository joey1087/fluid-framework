package com.sponberg.fluid.parser;

import com.sponberg.fluid.ApplicationInitializer;
import com.sponberg.fluid.FluidApp;
import com.sponberg.fluid.layout.Tab;
import com.sponberg.fluid.util.KeyValueList;
import com.sponberg.fluid.util.StringUtil;

public class TabParser implements ApplicationInitializer {

	@Override
	public void initialize(FluidApp app) {

		for (KeyValueList tabProps : app.getSettings().get("tabs")) {

			Tab tab = new Tab();
			tab.setTabId(tabProps.getValue());
			
			String label = tabProps.contains("label") ? tabProps.getValue("label") : tab.getTabId();
			tab.setLabel(StringUtil.processEscapes(label));
			
			tab.setScreenId(tabProps.getValue("screen"));
			if (tabProps.contains("ios-icon")) {
				tab.setImage(tabProps.getValue("ios-icon"));
			}
			
			if (tabProps.contains("ios-selected-icon")) {
				tab.setSelectedImage(tabProps.getValue("ios-selected-icon"));
			}
			app.getViewManager().addTab(tab);
		}
	}

	@Override
	public String[] getSupportedPlatforms() {
		return null;
	}
}
