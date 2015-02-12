package com.sponberg.fluid.layout;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class LengthCompute extends Length {

	@Override
	public boolean compute() {
		return true;
	}
	
}
