package com.sponberg.fluid.android.layout;

import android.content.Context;
import android.view.ViewGroup;

public class ViewBounded extends ViewGroup {

	Bounds bounds = new Bounds(0, 0, 0, 0);
	
	CustomLayout customLayout;
	
	public ViewBounded(Context context, CustomLayout customLayout) {
		super(context);
		this.customLayout = customLayout;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		super.setMeasuredDimension(bounds.width, bounds.height);
	}

	public Bounds getBounds() {
		return bounds;
	}

	public void setBounds(Bounds bounds) {
		this.bounds.setTo(bounds);
	}

	Bounds measuringBounds = new Bounds(0, 0, 0, 0);
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		// Do nothing, because CustomLayout takes care of this
	}
	
}
