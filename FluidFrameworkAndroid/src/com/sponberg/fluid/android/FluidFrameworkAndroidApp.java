package com.sponberg.fluid.android;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.sponberg.fluid.Callback;
import com.sponberg.fluid.FluidApp;
import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.Platforms;
import com.sponberg.fluid.android.activity.FluidActivity;
import com.sponberg.fluid.android.activity.LauncherActivity;
import com.sponberg.fluid.android.layout.FluidViewFactoryRegistration;
import com.sponberg.fluid.layout.ModalView;

public abstract class FluidFrameworkAndroidApp extends Application {

	static FluidFrameworkAndroidApp fluidAndroidApp;

	private Activity launcherActivity; // Only allow one launcher activity

	private FluidActivity currentRootActivity; // Only allow one current activity

	private FluidActivity currentActivity; // Only allow one activity over the current activity

	private Activity tempCurrentActivity;

	private Context tempCurrentContext;

	private String screenId; // hstdbc refactor activity code to this class

	private boolean started = false;

	private boolean isInForeground = false;

	private boolean initializedAndLoaded = false;

	private boolean initializeAndLoadInvoked = false;

	private boolean startedLaunchIntentFromActivityRecreateBug = false;

	Class<? extends FluidActivity> fluidActivityClass;

	ArrayList<Callback> initializeAndLoadCallbacks = new ArrayList<>();
	
	public String getCurrentScreenId() {
		String id = null;
		
		//TODO: replace this code block with getCurrentActivity()
		if (currentActivity != null) {

			id = currentActivity.getCurrentScreenId();
		} else if(currentRootActivity != null) {

			id = currentRootActivity.getCurrentScreenId();
		}
		
		return id;
	}
	
	public static FluidFrameworkAndroidApp getFluidAndroidApp() {
		return fluidAndroidApp;
	}

	public FluidFrameworkAndroidApp(FluidApp fluidApp) {

		FluidFrameworkAndroidApp.fluidAndroidApp = this;
		fluidApp.setPlatform(Platforms.Android);
		fluidApp.setSystemService(new DefaultSystemService(this));
		fluidApp.setLoggingService(new DefaultLoggingService());
	}

	@Override
	public void onCreate() {
        super.onCreate();

		setupApp(GlobalState.fluidApp);
    }

	protected void registerFluidViews() {
		FluidViewFactoryRegistration.registerViews(GlobalState.fluidApp);
	}

	public void setupApp(FluidApp fluidApp) {

		registerFluidViews();

		fluidApp.setHttpService(new DefaultHttpService());

		fluidApp.setUiService(new DefaultUIService(this));

		fluidApp.setResourceService(new DefaultResourceService(this));

		fluidApp.setSecurityService(new DefaultSecurityService(this, fluidApp.getPasswordProvider()));

		fluidApp.setDatastoreService(new DefaultDatastoreService(getApplicationContext()));

		fluidApp.setBaseUnit(computeBaseUnit());

		fluidApp.setDevicePixelMultiplier(computeDevicePixelMultiplier());

	}

	/**
	 * @param callback
	 * @return true if the app isn't initialized and loaded yet
	 */
	public boolean initializeAndLoad(final Callback callback) {

		synchronized (initializeAndLoadCallbacks) {

			if (initializedAndLoaded) {
				if (callback != null) {
					callback.run(null);
				}
				return false;
			}

			if (callback != null) {
				initializeAndLoadCallbacks.add(callback);
			}

			if (!initializeAndLoadInvoked) {
				initializeAndLoadHelper();
				initializeAndLoadInvoked = true;
			}

			return true;
		}
	}

	private void initializeAndLoadHelper() {

		GlobalState.fluidApp.initialize();

		String activityClassName = GlobalState.fluidApp.getSetting("android-activity-class");
		if (activityClassName == null) {
			fluidActivityClass = FluidActivity.class;
		} else {
			try {
				fluidActivityClass = (Class<? extends FluidActivity>) Class.forName(activityClassName);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}

		Callback initializedAndLoaded = new Callback() {
			@Override
			public void run(String msg) {

				initializedAndLoaded();
			}
		};
		GlobalState.fluidApp.loadAsync(initializedAndLoaded);
	}

	public void reload() {
		GlobalState.fluidApp.reloadAsync();
	}

	protected void initializedAndLoaded() {

		synchronized (initializeAndLoadCallbacks) {

			initializedAndLoaded = true;

			for (Callback callback : initializeAndLoadCallbacks) {
				callback.run(null);
			}
			initializeAndLoadCallbacks.clear();
		}
	}

	double computeBaseUnit() {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int dpi = metrics.densityDpi;
		double dpmm = dpi / 25.4;
	    double baseUnit = dpmm;
	    return baseUnit;
	}

	double computeDevicePixelMultiplier() {
		Resources r = getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, r.getDisplayMetrics());
		return px;
	}

	public Activity getCurrentActivity() {
		if (tempCurrentActivity != null) {
			return tempCurrentActivity;
		} else if (launcherActivity != null) {
			return launcherActivity;
		} else if (currentActivity != null) {
			return currentActivity;
		} else {
			return currentRootActivity;
		}
	}

	public Context getCurrentActivityContext() {
		if (tempCurrentContext != null) {
			return tempCurrentContext;
		} else {
			Context context = getCurrentActivity();
			if (context == null) {
				return this;
			} else {
				return context;
			}
		}
	}

	public Activity getCurrentNonRootActivity() {
		return currentActivity;
	}

	public Activity getCurrentRootActivity() {
		return currentRootActivity;
	}

	public void setCurrentRootActivity(FluidActivity currentActivity) {
		this.currentRootActivity = currentActivity;
		if (currentActivity != null) {
			this.launcherActivity = null;
		}
	}

	public void setCurrentActivity(FluidActivity currentActivity) {
		this.currentActivity = currentActivity;
		this.launcherActivity = null;
	}

	public void startApp() {
		if (!started) {
			GlobalState.fluidApp.start();
		}
		started = true;
	}

	public void restartApp() {
		if (!started) {
			return;
		}
		GlobalState.fluidApp.restart();
	}

	public void startOrRestartApp() {

		if (!started) {
			startApp();
		} else {
			restartApp();
		}
	}

	protected void disableUserActivityForCurrentView() {

		try {
			if (currentActivity != null) {
	
				currentActivity.disableUserActivityForCurrentView();
			} else {
	
				currentRootActivity.disableUserActivityForCurrentView();
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	public void popLayout() {

		disableUserActivityForCurrentView();

		GlobalState.fluidApp.getSystemService().runOnUiThread(new Runnable() {
			@Override
			public void run() {

				if (currentActivity != null) {

					currentActivity.popLayout(false);
				} else {

					currentRootActivity.popLayout(false);
				}
			}
		});
	}



	public void pushLayout(final String screenId) {

		disableUserActivityForCurrentView();

		GlobalState.fluidApp.getSystemService().runOnUiThread(new Runnable() {
			@Override
			public void run() {

				if (currentActivity != null) {

					currentActivity.pushLayout(screenId);
				} else {

					currentRootActivity.pushLayout(screenId);
				}
			}
		});
	}

	public void setLayoutStack(final String... screenIds) {

		disableUserActivityForCurrentView();

		GlobalState.fluidApp.getSystemService().runOnUiThread(new Runnable() {
			@Override
			public void run() {

				if (currentActivity != null) {

					currentActivity.setLayoutStack(screenIds);
				} else {

					currentRootActivity.setLayoutStack(screenIds);
				}
			}
		});
	}

	public void removeSplashScreen(final String firstScreenId, boolean insteadShowCurrentScreenIfAny) {

		final String showScreenId = (insteadShowCurrentScreenIfAny && screenId != null) ? screenId : firstScreenId;

		this.currentActivity = null;

		if (showScreenId == null) {
			throw new RuntimeException("Must set showScreenId " + insteadShowCurrentScreenIfAny + " " + currentActivity + " " + screenId);
		}

		if (launcherActivity == null) {
			// Splash screen is not showing. App was restarted after been background from home button, not back button.
			return;
		}

		if (currentRootActivity != null) {

			currentRootActivity.setClearRootActivityOnDestroy(false);
			currentRootActivity.finish();
		}

		final Runnable r = new Runnable () {

			@Override
			public void run() {

				Intent i = new Intent(FluidFrameworkAndroidApp.this, FluidFrameworkAndroidApp.fluidAndroidApp.fluidActivityClass);
				//i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				Bundle b = new Bundle();
				b.putString(FluidActivity.kScreenId, showScreenId);
				i.putExtras(b);
				launcherActivity.startActivity(i);
				launcherActivity.overridePendingTransition(0, 0);
				launcherActivity.finish();
	
				launcherActivity = null;
			}
		};

		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(10);
					GlobalState.fluidApp.getSystemService().runOnUiThread(r);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();

	}

	public void setLayout(final String screenId, final boolean stack) {

		if (launcherActivity != null) {
			throw new RuntimeException("Must removeSlashScreen first");
		}
		
		if (!GlobalState.fluidApp.getSystemService().isOnUiThread()) {
			GlobalState.fluidApp.getSystemService().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					setLayout(screenId, stack);
				}
			});
			
			return;
		}
		
		disableUserActivityForCurrentView();
		
		if (currentActivity != null) {
			currentActivity.setLayout(screenId, stack);
		} else if (currentRootActivity != null) {
			currentRootActivity.setLayout(screenId, stack);
		}
	}

	public void showModalView(final ModalView modalView) {

		if (modalView == null) {
			return;
		}
		
		if (launcherActivity != null) {
			GlobalState.fluidApp.getSystemService().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						((LauncherActivity) launcherActivity).showModalView(modalView);
					} catch (ClassCastException e) {
						e.printStackTrace();
						showModalView(modalView);
					} catch (NullPointerException e) {
						e.printStackTrace();
						showModalView(modalView);
					}
				}
			});
			return;
		}
		
		if (getCurrentActivity() == null) {
			return;
		}
		
		GlobalState.fluidApp.getSystemService().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					((FluidActivity) getCurrentActivity()).showModalView(modalView);
				} catch (ClassCastException e) {
					e.printStackTrace();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void dismissModalView(final ModalView modalView) {
		
		if (launcherActivity != null) {
			return;
		}
		
		if (modalView == null) {
			return;
		}
		
		if ((FluidActivity) getCurrentActivity() == null) {
			return;
		}
		
		/*
		 * The app might be put in the background from user 
		 * pressing back button which means there will be 
		 * no running activity. 
		 * 
		 * TODO : I will refactor this whole setting up of 
		 * activities for the app, this is just a patch for 
		 * now.
		 */
		GlobalState.fluidApp.getSystemService().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					((FluidActivity) getCurrentActivity()).dismissModalView(modalView); 
				} catch (NullPointerException e) {
					e.printStackTrace();
				} catch (ClassCastException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void closeCurrentLayout() {

		disableUserActivityForCurrentView();

		GlobalState.fluidApp.getSystemService().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (currentActivity != null) {
					currentActivity.closeLayout();
				}
				currentActivity = null;
			}
		});
	}

	public void grabFocusForView(final String viewId) {
		GlobalState.fluidApp.getSystemService().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				((FluidActivity) getCurrentActivity()).grabFocusForView(viewId);
			}
		});
	}

	public void hideKeyboard() {
		GlobalState.fluidApp.getSystemService().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				((FluidActivity) getCurrentActivity()).hideKeyboard();
			}
		});
	}

	public void scrollToBottom(final String viewPath, final String viewId) {
		GlobalState.fluidApp.getSystemService().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				((FluidActivity) getCurrentActivity()).scrollToBottom(viewPath, viewId);
			}
		});
	}

	public String getScreenId() {
		return screenId;
	}

	public void setScreenId(String screenId) {
		this.screenId = screenId;
	}

	public Activity getLauncherActivity() {
		return launcherActivity;
	}

	public void setLauncherActivity(Activity launcherActivity) {
		this.launcherActivity = launcherActivity;
	}

	public boolean isInForeground() {
		return isInForeground;
	}

	public void setInForeground(boolean isInForeground) {
		this.isInForeground = isInForeground;
	}

	public Activity getTempCurrentActivity() {
		return tempCurrentActivity;
	}

	public void setTempCurrentActivity(Activity tempCurrentActivity) {
		this.tempCurrentActivity = tempCurrentActivity;
	}

	public Context getTempCurrentContext() {
		return tempCurrentContext;
	}

	public void setTempCurrentContext(Context tempCurrentContext) {
		this.tempCurrentContext = tempCurrentContext;
	}

	public synchronized void startLaunchIntentFromActivityRecreateBug(Context context, String packageName, String classname) {

		if (startedLaunchIntentFromActivityRecreateBug) {
			return;
		}

		startedLaunchIntentFromActivityRecreateBug = true;

		Intent i = new Intent();
		i.setComponent(new ComponentName(packageName, classname));
		i.setAction(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        context.startActivity(i);
	}


	public Class<? extends FluidActivity> getFluidActivityClass() {

		return fluidActivityClass;
	}

	
	public boolean isInitializedAndLoaded() {
	
		return initializedAndLoaded;
	}

}
