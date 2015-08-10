package com.sponberg.fluid.layout;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class Screen {

	private final Layout layout;
	
	private final List<MenuButtonItem> navigationMenuItems = new ArrayList<>();
	
	private boolean showTabBar = true;
	
	private boolean showNavigationBar = true;
	
	private boolean showStatusBar = true;
	
	private String backButtonText = null;
	
	private boolean hideBackButton = false;
	
	private Object nativePlugin = null;
	
	protected ArrayList<ScreenListener> screenListeners = new ArrayList<>();

	public Screen(Layout layout) {
		this.layout = layout;
	}
	
	public String getName() {
		return layout.getName();
	}
	
	public String getNameKey() {
		return layout.getNameKey();
	}
	
	public String getSubtitle() {
		return layout.getSubtitle();
	}
	
	public String getSubtitleKey() {
		return layout.getSubtitleKey();
	}
	
	public String getScreenId() {
		return layout.getId();
	}
	
	public View getView(String view) {
		return layout.getViewMap().get(view);
	}
	
	public void screenDidLoad() {
	}
	
	public void addScreenListener(ScreenListener listener) {
		this.screenListeners.add(listener);
	}
	
	public void screenWillAppear() {
		for (ScreenListener l : screenListeners) {
			l.screenWillAppear();
		}
	}
	
	public void screenDidAppear() {
		for (ScreenListener l : screenListeners) {
			l.screenDidAppear();
		}
	}
	
	public void screenDidDisappear() {
		for (ScreenListener l : screenListeners) {
			l.screenDidDisappear();
		}
	}

	public void screenWasRemoved() {
		for (ScreenListener l : screenListeners) {
			l.screenWasRemoved();
		}
	}
	
	public void setNativePlugin(Object plugin) {
		this.nativePlugin = plugin;
	}
	
	/*
	 * 21-05-15: we could create a base 
	 * native plugin class 
	 */
	public Object getNativePlugin() {
		return this.nativePlugin;
	}
}








