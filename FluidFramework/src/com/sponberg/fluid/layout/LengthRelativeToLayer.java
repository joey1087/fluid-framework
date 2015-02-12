package com.sponberg.fluid.layout;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class LengthRelativeToLayer extends Length {

	final double ratio;

	final int layerIndex;
	
	public LengthRelativeToLayer(double ratio, int layerIndex) {
		this.ratio = ratio;
		this.layerIndex = layerIndex;
	}
	
	public LengthRelativeToLayer(double ratio, int layerIndex, ArrayList<Subtractor> subtractors) {
		this(ratio, layerIndex);
		super.setSubtractors(subtractors);
	}
	
	@Override
	public boolean relativeToLayer() {
		return true;
	}

	@Override
	public int getLayerIndex() {
		return layerIndex;
	}
	
	@Override
	public double getRatio() {
		return ratio;
	}
	
}
