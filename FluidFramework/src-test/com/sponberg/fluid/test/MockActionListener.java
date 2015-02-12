package com.sponberg.fluid.test;

import com.sponberg.fluid.layout.ActionListener;

public class MockActionListener implements ActionListener {

	int userTapped, userChangedValueTo, userCancelled;
	
	@Override
	public void userTapped(EventInfo eventInfo) {
		userTapped++;
	}

	@Override
	public void userChangedValueTo(EventInfo eventInfo, Object value) {
		userChangedValueTo++;
	}

	@Override
	public void userCancelled() {
		userCancelled++;
	}

}
