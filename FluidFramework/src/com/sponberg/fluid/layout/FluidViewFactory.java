package com.sponberg.fluid.layout;

import java.util.HashMap;

public class FluidViewFactory {

	HashMap<String, FluidViewBuilder> registeredViewTypes = new HashMap<>();
	
	public void registerView(String type, FluidViewBuilder builder) {
		registeredViewTypes.put(type, builder);
	}
	
	public Object createView(String type, ViewPosition view, Object userInfo) {
		FluidViewBuilder builder = registeredViewTypes.get(type);
		if (builder == null) {
			throw new RuntimeException("There is no builder registered for " + type);
		}
		return registeredViewTypes.get(type).createFluidView(view, userInfo);
	}
	
	public void updateView(String type, Object fluidView, ViewPosition view, Object userInfo) {
		FluidViewBuilder builder = registeredViewTypes.get(type);
		if (builder == null) {
			throw new RuntimeException("There is no builder registered for " + type);
		}
		registeredViewTypes.get(type).updateFluidView(fluidView, view, userInfo);
	}
	
	public void cleanupView(String type, Object fluidView) {
		FluidViewBuilder builder = registeredViewTypes.get(type);
		if (builder == null) {
			throw new RuntimeException("There is no builder registered for " + type);
		}
		registeredViewTypes.get(type).cleanupFluidView(fluidView);		
	}
	
	public static interface FluidViewBuilder {
		
		public Object createFluidView(ViewPosition view, Object userInfo);
		
		public void updateFluidView(Object fluidView, ViewPosition view, Object userInfo);
		
		public void cleanupFluidView(Object fluidView);
	}
	
}
