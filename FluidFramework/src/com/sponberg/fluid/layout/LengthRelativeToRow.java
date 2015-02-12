package com.sponberg.fluid.layout;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class LengthRelativeToRow extends Length {

	final double ratio;

	public LengthRelativeToRow(double ratio) {
		this.ratio = ratio;
	}
	
	public LengthRelativeToRow(double ratio, ArrayList<Subtractor> subtractors) {
		super.setSubtractors(subtractors);
		this.ratio = ratio;
	}
	
	@Override
	public boolean relativeToRow() {
		return true;
	}

	@Override
	public double getRatio() {
		return ratio;
	}
	
}
