package com.sponberg.fluid.android.layout;

import android.content.Context;
import android.widget.ScrollView;

public class ScrollViewBounded extends ScrollView {

	Bounds bounds = new Bounds(0, 0, 0, 0);

	public ScrollViewBounded(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		super.setMeasuredDimension(bounds.width, bounds.height);
	}
}
