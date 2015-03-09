package com.sponberg.fluid.test;

import java.util.Stack;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

import lombok.Data;

import com.sponberg.fluid.Callback;
import com.sponberg.fluid.layout.ModalView;
import com.sponberg.fluid.layout.UIService;

@Data
public class MockUIService implements UIService {

	CountDownLatch removeSplashLatch;
	
	Semaphore screenChangedSemphore = new Semaphore(0);
	
	Semaphore alertSemaphore = new Semaphore(0);
	
	Semaphore modalDialogSemaphore = new Semaphore(0);
	
	String alertTitle;
	String alertMessage;
	
	Stack<String> screenIds = new Stack<>();
	
	ModalView currentModalView;
	
	Object completeModalWithData;
	
	public MockUIService(CountDownLatch removeSplashLatch) {
		this.removeSplashLatch = removeSplashLatch;
	}
	
	public MockUIService() {
		
	}
	
	@Override
	public void pushLayout(String screenId) {
		screenIds.pop();
		screenChangedSemphore.release();
	}

	@Override
	public void popLayout() {
		screenIds.pop();
		screenChangedSemphore.release();
	}

	@Override
	public void showAlert(String title, String message) {
		alertTitle = title;
		alertMessage = message;
		alertSemaphore.release();
	}

	@Override
	public void setLayout(String screenId, boolean stack) {
		screenIds.add(screenId);
		screenChangedSemphore.release();
	}

	@Override
	public void closeCurrentLayout() {
		screenIds.pop();
		screenChangedSemphore.release();
	}

	@Override
	public void showModalView(ModalView modalView) {
		currentModalView = modalView;
		screenIds.add("modalView");
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
		currentModalView = null;
		screenIds.pop();
		modalDialogSemaphore.release();
	}

	@Override
	public void removeSplashScreen(String firstScreenId, boolean insteadShowCurrentScreenIfAny) {
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
		alertTitle = title;
		alertMessage = message;
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
	}

	public void hideKeyboard() {
		// TODO Auto-generated method stub
	}

	public void scrollToBottom(String viewPath, String viewId) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isOrientationLandscape() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
