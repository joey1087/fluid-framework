package com.sponberg.fluid.test;

import java.util.Stack;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

import lombok.Data;

import com.sponberg.fluid.Callback;
import com.sponberg.fluid.layout.ModalView;
import com.sponberg.fluid.layout.UIService;
import com.sponberg.fluid.util.Logger;

@Data
public class MockUIService implements UIService {

	CountDownLatch removeSplashLatch;
	
	Semaphore screenChangedSemphore = new Semaphore(0);
	
	Semaphore alertSemaphore = new Semaphore(0);
	
	Semaphore modalDialogSemaphore = new Semaphore(0);
	
	String alertTitle;
	String alertMessage;
	String alertButtonTitle;
	
	Stack<String> screenIds = new Stack<>();
	
	ModalView currentModalView;
	
	Object completeModalWithData;
	
	boolean log = true;
	
	public String getCurrentScreenId() {
		//TODO : implement?
		return null;
	}
	
	public MockUIService(CountDownLatch removeSplashLatch) {
		this.removeSplashLatch = removeSplashLatch;
	}
	
	public MockUIService() {
		
	}
	
	@Override
	public void pushLayout(String screenId) {
		
		if (log) {
			Logger.debug(this, "pop layout " + screenIds.peek());
		}
		
		screenIds.pop();
		screenChangedSemphore.release();
	}

	@Override
	public void popLayout() {
		
		if (log) {
			Logger.debug(this, "pop layout " + screenIds.peek());
		}

		screenIds.pop();
		screenChangedSemphore.release();
	}

	@Override
	public void showAlert(String title, String message) {
		showAlert(title, message, "");
	}
	
	@Override
	public void showAlert(String title, String message, String buttonTitle) {
		alertTitle = title;
		alertMessage = message;
		alertButtonTitle = buttonTitle;
		alertSemaphore.release();
	}

	@Override
	public void setLayout(String screenId, boolean stack) {
		
		if (log) {
			Logger.debug(this, "setLayout " + screenId);
		}

		screenIds.add(screenId);
		screenChangedSemphore.release();
	}

	@Override
	public void closeCurrentLayout() {
		
		if (log) {
			Logger.debug(this, "closeCurrentLayout " + screenIds.peek());
		}

		screenIds.pop();
		screenChangedSemphore.release();
	}

	@Override
	public void showModalView(ModalView modalView) {
		
		currentModalView = modalView;
		
		if (modalView.getSystemId().equals(ModalView.FluidLayout) || modalView.getSystemId().equals(ModalView.FluidLayout)) {
			screenIds.add(modalView.getFluidData().toString());	
		} else {
			screenIds.add(modalView.getSystemId());
		}
		
		if (log) {
			Logger.debug(this, "showModalView " + screenIds.peek());
		}
		
		modalDialogSemaphore.release();
		if (completeModalWithData != null) {
			modalView.modalComplete(completeModalWithData);
		}
	}

	@Override
	public float computeHeightOfText(String text, float width, String fontName,
			float fontSizeInUnits) {
		return 50;
	}

	@Override
	public void dismissModalView(ModalView modalView) {
		
		if (log) {
			Logger.debug(this, "pop layout " + screenIds.peek());
		}

		currentModalView = null;
		screenIds.pop();
		modalDialogSemaphore.release();
	}

	@Override
	public void removeSplashScreen(String firstScreenId, boolean insteadShowCurrentScreenIfAny) {
		
		if (log) {
			Logger.debug(this, "removeSplashScreen to " + firstScreenId);
		}
		
		screenIds.clear();
		screenIds.add(firstScreenId);
		removeSplashLatch.countDown();
	}

	@Override
	public int getScreenWidthInPixels() {
		return 480;
	}

	@Override
	public int getScreenHeightInPixels() {
		return 640;
	}

	@Override
	public void showAlert(String title, String message, Callback callback) {
		showAlert(title, message, "", callback);
	}
	
	@Override
	public void showAlert(String title, String message, String buttonTitle, Callback callback) {
		alertTitle = title;
		alertMessage = message;
		alertButtonTitle = buttonTitle;
		alertSemaphore.release();
	}

	@Override
	public void refreshMenuButtons() {
	}

	@Override
	public void grabFocusForView(String viewId) {
	}

	@Override
	public void setLayoutStack(String... screenIds) {
		
		this.screenIds.clear();
		
		for (String screenId : screenIds) {
			
			if (log) {
				Logger.debug(this, "push layout " + screenId);
			}
			
			this.screenIds.push(screenId);
		}
		
		screenChangedSemphore.release();
	}

	@Override
	public void hideKeyboard() {
	}

	@Override
	public void scrollToBottom(final String viewPath, final String viewId) {
	}

	@Override
	public boolean isOrientationLandscape() {
		return false;
	}

	@Override
	public void pushLayout(String screenId, boolean animated) {
		// TODO Auto-generated method stub
		
	}
	
}
