package com.sponberg.fluid.layout;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class LengthFillRatio extends Length {

	final double ratio;
	
	public LengthFillRatio(double ratio) {
		this.ratio = ratio;
	}

	@Override
	public boolean fillRatio() {
		return true;
	}

	@Override
	public double getRatio() {
		return ratio;		
	}
	
}
