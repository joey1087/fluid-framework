package com.sponberg.app.form;

import com.sponberg.fluid.layout.TableRowWithId;

public class WhenOption implements TableRowWithId {
	
	long id;
	
	String option;
	
	public WhenOption(String option, long id) {
		this.option = option;
		this.id = id;
	}

	@Override
	public String toString() {
		return option;
	}

	@Override
	public Long getFluidTableRowObjectId() {
		return id;
	}
	
}