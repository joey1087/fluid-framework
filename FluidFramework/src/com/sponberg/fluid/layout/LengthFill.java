package com.sponberg.fluid.layout;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class LengthFill extends Length {

	@Override
	public boolean fill() {
		return true;
	}
	
}
