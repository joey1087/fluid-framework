package com.sponberg.fluid.layout;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class LengthSummation extends Length {

	final String[] sumOf;

	public LengthSummation(String... sumOf) {
		this.sumOf = sumOf;
	}
	
	@Override
	public boolean summation() {
		return true;
	}
	
	@Override
	public String[] getSummationOf() {
		return sumOf;
	}

}
