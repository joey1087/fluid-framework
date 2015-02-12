package com.sponberg.fluid.layout;

import lombok.Data;

import com.sponberg.fluid.GlobalState;

@Data
public class ViewPosition {

	final String id;

	final String key;

	final String visibleCondition;

	ViewBehavior viewBehavior;

	final double x, y, width, height;

	final boolean visible;

	final String viewPathKey;

	public ViewPosition(String viewPath, View view) {
		this.id = view.id;
		this.key = view.key;
		this.visibleCondition = view.visibleCondition;
		this.viewBehavior = view.viewBehavior;
		this.x = view.x;
		this.y = view.y;
		this.width = view.width;
		this.height = view.height;
		this.visible = view.visible;
		this.viewPathKey = viewPath + "." + view.getId();
	}

	public ViewPosition(String viewPathKey, int height) {

		// For use with subview repeat section

		this.height = height;
		this.viewPathKey = viewPathKey;

		this.id = null;
		this.key = null;
		this.visibleCondition = null;
		this.viewBehavior = null;
		this.x = -1;
		this.y = -1;
		this.width = -1;
		this.visible = true;
	}

	public ViewPosition(String id, String key, String visibleCondition,
			double x, double y, double width,
			double height, boolean visible, String viewPathKey) {
		this.id = id;
		this.key = key;
		this.visibleCondition = visibleCondition;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.visible = visible;
		this.viewPathKey = viewPathKey;
	}

	public String getValue(String prefix, String keys, String messageFormat) {
		String unkownText = viewBehavior.getUnknownText();
		return GlobalState.fluidApp.getDataModelManager().getValue(prefix, keys, messageFormat, unkownText);
	}

	public void setValue(String prefix, String key, Object value) {
		GlobalState.fluidApp.getDataModelManager().setValue(prefix, key, value);
	}

}
