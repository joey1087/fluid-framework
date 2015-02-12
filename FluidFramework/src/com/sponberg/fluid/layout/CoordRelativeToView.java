package com.sponberg.fluid.layout;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class CoordRelativeToView extends Coord {

	final String edge;
	
	final String viewId;
	
	public CoordRelativeToView(String edge, String viewId, ArrayList<Subtractor> subtractors) {
		this.edge = edge;
		this.viewId = viewId;
		this.subtractors = subtractors;
	}

	@Override
	public boolean isRelativeToView() {
		return true;
	}

	@Override
	public String getRelativeId() {
		return viewId;		
	}

	@Override
	public String getRelativeEdge() {
		return edge;
	}
}
