package com.sponberg.fluid.android.layout;

import android.content.Context;
import android.widget.EditText;

import com.sponberg.fluid.layout.ViewPosition;

public class EditTextFluid extends EditText implements FluidViewAndroid {

	Bounds bounds;
	
	final ViewPosition view;
	
	String dataModelListenerId;
	
	public EditTextFluid(Context context, ViewPosition view) {
		super(context);
		this.view = view;
	}

	public ViewPosition getView() {
		return view;
	}

	public Bounds getBounds() {
		return bounds;
	}

	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		  this.setMeasuredDimension(bounds.width, bounds.height);
    }

	@Override
	public void cleanup() {
	}

	public String getDataModelListenerId() {
		return dataModelListenerId;
	}

	public void setDataModelListenerId(String dataModelListenerId) {
		this.dataModelListenerId = dataModelListenerId;
	}
	
}
