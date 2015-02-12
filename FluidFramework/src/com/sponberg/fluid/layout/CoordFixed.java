package com.sponberg.fluid.layout;

public class CoordFixed extends Coord {

	final double l;
	
	public CoordFixed(double l) {
		this.l = l;
	}

	@Override
	public Double getFixed() {
		return l;
	}
	
}
