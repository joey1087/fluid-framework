package com.sponberg.fluid.layout;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.util.KeyValueList;

@ToString
@Getter
@Setter
public class ViewBehaviorSubview extends ViewBehavior {

	private String subview;

	private final String key;

	private final String keyPrefix;

	public ViewBehaviorSubview(KeyValueList properties) {
		super(ViewBehavior.subview, properties);
		this.subview = getStringProperty("subview", null, properties);
		this.key = getStringProperty("key", null, properties);

		if (this.key != null) {
			keyPrefix = (key.equals(".")) ? "" : key + ".";
		} else {
			keyPrefix = "";
		}
	}

	@Override
	protected boolean supportsHeightCompute() {
		return true;
	}

	@Override
	public float computeHeight(boolean landscape, String dataModelPrefix, View view, boolean useCache) {
		Double width = view.getWidth();
		if (width == null) {
			throw new RuntimeException("View's width must be set before calling computeHeight");
		}
		if (dataModelPrefix == null || dataModelPrefix.equals("")) {
			dataModelPrefix = keyPrefix;
		} else if (!keyPrefix.equals("")) {
			dataModelPrefix += "." + keyPrefix;
		}
		return (float) GlobalState.fluidApp.getLayout(subview).calculateHeight(landscape, width.floatValue(), dataModelPrefix, useCache);
	}

	@Override
	public void precomputeViewPositions(boolean landscape,
			String precomputePrefix, ViewPosition view,
			String viewPathPrefixView, Collection<ViewPosition> newViewPositions) {

		String layoutId = ((ViewBehaviorSubview) view.getViewBehavior()).getSubview();
		Layout subviewLayout = GlobalState.fluidApp.getLayout(layoutId);
		if (subviewLayout.isPrecomputedPositions()) {

			String key = view.getKey();
			if (key == null) {
				key = ".";
			}
			String precomputePrefixView = DataModelManager.getFullKey(precomputePrefix, key);
			PrecomputeLayoutManager.precomputeViewPositionsFor(subviewLayout, landscape, viewPathPrefixView, precomputePrefixView, view, null, newViewPositions);
		}
	}
}
