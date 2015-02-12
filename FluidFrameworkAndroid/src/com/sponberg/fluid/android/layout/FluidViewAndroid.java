package com.sponberg.fluid.android.layout;

import com.sponberg.fluid.layout.FluidView;

public interface FluidViewAndroid extends FluidView {

	public void setBounds(Bounds bounds);
	
	public void layout(int l, int r, int t, int b);
	
	public void measure(int widthSpec, int heightSpec);

	public void cleanup();
	
}
