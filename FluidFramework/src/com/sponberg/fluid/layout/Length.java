package com.sponberg.fluid.layout;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Length {

	ArrayList<Subtractor> subtractors = new ArrayList<>();
	
	public void addSubtractor(Subtractor length) {
		subtractors.add(length);
	}
	
	public boolean fill() {
		return false;
	}

	public boolean compute() {
		return false;
	}

	public boolean fillRatio() {
		return false;
	}

	public boolean equal() {
		return false;
	}
	
	public boolean relativeToView() {
		return false;
	}
	
	public boolean relativeToParent() {
		return false;
	}
	
	public boolean relativeToRow() {
		return false;
	}
	
	public boolean relativeToLayer() {
		return false;
	}
	
	public boolean summation() {
		return false;
	}
	
	public boolean isDynamic() {
		return 	fill() || 
				fillRatio() || 
				equal() || 
				relativeToView() || 
				relativeToParent() || 
				summation() || 
				compute() || 
				relativeToRow() ||
				relativeToLayer();
	}
	
	public Double getFixedLength() {
		throw new RuntimeException("Not implemented");
	}
	
	public String getRelativeId() {
		throw new RuntimeException("Not implemented");		
	}

	public double getRatio() {
		throw new RuntimeException("Not implemented");		
	}
	
	public int getLayerIndex() {
		throw new RuntimeException("Not implemented");		
	}
	
	public String[] getSummationOf() {
		throw new RuntimeException("Not implemented");
	}
	
}
