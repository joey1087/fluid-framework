package com.sponberg.fluid.layout;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class LengthRelativeToParent extends Length {

	final double ratio;

	public LengthRelativeToParent(double ratio) {
		this.ratio = ratio;
	}
	
	public LengthRelativeToParent(double ratio, ArrayList<Subtractor> subtractors) {
		super.setSubtractors(subtractors);
		this.ratio = ratio;
	}
	
	@Override
	public boolean relativeToParent() {
		return true;
	}

	@Override
	public double getRatio() {
		return ratio;
	}
	
}
