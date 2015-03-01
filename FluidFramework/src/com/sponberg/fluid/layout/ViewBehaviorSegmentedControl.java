package com.sponberg.fluid.layout;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.sponberg.fluid.util.KeyValueList;

@ToString
@Getter
@Setter
public class ViewBehaviorSegmentedControl extends ViewBehavior {

	private String[] options;

	private Color textAndLineColor;
	
	private Color selectedTextColor;

	private Color backgroundColor;

	private Color selectedBackgroundColor;

	private String selectedIndexKey;

	private Double androidPadding;

	private Double fontSize;
	
	public ViewBehaviorSegmentedControl(KeyValueList properties) {
		super(ViewBehavior.segmentedControl, properties);

		List<String> optionsList = properties.getValues("options");
		if (optionsList == null || optionsList.size() == 0) {
			throw new RuntimeException("options must be specified for segmented-control");
		}

		options = optionsList.toArray(new String[optionsList.size()]);

		this.textAndLineColor = getColorProperty("text-and-line-color", null, properties);
		this.selectedTextColor = getColorProperty("selected-text-color", new Color(255, 255, 255, 255), properties);
		this.backgroundColor = getColorProperty("background-color", new Color(0, 0, 0, 0), properties);
		this.selectedBackgroundColor = getColorProperty("background-color", textAndLineColor, properties);

		this.selectedIndexKey = getStringProperty("selected-index", null, properties);

		this.androidPadding = getSizeProperty("android-padding", "0", properties);
		
		this.fontSize = getFontSizeProperty("font-size", null, properties);
	}

}
