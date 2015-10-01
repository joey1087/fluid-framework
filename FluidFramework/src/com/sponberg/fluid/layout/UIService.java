package com.sponberg.fluid.layout;

import com.sponberg.fluid.Callback;

public interface UIService {

	public void removeSplashScreen(String firstScreenId, boolean insteadShowCurrentScreenIfAny);
	
	public void pushLayout(String screenId);
	
	public void pushLayout(String screenId, boolean animated);
	
	public void popLayout();

	public void setLayout(String screenId, boolean stack);
	
	public void showModalView(ModalView modalView);
	
	public void dismissModalView(ModalView modalView);
	
	public void closeCurrentLayout();
	
	public void showAlert(String title, String message);

	public void showAlert(String title, String message, Callback callback);

	public float computeHeightOfText(String text, float width, String fontName, float fontSizeInUnits);

	public int getScreenWidthInPixels();
	
	public int getScreenHeightInPixels();
	
	public void refreshMenuButtons();
	
	public void grabFocusForView(String viewId);
	
	public void hideKeyboard();
	
	public void setLayoutStack(String... screenIds);
	
	public void scrollToBottom(String viewPath, String viewId);
	
	public boolean isOrientationLandscape();
	
	public String getCurrentScreenId();
	
}
