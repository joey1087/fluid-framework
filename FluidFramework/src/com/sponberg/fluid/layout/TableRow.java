package com.sponberg.fluid.layout;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class TableRow {

	private long id;
	
	private String layout;
	
	private String key;

	private boolean listenToDataModelChanges = false;
	
}
