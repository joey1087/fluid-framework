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

	private Color color;

	private String selectedIndexKey;

	private Double androidPadding;

	public ViewBehaviorSegmentedControl(KeyValueList properties) {
		super(ViewBehavior.segmentedControl, properties);

		List<String> optionsList = properties.getValues("options");
		if (optionsList == null || optionsList.size() == 0) {
			throw new RuntimeException("options must be specified for segmented-control");
		}

		options = optionsList.toArray(new String[optionsList.size()]);

		this.color = getColorProperty("color", null, properties);

		this.selectedIndexKey = getStringProperty("selected-index", null, properties);

		this.androidPadding = getSizeProperty("android-padding", "0", properties);
	}

}
