package com.sponberg.fluid.layout;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@EqualsAndHashCode
public class Constraints {

	public Coord x, y;
	
	public Coord x2, y2;
	
	public Length width, height;
	
}
