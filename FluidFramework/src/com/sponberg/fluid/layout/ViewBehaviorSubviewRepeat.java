package com.sponberg.fluid.layout;

import java.util.Collection;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.util.KeyValueList;

@ToString
@Getter
@Setter
public class ViewBehaviorSubviewRepeat extends ViewBehavior {

	private final String subview;

	private final String key;

	public ViewBehaviorSubviewRepeat(KeyValueList properties) {
		super(ViewBehavior.subviewRepeat, properties);
		this.subview = getStringProperty("subview", null, properties);
		this.key = getStringProperty("key", null, properties);
		if (this.key == null) {
			throw new RuntimeException("key is required");
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
		List<?> list = GlobalState.fluidApp.getDataModelManager().getValueList(dataModelPrefix, key);
		int size = (list == null) ? 0 : list.size();
		double height = 0;
		if (dataModelPrefix == null || dataModelPrefix.equals("")) {
			dataModelPrefix = key + ".";
		} else {
			dataModelPrefix += "." + key + ".";
		}
		for (int index = 0; index < size; index++) {
			height += GlobalState.fluidApp.getLayout(subview).calculateHeight(landscape, width.floatValue(), dataModelPrefix + index, useCache);
		}
		return (float) height;
	}

	@Override
	public void precomputeViewPositions(boolean landscape,
			String precomputePrefix, ViewPosition view,
			String viewPathPrefixView, Collection<ViewPosition> newViewPositions) {

		String layoutId = ((ViewBehaviorSubviewRepeat) view.getViewBehavior()).getSubview();
		Layout subviewLayout = GlobalState.fluidApp.getLayout(layoutId);

		if (subviewLayout.isPrecomputedPositions()) {

			String precomputePrefixView = DataModelManager.getFullKey(precomputePrefix, view.getKey());

			List<?> list = GlobalState.fluidApp.getDataModelManager().getValueList(precomputePrefixView);
			int size = (list == null) ? 0 : list.size();

			precomputePrefixView += ".";
			viewPathPrefixView += ".";

			for (int index = 0; index < size; index++) {

				String viewPathPrefixViewSection = viewPathPrefixView + index;

				double subviewHeight = PrecomputeLayoutManager
						.precomputeViewPositionsFor(subviewLayout, landscape,
								viewPathPrefixViewSection, precomputePrefixView
										+ index, view, null, newViewPositions);
				ViewPosition subviewPosition = new ViewPosition(viewPathPrefixViewSection,
						(int) Math.round(subviewHeight));
				newViewPositions.add(subviewPosition);
				GlobalState.fluidApp.getPrecomputeLayoutManager()
						.setViewPosition(viewPathPrefixViewSection,
								subviewPosition);
			}
		}
	}

}
