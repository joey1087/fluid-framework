package com.sponberg.fluid.android.activity;

import java.util.HashMap;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.Window;

import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.android.FluidFrameworkAndroidApp;
import com.sponberg.fluid.android.layout.Bounds;
import com.sponberg.fluid.android.layout.CustomLayout;
import com.sponberg.fluid.layout.Layout;
import com.sponberg.fluid.layout.ModalView;
import com.sponberg.fluid.layout.Screen;

public abstract class LauncherActivity extends ActionBarActivity  {

	public static final String kFromNotification = "com.sponberg.fluid.android.LauncherActivity.kFromNotification";
	
	public static final String kPushNotificationData = "com.sponberg.fluid.android.LauncherActivity.kPushNotificationData";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setSplashScreen();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	
        	if (extras.getBoolean(LauncherActivity.kFromNotification)) {
        		
        		@SuppressWarnings("unchecked")
				HashMap<String, String> pushNotificationData = (HashMap<String, String>) extras.getSerializable(LauncherActivity.kPushNotificationData);
        		GlobalState.fluidApp.getLaunchOptionsManager().setPushNotification(pushNotificationData);
        	}
        }
		
		((FluidFrameworkAndroidApp) this.getApplicationContext()).setLauncherActivity(this);
		
		//getWindow().requestFeature(Window.FEATURE_NO_TITLE); taken from theme
	    
		return;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}	
	
	@Override
	protected void onResume() {
	    
		super.onResume();
	    
		((FluidFrameworkAndroidApp) this.getApplicationContext()).setLauncherActivity(this);
		
		//((FluidFrameworkAndroidApp) this.getApplicationContext()).setInForeground(true);
		
		launchAppActivity();
	}
	
	@Override
	protected void onPause() {
		
		super.onPause();
		
		//((FluidFrameworkAndroidApp) this.getApplicationContext()).setInForeground(false);
		
	}
	
	protected abstract void onResumeClient();
	
	protected void launchAppActivity() {

		new Thread() {
			
			public void run() {

				try {
					// Without this sleep, android will have a white splash screen instead of showing our loading screen
					Thread.sleep(200);
				} catch (InterruptedException e) {
				}
				
				Runnable r = new Runnable() {
					public void run() {
						
						onResumeClient(); 
						
						boolean loading = ((FluidFrameworkAndroidApp) getApplicationContext()).initializeAndLoad(null);

						if (!loading) {
							((FluidFrameworkAndroidApp) getApplicationContext()).reload();
						}
					}
				};
				GlobalState.fluidApp.getSystemService().runOnUiThread(r);					
			}
		}.start();
	}

	protected abstract void setSplashScreen();
	
	public void showModalView(final ModalView modalView) {

		if (!GlobalState.fluidApp.isInitialized()) {
			new Thread() {
				public void run() {
					while (!GlobalState.fluidApp.isInitialized()) {
						try {
							Thread.sleep(300);
						} catch (InterruptedException e) {
						}						
					}
					runOnUiThread(new Runnable() {
						public void run() {
							showModalView(modalView);
						}
					});
				}
			}.start();
			return;
		}
		
		boolean landscape = false;
		
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
					if (modalView.getUserSelection() != null)
						modalView.modalComplete(modalView.getUserSelection());
					else
						modalView.modalCanceled();
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
			
		}	
	}

}
