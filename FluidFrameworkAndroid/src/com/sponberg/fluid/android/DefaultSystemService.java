package com.sponberg.fluid.android;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.sponberg.fluid.CallbackFailable;
import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.SystemService;
import com.sponberg.fluid.android.util.DeviceUtil;
import com.sponberg.fluid.util.Logger;

public class DefaultSystemService implements SystemService {
	
	private final FluidFrameworkAndroidApp app;

	static boolean googlePlayAvailable = true;
	
	ScheduledExecutorService runOnUiThreadService = Executors.newSingleThreadScheduledExecutor();

	protected String appVersion = null;
	
	public DefaultSystemService(FluidFrameworkAndroidApp app) {
		this.app = app;
	}
	
	private Context getCurrentActivityContext() {
		return app.getCurrentActivityContext();
	}
	
	@Override
	public void initiatePhoneCall(String phoneNumber) {
		Intent intent = new Intent(Intent.ACTION_CALL);
		//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setData(Uri.parse("tel:" + phoneNumber));
		//getCurrentActivityContext().startActivity(intent);
		app.getCurrentActivity().startActivity(intent);
	}

	@Override
	public void runOnUiThread(final Runnable runnable) {
		
		// Return really quick by sending to an already existing thread
		
		Runnable r = new Runnable() {
			@Override
			public void run() {
				runOnUiThreadHelper(runnable);
			}
		};
		runOnUiThreadService.schedule(r, 0, TimeUnit.SECONDS);
	}
		
	public void runOnUiThreadHelper(final Runnable runnable) {
		
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(runnable);
	}

	@Override
	public boolean isOnUiThread() {
		return Looper.myLooper() == Looper.getMainLooper();
	}
	
	@Override
	public void getDeviceNotificationId(final CallbackFailable callback) {
		
		if (DeviceUtil.isEmulator()) {
			callback.run("emulator");
			return;
		} else if (!googlePlayAvailable) {
    		callback.fail("Please install Google Play services. "
    				+ "(If your device supports Google Play, you could be behind a firewall, "
					+ "or you don't have an internet connection)");
    		return;
		}
		
		String registrationId = FluidAndroidAppPreferences.getRegistrationId(getCurrentActivityContext());
		if (registrationId.isEmpty()) {
			registerInBackground(callback);
		} else {
			callback.run(registrationId);
		}
	}

	private void registerInBackground(final CallbackFailable callback) {
		
		Logger.debug(this, "registering with GCM");
		
		new Thread() {
			@Override
			public void run() {
				
				int attempt = 0;
				
				int tries = 3;
				
				while (true) {
				
					attempt++;
					
					try {
		            	
		            	GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getCurrentActivityContext());
		            	String projectNumber = GlobalState.fluidApp.getSetting("push-notifications", "settings", "google-project-number");
		            	
		                String regId = gcm.register(projectNumber);
	
		                FluidAndroidAppPreferences.storeRegistrationId(getCurrentActivityContext(), regId);
		                
		                callback.run(regId);
		                break;
		                
		            } catch (IOException ex) {	            	
	
		            	if (attempt < tries) {
		            		
		            		try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
							}
		            		
		            	} else {
			            	Logger.error(this, ex);			            	
		            		callback.fail("Google Play is not enabled. "
		            				+ "(If your device supports Google Play, you could be behind a firewall, "
		            				+ "or you don't have an internet connection)");	
		            		break;
		            	}
		            }
				}
			}
		}.start();

	}

	@Override
	public void initiateEmail(String[] emails, String subject) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/rfc822");
		intent.putExtra(Intent.EXTRA_EMAIL, emails);
		if (subject != null) {
			intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		}
		getCurrentActivityContext().startActivity(Intent.createChooser(intent, "Send Email"));
	}

	public static boolean isGooglePlayAvailable() {
		return googlePlayAvailable;
	}

	public static void setGooglePlayAvailable(boolean available) {
		googlePlayAvailable = available;
	}

	@Override
	public String getAppVersion() {
		
		if (appVersion != null) {
			return appVersion;
		}
		
		if (getCurrentActivityContext() == null) {
			return null;
		}
		
		try {
			PackageInfo pInfo = getCurrentActivityContext().getPackageManager().getPackageInfo(getCurrentActivityContext().getPackageName(), 0);
			appVersion = pInfo.versionName;
			return appVersion;
		} catch (NameNotFoundException e) {
			Logger.error(this, e);
			return null;
		}
	}

	@Override
	public String getDeviceModel() {
		return android.os.Build.PRODUCT;
	}

	@Override
	public String getDeviceName() {
		return android.os.Build.MANUFACTURER;
	}

	@Override
	public String getDeviceSystemName() {
		return "Android " + android.os.Build.VERSION.SDK_INT;
	}

	@Override
	public String getDeviceSystemVersion() {
		return android.os.Build.VERSION.RELEASE;
	}

	@Override
	public void openBrowserWith(String url) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		getCurrentActivityContext().startActivity(browserIntent);
	}

	@Override
	public void openAppStorePageForRating() {
		Uri uri = Uri.parse("market://details?id=" + getCurrentActivityContext().getPackageName());
		Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
		try {
			getCurrentActivityContext().startActivity(goToMarket);
		} catch (ActivityNotFoundException e) {
			getCurrentActivityContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getCurrentActivityContext().getPackageName())));
		}
	}

}
