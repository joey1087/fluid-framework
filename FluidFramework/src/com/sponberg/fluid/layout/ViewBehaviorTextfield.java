package com.sponberg.fluid.layout;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.util.KeyValueList;

@ToString
@Getter
@Setter
public class ViewBehaviorTextfield extends ViewBehavior {

	public static final String kKeyboardDefault = "default";
	public static final String kKeyboardAlphabet = "alphabet";
	public static final String kKeyboardEmail = "email";
	public static final String kKeyboardUrl = "url";
	public static final String kKeyboardNumber = "number";
	public static final String kKeyboardPhone = "phone";

	public static final String kBorderStyleDefault = "default";
	public static final String kBorderStyleNone = "none";

	String label;

	String keyboard;

	String borderStyle;

	boolean dismissKeyboardWithTap;

	boolean multiLine;

	boolean autoCorrect;

	boolean password;

	private Color placeholderTextColor;

	private Color textEnabledColor;

	private Color textDisabledColor;

	private String enabledKey;

	private String formattedPlaceholder;

	private String capitalize;

	private Color androidLineColor; // Use border-style: none to turn off the bracketed line for android

	protected String fontFamilyName;
	protected String fontStyle; 
	protected Double fontSize;
	
	public ViewBehaviorTextfield(KeyValueList properties) {
		super(ViewBehavior.textfield, properties);
		this.label = getStringProperty("label", null, properties);
		this.keyboard = getStringProperty("keyboard", "default", properties);
		this.dismissKeyboardWithTap = getBooleanProperty("dismissKeyboardWithTap", true, properties);
		this.borderStyle = getStringProperty("border-style", null, properties);
		this.multiLine = getBooleanProperty("multi-line", false, properties);
		this.autoCorrect = getBooleanProperty("auto-correct", this.keyboard.equals("default"), properties);
		this.capitalize = getStringProperty("capitalize", null, properties);
		this.password = getBooleanProperty("password", false, properties);
		this.placeholderTextColor = getColorProperty("placeholder-text-color", null, properties);
		this.enabledKey = getStringProperty("enabled-key", null, properties);
		this.formattedPlaceholder = getStringProperty("formatted-placeholder", null, properties);
		this.androidLineColor = getColorProperty("android-line-color", null, properties);
		this.textEnabledColor = getColorProperty("text-enabled-color", null, properties);
		this.textDisabledColor = getColorProperty("text-disabled-color", null, properties);
		
		String specifiedDefaultFontName = GlobalState.fluidApp.getViewManager().getSpecifiedDefaultFontFamilyName();	         
		this.fontFamilyName = getFontFamilyName("font-family", specifiedDefaultFontName, properties); 
		
		String specifiedDefaultFontStyle = GlobalState.fluidApp.getViewManager().getSpecifiedDefaultFontStyle();
		this.fontStyle = getFontStyle("font-style", specifiedDefaultFontStyle, properties);
		
		this.fontSize = getFontSizeProperty("font-size", null, properties);
	}

	static boolean getBoolean(String dismissKeyboardWithTap, boolean defaultValue) {
		if (dismissKeyboardWithTap == null) {
			return defaultValue;
		} else if (dismissKeyboardWithTap.equalsIgnoreCase("true")) {
			return true;
		} else if (dismissKeyboardWithTap.equalsIgnoreCase("false")) {
			return false;
		} else {
			return defaultValue;
		}
	}

	@Override
	public boolean isViewFactorySetsBackground() {
		return true;
	}

	public boolean isEnabled(String dataModelPrefix) {

		if (this.enabledKey == null) {
			return true;
		} else {
			return GlobalState.fluidApp.getDataModelManager().checkCondition(enabledKey, dataModelPrefix);
		}
	}

}
