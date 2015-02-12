package com.sponberg.fluid;

public interface SystemService {

	public void initiatePhoneCall(String phoneNumber);

	public void initiateEmail(String[] emails, String subject);
	
	public void runOnUiThread(Runnable runnable);
	
	public boolean isOnUiThread();
	
	public void getDeviceNotificationId(CallbackFailable callback);
	
	public void openBrowserWith(String url);
	
	public String getDeviceModel();
	
	public String getDeviceName();
	
	public String getDeviceSystemName();
	
	public String getDeviceSystemVersion();

	public String getAppVersion();
	
}
