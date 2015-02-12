package com.sponberg.fluid.test;

import java.util.HashMap;

import lombok.Data;

import com.sponberg.fluid.CallbackFailable;
import com.sponberg.fluid.SystemService;

@Data
public class MockSystemService implements SystemService {

	public HashMap<String, Integer> phoneCallCounts = new HashMap<>();
	
	public HashMap<String, Integer> emailCounts = new HashMap<>();
	
	boolean getDeviceNotificationIdEnabled = true;
	
	public void initiatePhoneCall(String phoneNumber) {
		Integer i = phoneCallCounts.get(phoneNumber);
		if (i == null)
			i = 0;
		i++;
		phoneCallCounts.put(phoneNumber, i);
	}

	public void runOnUiThread(final Runnable runnable) {
		new Thread() {
			public void run() {
				runnable.run();
			}
		}.start();
	}

	@Override
	public void getDeviceNotificationId(final CallbackFailable callback) {
		
		if (!getDeviceNotificationIdEnabled) {
			return;
		}
		
		new Thread() {
			public void run() {
				callback.run("testNotificationId");
			}
		}.start();
	}

	@Override
	public void initiateEmail(String[] emails, String subject) {
		for (String email : emails) {
			Integer i = emailCounts.get(email);
			if (i == null)
				i = 0;
			i++;
			emailCounts.put(email, i);
		}
	}

	@Override
	public boolean isOnUiThread() {
		return true;
	}

	@Override
	public void openBrowserWith(String url) {
	}

	@Override
	public String getDeviceModel() {
		return "mock";
	}

	@Override
	public String getDeviceName() {
		return "mock";
	}

	@Override
	public String getDeviceSystemName() {
		return "mock";
	}

	@Override
	public String getDeviceSystemVersion() {
		return "mock";
	}

	@Override
	public String getAppVersion() {
		return "mock";
	}

}
