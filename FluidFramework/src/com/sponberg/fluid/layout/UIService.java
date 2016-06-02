package com.sponberg.fluid.layout;

import java.util.List;

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
	
	public void showAlert(String title, String message, String buttonText);

	public void showAlert(String title, String message, String buttonText, Callback callback);

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
	
	/**
	 * 
	 * @author joey
	 *
	 */
	public static class OverflowMenuDescriptor {
		
		/**
		 * 
		 * @author joey
		 *
		 */
		public static class OverflowMenuButton {
			
			final String buttonTitle;
			
			public String getButtonTitle() {
				
				return buttonTitle;
			}
			
			public OverflowMenuButton(String buttonTitle) {
				
				this.buttonTitle = buttonTitle;
			}
		}
		
		final List<OverflowMenuButton> buttons;
		
		/**
		 * 
		 * @return
		 */
		public List<OverflowMenuButton> getButtons() {
			
			return buttons;
		}
		
		/**
		 * Use this if you need to pass extra information 
		 * into the platform implementor.
		 */
		final Object extra;
		
		/**
		 * 
		 * @return
		 */
		public Object getExtra() {
			
			return extra;
		}
		
		/**
		 * If the menu should contain a dismiss button 
		 * where when the user taps on it will dismiss
		 * the overflow menu.
		 */
		final boolean showDismissButton;
		
		/**
		 * 
		 * @return
		 */
		public boolean isShowDismissButton() {
			
			return showDismissButton;
		}
		
		public OverflowMenuDescriptor(List<OverflowMenuButton> buttons) {
			
			this(buttons, true, null);
		}
		
		public OverflowMenuDescriptor(List<OverflowMenuButton> buttons, boolean showDismissButton) {
			
			this(buttons, showDismissButton, null);
		}
		
		public OverflowMenuDescriptor(List<OverflowMenuButton> buttons, Object extra) {
			
			this(buttons, true, extra);
		}
		
		private OverflowMenuDescriptor(List<OverflowMenuButton> buttons, boolean showDissmissButton, Object extra) {
			
			this.buttons = buttons;
			this.extra = extra;
			this.showDismissButton = showDissmissButton;
		}
	}
	
	/**
	 * 
	 * @author joey
	 *
	 */
	public interface IOverflowMenuHandler {
		
		public void handleUserSelectOverflowMenuButton(int index);
		
		public void hanldeUserSelectDismissOverflowMenu();
		
		/*
		 * we might want to add one for when user taps outside of the menu ?
		 */
	}
	
	/**
	 * 
	 * @param menuDescriptor
	 * @param handler
	 * @return true if the service can show the overflow menu, false if its already showing one. We dont support 
	 * overlaying overflow menus.
	 */
	public boolean showOverflowMenu(OverflowMenuDescriptor menuDescriptor, IOverflowMenuHandler handler);
}









