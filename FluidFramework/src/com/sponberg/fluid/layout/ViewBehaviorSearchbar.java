package com.sponberg.fluid.layout;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.sponberg.fluid.util.KeyValueList;

@ToString
@Getter
@Setter
public class ViewBehaviorSearchbar extends ViewBehavior {

	private String text;

	private Color textColor;

	private boolean showCancelButton;

	private String placeholderText;

	public ViewBehaviorSearchbar(KeyValueList properties) {
		super(ViewBehavior.searchbar, properties);
		this.text = getStringProperty("text", null, properties);
		this.textColor = getColorProperty("text-color", null, properties);
		showCancelButton = getBooleanProperty("show-cancel-button", true, properties);
		placeholderText = getStringProperty("placeholder", null, properties);
	}

}
