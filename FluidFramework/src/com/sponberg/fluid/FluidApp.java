package com.sponberg.fluid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import com.sponberg.fluid.HttpServiceWrapper.MapMode;
import com.sponberg.fluid.SecurityService.PasswordProvider;
import com.sponberg.fluid.datastore.DatastoreManager;
import com.sponberg.fluid.datastore.DatastoreService;
import com.sponberg.fluid.initializer.LoggingInitializer;
import com.sponberg.fluid.layout.ActionListener;
import com.sponberg.fluid.layout.DataModelManager;
import com.sponberg.fluid.layout.FluidViewFactory;
import com.sponberg.fluid.layout.ImageManager;
import com.sponberg.fluid.layout.Layout;
import com.sponberg.fluid.layout.PrecomputeLayoutManager;
import com.sponberg.fluid.layout.Screen;
import com.sponberg.fluid.layout.ScreenListener;
import com.sponberg.fluid.layout.Tab;
import com.sponberg.fluid.layout.UIService;
import com.sponberg.fluid.layout.ViewManager;
import com.sponberg.fluid.layout.WebviewActionListener;
import com.sponberg.fluid.manager.LaunchOptionsManager;
import com.sponberg.fluid.manager.PushNotificationManager;
import com.sponberg.fluid.parser.SettingsParser;
import com.sponberg.fluid.parser.TabParser;
import com.sponberg.fluid.parser.ViewBehaviorFactory;
import com.sponberg.fluid.parser.ViewsParser;
import com.sponberg.fluid.sdk.ExternalSDK;
import com.sponberg.fluid.util.KVLReader;
import com.sponberg.fluid.util.KeyValueList;
import com.sponberg.fluid.util.Logger;
import com.sponberg.fluid.util.Logger.SimpleLoggingService;

@Getter
@Setter
public abstract class FluidApp {

	private HttpService httpService;

	private UIService uiService;

	private ResourceService resourceService;

	private LoggingService loggingService;

	private DatastoreService datastoreService;

	private SystemService systemService;

	private SecurityService securityService;

	private ViewManager viewManager = new ViewManager();

	private ImageManager imageManager = new ImageManager();

	private DataModelManager dataModelManager = new DataModelManager();

	private DatastoreManager datastoreManager = new DatastoreManager();

	private EventsManager eventsManager = new EventsManager();

	private WebviewEventsManager webviewEventsManager = new WebviewEventsManager();

	private PrecomputeLayoutManager precomputeLayoutManager = new PrecomputeLayoutManager();

	private PushNotificationManager pushNotificationManager = new PushNotificationManager();

	private LaunchOptionsManager launchOptionsManager = new LaunchOptionsManager();

	private ArrayList<ApplicationInitializer> initializers = new ArrayList<>();

	private ArrayList<ApplicationLoader> loaders = new ArrayList<>();

	private ArrayList<ApplicationReloader> reloaders = new ArrayList<>();

	private HashMap<String, KeyValueList> defaultsByCategory = new HashMap<>();

	private HashMap<String, ExternalSDK> externalSdks = new HashMap<>();

	private FluidViewFactory fluidViewFactory = new FluidViewFactory();

	private ViewBehaviorFactory viewBehaviorFactory = new ViewBehaviorFactory();

	private KVLReader settings;

	private String platform = null;

	private PasswordProvider passwordProvider;

	private boolean initialized = false;

	private boolean loaded = false;

	private boolean started = false;

	public static final boolean useCaching = true;

	private String settingsOverride = null;

	public FluidApp() {

		GlobalState.fluidApp = this;

		setLoggingService(new SimpleLoggingService());

		addInitializer(new SettingsParser());
		addInitializer(new LoggingInitializer());
		addInitializer(new ViewsParser());
		addInitializer(new TabParser());
		addInitializer(viewManager);
		addInitializer(imageManager);
		addInitializer(precomputeLayoutManager);

		addLoader(datastoreManager);
	}

	public void setHttpService(HttpService service) {
		this.httpService = new HttpServiceWrapper(service);
	}

	public void setHttpServiceRequestParametersMapMode(MapMode mapMode) {
		((HttpServiceWrapper) httpService).setMapMode(mapMode);
	}

	public void addInitializer(ApplicationInitializer i) {
		initializers.add(i);
	}

	public void removeInitializer(ApplicationInitializer i) {
		initializers.remove(i);
	}
	
	public void addInitializer(ApplicationInitializer... list) {
		for (ApplicationInitializer i : list) {
			initializers.add(i);
		}
	}

	public void setExternalSDK(String id, ExternalSDK sdk) {
		this.externalSdks.put(id, sdk);
	}

	public ExternalSDK getExternalSDK(String id) {
		return this.externalSdks.get(id);
	}

	public final synchronized void initialize() {

		if (initialized) {
			return;
		}

		initializeHelper();
		initializers.clear();

		// Don't need this anymore
		viewBehaviorFactory = null;

		initialized = true;

		String mode = getSetting("mode");
		if (mode != null && !mode.equalsIgnoreCase("release")) {
			Logger.info(this, "Using mode: {}", mode);
		} else {
			Logger.info(this, "Using mode: release");
		}
	}

	public final synchronized void loadAsync() {
		loadAsync(null);
	}

	public final synchronized void loadAsync(final Callback callback) {

		if (loaded) {
			if (callback != null) {
				callback.run(null);
			}
			return;
		}

		new Thread() {
			@Override
			public void run() {
				Runnable r = new Runnable() {
					@Override
					public void run() {
						load(callback);
					}
				};
				getSystemService().runOnUiThread(r);
			}
		}.start();
	}

	public final synchronized void reloadAsync() {
		new Thread() {
			@Override
			public void run() {
				Runnable r = new Runnable() {
					@Override
					public void run() {
						reload(null);
					}
				};
				getSystemService().runOnUiThread(r);
			}
		}.start();
	}

	public void addLoader(ApplicationLoader l) {
		loaders.add(l);
	}

	protected void addLoader(ApplicationLoader... list) {
		for (ApplicationLoader l : list) {
			loaders.add(l);
		}
	}

	public void addReloader(ApplicationReloader l) {
		reloaders.add(l);
	}

	protected void addReloader(ApplicationReloader... list) {
		for (ApplicationReloader l : list) {
			reloaders.add(l);
		}
	}

	private final void load(Callback callback) {

		loadHelper();
		loaders.clear();

		loaded = true;

		if (callback != null) {
			callback.run(null);
		}
	}

	private final void reload(Callback callback) {

		reloadHelper();

		if (callback != null) {
			callback.run(null);
		}
	}

	public final void start() {

		if (!initialized) {
			throw new RuntimeException("Not initialized");
		}

		if (!loaded) {
			throw new RuntimeException("Not loaded");
		}

		Logger.info(this, "Starting Fluid App");
		startApp();
	}

	public final void restart() {

		Logger.info(this, "Re-starting Fluid App");
		reStartApp();
	}

	protected void initializeHelper() {

		if (platform == null) {
			throw new RuntimeException("platform not set");
		}

		if (getUiService() == null) {
			throw new RuntimeException("UI Service not initialized");
		}

		if (getResourceService() == null) {
			throw new RuntimeException("Resource Service not initialized");
		}

		if (getSystemService() == null) {
			throw new RuntimeException("System Service not initialized");
		}

		for (ApplicationInitializer i : initializers) {
			if (i.getSupportedPlatforms() != null && !isSupportedOnPlatform(i)) {
				continue;
			}
			Logger.debug(this, "Running ApplicationInitializer {}", i.getClass().getName());
			i.initialize(this);
		}
	}

	protected void loadHelper() {

		for (ApplicationLoader i : loaders) {
			if (i.getSupportedPlatforms() != null && !isSupportedOnPlatform(i)) {
				continue;
			}
			Logger.debug(this, "Running ApplicationLoader {}", i.getClass().getName());
			i.load(this);
		}
	}

	protected void reloadHelper() {

		for (ApplicationReloader i : reloaders) {
			if (i.getSupportedPlatforms() != null && !isSupportedOnPlatform(i)) {
				continue;
			}
			Logger.debug(this, "Running Re-ApplicationLoader {}", i.getClass().getName());
			i.reload(this);
		}
	}

	protected boolean isSupportedOnPlatform(PlatformSpecifier i) {
		for (String p : i.getSupportedPlatforms()) {
			if (p.equals(platform)) {
				return true;
			}
		}
		return false;
	}

	public void setDefaults(List<? extends KeyValueList> properties) {
		this.defaultsByCategory = new HashMap<>();
		if (properties != null) {
			for (KeyValueList kvl : properties) {
				defaultsByCategory.put(kvl.getValue(), kvl);
			}
		}
	}

	public String getDefault(String category, String key) {
		KeyValueList kvl = this.defaultsByCategory.get(category);
		if (kvl != null && kvl.contains(key)) {
			return kvl.getValue(key);
		} else {
			return null;
		}
	}

	public String getSetting(String... keys) {
		return getSettingsHelper(this.settings, keys, 0);
	}

	protected String getSettingsHelper(KeyValueList list, String[] keys, int index) {
		if (index == keys.length - 1) {
			if (list == null || !list.contains(keys[index])) {
				return null;
			} else {
				return list.getValue(keys[index]);
			}
		} else {
			KeyValueList kvl = list.getWithValue(keys[index], keys[index + 1]);
			return getSettingsHelper(kvl, keys, index + 2);
		}
	}

	protected abstract void startApp();

	protected abstract void reStartApp();

	public Screen getScreen(String screenId) {
		return viewManager.getScreen(screenId);
	}

	public List<Tab> getTabs() {
		return viewManager.getTabs();
	}

	public Layout getLayout(String layoutId) {
		return viewManager.getLayout(layoutId);
	}

	public void setBaseUnit(double unit) {
		viewManager.setBaseUnit(unit);
	}

	public void setDevicePixelMultiplier(double devicePixelMultiplier) {
		viewManager.setDevicePixelMultiplier(devicePixelMultiplier);
	}

	public void setDevicePixelActualMultiplier(double devicePixelActualMultiplier) {
		viewManager.setDevicePixelActualMultiplier(devicePixelActualMultiplier);
	}

	public double sizeToPixels(String size) {
		return viewManager.sizeToPixels(size);
	}

	public double unitsToPixels(double units) {
		return viewManager.unitsToPixels(units);
	}

	public double pixelsToPixels(int pixels) {
		return viewManager.pixelsToPixels(pixels);
	}

	public double fontPointsToPixels(double points) {
		return viewManager.fontPointsToPixels(points);
	}

	public double unitsToFontPoints(double units) {
		return viewManager.unitsToFontPoints(units);
	}

	public double pixelsToUnits(double pixels) {
		return viewManager.pixelsToUnits(pixels);
	}

	public void setDataModel(String key, Object dataModel) {
		dataModelManager.setDataModel(key, dataModel);
	}

	public AddActionListenerBuilder addActionListener(String... keyPath) {
		AddActionListenerBuilder builder = new AddActionListenerBuilder();
		builder.keyPath = keyPath;
		return builder;
	}

	public AddWebviewActionListenerBuilder addWebviewActionListener(String... keyPath) {
		AddWebviewActionListenerBuilder builder = new AddWebviewActionListenerBuilder();
		builder.keyPath = keyPath;
		return builder;
	}

	public void addScreenListener(String screenId, ScreenListener listener) {
		 getScreen(screenId).addScreenListener(listener);
	}

	public class AddActionListenerBuilder {

		String[] keyPath;

		public AddActionListenerBuilder listener(ActionListener listener) {
			eventsManager.addEventListener(listener, keyPath);
			return this;
		}

	}

	public class AddWebviewActionListenerBuilder {

		String[] keyPath;

		public AddWebviewActionListenerBuilder listener(WebviewActionListener listener) {
			webviewEventsManager.addEventListener(listener, keyPath);
			return this;
		}

	}
	
	public boolean isRecoverFromExceptions() {

		String setting = getSetting("recover-from-exceptions");
		if (settings == null) {
			return false;
		} else {
			return setting.equalsIgnoreCase("true");
		}
	}

}
