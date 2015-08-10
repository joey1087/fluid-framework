package com.sponberg.fluid.layout;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@EqualsAndHashCode
public class Tab {

	String tabId;
	
	String label;
	
	String image;
	
	String selectedImage;
	
	String screenId;
	
}
