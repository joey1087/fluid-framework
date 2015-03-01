package com.sponberg.fluid.layout;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class LengthFromDataModel extends Length {

	protected String key;
	
	public LengthFromDataModel(String key) {
		this.key = key;
	}
	
	@Override
	public boolean isFromDataModel() {
		return true;
	}
	
	@Override
	public String getDataModelKey() {
		return key;
	}
	
}
