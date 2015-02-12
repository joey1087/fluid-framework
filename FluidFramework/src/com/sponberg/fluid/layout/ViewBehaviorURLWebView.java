package com.sponberg.fluid.layout;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.sponberg.fluid.util.KeyValueList;

@ToString
@Getter
@Setter
public class ViewBehaviorURLWebView extends ViewBehavior {

	final String url;

	final String urlKey;

	public ViewBehaviorURLWebView(KeyValueList properties) {
		super(ViewBehavior.urlWebview, properties);
		this.url = getStringProperty("url", null, properties);
		this.urlKey = getStringProperty("url-key", null, properties);
	}

}
