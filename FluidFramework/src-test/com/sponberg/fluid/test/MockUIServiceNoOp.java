package com.sponberg.fluid.test;

import java.util.concurrent.CountDownLatch;

import com.sponberg.fluid.Callback;
import com.sponberg.fluid.layout.ModalView;

public class MockUIServiceNoOp extends MockUIService {

	public MockUIServiceNoOp(CountDownLatch removeSplashLatch) {
		super(removeSplashLatch);
	}
	
	public MockUIServiceNoOp() {
		
	}
	
	@Override
	public void pushLayout(String screenId) {
	}

	@Override
	public void popLayout() {
	}

	@Override
	public void showAlert(String title, String message) {
	}

	@Override
	public void setLayout(String screenId, boolean stack) {
	}

	@Override
	public void closeCurrentLayout() {
	}

	@Override
	public void showModalView(ModalView modalView) {
	}

	@Override
	public void dismissModalView(ModalView modalView) {
	}

	@Override
	public void showAlert(String title, String message, Callback callback) {
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
	
}
