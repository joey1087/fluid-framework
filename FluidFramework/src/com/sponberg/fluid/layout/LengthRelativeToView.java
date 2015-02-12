package com.sponberg.fluid.layout;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class LengthRelativeToView extends Length {

	final double ratio;
	
	final String viewId;
	
	public LengthRelativeToView(double ratio, String viewId) {
		this.ratio = ratio;
		this.viewId = viewId;
	}

	public LengthRelativeToView(double ratio, String viewId, ArrayList<Subtractor> subtractors) {
		super.setSubtractors(subtractors);
		this.ratio = ratio;
		this.viewId = viewId;
	}

	@Override
	public boolean relativeToView() {
		return true;
	}

	@Override
	public String getRelativeId() {
		return viewId;		
	}

	@Override
	public double getRatio() {
		return ratio;
	}
	
}
