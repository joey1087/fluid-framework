package com.sponberg.fluid.layout;

import java.util.ArrayList;
import java.util.Collection;

import lombok.Getter;

import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.util.KeyValueList;

@Getter
public abstract class ViewBehavior {

	public static final String label = "com.sponberg.fluid.label";
	public static final String button = "com.sponberg.fluid.button";
	public static final String image = "com.sponberg.fluid.image";
	public static final String table = "com.sponberg.fluid.table";
	public static final String space = "com.sponberg.fluid.space";
	public static final String subview = "com.sponberg.fluid.subview";
	public static final String subviewRepeat = "com.sponberg.fluid.subviewRepeat";
	public static final String webview = "com.sponberg.fluid.webview";
	public static final String urlWebview = "com.sponberg.fluid.urlWebview";
	public static final String textfield = "com.sponberg.fluid.textfield";
	public static final String searchbar = "com.sponberg.fluid.searchbar";
	public static final String segmentedControl = "com.sponberg.fluid.segmentedControl";

	final String type;
	
	protected ArrayList<ColorWithCondition> backgroundColors = new ArrayList<>();
	
	private String unknownText = null;
	
	protected Integer cornerRadius;
	protected Integer cornerTopLeftRadius;
	protected Integer cornerTopRightRadius;
	protected Integer cornerBottomRightRadius;
	protected Integer cornerBottomLeftRadius;
	
	private Double borderSize;
	
	private Color borderColor;
	
	protected ViewBehavior(String type, KeyValueList properties) {
		this.type = type;
		parseKVL(properties);
	}
	
	protected void parseKVL(KeyValueList list) {
		if (list == null) {
			return;
		}
		parseBackgroundColors(list);
		if (list.contains("unknown-text")) {
			unknownText = list.getValue("unknown-text");
		}
		this.cornerRadius = getIntegerProperty("corner-radius", 0, list);
		this.cornerTopLeftRadius = getIntegerProperty("corner-top-left-radius", 0, list);
		this.cornerTopRightRadius = getIntegerProperty("corner-top-right-radius", 0, list);
		this.cornerBottomRightRadius = getIntegerProperty("corner-bottom-right-radius", 0, list);
		this.cornerBottomLeftRadius = getIntegerProperty("corner-bottom-left-radius", 0, list);
		
		this.borderSize = getSizeProperty("border-size", "0p", list);
		this.borderColor = getColorProperty("border-color", null, list);
	}
	
	private void parseBackgroundColors(KeyValueList properties) {
		if (!properties.contains("background-color")) {
			return;
		}
		for (KeyValueList kvl : properties.get("background-color")) {
			ColorWithCondition color = new ColorWithCondition();
			backgroundColors.add(color);
			color.color = GlobalState.fluidApp.getViewManager().getColor(kvl.getValue());
			if (kvl.contains("condition")) {
				color.condition = kvl.getValue("condition");
			}
		}
	}
	
	public Color getBackgroundColor(String dataModelPrefix) {
		for (ColorWithCondition i : backgroundColors) {
			if (i.condition == null) {
				return i.color;
			} else if (GlobalState.fluidApp.getDataModelManager().checkCondition(i.condition, dataModelPrefix)) {
				return i.color;
			}
		}
		return null;
	}
	
	public Layout getLayout(String layoutId) {
		return null;
	}

	public static String getStringProperty(String key, String defaultValue, KeyValueList properties) {
		if (!properties.contains(key)) {
			return defaultValue;
		} else {
			return properties.getValue(key);
		}
	}

	public static Boolean getBooleanProperty(String key, boolean defaultValue, KeyValueList properties) {
		if (!properties.contains(key)) {
			return defaultValue;
		} else {
			return properties.getValue(key).equalsIgnoreCase("true");
		}
	}

	public static Integer getIntegerProperty(String key, Integer defaultValue, KeyValueList properties) {
		if (!properties.contains(key)) {
			return defaultValue;
		} else {
			return Integer.parseInt(properties.getValue(key));
		}
	}

	public static Double getDoubleProperty(String key, Double defaultValue, KeyValueList properties) {
		if (!properties.contains(key)) {
			return defaultValue;
		} else {
			return Double.parseDouble(properties.getValue(key));
		}
	}

	public static String getFontFamilyName(String key, String defaultValue, KeyValueList properties) {
		if (properties == null || key == null) {
			return null;
		}
		
		if (!properties.contains(key)) {
			return defaultValue;
		} else {		
			String fontNameOrfontRefId = properties.getValue(key); /// font might have been specified using actual font name or a ref to an entry in fonts.txt
			
			String fontName = GlobalState.fluidApp.getViewManager().getFontFamilyName(fontNameOrfontRefId);
			
			if (fontName == null) { /// if fontNameOrfontRefId is not a ref to a font in fonts.txt
				fontName = fontNameOrfontRefId;
			}
			
			return fontName;
		}
	}
	
	public static String getFontStyle(String key, String defaultValue, KeyValueList properties) {
		if (properties == null || key == null) {
			return null;
		}
		
		if (!properties.contains(key)) {
			return defaultValue;
		} else {		
			String styleValueOrRefId = properties.getValue(key); 
			
			String fontStyle = GlobalState.fluidApp.getViewManager().getFontStyle(styleValueOrRefId);
			
			if (fontStyle == null) {
				fontStyle = styleValueOrRefId;
			}
			
			return fontStyle;
		}
	}
	
	public static Double getFontSizeProperty(String key, Double defaultValue, KeyValueList properties) {
		if (!properties.contains(key)) {
			return defaultValue;
		} else {
			String size = getSizeFromLayoutVariablesOrSizes(key, properties);
			return Double.parseDouble(size);
		}
	}

	public static Double getSizeProperty(String key, String defaultValue, KeyValueList properties) {
		if (!properties.contains(key)) {
			if (defaultValue == null) {
				return null;
			} else {
				return GlobalState.fluidApp.sizeToPixels(defaultValue);
			}
		} else {
			String size = getSizeFromLayoutVariablesOrSizes(key, properties);
			return GlobalState.fluidApp.sizeToPixels(size);
		}
	}
		
	private static String getSizeFromLayoutVariablesOrSizes(String key,
			KeyValueList properties) {
		String size;
		KeyValueList kvl = properties.getWithValue("layout-variables", "sizes");
		if (kvl != null && kvl.contains(key)) {
			size = kvl.getValue(key);
		} else {
			// If not in sizes, getSize returns the literal
			size = GlobalState.fluidApp.getViewManager().getSize(properties.getValue(key));
		}
		return size;
	}
	
	public static Double getUnitsToPixelsProperty(String key, Double defaultValue, KeyValueList properties) {
		if (!properties.contains(key)) {
			return defaultValue;
		} else {
			return GlobalState.fluidApp.unitsToPixels(Double.parseDouble(properties.getValue(key)));
		}
	}
	
	public Color getColorProperty(String key, Color defaultValue, KeyValueList properties) {
		if (!properties.contains(key)) {
			return defaultValue;
		} else {
			return GlobalState.fluidApp.getViewManager().getColor(properties.getValue(key));
		}
	}
	
	public void validateConstraints(Constraints c) {
		
		if (c.getWidth().compute() && !supportsWidthCompute()) {
			throw new RuntimeException("compute width not supported for type");
		}
		
		if (c.getHeight().compute() && !supportsHeightCompute()) {
			throw new RuntimeException("compute width not supported for type");
		}
		
	}
	
	protected boolean supportsWidthCompute() {
		return false;
	}
	
	protected boolean supportsHeightCompute() {
		return false;
	}	
	
	public float computeHeight(boolean landscape, String dataModelPrefix, View view, boolean useCache) {
		throw new RuntimeException("Not implemented in " + this.getClass());
	}

	public void precomputeViewPositions(boolean landscape,
			String precomputePrefix, ViewPosition view,
			String viewPathPrefixView, Collection<ViewPosition> newViewPositions) {
		// do nothing
	}
	
	public boolean isViewFactorySetsBackground() {
		return false;
	}
	
	public boolean isShouldBePresentedToUI() {
		return true;
	}
	
	public static class ColorWithCondition {
		
		Color color;
		
		String condition;
		
	}

}
