package com.sponberg.app;

import lombok.Getter;
import lombok.Setter;

import com.sponberg.app.datastore.UpgradeListenerPostcodes_01_01;
import com.sponberg.app.datastore.UpgradeListener_01_05;
import com.sponberg.app.form.SignupForm;
import com.sponberg.app.manager.DynamicTableDataManager;
import com.sponberg.app.manager.MeasureManager;
import com.sponberg.app.manager.WeatherManager;
import com.sponberg.app.ui.Details;
import com.sponberg.app.ui.Home;
import com.sponberg.app.ui.Measure;
import com.sponberg.app.ui.PickWhen;
import com.sponberg.app.ui.RecursionScreens;
import com.sponberg.app.ui.SearchWhere;
import com.sponberg.app.ui.SearchWhereAndroid;
import com.sponberg.app.ui.SeparateActivity;
import com.sponberg.app.ui.TableForm;
import com.sponberg.fluid.ApplicationLoader;
import com.sponberg.fluid.FluidApp;
import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.datastore.DatastoreVersion;
import com.sponberg.fluid.util.JsonUtil;

@Getter
@Setter
public class SampleApp extends FluidApp {

	MeasureManager measureManager = new MeasureManager();
	
	WeatherManager weatherManager = new WeatherManager();
	
	SignupForm quoteForm = new SignupForm();
	
	DynamicTableDataManager dynamicTableDataManager = new DynamicTableDataManager();
	
	public SampleApp() {
		super();
		
		JsonUtil.setUnderscoreSeparatesWords(false);
		
		addApplicationInitializers();
		
		addDatastoreUpgradeListeners();
	}

	public static SampleApp getApp() {
		return (SampleApp) GlobalState.fluidApp;
	}
	
	private void addApplicationInitializers() {
		
		addInitializer(new DataModelInitializer());
		
		addLoader(weatherManager);
		
		addUILoader();
		
		addLoader(new ApplicationLoader() {
			@Override
			public String[] getSupportedPlatforms() {
				return null;
			}
			@Override
			public void load(FluidApp app) {
				getUiService().removeSplashScreen("Home", false);
			}
		});
    }

	private void addUILoader() {
		addLoader(new Details());
		addLoader(new Home());
		addLoader(new Measure());
		addLoader(new RecursionScreens());
		addLoader(new SearchWhere(), new SearchWhereAndroid());
		addLoader(new SeparateActivity());
		addLoader(new TableForm());
		addLoader(new PickWhen());
	}
	
	private void addDatastoreUpgradeListeners() {
		this.getDatastoreManager().setUpgradeListener(new DatastoreVersion(1, 1), new UpgradeListenerPostcodes_01_01());
		this.getDatastoreManager().setUpgradeListener(new DatastoreVersion(1, 5), new UpgradeListener_01_05());
	}
	
	@Override
	protected void startApp() {

		if (getHttpService() == null) {
			throw new RuntimeException("Http Service not initialized");
		}
		
	}

	@Override
	protected void reStartApp() {
		
		getUiService().removeSplashScreen("Home", true);
	}
	
}
