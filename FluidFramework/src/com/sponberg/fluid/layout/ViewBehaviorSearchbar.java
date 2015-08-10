package com.sponberg.fluid.layout;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.util.KeyValueList;

@ToString
@Getter
@Setter
public class ViewBehaviorSearchbar extends ViewBehavior {

	private String text;

	private Color textColor;
	
	private String fontFamilyName;
	
	private String fontStyle;
	
	private Double fontSize;

	private boolean showCancelButton;

	private String placeholderText;
	
	private Color searchBarBackgroundColor;
	
	private boolean shouldBecomeFirstResponderWhenViewLoaded;

	public ViewBehaviorSearchbar(KeyValueList properties) {
		super(ViewBehavior.searchbar, properties);
		String specifiedDefaultFontName = GlobalState.fluidApp.getViewManager().getSpecifiedDefaultFontFamilyName();
		this.text = getStringProperty("text", null, properties);
		this.textColor = getColorProperty("text-color", null, properties);
		this.fontFamilyName = getStringProperty("font-family", specifiedDefaultFontName, properties);
		this.fontStyle = getStringProperty("font-style", null, properties);
		this.fontSize = getFontSizeProperty("font-size", null, properties);
		showCancelButton = getBooleanProperty("show-cancel-button", true, properties);
		placeholderText = getStringProperty("placeholder", null, properties);
		searchBarBackgroundColor = getColorProperty("search-bar-background-color", null, properties);
		shouldBecomeFirstResponderWhenViewLoaded = getBooleanProperty("become-first-responder-on-load", false, properties);
	}

}
