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
	
	private Color textColor;
	
	private Color lineColor;
	
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

		if (properties.contains("text-and-line-color")) {
			
			Color color = getColorProperty("text-and-line-color", null, properties);
			this.textColor = color;
			this.lineColor = color;
		} else {
			
			this.textColor = getColorProperty("text-color", null, properties);
			this.lineColor = getColorProperty("line-color", null, properties);
		}
		
		this.selectedTextColor = getColorProperty("selected-text-color", null, properties);
		this.backgroundColor = getColorProperty("background-color", new Color(0, 0, 0, 0), properties);
		this.selectedBackgroundColor = getColorProperty("background-color", lineColor, properties);

		this.selectedIndexKey = getStringProperty("selected-index", null, properties);

		this.androidPadding = getSizeProperty("android-padding", "0", properties);
		
		this.fontSize = getFontSizeProperty("font-size", null, properties);
	}

}
