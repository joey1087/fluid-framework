package com.sponberg.fluid.layout;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class LengthEquals extends Length {

	final String equalTo;

	public LengthEquals(String equalTo) {
		this.equalTo = equalTo;
	}
	
	@Override
	public boolean equal() {
		return true;
	}
	
}
