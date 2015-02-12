package com.sponberg.fluid.layout;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.sponberg.fluid.FluidApp;
import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.util.KeyValueList;
import com.sponberg.fluid.util.LRUCache;

@ToString
@Getter
@Setter
public class ViewBehaviorLabel extends ViewBehaviorBaseLabel {

	Double minHeight;

	public Color textColorPressed;

	public ViewBehaviorLabel(KeyValueList properties) {
		super(ViewBehavior.label, properties);
		minHeight = super.getDoubleProperty("min-h", null, properties);
		if (minHeight != null) {
			minHeight = GlobalState.fluidApp.unitsToPixels(minHeight.doubleValue());
		}
		this.textColorPressed = getColorProperty("text-color-pressed", null, properties);
	}

	@Override
	protected boolean supportsHeightCompute() {
		return true;
	}

	static LRUCache<String, Float> cache = new LRUCache<>(100);

	@Override
	public float computeHeight(boolean landcape, String dataModelPrefix, View view, boolean useCache) {
		String text = this.getText();
		if (view.getKey() != null) {
			String unknownText = this.getUnknownText();
			text = GlobalState.fluidApp.getDataModelManager().getValue(dataModelPrefix, view.getKey(), text, unknownText);
		}
		Double width = view.getWidth();
		if (width == null) {
			throw new RuntimeException("View's width must be set before calling computeHeight");
		}
		if (fontSize == null) {
			throw new RuntimeException("Must set font-size with compute height");
		}

		String cacheKey = null;
		if (FluidApp.useCaching) {
			cacheKey = text + "|" + width + "|" + fontSize.floatValue();
			Float v = cache.get(cacheKey);
			if (v != null) {
				return v.floatValue();
			}
		}

		float computedHeight = GlobalState.fluidApp.getUiService().computeHeightOfText(text, width.floatValue(), null, this.fontSize.floatValue());
		if (minHeight != null && minHeight > computedHeight) {
			computedHeight = minHeight.floatValue();
		}

		if (FluidApp.useCaching) {
			cache.put(cacheKey, computedHeight);
		}

		return computedHeight;
	}

}
