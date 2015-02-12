package com.sponberg.fluid.android.layout;

import java.util.ArrayList;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.layout.ActionListener.EventInfo;
import com.sponberg.fluid.layout.ViewBehaviorSegmentedControl;

public class FluidRadioGroup extends RadioGroup implements FluidViewAndroid {

	Bounds bounds;
	
	private String viewPath;
	
	protected int selectedIndex = 0;
	
	ArrayList<RadioButton> buttons = new ArrayList<>();
	
	CustomLayout rootCustomLayout;
	
	public FluidRadioGroup(Context context, ViewBehaviorSegmentedControl viewBehavior, String viewPath, Double androidPadding, CustomLayout rootCustomLayout) {
		super(context);
		this.viewPath = viewPath;
		this.rootCustomLayout = rootCustomLayout;
		
		this.setOrientation(RadioGroup.HORIZONTAL);
		this.setGravity(Gravity.CENTER_HORIZONTAL);
		
		int index = 0;
		for (String option : viewBehavior.getOptions()) {
			
			if (index > 0) {
				this.addView(createSpace(context, androidPadding));
			}
			
			RadioButton btn = new RadioButton(context);
			buttons.add(btn);
			btn.setText(option);
			this.addView(btn);
			
			if (index == 0)
				btn.setChecked(true);
			
			final int indexFinal = index;
			btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					selectedIndex = indexFinal;
					userTapped();
				}				
			});
			
			index++;
		}
	}

	protected View createSpace(Context context, Double androidPadding) {
		
		Bounds bounds = new Bounds(0, 0, androidPadding.intValue(), 1);
		
		ViewFluidSpace space = new ViewFluidSpace(context);
		space.bounds = bounds;
		return space;
	}
	
    protected void onMeasure(int width, int height) {
    	super.onMeasure(width, height);
    	this.setMeasuredDimension(bounds.width, bounds.height);
    }

	public Bounds getBounds() {
		return bounds;
	}

	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}
    
	public void userTapped() {
		
		if (!rootCustomLayout.isUserActivityEnabled()) {
			return;
		}
		
		EventInfo eventInfo = new EventInfo();
		eventInfo.setUserInfo(selectedIndex);
		GlobalState.fluidApp.getEventsManager().userTapped(viewPath, eventInfo);
	}
	
	public void setSelectdIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
		for (int index = 0; index < buttons.size(); index++) {
			buttons.get(index).setChecked(index == selectedIndex);
		}
	}
	
	@Override
	public void cleanup() {
	}

	public String getViewPath() {
		return viewPath;
	}

	public void setViewPath(String viewPath) {
		this.viewPath = viewPath;
	}

}
