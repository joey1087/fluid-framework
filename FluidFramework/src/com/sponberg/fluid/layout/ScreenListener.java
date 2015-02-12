package com.sponberg.fluid.layout;

public interface ScreenListener {

	public void screenWillAppear();
	
	public void screenDidAppear();
	
	public void screenDidDisappear(); // hidden, but still on view stack

	public void screenWasRemoved(); // deallocated

}
