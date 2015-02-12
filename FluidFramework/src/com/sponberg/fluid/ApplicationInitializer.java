package com.sponberg.fluid;

public interface ApplicationInitializer extends PlatformSpecifier {

	public void initialize(final FluidApp app); // null means all platforms
	
}
