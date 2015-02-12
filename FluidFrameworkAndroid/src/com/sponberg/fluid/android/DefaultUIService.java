package com.sponberg.fluid.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Configuration;
import android.text.Spannable;
import android.view.Display;

import com.sponberg.fluid.Callback;
import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.android.layout.CustomTextView;
import com.sponberg.fluid.android.layout.CustomTextView.BoundsWithFontSize;
import com.sponberg.fluid.android.layout.FluidViewFactoryRegistration;
import com.sponberg.fluid.layout.ModalView;
import com.sponberg.fluid.layout.UIService;

public class DefaultUIService implements UIService {
	
	private final FluidFrameworkAndroidApp app;

	public DefaultUIService(FluidFrameworkAndroidApp app) {
		this.app = app;
	}
	
	private Context getCurrentActivityContext() {
		return app.getCurrentActivity();
	}
	
	@Override
	public void popLayout() {
		app.popLayout();
	}

	@Override
	public void pushLayout(String screenId) {
		app.pushLayout(screenId);
	}

	@Override
	public void showAlert(final String title, final String message) {
		
		if (!GlobalState.fluidApp.getSystemService().isOnUiThread()) {
			
			Runnable r = new Runnable() {
				@Override
				public void run() {
					showAlert(title, message);
				}
			};
			GlobalState.fluidApp.getSystemService().runOnUiThread(r);
			return;
		}
		
		new AlertDialog.Builder(getCurrentActivityContext())
	    .setTitle(title)
	    .setMessage(message)
	    .setPositiveButton(android.R.string.ok, null)
	    .show();
	}

	@Override
	public void showAlert(final String title, final String message, final Callback callback) {
		
		if (!GlobalState.fluidApp.getSystemService().isOnUiThread()) {
			
			Runnable r = new Runnable() {
				@Override
				public void run() {
					showAlert(title, message, callback);
				}
			};
			GlobalState.fluidApp.getSystemService().runOnUiThread(r);
			return;
		}
		
		AlertDialog dialog = 
			new AlertDialog.Builder(getCurrentActivityContext())
		    .setTitle(title)
		    .setMessage(message)
		    .setPositiveButton(android.R.string.ok, null)
		    .show();
		
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface di) {
				if (callback != null) {
					callback.run("dismissed");
				}
			}			
		});
	}
	
	@Override
	public void setLayout(String screenId, boolean stack) {
		app.setLayout(screenId, stack);
	}

	@Override
	public void closeCurrentLayout() {
		app.closeCurrentLayout();
	}

	@Override
	public void showModalView(ModalView modalView) {
		app.showModalView(modalView);
	}

	@Override
	public void dismissModalView(ModalView modalView) {
		app.dismissModalView(modalView);
	}
	
	@Override
	public float computeHeightOfText(String text, float width, String fontName, float fontSizeInUnits) {
		Spannable s = FluidViewFactoryRegistration.createAttributedText(text);
		BoundsWithFontSize boundsWithFontSize = 
			CustomTextView.computeHeightOfText(s, (int) Math.ceil(width), Float.MAX_VALUE, fontName, 
				Double.valueOf(fontSizeInUnits), null, null, getCurrentActivityContext());
		return boundsWithFontSize.getComputedHeight();
	}

	@Override
	public void removeSplashScreen(String firstScreenId, boolean insteadShowCurrentScreenIfAny) {
		app.removeSplashScreen(firstScreenId, insteadShowCurrentScreenIfAny);
	}

	@Override
	public int getScreenWidthInPixels() {
		Display display = app.getCurrentActivity().getWindowManager().getDefaultDisplay();
		return display.getWidth();
	}

	@Override
	public int getScreenHeightInPixels() {
		Display display = app.getCurrentActivity().getWindowManager().getDefaultDisplay();
		return display.getHeight();
	}

	@Override
	public void refreshMenuButtons() {
		GlobalState.fluidApp.getSystemService().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				app.getCurrentActivity().invalidateOptionsMenu();
			}
		});
	}

	@Override
	public void grabFocusForView(String viewId) {
		app.grabFocusForView(viewId);
	}

	@Override
	public void hideKeyboard() {
		app.hideKeyboard();
	}

	@Override
	public void setLayoutStack(String... screenIds) {
		app.setLayoutStack(screenIds);
	}

	@Override
	public void scrollToBottom(final String viewPath, final String viewId) {
		app.scrollToBottom(viewPath, viewId);
	}

	@Override
	public boolean isOrientationLandscape() {
		return app.getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
	}
	
}
