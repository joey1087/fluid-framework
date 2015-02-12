package com.sponberg.fluid;

public interface CallbackFailable {

	public void run(String msg);
	
	public void fail(String msg);
	
}
