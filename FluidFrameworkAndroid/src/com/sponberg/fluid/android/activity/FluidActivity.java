package com.sponberg.fluid.android.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.android.FluidFrameworkAndroidApp;
import com.sponberg.fluid.android.R;
import com.sponberg.fluid.android.layout.Bounds;
import com.sponberg.fluid.android.layout.CustomLayout;
import com.sponberg.fluid.android.layout.FluidViewFactoryRegistration;
import com.sponberg.fluid.layout.Layout;
import com.sponberg.fluid.layout.MenuButtonItem;
import com.sponberg.fluid.layout.ModalView;
import com.sponberg.fluid.layout.Screen;
import com.sponberg.fluid.util.Logger;

public class FluidActivity extends ActionBarActivity  {

	public static final String kIsRootActivity = "com.sponberg.fluid.android.FluidActivity.kIsRootActivity";
	public static final String kScreenId = "com.sponberg.fluid.android.FluidActivity.kScreenId";

	static final int REQUEST_PHOTO = 0;
	public static final int ACTIVITY_RESULT_CUSTOM_START_INDEX = 1000; // FluidActivity reserves the first 1000 activity response codes

	static boolean rootIsRunning = false;
	protected static boolean nonRootIsRunning = false;

	protected HashMap<String, CustomLayout> screensById = new HashMap<>();

	protected HashMap<String, Stack<String>> tabScreenStack = new HashMap<>();

	protected Stack<String> nonTabScreenStack = new Stack<>();

	protected HashMap<String, String> baseScreenIdForTab = new HashMap<>();

	protected String baseScreenIdForNonTab;

	protected HashMap<String, Tab> tabForScreenId = new HashMap<>();

	protected boolean rootActivity = true;

	ModalView currentModalView = null; // hstdbc remove this put on intent

	//Dialog currentModalDialog = null;

	protected FluidTabListener tabListener = null;

	protected String selectedTab;

	boolean landscape = false;

	boolean clearRootActivityOnDestroy = true;

	boolean aborted = false;

	CustomLayout currentContentView = null;
	
	public String getCurrentScreenId() {
		if (currentContentView == null) {
			return null;
		}
		
		String returnString = null;
		
		if (currentContentView.getScreen() != null) {
			returnString = currentContentView.getScreen().getScreenId();
		}
		
		return returnString;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		if (!GlobalState.fluidApp.isLoaded()) {

			aborted = true;

			getWindow().requestFeature(Window.FEATURE_NO_TITLE);
	        finish();

	        // Invoking a runnable flashes a black screen, but less time on the home screen
	        //Runnable r = new Runnable() {
	        //	public void run() {

	        		String packageName = getPackageName();
	    			Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
	    			String className = launchIntent.getComponent().getClassName();

	    			FluidFrameworkAndroidApp.getFluidAndroidApp().startLaunchIntentFromActivityRecreateBug(FluidActivity.this, packageName, className);
	        //	}
	        //};
	        //GlobalState.fluidApp.getSystemService().runOnUiThread(r);

	        return;
	    }

		// Without SOFT_INPUT_STATE_HIDDEN the keyboard will automatically launch
		super.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		ActionBar actionBar = getSupportActionBar();
		actionBar.hide();

		Bundle extras = getIntent().getExtras();
		boolean isRootActivity = (extras == null) ? true : extras.getBoolean(kIsRootActivity, true);

		if (isRootActivity) {
			((FluidFrameworkAndroidApp) this.getApplicationContext()).setCurrentRootActivity(this);
			((FluidFrameworkAndroidApp) this.getApplicationContext()).setCurrentActivity(null);
		} else {
			((FluidFrameworkAndroidApp) this.getApplicationContext()).setCurrentActivity(this);
		}

		String screenId = null;
		if (extras != null) {
			screenId = extras.getString(kScreenId);
		}

		if (isRootActivity) {

			String showTabs = GlobalState.fluidApp.getSetting("show-tabs");

			boolean useTabs = true;
			if (showTabs == null || !showTabs.equalsIgnoreCase("true")) {
				useTabs = false;
			}

			if (useTabs) {

				tabListener = new FluidTabListener();

				for (com.sponberg.fluid.layout.Tab tab : GlobalState.fluidApp
						.getTabs()) {
					
					Tab tabView = actionBar.newTab();
					tabView.setText(tab.getLabel());
					tabView.setCustomView(getCustomViewForTab(tab));
					
					baseScreenIdForTab.put(tabView.getText().toString(), tab.getScreenId());
					tabForScreenId.put(tab.getScreenId(), tabView);
					tabView.setTabListener(tabListener);
					tabScreenStack.put(tabView.getText().toString(),
							new Stack<String>());
					actionBar.addTab(tabView);
				}

				if (screenId == null) {
					screenId = GlobalState.fluidApp.getTabs().get(0).getScreenId();
				}
				selectedTab = GlobalState.fluidApp.getTabs().get(0).getLabel();
			} else {

				baseScreenIdForNonTab = screenId;
			}

			if (screenId == null) {
				Logger.error(this, "screenId is null");
			} else {
				setFluidScreen(screenId, true, true);
			}

		} else {
			rootActivity = false;
			baseScreenIdForNonTab = extras.getString(kScreenId);
			setFluidScreen(baseScreenIdForNonTab, true, false);
		}
	}

	protected View getCustomViewForTab(com.sponberg.fluid.layout.Tab tab) {
		return null;
	}
	
	protected void setFluidScreen(String screenId, boolean removeCurrentView, boolean saveCurrentScreen, boolean animated) {
		String saveScreenId = screenId;

		Screen screen = GlobalState.fluidApp.getScreen(screenId);

		saveCurrentScreen = initScreenWithTab(screen, saveCurrentScreen);

		CustomLayout layout = screensById.get(screenId);
		if (layout == null) {
			Layout l = screen.getLayout();
			Display display = getWindowManager().getDefaultDisplay();
			int width = display.getWidth();
			int height = display.getHeight();
			layout = new CustomLayout(this, screen, l, new Bounds(0, 0, width, height), null, null, screen.getScreenId(), false, null, true, null, null, l.isWrapInScrollView(), false, null);
			layout.setRoot(true);
			screensById.put(screenId, layout);
		}
		layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

		if (currentContentView == null) {
			setContentView(R.layout.activity_main);
		}

		FrameLayout view = (FrameLayout) findViewById(R.id.container);

		if (currentContentView != null && removeCurrentView) {
			// currentContentView.cleanup(); // hstdbc should we do this here? if they are not the same
			view.removeView(currentContentView);
		}

		if (currentContentView != null) {
			//currentContentView.viewDidDisappear();
		}

		layout.viewWillAppear();

		view.addView(layout);
		
		if (animated) {
			AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
			anim.setDuration(300);
			layout.startAnimation(anim);
		}

		layout.viewDidAppear();

		currentContentView = layout;

		currentContentView.setUserActivityEnabled(true);

		String name = GlobalState.fluidApp.getScreen(screenId).getName();
		String nameKey = GlobalState.fluidApp.getScreen(screenId).getNameKey();

		if (nameKey != null) {
			String text = GlobalState.fluidApp.getDataModelManager().getValue(null, nameKey, name, null);
			getSupportActionBar().setTitle(text);
		} else {
			getSupportActionBar().setTitle(name);
		}

		String subtitle = GlobalState.fluidApp.getScreen(screenId).getSubtitle();
		String subtitleKey = GlobalState.fluidApp.getScreen(screenId).getSubtitleKey();

		String subtitleText = null;
		if (subtitleKey != null) {
			subtitleText = GlobalState.fluidApp.getDataModelManager().getValue(null, subtitleKey, subtitle, null);
		} else if (subtitle != null) {
			subtitleText = subtitle;
		}

		if (subtitleText == null || subtitleText.equals("")) {
			getSupportActionBar().setSubtitle(null);
		} else {
			getSupportActionBar().setSubtitle(subtitleText);
		}

		// Update options menu
		invalidateOptionsMenu(); // Android will call onPrepareOptionsMenu

		if (!overridingUpButton) {
			if (shouldShowUpButton()) {
				showUpButton(true, null);
			} else {
				showUpButton(false, null);
			}
		}

		if (rootActivity && saveCurrentScreen) {
			((FluidFrameworkAndroidApp) this.getApplicationContext()).setScreenId(saveScreenId);
		}
	}
	
	
	protected void setFluidScreen(String screenId, boolean removeCurrentView, boolean saveCurrentScreen) {
		
		String saveScreenId = screenId;

		Screen screen = GlobalState.fluidApp.getScreen(screenId);

		saveCurrentScreen = initScreenWithTab(screen, saveCurrentScreen);

		CustomLayout layout = screensById.get(screenId);
		if (layout == null) {
			Layout l = screen.getLayout();
			Display display = getWindowManager().getDefaultDisplay();
			int width = display.getWidth();
			int height = display.getHeight();
			layout = new CustomLayout(this, screen, l, new Bounds(0, 0, width, height), null, null, screen.getScreenId(), false, null, true, null, null, l.isWrapInScrollView(), false, null);
			layout.setRoot(true);
			screensById.put(screenId, layout);
		}
		layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

		if (currentContentView == null) {
			setContentView(R.layout.activity_main);
		}

		FrameLayout view = (FrameLayout) findViewById(R.id.container);

		if (currentContentView != null && removeCurrentView) {
			// currentContentView.cleanup(); // hstdbc should we do this here? if they are not the same
			view.removeView(currentContentView);
		}

		if (currentContentView != null) {
			//currentContentView.viewDidDisappear();
		}

		layout.viewWillAppear();

		view.addView(layout);
		
//		//---TESTING CODE
//		AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
//		anim.setDuration(300);
//		layout.startAnimation(anim);

		layout.viewDidAppear();

		currentContentView = layout;

		currentContentView.setUserActivityEnabled(true);

		String name = GlobalState.fluidApp.getScreen(screenId).getName();
		String nameKey = GlobalState.fluidApp.getScreen(screenId).getNameKey();

		if (nameKey != null) {
			String text = GlobalState.fluidApp.getDataModelManager().getValue(null, nameKey, name, null);
			getSupportActionBar().setTitle(text);
		} else {
			getSupportActionBar().setTitle(name);
		}

		String subtitle = GlobalState.fluidApp.getScreen(screenId).getSubtitle();
		String subtitleKey = GlobalState.fluidApp.getScreen(screenId).getSubtitleKey();

		String subtitleText = null;
		if (subtitleKey != null) {
			subtitleText = GlobalState.fluidApp.getDataModelManager().getValue(null, subtitleKey, subtitle, null);
		} else if (subtitle != null) {
			subtitleText = subtitle;
		}

		if (subtitleText == null || subtitleText.equals("")) {
			getSupportActionBar().setSubtitle(null);
		} else {
			getSupportActionBar().setSubtitle(subtitleText);
		}

		// Update options menu
		invalidateOptionsMenu(); // Android will call onPrepareOptionsMenu

		if (!overridingUpButton) {
			if (shouldShowUpButton()) {
				showUpButton(true, null);
			} else {
				showUpButton(false, null);
			}
		}

		if (rootActivity && saveCurrentScreen) {
			((FluidFrameworkAndroidApp) this.getApplicationContext()).setScreenId(saveScreenId);
		}
	}

	public interface UpButtonEventHandler {
		public void onUpButtonPressed();
	}

	UpButtonEventHandler currentUpButtonPressedHandler = null;
	boolean overridingUpButton = false;

	public void showUpButton(boolean shouldShow, UpButtonEventHandler handler) {
		getSupportActionBar().setDisplayHomeAsUpEnabled(shouldShow);
		getSupportActionBar().setHomeButtonEnabled(shouldShow);
		overridingUpButton = shouldShow && (handler != null);
		currentUpButtonPressedHandler = handler;
	}

	protected boolean initScreenWithTab(Screen screen, boolean saveCurrentScreen) {

		String screenId = screen.getScreenId();

		if (rootActivity) {
			ActionBar actionBar = getSupportActionBar();
			if (tabListener != null) {
				tabListener.enabled = false;
			}
			if (screen.isShowTabBar()) {
				if (actionBar.getNavigationMode() != ActionBar.NAVIGATION_MODE_TABS) {
					actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
				}
				Tab tab = tabForScreenId.get(screenId);
				if (tab != null) {
					tab.select();
					selectedTab = tab.getText().toString();
					screenId = getScreenIdForTab(selectedTab);
					saveCurrentScreen = true;
				}
			} else {
				if (actionBar.getNavigationMode() != ActionBar.NAVIGATION_MODE_STANDARD) {
					tabListener.enabled = true;
					actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
				}
			}
			if (tabListener != null) {
				tabListener.enabled = true;
			}
		}

		ActionBar actionBar = getSupportActionBar();
		if (screen.isShowNavigationBar()) {
			if (!setActionBarVisible(true)) {
				actionBar.show();
			}
		} else {
			if (!setActionBarVisible(false)) {
				actionBar.hide();
			}
		}

		return saveCurrentScreen;
	}

	public boolean setActionBarVisible(boolean isVisible) {

		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR2) {
			ActionBar actionBar = getSupportActionBar();
			if (isVisible) {
				actionBar.show();
			} else {
				actionBar.hide();
			}
			return true;
		}

	    View decorView = getWindow().getDecorView();
	    int resId;
	    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
	        || Build.VERSION.SDK_INT >= 20) {
	        resId = getResources().getIdentifier(
	                "action_bar_container", "id", getPackageName());
	    } else {
	        resId = Resources.getSystem().getIdentifier(
	                "action_bar_container", "id", "android");
	    }

	    if (resId != 0 && decorView.findViewById(resId) != null) {
	        decorView.findViewById(resId).setVisibility(isVisible ? View.VISIBLE : View.GONE);
	        return true;
	    }
	    return false;
	}

	protected boolean isUsingTabbedNavigation() {
		//return getSupportActionBar().getNavigationMode() == ActionBar.NAVIGATION_MODE_TABS;
		if (!rootActivity) {
			return false;
		}
		return true;
	}

	@Override
	protected void onResume() {
        super.onResume();

        if (aborted) {
        	return;
        }

        if (rootActivity) {
        	((FluidFrameworkAndroidApp) this.getApplicationContext()).setCurrentRootActivity(this);
        } else {
        	((FluidFrameworkAndroidApp) this.getApplicationContext()).setCurrentActivity(this);
        }
		((FluidFrameworkAndroidApp) this.getApplicationContext()).setInForeground(true);

		if (rootActivity && !nonRootIsRunning) {
			((FluidFrameworkAndroidApp) this.getApplicationContext()).startOrRestartApp();
		} else if (!rootActivity && !rootIsRunning) {
			((FluidFrameworkAndroidApp) this.getApplicationContext()).startOrRestartApp();
		}

		currentContentView.setUserActivityEnabled(true);
    }

	@Override
	protected void onPause() {
        //((FluidFrameworkAndroidApp) this.getApplicationContext()).setCurrentActivity(null);

		((FluidFrameworkAndroidApp) this.getApplicationContext()).setInForeground(false);
        super.onPause();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return onPrepareOptionsMenu(menu);
	}

	HashMap<CharSequence, MenuButtonItem> menuButtonByItem = new HashMap<>();
	HashMap<CharSequence, MenuItem> menuItems = new HashMap<>();


	public float actionBarSize() {

		TypedValue tv = new TypedValue();
	    if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
	    	return TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
	    }
		return 32f;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		menu.clear();
		menuButtonByItem.clear();
		menuItems.clear();

		if (currentContentView != null) {
			int order = 0;


			for (MenuButtonItem item : currentContentView.getScreen().getNavigationMenuItems()) {

				final MenuItem menuItem = menu.add(Menu.NONE, Menu.NONE, order, getMenuButtonTitleText(item));

				if (item.getSystemId() != null && !item.getSystemId().equals(MenuButtonItem.SystemItemCustom)) {

					menuItem.setIcon(getIconIdFor(item.getSystemId()));
				} else if (item.getIconName() != null) {

					int actionBarSize = (int) (actionBarSize() * .55);

					Bitmap bm = FluidViewFactoryRegistration.getBitmapFor(item.getIconName(), actionBarSize, actionBarSize, null);

					BitmapDrawable bmDrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bm, actionBarSize, actionBarSize, true));

					//ImageView icon = (ImageView) findViewById(android.R.id.home);
					//icon.getHeight();

					int textColor = android.graphics.Color.argb(255, 255, 255, 255);
					String colorString = GlobalState.fluidApp.getSetting("defaults", "colors", "android-nav-bar-tint");
				    if (colorString != null) {
				    	textColor = CustomLayout.getColor(GlobalState.fluidApp.getViewManager().getColor(colorString));
				    }
					bmDrawable.mutate().setColorFilter(textColor, PorterDuff.Mode.SRC_IN);

					menuItem.setIcon(bmDrawable);
				}

				if (!item.isShowOnMainBar()) {
					menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
				} else if (order < 1) {
					menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
				} else {
					menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
				}

				if (item.getActionFlavor() == MenuButtonItem.ActionFlavorSearch) {
					menuItem.setActionView(getMenuActionViewSearch(item));
				}

				menuItem.setEnabled(item.isEnabled());

				menuButtonByItem.put(menuItem.getTitle(), item);
				menuItems.put(item.getSystemId(), menuItem);
				order++;
			}
		}

		return true;
	}

	protected SearchView getMenuActionViewSearch(final MenuButtonItem item) {

	    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

	    SearchView searchView = new SearchView(this);
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
	    searchView.setIconifiedByDefault(false);
	    searchView.setQueryHint(item.getProperty("queryHint"));

	    String colorString = item.getProperty("textColor");
	    String placeholderColorString = item.getProperty("placeholderTextColor");
	    if (colorString != null || placeholderColorString != null) {
	    	Integer color = null;
	    	if (colorString != null) {
	    		color = CustomLayout.getColor(GlobalState.fluidApp.getViewManager().getColor(colorString));
	    	}
	    	Integer placeholderColor = null;
	    	if (placeholderColorString != null) {
	    		placeholderColor = CustomLayout.getColor(GlobalState.fluidApp.getViewManager().getColor(placeholderColorString));
	    	}
	    	setSearchviewTextColor(searchView, color, placeholderColor);
	    }
	    String textSizeString = item.getProperty("textSize");
	    if (textSizeString != null) {
	    	setSearchviewTextSize(searchView, (float) GlobalState.fluidApp.sizeToPixels(textSizeString));
	    }
	    
	    String androidLineColorString = item.getProperty("androidLineColor");
	    if (androidLineColorString != null) {
	    	int lineColor = CustomLayout.getColor(GlobalState.fluidApp.getViewManager().getColor(androidLineColorString));
	    	int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
	        View searchPlate = searchView.findViewById(searchPlateId);
	        searchPlate.getBackground().setColorFilter(lineColor, PorterDuff.Mode.SRC_ATOP);
	    }

	    searchView.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String text) {
				return true;
			}
			@Override
			public boolean onQueryTextChange(String text) {
				item.userChangedValueTo(text);
				return true;
			}
		});

	    return searchView;
	}

	protected void setSearchviewTextColor(ViewGroup view, Integer color, Integer placeholderColor) {
    	int i = view.getChildCount();
    	for (int index = 0; index < i; index++) {
    		View child = view.getChildAt(index);
    		if (child instanceof EditText) {
    			if (color != null) {
    				((EditText) child).setTextColor(color);
    			}
    			if (placeholderColor != null) {
    				((EditText) child).setHintTextColor(placeholderColor);
    			}
    			return;
    		} else if (child instanceof ViewGroup) {
    			setSearchviewTextColor((ViewGroup) child, color, placeholderColor);
    		}
    	}
	}

	protected void setSearchviewTextSize(ViewGroup view, float fontPixels) {
    	int i = view.getChildCount();
    	for (int index = 0; index < i; index++) {
    		View child = view.getChildAt(index);
    		if (child instanceof EditText) {
    			((EditText) child).setTextSize(TypedValue.COMPLEX_UNIT_PX, fontPixels);
    			return;
    		} else if (child instanceof ViewGroup) {
    			setSearchviewTextSize((ViewGroup) child, fontPixels);
    		}
    	}
	}
	
	protected int getIconIdFor(String systemId) {
		if (systemId == null) {
			return Menu.NONE;
		}
		if (systemId.equals(MenuButtonItem.SystemItemCamera)) {
			return android.R.drawable.ic_menu_camera;
		} else if (systemId.equals(MenuButtonItem.SystemItemAction)) {
			return android.R.drawable.ic_menu_share;
		} else if (systemId.equals(MenuButtonItem.SystemItemStop)) {
			return android.R.drawable.ic_menu_close_clear_cancel;
		}
		return Menu.NONE;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		MenuButtonItem buttonItem = menuButtonByItem.get(item.getTitle());
		if (buttonItem != null) {
			if (currentContentView.isUserActivityEnabled()) {
				buttonItem.userTapped();
			}
			return true;
		}

		int id = item.getItemId();
		if (id == android.R.id.home) {
			if (overridingUpButton) {
				if (currentUpButtonPressedHandler != null) {
					currentUpButtonPressedHandler.onUpButtonPressed();
				}
			} else {
				popLayout(true);
			}

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void disableUserActivityForCurrentView() {

		this.currentContentView.setUserActivityEnabled(false);
	}
	
	public void pushLayout(String screenId) {

		InputMethodManager imm = (InputMethodManager)getSystemService(
			      Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(currentContentView.getWindowToken(), 0);

		if (isUsingTabbedNavigation()) {
			Stack<String> stack = tabScreenStack.get(selectedTab);
			stack.push(screenId);
		} else {
			nonTabScreenStack.push(screenId);
		}

		Animation anim = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
		anim.setAnimationListener(new RemoveViewListener(this.currentContentView));
		this.currentContentView.startAnimation(anim);

		setFluidScreen(screenId, true, false); //hstdbc
		
		this.currentContentView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_left));
	}

	private boolean shouldShowUpButton() {
		if (isUsingTabbedNavigation() && selectedTab != null) {
			Stack<String> stack = tabScreenStack.get(selectedTab);
			if (stack != null && stack.size() > 0) {
				return true;
			}
		} else {
			if (nonTabScreenStack != null && nonTabScreenStack.size() > 0) {
				return true;
			}
		}

		return false;
	}

	public void setLayout(String screenId, boolean stack) {

		InputMethodManager imm = (InputMethodManager)getSystemService(
			      Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(currentContentView.getWindowToken(), 0);

		if (stack) {
			Intent intent = new Intent(this, FluidFrameworkAndroidApp.getFluidAndroidApp().getFluidActivityClass());
			Bundle b = new Bundle();
			b.putBoolean(kIsRootActivity, false);
			b.putString(kScreenId, screenId);
			intent.putExtras(b);
			startActivity(intent);

			if (currentContentView != null) {
				//currentContentView.viewDidDisappear();
			}
		} else {
			setFluidScreen(screenId, true, false);
		}
	}

	public void setLayoutStack(String... screenIds) {

		if (isUsingTabbedNavigation()) {
			Stack<String> stack = tabScreenStack.get(selectedTab);
			for (String screenId : screenIds) {
				stack.push(screenId);
			}
		} else {
			for (String screenId : screenIds) {
				nonTabScreenStack.push(screenId);
			}
		}

		Screen screen = GlobalState.fluidApp.getScreen(screenIds[0]);
		initScreenWithTab(screen, false);

		setFluidScreen(screenIds[screenIds.length - 1], true, false);

		if (rootActivity) {
			((FluidFrameworkAndroidApp) this.getApplicationContext()).setScreenId(screenIds[0]);
		}
	}

	public void showModalView(final ModalView modalView) {

		currentModalView = modalView;

		if (modalView.getSystemId().equals(ModalView.FluidLayout)) {

			String screenId = (String) modalView.getUserData();

			final Dialog d = new Dialog(this);
			modalView.setFluidData(d);
			d.requestWindowFeature(Window.FEATURE_NO_TITLE);

			if (modalView.isUserCancelable()) {
				d.setCancelable(true);
				d.setCanceledOnTouchOutside(true);
			} else {
				d.setCancelable(false);
				d.setCanceledOnTouchOutside(false);
			}

			d.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					Dialog d = (Dialog) modalView.getFluidData();
					if (modalView.getUserSelection() != null) {
						modalView.modalComplete(modalView.getUserSelection());
					} else {
						modalView.modalCanceled();
					}
					currentModalView = null;
				}
			});

			Screen screen = GlobalState.fluidApp.getScreen(screenId);
			Layout l = screen.getLayout();
			Display display = getWindowManager().getDefaultDisplay();
			int width = (int) (display.getWidth() * .9);
			int height = (int) Math.round(l.calculateHeight(landscape, width, null));

			CustomLayout layout = new CustomLayout(this, screen, l, new Bounds(0, 0, width, height), null, null, screen.getScreenId(), false, null, true, null, modalView, false, false, null);
			layout.setRoot(false);

			d.setContentView(layout);
			d.show();

		} else if (modalView.getSystemId().equals(ModalView.FluidLayoutFullScreen)) {

			String screenId = (String) modalView.getUserData();

			setLayout(screenId, true);

		} else if (modalView.getSystemId().equals(ModalView.Confirmation)) {

			final ModalView.ModalViewConfirmation userData = (ModalView.ModalViewConfirmation) modalView.getUserData();

			AlertDialog.Builder builder = new AlertDialog.Builder(this)
		    	.setTitle(userData.getTitle())
		    	.setOnCancelListener(new DialogInterface.OnCancelListener() {
				    @Override
				    public void onCancel(DialogInterface dialog) {
				        modalView.modalCanceled();
				    }
				});

			if (userData.getMessage() != null && userData.getMessage().length() > 0) {
				builder.setMessage(userData.getMessage());
			}

			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
		        @Override
		        public void onClick(DialogInterface dialog, int which) {
		            if (which == DialogInterface.BUTTON_POSITIVE) {
		            	modalView.modalComplete(userData.getOk().toLowerCase());
		            } else {
		            	modalView.modalComplete(userData.getCancel().toLowerCase());
		            }
		        }
		    };

			builder.setPositiveButton(userData.getOk(), listener);
			builder.setNegativeButton(userData.getCancel(), listener);

			modalView.setFluidData(builder.show());

		} else if (modalView.getSystemId().equals(ModalView.WaitingDialog)) {

			ModalView.ModalViewWaitingDialog userData = (ModalView.ModalViewWaitingDialog) modalView.getUserData();

			AlertDialog.Builder builder = new AlertDialog.Builder(this)
			    .setTitle(userData.getTitle())
			    .setCancelable(false);

			if (userData.getMessage() != null && userData.getMessage().length() > 0) {
				builder.setMessage(userData.getMessage());
			}

			modalView.setFluidData(builder.show());

		} else if (modalView.getSystemId().equals(ModalView.ImagePicker)) {

			Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
		    galleryIntent.setType("image/*");
		    galleryIntent.putExtra("return-data", true);
		    galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);

		    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

		    Intent[] intentArray =  {cameraIntent};
		    Intent chooser = new Intent(Intent.ACTION_CHOOSER);
		    chooser.putExtra(Intent.EXTRA_INTENT, galleryIntent);
		    chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
		    chooser.putExtra(Intent.EXTRA_TITLE, "Choose Image Source");

		    startActivityForResult(chooser, REQUEST_PHOTO);
		}
	}

	public void dismissModalView(final ModalView modalView) {
		if (modalView == null) {
			return;
		}
		
		Dialog d = (Dialog) modalView.getFluidData();
		if (d == null) {
			return;
		}
		try {
			d.dismiss();
		} catch (IllegalArgumentException e) {
			// In case the dialog was launched with splash screen, which is gone now
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == REQUEST_PHOTO && resultCode == Activity.RESULT_OK) {

			boolean success = false;

			Bitmap bitmap = null;
			if (data.getData() != null) {
				try {

					InputStream stream = getContentResolver().openInputStream(
							data.getData());
					bitmap = BitmapFactory.decodeStream(stream);
					stream.close();

				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				bitmap = (Bitmap) data.getExtras().get("data");
			}

			if (bitmap != null) {

				HashMap<String, String> settings = (HashMap<String, String>) currentModalView.getUserData();

				int newWidth = bitmap.getWidth();
				int newHeight = bitmap.getHeight();

				int maxWidth = 0;
				int maxHeight = 0;
				if (settings.get("maxWidth") != null) {
					maxWidth = Integer.parseInt(settings.get("maxWidth"));
				}
				if (settings.get("maxHeight") != null) {
					maxWidth = Integer.parseInt(settings.get("maxHeight"));
				}

			    if (maxWidth > 0 && maxWidth < newWidth) {
			        double scale = maxWidth * 1.0 / newWidth;
			        newWidth = maxWidth;
			        newHeight = (int) (newHeight * scale);
			    }
			    if (maxHeight > 0 && maxHeight < newHeight) {
			        double scale = maxHeight * 1.0 / newHeight;
			        newHeight = maxHeight;
			        newWidth = (int) (newWidth * scale);
			    }

				bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);

				int quality = 100;
				if (settings.get("quality") != null) {
					quality = Integer.parseInt(settings.get("quality"));
				}

				String format = settings.get("format");
				if (format == null) {
					format = "png";
				}

				ByteArrayOutputStream out = new ByteArrayOutputStream();
				if (format.equals("jpg")) {
					bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
				} else {
					bitmap.compress(Bitmap.CompressFormat.PNG, quality, out);
				}

				try {
					out.close();
					success = true;
					currentModalView.modalComplete(out.toByteArray());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (!success) {
				currentModalView.modalCanceled(); // hstdbc error instead?
			}

			super.onActivityResult(requestCode, resultCode, data);
		} else if (resultCode == Activity.RESULT_CANCELED) {
			if (currentModalView != null) {
				currentModalView.modalCanceled();
			}
		}

		currentModalView = null;
	}

	public void closeLayout() {

		InputMethodManager imm = (InputMethodManager)getSystemService(
			      Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(currentContentView.getWindowToken(), 0);

		currentContentView.screenWasRemoved();

		((FluidFrameworkAndroidApp) this.getApplicationContext()).setCurrentActivity(null);

		finish();
	}

	public String getScreenIdForTab(String tabName) {
		Stack<String> stack = tabScreenStack.get(tabName);
		if (stack.size() == 0) {
			return baseScreenIdForTab.get(tabName);
		} else {
			return stack.peek();
		}
	}

	public String getScreenIdForNonTab() {
		Stack<String> stack = nonTabScreenStack;
		if (stack.size() == 0) {
			return baseScreenIdForNonTab;
		} else {
			return stack.peek();
		}
	}

	public boolean popLayout(final boolean popAll) {

		if (Looper.myLooper() != Looper.getMainLooper()) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					popLayout(popAll);
				}
			});
			return false;
		}

		InputMethodManager imm = (InputMethodManager)getSystemService(
			      Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(currentContentView.getWindowToken(), 0);

		Stack<String> stack;
		if (isUsingTabbedNavigation()) {
			stack = tabScreenStack.get(selectedTab);
		} else {
			stack = nonTabScreenStack;
		}

		ArrayList<String> previousScreenIds = new ArrayList<>();

		if (stack != null && stack.size() > 0) {
			if (popAll) {
				while(stack.size() > 0) {
					previousScreenIds.add(stack.pop());
				}
			} else {
				previousScreenIds.add(stack.pop());
			}

			String nextScreenId;
			if (isUsingTabbedNavigation()) {
				nextScreenId = getScreenIdForTab(selectedTab);
			} else {
				nextScreenId = getScreenIdForNonTab();
			}

			Animation anim = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);
			anim.setAnimationListener(new RemoveViewListener(this.currentContentView));
			this.currentContentView.startAnimation(anim);
			setFluidScreen(nextScreenId, false, false);
			this.currentContentView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));

			for (String screenId : previousScreenIds) {
				CustomLayout previousContentView = screensById.get(screenId);
				if (previousContentView != null) {
					previousContentView.screenWasRemoved();
				}
				screensById.remove(screenId);
			}

			return true;
		}

		return false;
	}

	public void grabFocusForView(final String viewId) {
		if (Looper.myLooper() != Looper.getMainLooper()) {
			Runnable r = new Runnable() {
				@Override
				public void run() {
					grabFocusForView(viewId);
				}
			};
			runOnUiThread(r);
		} else {
			if (menuItems != null && menuItems.size() > 0) {
				final MenuItem menuItem = menuItems.get(viewId);
				if (menuItem != null) {
					boolean shouldShowKeyboard = (viewId == MenuButtonItem.SystemItemSearch) ? true : false;
					grabFocusForMenuItem(menuItem, shouldShowKeyboard);
					return;
				}
			}

			currentContentView.grabFocusForView(viewId);
		}
	}

	public void scrollToBottom(final String viewPath, final String viewId) {
		currentContentView.scrollToBottom(viewPath, viewId);
	}

	public void hideKeyboard() {

		if (Looper.myLooper() != Looper.getMainLooper()) {
			Runnable r = new Runnable() {
				@Override
				public void run() {
					hideKeyboard();
				}
			};
			runOnUiThread(r);
		} else {
			InputMethodManager imm = (InputMethodManager)getSystemService(
				      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(currentContentView.getWindowToken(), 0);
		}
	}

	protected void grabFocusForMenuItem(MenuItem menuItem, boolean showKeyboard) {
		if (menuItem != null) {
			menuItem.getActionView().requestFocus();
			if (showKeyboard) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
			}
		}
	}

	@Override
	public void onBackPressed() {

		if (Looper.myLooper() != Looper.getMainLooper()) {
			Runnable r = new Runnable() {
				@Override
				public void run() {
					if (!popLayout(false)) {
						FluidActivity.this.closeLayout();
					}
				}
			};
			runOnUiThread(r);
		} else {
			if (!popLayout(false)) {
				FluidActivity.this.closeLayout();
			}
		}
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();

		if (currentContentView != null) {
			currentContentView.cleanup();
		}

		if (rootActivity && clearRootActivityOnDestroy && this.getApplicationContext() != null && !aborted) {
			((FluidFrameworkAndroidApp) this.getApplicationContext()).setCurrentRootActivity(null); //TODO : do we need to check if the currentRootActivity is this one? cuz you might nullify a different one
		}

		if (rootActivity) {
			rootIsRunning = false;
			nonRootIsRunning = false;
		} else {
			nonRootIsRunning = false;
		}
	}

	@Override
	protected void onStart() {

		super.onStart();
		if (rootActivity) {
			((FluidFrameworkAndroidApp) this.getApplicationContext()).setCurrentRootActivity(this);
		} else {
			((FluidFrameworkAndroidApp) this.getApplicationContext()).setCurrentActivity(this);
		}

		if (rootActivity) {
			rootIsRunning = true;
		} else {
			nonRootIsRunning = true;
		}
	}

	@Override
	protected void onStop() {

		//((FluidFrameworkAndroidApp) this.getApplicationContext()).setCurrentActivity(null);

		if (rootActivity) {
			rootIsRunning = false;
		} else {
			nonRootIsRunning = false;
		}

		super.onStop();
	}

	protected class FluidTabListener implements TabListener {

		boolean enabled = true;

		ScheduledExecutorService tabSwitcher = Executors.newSingleThreadScheduledExecutor();

		@Override
		public void onTabReselected(final Tab tab, FragmentTransaction arg1) {
			if (!enabled) {
				return;
			}

			Stack<String> stack = tabScreenStack.get(tab.getText().toString());
			stack.clear();
			selectedTab = tab.getText().toString();

			Runnable r = new Runnable() {
				@Override
				public void run() {
					GlobalState.fluidApp.getSystemService().runOnUiThread(new Runnable() {
						@Override
						public void run() {							
							setFluidScreen(getScreenIdForTab(tab.getText().toString()), true, true);
						}
					});
				}
			};
			tabSwitcher.schedule(r, 5, TimeUnit.MILLISECONDS);
		}

		@Override
		public void onTabSelected(final Tab tab, FragmentTransaction arg1) {
			if (!enabled) {
				return;
			}
			selectedTab = tab.getText().toString();

			Runnable r = new Runnable() {
				@Override
				public void run() {
					GlobalState.fluidApp.getSystemService().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							setFluidScreen(getScreenIdForTab(tab.getText().toString()), true, true, true);
						}
					});
				}
			};
			tabSwitcher.schedule(r, 5, TimeUnit.MILLISECONDS);
		}

		@Override
		public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
			if (!enabled) {
				return;
			}
		}

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

	}

	class RemoveViewListener implements AnimationListener {

		final View contentView;

		public RemoveViewListener(View contentView) {
			this.contentView = contentView;
		}

		@Override
		public void onAnimationEnd(Animation arg0) {
			final FrameLayout view = (FrameLayout) findViewById(R.id.container);

			view.post(new Runnable() {
	            @Override
				public void run() {
	                runOnUiThread(new Runnable() {
	                    @Override
						public void run() {
	                    	view.removeView(contentView);
	                    }
	                });
	            }
	        });

		}

		@Override
		public void onAnimationRepeat(Animation arg0) {
		}

		@Override
		public void onAnimationStart(Animation arg0) {
		}

	}

	public void setClearRootActivityOnDestroy(boolean clearRootActivityOnDestroy) {
		this.clearRootActivityOnDestroy = clearRootActivityOnDestroy;
	}

	public static String getMenuButtonTitleText(MenuButtonItem item) {

		return item.getTitle();
	}

}
