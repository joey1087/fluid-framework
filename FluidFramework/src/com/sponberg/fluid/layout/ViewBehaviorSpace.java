package com.sponberg.fluid.layout;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.sponberg.fluid.util.KeyValueList;

@ToString
@Getter
@Setter
public class ViewBehaviorSpace extends ViewBehavior {

	public ViewBehaviorSpace(KeyValueList properties) {
		super(ViewBehavior.space, properties);
	}

	@Override
	public boolean isShouldBePresentedToUI() {
		return backgroundColors.size() > 0 || getBorderSize() > 0;
	}

}
