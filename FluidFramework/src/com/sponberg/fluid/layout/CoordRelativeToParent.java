package com.sponberg.fluid.layout;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class CoordRelativeToParent extends Coord {

	final String edge;
	
	public CoordRelativeToParent(String edge, ArrayList<Subtractor> subtractors) {
		this.edge = edge;
		if (subtractors != null) {
			this.subtractors.addAll(subtractors);
		}
	}

	@Override
	public boolean isRelativeToParent() {
		return true;
	}

	@Override
	public String getRelativeEdge() {
		return edge;
	}
}
