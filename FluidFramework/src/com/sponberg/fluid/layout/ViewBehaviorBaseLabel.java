package com.sponberg.fluid.layout;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.util.KeyValueList;

@ToString
@Getter
@Setter
public class ViewBehaviorBaseLabel extends ViewBehavior {
	
	public static String kAlignLeft = "left";
	public static String kAlignCenter = "center";
	public static String kAlignRight = "right";
	
	public static String kVerticalAlignTop = "top";
	public static String kVerticalAlignMiddle = "middle";
	public static String kVerticalAlignBottom = "bottom";
	
	public static String kFontStyleNormal = "normal";
	public static String kFontStyleBold = "bold";
	public static String kFontStyleItalic = "italic";
	public static String kFontStyleBoldItaclic = "bold-italic";
	
	protected String text;
	protected Color textColor;
	protected Color unknownTextColor;
	protected String align;
	protected String verticalAlign;
	
	protected String fontFamilyName;
	protected String fontStyle; 
	protected Double fontSize; // Fixed, no adjustments will be made, could clip line
	protected Double maxFontSize;
	protected Double minFontSize;

	protected Color backgroundColorPressed;
	
	protected boolean ellipsize;
	
	public ViewBehaviorBaseLabel(String type, KeyValueList properties) {
		super(type, properties);
	
		this.text = getStringProperty("text", null, properties);
		this.align = getStringProperty("align", null, properties);
		this.verticalAlign = getStringProperty("vertical-align", null, properties);
		this.textColor = getColorProperty("text-color", null, properties);
		this.unknownTextColor = getColorProperty("unknown-text-color", null, properties);
		
		/// Fonts 
		String specifiedDefaultFontName = GlobalState.fluidApp.getViewManager().getSpecifiedDefaultFontFamilyName();	         
		this.fontFamilyName = getFontFamilyName("font-family", specifiedDefaultFontName, properties); 
		
		String specifiedDefaultFontStyle = GlobalState.fluidApp.getViewManager().getSpecifiedDefaultFontStyle();
		this.fontStyle = getFontStyle("font-style", specifiedDefaultFontStyle, properties);
		
		this.fontSize = getFontSizeProperty("font-size", null, properties);
		this.maxFontSize = getFontSizeProperty("max-font-size", null, properties);
		this.minFontSize = getFontSizeProperty("min-font-size", null, properties);
		
		this.backgroundColorPressed = getColorProperty("background-color-pressed", null, properties);
		this.ellipsize = getBooleanProperty("ellipsize", false, properties);

		if (minFontSize != null && maxFontSize != null && minFontSize > maxFontSize) {
			throw new RuntimeException("min-font-size must be less than max-font-size");
		}
		if (minFontSize != null && fontSize != null) {
			throw new RuntimeException("min-font-size may not be used with font-size");
		}
		if (maxFontSize != null && fontSize != null) {
			throw new RuntimeException("max-font-size may not be used with font-size");
		}
	}
}