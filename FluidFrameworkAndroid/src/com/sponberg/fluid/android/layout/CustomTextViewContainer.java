package com.sponberg.fluid.android.layout;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class CustomTextViewContainer extends ViewGroup implements FluidViewAndroid {

	private Bounds bounds;
	
	CustomTextView view;
	
	String dataModelListenerId;
	
	public CustomTextViewContainer(Context context, CustomTextView view, Bounds bounds) {
        super(context);
        this.bounds = bounds;
        this.view = view;
        addView(view);
    }

	public Bounds getBounds() {
		return bounds;
	}

	@Override
	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
		if (bounds.getHeight() != 0) {
			view.setBounds(bounds);
		}
	}

	public View getView() {
		return getChildAt(0);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		view.measure(widthMeasureSpec, heightMeasureSpec);
		this.setMeasuredDimension(bounds.width, bounds.height);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {		
		
		if (view.needsSizeAndPosition) {
			view.forceMeasure();
		}
		
		view.layout(0, (int) view.getAdjustY(), bounds.width, (int) view.getAdjustY() + view.getMeasuredHeight());	
	}

	public String getDataModelListenerId() {
		return dataModelListenerId;
	}

	public void setDataModelListenerId(String dataModelListenerId) {
		this.dataModelListenerId = dataModelListenerId;
	}

	@Override
	public void cleanup() {
	}
	
}
