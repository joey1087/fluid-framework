package com.sponberg.fluid.layout;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class LengthFixed extends Length {

	final double l;
	
	public LengthFixed(double l) {
		this.l = l;
	}

	@Override
	public Double getFixedLength() {
		return l;
	}
	
}
