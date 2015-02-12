package com.sponberg.fluid.android.layout;

import android.content.Context;
import android.view.View;

public class ViewFluidSpace extends View implements FluidViewAndroid {

	Bounds bounds;
	
	public ViewFluidSpace(Context context) {
		super(context);
	}

	@Override
	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(bounds.width, bounds.height);
	}

	@Override
	public void cleanup() {
	}

}
