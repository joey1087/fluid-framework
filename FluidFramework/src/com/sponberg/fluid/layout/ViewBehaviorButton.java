package com.sponberg.fluid.layout;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.util.KeyValueList;

@ToString
@Getter
@Setter
public class ViewBehaviorButton extends ViewBehavior {

	private String text;
	
	private Color textColor;
	
	private Double fontSize;
	protected String fontFamilyName;
	protected String fontStyle;
	
	private Color backgroundColorPressed;
	
	private String backgroundImage;

	private String image;
	
	private Double imageWidth;
	
	private Double imageHeight;
	
	private Double imageSpace;
	
	public ViewBehaviorButton(KeyValueList properties) {
		super(ViewBehavior.button, properties);
		
		this.text = getStringProperty("text", "", properties);;
		this.textColor = getColorProperty("text-color", null, properties);
		this.backgroundColorPressed = getColorProperty("background-color-pressed", null, properties);
		this.image = getStringProperty("image", null, properties);
		this.backgroundImage = getStringProperty("background-image", null, properties);
		this.imageWidth = getSizeProperty("image-width", null, properties);
		this.imageHeight = getSizeProperty("image-height", null, properties);
		this.imageSpace = getSizeProperty("image-space", "5p", properties);
		
		if (this.image != null) {
			if (imageWidth == null) {
				throw new RuntimeException("image width must be specified");
			}
			if (imageHeight == null) {
				throw new RuntimeException("image height must be specified");
			}
		}
		
		this.fontSize = getFontSizeProperty("font-size", null, properties);
		String specifiedDefaultFontName = GlobalState.fluidApp.getViewManager().getSpecifiedDefaultFontFamilyName();	         
		this.fontFamilyName = getFontFamilyName("font-family", specifiedDefaultFontName, properties); 
		
		String specifiedDefaultFontStyle = GlobalState.fluidApp.getViewManager().getSpecifiedDefaultFontStyle();
		this.fontStyle = getFontStyle("font-style", specifiedDefaultFontStyle, properties);
	}

	@Override
	public boolean isViewFactorySetsBackground() {
		return true;
	}
	
}
