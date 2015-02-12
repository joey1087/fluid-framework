package com.sponberg.fluid.android.layout;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.widget.LinearLayout;

import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.android.layout.FluidViewFactoryRegistration.FluidViewBuilderInfo;
import com.sponberg.fluid.layout.Layout;
import com.sponberg.fluid.layout.ViewBehaviorSubviewRepeat;
import com.sponberg.fluid.layout.ViewPosition;
import com.sponberg.fluid.util.Logger;

public class SubviewRepeatView extends LinearLayout implements FluidViewAndroid {

	Bounds bounds;

	ViewBehaviorSubviewRepeat viewBehavior;
	
	String dataModelPrefix;
	
	String subviewDataModelPrefix;
	
	boolean precompute = false;
	
	ArrayList<CustomLayout> layouts = new ArrayList<>();
	
	String viewPath;
	
	public SubviewRepeatView(FluidViewBuilderInfo info, ViewBehaviorSubviewRepeat viewBehavior) {
		
		super(info.context);
		
		super.setOrientation(LinearLayout.VERTICAL);
		
		this.viewBehavior = viewBehavior;
		
		this.viewPath = info.viewPath;
		
		this.dataModelPrefix = info.dataModelPrefix;
		
		Layout layout = GlobalState.fluidApp.getLayout(viewBehavior.getSubview());
		
		this.precompute = layout.isPrecomputedPositions();
		
		if (dataModelPrefix == null || dataModelPrefix.equals("")) {
			subviewDataModelPrefix = viewBehavior.getKey() + ".";
		} else {
			subviewDataModelPrefix = dataModelPrefix + "." + viewBehavior.getKey() + ".";
		}
		
		int size = GlobalState.fluidApp.getDataModelManager().getValueList(dataModelPrefix, viewBehavior.getKey()).size();
		String dataModelPrefix = info.dataModelPrefix;
		if (dataModelPrefix == null || dataModelPrefix.equals("")) {
			dataModelPrefix = viewBehavior.getKey() + ".";
		} else {
			dataModelPrefix += "." + viewBehavior.getKey() + ".";
		}
		for (int index = 0; index < size; index++) {			
			CustomLayout customLayout = new CustomLayout(info.context, null,
					layout, info.bounds, subviewDataModelPrefix + index, null, info.viewPath + "." + index, true, info.customLayout, false, null, null, false, true, info.insideTableView);
			super.addView(customLayout);
			layouts.add(customLayout);
		}
	}

    @Override
	@SuppressLint("DrawAllocation")
	protected void onMeasure(int widthZ, int heightZ) {
    	
    	super.onMeasure(widthZ, heightZ);
    	
    	Layout l = GlobalState.fluidApp.getLayout(viewBehavior.getSubview());
    	
    	List<?> valueList = GlobalState.fluidApp.getDataModelManager().getValueList(dataModelPrefix, viewBehavior.getKey());
    	if (valueList != null) {
			int size = valueList.size();
			double y = 0;
			for (int index = 0; index < size; index++) {			
				CustomLayout layout = (CustomLayout) super.getChildAt(index);				

				boolean landscape = GlobalState.fluidApp.getUiService().isOrientationLandscape();
				double height = -1;
				if (precompute) {
					String viewPath = layout.getViewPath();
					ViewPosition vp = GlobalState.fluidApp.getPrecomputeLayoutManager().getViewPosition(viewPath);
					if (vp == null) {
						Logger.warn(this, "If using precompute, viewposition must be calculated for {}", viewPath);
					} else {
						height = vp.getHeight();
					}
				}
				if (height == -1) {
					height = l.calculateHeight(landscape, bounds.width, subviewDataModelPrefix + index);
				}
				
				layout.setBounds(new Bounds(0, (int) Math.ceil(y), bounds.width, (int) Math.ceil(height)));
				
				y += height;
			}
    	}
    	
    	this.setMeasuredDimension(bounds.width, bounds.height);
    }

	@Override
	public void cleanup() {
		for (CustomLayout layout : layouts) {
			layout.cleanup();
		}
		layouts.clear();
	} 
	
	@Override
	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}
	
}
