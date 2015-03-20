package com.sponberg.fluid.layout;

import java.util.ArrayList;
import java.util.HashMap;

import com.sponberg.fluid.ApplicationInitializer;
import com.sponberg.fluid.FluidApp;
import com.sponberg.fluid.util.KeyValueList;

public class ViewManager implements ApplicationInitializer {

	HashMap<String, Screen> screens = new HashMap<>();

	HashMap<String, Layout> layouts = new HashMap<>();

	HashMap<String, TableLayout> tableLayouts = new HashMap<>();

	KeyValueList colorsByName;

	KeyValueList sizesByName;
	
	KeyValueList fontsByName;
	
	KeyValueList fontStyles;

	ArrayList<Tab> tabs = new ArrayList<>();

	private double baseUnit = 0;

	private double devicePixelToPixelMultiplier = 1;

	private double devicePixelActualToPixelMultiplier = 1;

	public double getBaseUnit() {
		return baseUnit;
	}

	public void setBaseUnit(double baseUnit) {
		this.baseUnit = baseUnit;
	}

	public void addScreen(Layout layout) {
		screens.put(layout.getId(), new Screen(layout));
	}

	public void addLayout(Layout layout) {
		layouts.put(layout.getId(), layout);
	}

	public void addTableLayout(TableLayout layout) {
		tableLayouts.put(layout.getId(), layout);
	}

	public void addTab(Tab tab) {
		tabs.add(tab);
	}

	public ArrayList<Tab> getTabs() {
		return tabs;
	}

	@Override
	public void initialize(FluidApp app) {
		if (baseUnit == 0) {
			throw new RuntimeException("Must set baseUnit");
		}
	}

	public Screen getScreen(String screenId) {
		return screens.get(screenId);
	}

	public Layout getLayout(String layoutId) {
		return layouts.get(layoutId);
	}

	public TableLayout getTableLayout(String tableLayoutId) {
		return tableLayouts.get(tableLayoutId);
	}

	public void setColorsByName(KeyValueList colorsByName) {
		this.colorsByName = colorsByName;
	}

	public void setSizesByName(KeyValueList sizesByName) {
		this.sizesByName = sizesByName;
	}
	
	public void setFontsByName(KeyValueList fontsByName) {
		this.fontsByName = fontsByName;
	}
	
	public void setFontStyles(KeyValueList fontStyles) {
		this.fontStyles = fontStyles;
	}
	
	public Color getColor(String name) {
		if (colorsByName.contains(name)) {
			return Color.colorFromString(colorsByName.getValue(name));
		} else {
			return Color.colorFromString(name);
		}
	}
	
	public int getInt(String intString) {
		return Integer.parseInt(intString);
	}
	
	public String getFontFamilyName(String refId) {
		if (fontsByName != null && fontsByName.contains(refId)) {
			return fontsByName.getValue(refId);
		} else {
			return null;
		}
	}
	
	public String getFontStyle(String refId) {
		if (fontStyles != null && fontStyles.contains(refId)) {
			return fontStyles.getValue(refId);
		} else {
			return null;
		}
	}
	
	public String getSpecifiedDefaultFontFamilyName() { 
		if (fontsByName != null && fontsByName.contains("default-font")) {
			return fontsByName.getValue("default-font");
		} else {
			return null;
		}
	}
	
	public String getSpecifiedDefaultFontStyle() {
		if (fontStyles != null && fontStyles.contains("default-style")) {
			return fontStyles.getValue("default-style");
		} else {
			return null;
		}
	}
	
	public String getSize(String name) {
		if (sizesByName.contains(name)) {
			return sizesByName.getValue(name);
		} else {
			return name;
		}
	}

	public double sizeToPixels(String size) {
		if (size.endsWith("pa")) {
			// Pixels Actual
			return pixelsActualToPixels(Double.parseDouble(size.substring(0, size.length() - 2)));
		} else if (size.endsWith("p")) {
			// Pixels
			return pixelsToPixels(Double.parseDouble(size.substring(0, size.length() - 1)));
		} else {
			// Units
			return unitsToPixels(Double.parseDouble(size));
		}
	}

	public double unitsToPixels(double units) {
		return units * baseUnit;
	}

	public double pixelsToPixels(double pixels) {
		return pixels * devicePixelToPixelMultiplier;
	}

	public double pixelsActualToPixels(double pixels) {
		return pixels * devicePixelActualToPixelMultiplier;
	}

	public double pixelsToUnits(double pixels) {
		return pixels / baseUnit;
	}

	public double fontPointsToPixels(double points) {
		double units = points / 72 * 25.4 * devicePixelActualToPixelMultiplier;
		return unitsToPixels(units);
	}

	public double unitsToFontPoints(double units) {
		return units * 72 / 25.4 / devicePixelActualToPixelMultiplier;
	}

	@Override
	public String[] getSupportedPlatforms() {
		return null;
	}

	public double getDevicePixelMultiplier() {
		return devicePixelToPixelMultiplier;
	}

	public double getDevicePixelActualMultiplier() {
		return devicePixelActualToPixelMultiplier;
	}

	public void setDevicePixelMultiplier(double devicePixelMultiplier) {
		this.devicePixelToPixelMultiplier = devicePixelMultiplier;
	}

	public void setDevicePixelActualMultiplier(double devicePixelActualMultiplier) {
		this.devicePixelActualToPixelMultiplier = devicePixelActualMultiplier;
	}

}
