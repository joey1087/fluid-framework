package com.sponberg.fluid.parser;

import java.util.HashMap;

import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.layout.TableLayout;
import com.sponberg.fluid.layout.ViewBehavior;
import com.sponberg.fluid.layout.ViewBehaviorButton;
import com.sponberg.fluid.layout.ViewBehaviorImage;
import com.sponberg.fluid.layout.ViewBehaviorLabel;
import com.sponberg.fluid.layout.ViewBehaviorSearchbar;
import com.sponberg.fluid.layout.ViewBehaviorSegmentedControl;
import com.sponberg.fluid.layout.ViewBehaviorSpace;
import com.sponberg.fluid.layout.ViewBehaviorSubview;
import com.sponberg.fluid.layout.ViewBehaviorSubviewRepeat;
import com.sponberg.fluid.layout.ViewBehaviorTable;
import com.sponberg.fluid.layout.ViewBehaviorTable.RowProvider;
import com.sponberg.fluid.layout.ViewBehaviorTextfield;
import com.sponberg.fluid.layout.ViewBehaviorURLWebView;
import com.sponberg.fluid.layout.ViewBehaviorWebView;
import com.sponberg.fluid.util.KeyValueList;

public class ViewBehaviorFactory {

	public static final String SEGMENTED_CONTROL = "segmented-control";
	public static final String SEARCHBAR = "searchbar";
	public static final String TEXTFIELD = "textfield";
	public static final String URL_WEBVIEW = "url-webview";
	public static final String WEBVIEW = "webview";
	public static final String SUBVIEW_REPEAT = "subview-repeat";
	public static final String SUBVIEW = "subview";
	public static final String IMAGE = "image";
	public static final String SPACE = "space";
	public static final String TABLE = "table";
	public static final String BUTTON = "button";
	public static final String LABEL = "label";
	
	HashMap<String, ViewBehaviorBuilder> viewBehaviorBuilders = new HashMap<>();
	
	public ViewBehaviorFactory() {
		registerDefaults();
	}
	
	public void register(String key, ViewBehaviorBuilder builder) {
		viewBehaviorBuilders.put(key, builder);
	}
	
	public ViewBehavior getViewBehavior(String key, KeyValueList properties) {
		
		ViewBehaviorBuilder builder = viewBehaviorBuilders.get(key);
		if (builder == null) {
			return null;
		}
		
		return builder.build(properties);
	}
	
	protected void registerDefaults() {
		
		register(LABEL, new ViewBehaviorBuilder() {
			@Override
			public ViewBehavior build(KeyValueList properties) {
				return new ViewBehaviorLabel(properties);
			}
		});
		
		register(BUTTON, new ViewBehaviorBuilder() {
			@Override
			public ViewBehavior build(KeyValueList properties) {
				return new ViewBehaviorButton(properties);
			}
		});
		
		register(TABLE, new ViewBehaviorBuilder() {
			@Override
			public ViewBehavior build(KeyValueList properties) {
				return getViewBehaviorTable(properties);
			}
		});
		
		register(SPACE, new ViewBehaviorBuilder() {
			@Override
			public ViewBehavior build(KeyValueList properties) {
				return new ViewBehaviorSpace(properties);
			}
		});
		
		register(IMAGE, new ViewBehaviorBuilder() {
			@Override
			public ViewBehavior build(KeyValueList properties) {
				return new ViewBehaviorImage(properties);
			}
		});
		
		register(SUBVIEW, new ViewBehaviorBuilder() {
			@Override
			public ViewBehavior build(KeyValueList properties) {
				return new ViewBehaviorSubview(properties);
			}
		});
		
		register(SUBVIEW_REPEAT, new ViewBehaviorBuilder() {
			@Override
			public ViewBehavior build(KeyValueList properties) {
				return new ViewBehaviorSubviewRepeat(properties);
			}
		});
		
		register(WEBVIEW, new ViewBehaviorBuilder() {
			@Override
			public ViewBehavior build(KeyValueList properties) {
				return new ViewBehaviorWebView(properties);
			}
		});
		
		register(URL_WEBVIEW, new ViewBehaviorBuilder() {
			@Override
			public ViewBehavior build(KeyValueList properties) {
				return new ViewBehaviorURLWebView(properties);
			}
		});
		
		register(TEXTFIELD, new ViewBehaviorBuilder() {
			@Override
			public ViewBehavior build(KeyValueList properties) {
				return new ViewBehaviorTextfield(properties);
			}
		});
		
		register(SEARCHBAR, new ViewBehaviorBuilder() {
			@Override
			public ViewBehavior build(KeyValueList properties) {
				return new ViewBehaviorSearchbar(properties);
			}
		});
		
		register(SEGMENTED_CONTROL, new ViewBehaviorBuilder() {
			@Override
			public ViewBehavior build(KeyValueList properties) {
				return new ViewBehaviorSegmentedControl(properties);
			}
		});

	}
	
	public ViewBehaviorTable getViewBehaviorTable(KeyValueList properties) {
		
		RowProvider provider = null;

		if (properties.contains("row-layout") && properties.contains("table-layout")) {
			throw new RuntimeException("May not use row-layout and table-layout at the same time.");
		}
		
		if (properties.contains("row-layout")) {
			provider = buildProviderForRowLayout(properties);
		} else if (properties.contains("table-layout")) {
			provider = buildProviderForTableLayout(properties);			
		} else {
			throw new RuntimeException("Must use row-layout or table-layout");
		}
		
		return new ViewBehaviorTable(properties, provider);
	}
	
	private RowProvider buildProviderForRowLayout(KeyValueList properties) {
		
		if (!properties.contains("key")) {
			throw new RuntimeException(
					"row-layout expects a key for the data model array providing data for each row");
		}
		
		final String rowLayout = properties.getValue("row-layout");
		
		final String key = properties.getValue("key");
		return new RowProviderRowLayout(key, rowLayout);
	}
	
	private RowProvider buildProviderForTableLayout(KeyValueList properties) {
		
		final TableLayout tableLayout = GlobalState.fluidApp.getViewManager().getTableLayout(properties.getValue("table-layout"));
		
		RowProvider provider = new RowProviderTableLayout(tableLayout);
		return provider;
	}
	
	public interface ViewBehaviorBuilder {
		
		public ViewBehavior build(KeyValueList properties);
		
	}
	
}
