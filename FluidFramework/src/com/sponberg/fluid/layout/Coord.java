package com.sponberg.fluid.layout;

import java.util.ArrayList;

public abstract class Coord {

	final ArrayList<Subtractor> subtractors = new ArrayList<>();
	
	public void addSubtractor(Subtractor length) {
		subtractors.add(length);
	}
	
	public boolean isRelativeToView() {
		return false;
	}	

	public boolean isRelativeToParent() {
		return false;
	}	

	public boolean isDynamic() {
		return 	isRelativeToView() ||
				isRelativeToParent();
	}
	
	public Double getFixed() {
		throw new RuntimeException("Not implemented");
	}
	
	public String getRelativeId() {
		throw new RuntimeException("Not implemented");		
	}

	public String getRelativeEdge() {
		throw new RuntimeException("Not implemented");		
	}

}
