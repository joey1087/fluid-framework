package com.sponberg.fluid.layout;

import lombok.Getter;
import lombok.Setter;

import com.sponberg.fluid.GlobalState;

@Getter
@Setter
public class WebView {

	String filename;

	String html;

	private void generateHtml() {
		html = GlobalState.fluidApp.getResourceService().getResourceAsString("webview", filename);
	}

	@Override
	public String toString() {
		if (html == null) {
			generateHtml();
		}
		return html;
	}

}
