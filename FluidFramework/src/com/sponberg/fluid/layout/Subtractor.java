package com.sponberg.fluid.layout;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Subtractor {

	double ratioRelativeToView;
	
	String relativeToView;
	
	double fixed;
	
	public Subtractor(double ratioRelativeToView, String relativeToView) {
		this.ratioRelativeToView = ratioRelativeToView;
		this.relativeToView = relativeToView;
	}
	
	public Subtractor(double fixed) {
		this.fixed = fixed;
	}
	
	public boolean isRelativeToView() {
		return relativeToView != null;
	}
}
