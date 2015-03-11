package com.sponberg.fluid.parser;

import java.io.IOException;

import com.sponberg.fluid.ApplicationInitializer;
import com.sponberg.fluid.FluidApp;
import com.sponberg.fluid.HttpServiceWrapper.MapMode;
import com.sponberg.fluid.util.KVLReader;
import com.sponberg.fluid.util.KeyValueList;

public class SettingsParser implements ApplicationInitializer {

	@Override
	public void initialize(FluidApp app) {

		String s = app.getResourceService().getResourceAsString("settings", "settings.txt");
		
		if (s == null) {
			throw new RuntimeException("Unable to find settings.txt");
		}
		
		String colors = app.getResourceService().getResourceAsString("", "colors.txt");
		
		if (colors == null) {
			throw new RuntimeException("Unable to find colors.txt");
		}
		
		String sizes = app.getResourceService().getResourceAsString("", "sizes.txt");
		
		if (sizes == null) {
			throw new RuntimeException("Unable to find sizes.txt");
		}
		
		String fontsFileName = "fonts@" + app.getPlatform() + ".txt";
		String fontsNames = app.getResourceService().getResourceAsString("", fontsFileName);
		if (fontsNames == null) {
			fontsFileName = "fonts.txt";
			fontsNames = app.getResourceService().getResourceAsString("", fontsFileName);
		}
		
		String fontStylesFileName = "font-styles@" + app.getPlatform() + ".txt";
		String fontStyles = app.getResourceService().getResourceAsString("", fontStylesFileName);
		if (fontStyles == null) {
			fontStylesFileName = "font-styles.txt";
			fontStyles = app.getResourceService().getResourceAsString("", fontStylesFileName);
		}
		
		try {
			
			KVLReader reader = new KVLReader(s);
			
			// Check if there are platform specific overrides		
			String platformSpecific = "settings@" + app.getPlatform() + ".txt";
			String data = app.getResourceService().getResourceAsString("settings", platformSpecific);
			if (data != null) {
				KVLReader platformReader = new KVLReader(data);
				reader.overwriteSettingsFrom(platformReader);
			}
			
			if (app.getSettingsOverride() != null) {
				// Programmatically, from unit testing
				parseModeSettings(app, reader, app.getSettingsOverride());
			} else if (reader.contains("mode") && !reader.getValue("mode").equalsIgnoreCase("release")) {
				parseModeSettings(app, reader, reader.getValue("mode"));
			}
			
			app.setSettings(reader);
			
			setDefaults(app);
			
			KVLReader kvlColors = new KVLReader(colors);
			// Check if there are platform specific overrides for colors
			platformSpecific = "colors@" + app.getPlatform() + ".txt";
			data = app.getResourceService().getResourceAsString("", platformSpecific);
			if (data != null) {
				KVLReader platformReader;
				try {
					platformReader = new KVLReader(data);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				kvlColors.overwriteSettingsFrom(platformReader);
			}
			setColors(app, kvlColors);
			
			setSizes(app, new KVLReader(sizes));
					
			if (fontsNames != null) {
				KVLReader kvlFontsNames = new KVLReader(fontsNames);
				setFontsNames(app, kvlFontsNames);
			}
			
			if (fontStyles != null) {
				KVLReader kvlFontStyles = new KVLReader(fontStyles);
				setFontStyles(app, kvlFontStyles);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}	
	}
	
	private void parseModeSettings(FluidApp app, KVLReader reader, String mode) {
		
		String fileName = "settings-" + mode + ".txt";
		String s = app.getResourceService().getResourceAsString("settings", fileName);
		
		if (s == null) {
			throw new RuntimeException("Unable to find " + fileName);
		}
		
		KVLReader modeReader;
		try {
			modeReader = new KVLReader(s);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		reader.overwriteSettingsFrom(modeReader);
		
		// Check if there are platform specific overrides		
		String platformSpecific = "settings-" + mode + "@" + app.getPlatform() + ".txt";
		String data = app.getResourceService().getResourceAsString("settings", platformSpecific);
		if (data != null) {
			KVLReader platformReader;
			try {
				platformReader = new KVLReader(data);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			reader.overwriteSettingsFrom(platformReader);
		}
	}
	
	private void setDefaults(FluidApp app) {
	
		app.setDefaults(app.getSettings().get("defaults"));
		
		String mode = app.getDefault("http", "request-params-map-mode");
		if (mode == null) {
			mode = "";
		}
		if (mode.equalsIgnoreCase("jsonify") ) {
			app.setHttpServiceRequestParametersMapMode(MapMode.Jsonify);
		} else if (mode.equalsIgnoreCase("bracketify")) {
			app.setHttpServiceRequestParametersMapMode(MapMode.Bracketify);
		}
	}

	private void setColors(FluidApp app, KeyValueList kvl) {
		
		app.getViewManager().setColorsByName(kvl);
	}
	
	private void setSizes(FluidApp app, KeyValueList kvl) {
		
		app.getViewManager().setSizesByName(kvl);
	}
	
	private void setFontsNames(FluidApp app, KeyValueList kvl) {
		app.getViewManager().setFontsByName(kvl);
	}
	
	private void setFontStyles(FluidApp app, KeyValueList kvl) {
		app.getViewManager().setFontStyles(kvl);
	}
	
	@Override
	public String[] getSupportedPlatforms() {
		return null;
	}
}
