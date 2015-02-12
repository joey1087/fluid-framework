package com.sponberg.fluid.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sponberg.fluid.ApplicationInitializer;
import com.sponberg.fluid.FluidApp;
import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.layout.Constraints;
import com.sponberg.fluid.layout.Coord;
import com.sponberg.fluid.layout.CoordFixed;
import com.sponberg.fluid.layout.CoordRelativeToParent;
import com.sponberg.fluid.layout.CoordRelativeToView;
import com.sponberg.fluid.layout.Layout;
import com.sponberg.fluid.layout.Layout.Direction;
import com.sponberg.fluid.layout.Length;
import com.sponberg.fluid.layout.LengthCompute;
import com.sponberg.fluid.layout.LengthEquals;
import com.sponberg.fluid.layout.LengthFill;
import com.sponberg.fluid.layout.LengthFixed;
import com.sponberg.fluid.layout.LengthRelativeToLayer;
import com.sponberg.fluid.layout.LengthRelativeToParent;
import com.sponberg.fluid.layout.LengthRelativeToRow;
import com.sponberg.fluid.layout.LengthRelativeToView;
import com.sponberg.fluid.layout.Subtractor;
import com.sponberg.fluid.layout.TableLayout;
import com.sponberg.fluid.layout.TableLayout.TableSection;
import com.sponberg.fluid.layout.View;
import com.sponberg.fluid.layout.ViewBehavior;
import com.sponberg.fluid.util.KVLReader;
import com.sponberg.fluid.util.KVLReader.KeyValueListDefault;
import com.sponberg.fluid.util.KeyValueList;
import com.sponberg.fluid.util.KeyValueListModifyable;
import com.sponberg.fluid.util.Logger;

public class ViewsParser implements ApplicationInitializer {

	FluidApp app;
	
	KVLReader settings = null;
	
	KeyValueList screen = null;
	HashMap<String, KeyValueList> viewById;
	Layout currentLayout;
	boolean anchorSet = false;

	Layout.Direction nextHorDir = Layout.Direction.RIGHT;
	Layout.Align nextAlignment = Layout.Align.TOP;
	
	String currentFile;
	String lastLine;

	HashMap<String, View> viewObjectById = null;
	
	boolean parsingLandscape = false;
	
	@Override
	public void initialize(FluidApp app) {

		this.app = app;
		
		String s = app.getResourceService().getResourceAsString("generated", "views.txt");
		
		if (s == null) {
			throw new RuntimeException("Unable to find views.txt");
		}

		try {
			settings = new KVLReader(s);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		for (String tableLayout : settings.getValues("table-layouts")) {
			if (tableLayout.contains("@")) {
				continue;
			}
			currentFile = tableLayout;
			String data = getPlatformOrDefaultResource("views/table-layouts", tableLayout);
			if (data == null) {
				throw new RuntimeException("TableLayout not found " + tableLayout);
			}
			
			int i = tableLayout.indexOf(".");
			String id = tableLayout.substring(0, i);

			TableLayout layout = parseTableLayout(id, data, app.getViewManager().getBaseUnit());
			app.getViewManager().addTableLayout(layout);
		}
		
		for (String screen : settings.getValues("screens")) {
			if (screen.contains("@")) {
				continue;
			}
			currentFile = screen;
			String data = getPlatformOrDefaultResource("views/screens", screen);
			if (data == null) {
				throw new RuntimeException("Screen not found " + screen);
			}
			KeyValueList kvl = getScreenKVL(data);
			
			int i = screen.indexOf(".");
			String id = screen.substring(0, i);
			
			Layout layout = parseLayout(id, kvl, app.getViewManager().getBaseUnit());
			app.getViewManager().addScreen(layout);
			
			boolean showTabs = true;
			if (app.getSettings().contains("show-tabs")) {
				showTabs = Boolean.parseBoolean(app.getSettings().getValue("show-tabs"));
			}
			
			if (kvl.contains("show-tabs")) {
				showTabs = kvl.getValue("show-tabs").equalsIgnoreCase("true");
			}
			app.getViewManager().getScreen(layout.getId()).setShowTabBar(showTabs);
			
			boolean showNavigation = true;
			if (kvl.contains("show-navigation")) {
				showNavigation = kvl.getValue("show-navigation").equalsIgnoreCase("true");
			}
			app.getViewManager().getScreen(layout.getId()).setShowNavigationBar(showNavigation);
			
			boolean showStatusBar = true;
			if (kvl.contains("show-status-bar")) {
				showStatusBar = kvl.getValue("show-status-bar").equalsIgnoreCase("true");
			}
			app.getViewManager().getScreen(layout.getId()).setShowStatusBar(showStatusBar);
			
			String backButtonText = null;
			if (kvl.contains("back-button-text")) {
				backButtonText = kvl.getValue("back-button-text");
			}
			app.getViewManager().getScreen(layout.getId()).setBackButtonText(backButtonText);
			
			boolean hideBackButton = false;
			if (kvl.contains("hide-back-button")) {
				hideBackButton = kvl.getValue("hide-back-button").equalsIgnoreCase("true");
			}
			app.getViewManager().getScreen(layout.getId()).setHideBackButton(hideBackButton);
		}

		for (String component : settings.getValues("components")) {
			if (component.contains("@")) {
				continue;
			}
			currentFile = component;
			String data = getPlatformOrDefaultResource("views/components", component);
			if (data == null) {
				throw new RuntimeException("Component not found " + component);
			}
			
			int i = component.indexOf(".");
			String id = component.substring(0, i);

			Layout layout = parseLayout(id, getScreenKVL(data), app.getViewManager().getBaseUnit());
			app.getViewManager().addLayout(layout);
		}

		viewObjectById = null;
	}

	private String getPlatformOrDefaultResource(String dir, String defaultName) {
		int i = defaultName.indexOf(".");
		String platformSpecific = defaultName.substring(0, i) + "@" + app.getPlatform() + defaultName.substring(i);
		String data = app.getResourceService().getResourceAsString(dir, platformSpecific);
		if (data == null) {
			data = app.getResourceService().getResourceAsString(dir, defaultName);
		}
		return data;
	}
	
	private KeyValueList getScreenKVL(String screenAsString) {
		try {
			return new KVLReader(screenAsString);
		} catch (IOException e) {
			Logger.error(this, e);
			Logger.error(this, "Caught error " + e.getMessage() + ", for " + currentFile + " line " + lastLine);
			throw new RuntimeException(e);
		}		
	}
	
	private Layout parseLayout(String id, KeyValueList screen, double baseUnit) {
		return parseLayout("", id, screen, baseUnit);
	}
	
	private Layout parseLayout(String layoutIdPrefix, String id, KeyValueList reader, double baseUnit) {
		
		screen = reader;

		viewObjectById = new HashMap<>();
		
		try {

			setupViewById();

			String nameKey = null;
			if (screen.contains("name-key")) {
				nameKey = screen.getValue("name-key");
			}
			
			currentLayout = new Layout(layoutIdPrefix + id,
					screen.getValue("name"), nameKey, baseUnit);
			
			if (screen.contains("subtitle")) {
				currentLayout.setSubtitle(screen.getValue("subtitle"));
			}
			
			if (screen.contains("subtitle-key")) {
				currentLayout.setSubtitleKey(screen.getValue("subtitle-key"));
			}
			
			currentLayout.setProperties(screen.get("properties"));
			
			List<? extends KeyValueList> layoutVariables = screen.get("layout-variables");
			if (layoutVariables == null) {
				layoutVariables = new ArrayList<>();
			}
			
			if (screen.contains("background-color")) {
				currentLayout.setBackgroundColor(app.getViewManager().getColor(
						screen.getValue("background-color")));
			}

			if (screen.contains("precompute-positions")) {
				currentLayout.setPrecomputedPositions(screen.getValue("precompute-positions").equalsIgnoreCase("true"));
			}
			
			if (screen.contains("wrap-in-scroll-view")) {
				currentLayout.setWrapInScrollView(screen.getValue("wrap-in-scroll-view").equalsIgnoreCase("true"));
			}
			
			if (screen.contains("block-focus-view-on-load")) {
				currentLayout.setBlockFocusViewOnLoad(screen.getValue("block-focus-view-on-load").equalsIgnoreCase("true"));
			}
			
			nextHorDir = Layout.Direction.RIGHT;
			nextAlignment = Layout.Align.TOP;
			
			List<String> lines = screen.getValues("layout");
			parsingLandscape = false;
			parseLayout(lines, layoutVariables);
			
			if (screen.contains("layout-landscape")) {
				lines = screen.getValues("layout-landscape");
				currentLayout.createOrientationLandscape();
				currentLayout.setOrientationLandscape(true);
				parsingLandscape = true;
				parseLayout(lines, layoutVariables);				
			}
			
			currentLayout.setOrientationLandscape(false);
			
			return currentLayout;
			
		} catch (Exception e) {
			Logger.error(this, e);
			Logger.error(this, "Caught error " + e.getMessage() + ", for " + currentFile + " line " + lastLine);
			throw new RuntimeException(e);			
		}
	}

	private void parseLayout(List<String> lines, List<? extends KeyValueList> layoutVariables) {
		anchorSet = false;
		for (int index = 0; index < lines.size(); index += 2) {
			lastLine = lines.get(index);
			String[] lineParts = lastLine.split("\\|"); // 0 is views, 1 is row properties
			HashMap<String, String> rowProperties = new HashMap<>();
			if (lineParts.length > 1) {
				parseRowProperties(lineParts[1], rowProperties);
			}
			
			parseRow(lineParts[0], rowProperties, layoutVariables);
			if (index + 1 >= lines.size()) {
				break;
			}
			lastLine = lines.get(index + 1);
			
			if (lastLine.startsWith("*")) {
				lineParts = lastLine.split("\\|"); // 0 is views, 1 is row properties
				rowProperties = new HashMap<>();
				if (lineParts.length > 1) {
					parseRowProperties(lineParts[1], rowProperties);
				}
				parseNewLayer(rowProperties);
				continue;
			}
			
			parseDown(lastLine);	
		}
		
		fixRelativeLengths(app.getViewManager().getBaseUnit());
	}

	private HashMap<String, String> parseRowProperties(String lineProperties, HashMap<String, String> properties) {
		String[] propertyPairs = lineProperties.split(";");
		for (String propertyPair : propertyPairs) {
			String[] pair = propertyPair.split(":");
			properties.put(pair[0].trim(), pair[1].trim());
		}
		return properties;
	}
	
	private void fixRelativeLengths(double baseUnit) {
		nextViewWidth:
		for (View view : currentLayout.getAllViews()) {
			Length width = view.getGivenConstraints().getWidth();
			if (width.relativeToView()) {
				String relativeId = width.getRelativeId();
				if (relativeId != null) {
					View relativeView = currentLayout.getViewMap().get(relativeId);
					if (relativeView == null) {
						throw new RuntimeException("Relative view is null " + relativeId + " referenced from " + view.getId());
					}
					Length relativeWidthLength = relativeView.getGivenConstraints().getWidth();
					if (!relativeWidthLength.isDynamic()) {
						Double relativeWidth = relativeWidthLength.getFixedLength();
						for (Subtractor relativeWidthLength2 : relativeWidthLength.getSubtractors()) {
							if (relativeWidthLength2.isRelativeToView()) {
								// Can't compute now, skip setting this constraint to a fixed length
								continue nextViewWidth;
							}
							relativeWidth -= relativeWidthLength2.getFixed();
						}
						for (Subtractor relativeWidthLength2 : width.getSubtractors()) {
							if (relativeWidthLength2.isRelativeToView()) {
								// Can't compute now, skip setting this constraint to a fixed length
								continue nextViewWidth;
							} else {
								relativeWidth -= relativeWidthLength2.getFixed();
							}
						}
						view.getGivenConstraints().setWidth(new LengthFixed(relativeWidth));
					}
				}
			}
		}
		nextViewHeight:
		for (View view : currentLayout.getAllViews()) {
			Length height = view.getGivenConstraints().getHeight();
			if (height.relativeToView()) {
				String relativeId = height.getRelativeId();
				if (relativeId != null) {
					View relativeView = currentLayout.getViewMap().get(relativeId);
					Length relativeHeightLength = relativeView.getGivenConstraints().getHeight();
					if (!relativeHeightLength.isDynamic()) {
						Double relativeHeight = relativeHeightLength.getFixedLength();
						for (Subtractor relativeHeightLength2 : relativeHeightLength.getSubtractors()) {
							//Length relativeHeightLength2 = relativeView.getGivenConstraints().getHeight();
							if (relativeHeightLength2.isRelativeToView()) {
								// Can't compute now, skip setting this constraint to a fixed length
								continue nextViewHeight;
							}
							relativeHeight -= relativeHeightLength2.getFixed();
						}
						for (Subtractor relativeHeightLength2 : height.getSubtractors()) {
							if (relativeHeightLength2.isRelativeToView()) {
								// Can't compute now, skip setting this constraint to a fixed length
								continue nextViewHeight;
							} else {
								relativeHeight -= relativeHeightLength2.getFixed();
							}
						}
						view.getGivenConstraints().setHeight(new LengthFixed(relativeHeight));
					}
				}
			}
		}
	}
	
	private void setupViewById() {
		viewById = new HashMap<>();
		for (KeyValueList list : screen.get("views")) {
			viewById.put(list.getValue(), list);
		}
	}

	private void parseRow(String s, HashMap<String, String> rowProperties, List<? extends KeyValueList> layoutVariables) {
		s = s.trim().replaceAll(" +", " ");
		
		String[] sa = s.split(" ");

		String dir = sa[0];
		if (dir.equals("->")) {
			nextHorDir = Layout.Direction.RIGHT;
		} else if (dir.equals("<-")) {
			nextHorDir = Layout.Direction.LEFT;
		} else {
			throw new RuntimeException("Invalid format for moving direction, line " + s);
		}
		
		int start, end, inc;
		if (nextHorDir == Layout.Direction.LEFT) {
			start = sa.length - 1;
			end = 0;
			inc = -1;
		} else {
			start = 1;
			end = sa.length;
			inc = 1;
		}
		
		for (int index = start; index != end; index += inc) {
			String token = sa[index];
			
			boolean isView = !(token.contains("(") || token.contains(")"));
			
			if (isView) {
				View view = createView(token, rowProperties, layoutVariables);
				if (index == start) {
					if (!anchorSet) {
						currentLayout.setAnchor(view, nextHorDir);
						anchorSet = true;
					} else {
						Layout.Align align = Layout.Align.LEFT;
						if (nextAlignment == Layout.Align.CENTER) {
							align = Layout.Align.CENTER;
						} else if (nextAlignment == Layout.Align.RIGHT) {
							align = Layout.Align.RIGHT;
						} else if (nextAlignment == Layout.Align.UNASSIGNED) {
							align = (nextHorDir == Layout.Direction.RIGHT) ? Layout.Align.LEFT : Layout.Align.RIGHT;
						}
						currentLayout.addDown(view, align, nextHorDir);
					}	
				} else if (nextHorDir == Layout.Direction.LEFT) {
					Layout.Align align = (nextAlignment == Layout.Align.TOP) ? Layout.Align.TOP : Layout.Align.BOTTOM;
					currentLayout.addLeft(view, align);
				} else if (nextHorDir == Layout.Direction.RIGHT) {
					Layout.Align align = (nextAlignment == Layout.Align.TOP) ? Layout.Align.TOP : Layout.Align.BOTTOM;
					currentLayout.addRight(view, align);
				}
				nextAlignment = Layout.Align.TOP;
			} else {
				if (token.contains("top")) {
					nextAlignment = Layout.Align.TOP;
				} else if (token.contains("bottom")) {
					nextAlignment = Layout.Align.BOTTOM;
				} else {
					Logger.warn(this, "Attempted to set vertical alignment to unsupported value " + token);
				}
			}
		}
	}
	
	private void parseDown(String s) {
		s = s.trim();
		
		if (!s.contains("|")) {
			throw new RuntimeException("Invalid Format for down line, line " + s);
		}

		if (nextHorDir == Layout.Direction.RIGHT) {
			nextHorDir = Layout.Direction.LEFT;
		} else {
			nextHorDir = Layout.Direction.RIGHT;
		}

		if (s.contains("right")) {
			nextAlignment = Layout.Align.RIGHT;
		} else if (s.contains("center")) {
			nextAlignment = Layout.Align.CENTER;
		} else if (s.contains("left")) {
			nextAlignment = Layout.Align.LEFT;
		} else {
			nextAlignment = Layout.Align.UNASSIGNED;
		}
	}

	private void parseNewLayer(HashMap<String, String> rowProperties) {
		
		nextHorDir = Layout.Direction.RIGHT;
		nextAlignment = Layout.Align.TOP;
		
		anchorSet = false;

		String visibleCondition = rowProperties.get("visible-condition");
		
		if (rowProperties.containsKey("z-index")) {
			currentLayout.addNewLayer(visibleCondition, Integer.parseInt(rowProperties.get("z-index")));
		} else {
			currentLayout.addNewLayer(visibleCondition);
		}
	}
	
	private View createView(String id, HashMap<String, String> rowProperties, List<? extends KeyValueList> layoutVariables) {

		if (viewObjectById.containsKey(id)) {
			View view = viewObjectById.get(id);
			if (parsingLandscape) {
				view.setCurrentLayout(view.getLandscape());
			} else {
				view.setCurrentLayout(view.getPortrait());
			}				
			return view;
		}
		
		if (viewById.get(id) == null) {
			throw new RuntimeException("View not found " + id);
		}
		
		KeyValueListWithRowProperties list = new KeyValueListWithRowProperties(viewById.get(id), rowProperties, layoutVariables);

		Constraints c = new Constraints();

		c.setWidth(getLength(list.getSizeValue("w")));
		c.setHeight(getLength(list.getSizeValue("h")));

		if (!anchorSet) {
			if (list.contains("x")) {
				c.setX(getCoord(list.getValue("x")));
			}
			if (list.contains("x2")) {
				c.setX2(getCoord(list.getValue("x2")));
			}
			if (list.contains("y")) {
				c.setY(getCoord(list.getValue("y")));
			}
			if (list.contains("y2")) {
				c.setY2(getCoord(list.getValue("y2")));
			}
		}

		if (!anchorSet && c.getX() == null && c.getX2() == null) {
			if (nextHorDir == Direction.RIGHT) {
				c.setX(new CoordFixed(0.0));
			} else {
				c.setX2(new CoordRelativeToParent("right", null));
			}
		}
		if (!anchorSet && c.getY() == null && c.getY2() == null) {
			c.setY(new CoordFixed(0.0));
		}
		
		String type = list.getValue("type");
		ViewBehavior viewBehavior = app.getViewBehaviorFactory().getViewBehavior(type,  list);
		if (viewBehavior == null) {
			throw new RuntimeException("Unable to determine view behavior for type: "
					+ type + ", for line: " + lastLine);
		}

		viewBehavior.validateConstraints(c);
		
		String key = list.contains("key") ? list.getValue("key") : null;
		
		String visibleCondition = list.contains("visible-condition") ? list.getValue("visible-condition") : null;
		
		View view = new View(id, key, visibleCondition, currentLayout, c, viewBehavior);
		viewObjectById.put(id, view);
		return view;
	}

	private Length getLength(String value) {
		String[] sa = value.trim().split(" ");
		if (sa.length == 1) {
			if (sa[0].equals("fill")) {
				return new LengthFill();
			} else if (sa[0].equals("equal")) {
				return new LengthEquals(null);
			} else if (sa[0].equals("compute")) {
				return new LengthCompute();
			} else {
				return new LengthFixed(GlobalState.fluidApp.sizeToPixels(sa[0]));
			}
		} else {
			return parseLength(sa, value);
		}
	}

	private Coord getCoord(String value) {
		String[] sa = value.trim().split(" ");
		if (sa.length == 1) {
			return new CoordFixed(GlobalState.fluidApp.sizeToPixels(sa[0]));
		} else {
			if (!sa[1].equals("of")) {
				throw new RuntimeException("Unable to determine coordinate for "
						+ value + ", for line" + lastLine);
			}
			if (sa[2].equals("parent")) {
				ArrayList<Subtractor> subtractors = new ArrayList<>(); 
				if (sa.length > 3) {
					parseConstraints(value, subtractors, sa, 3);
				}
				return new CoordRelativeToParent(sa[0], subtractors);
			}
			if (sa[2].equals("view")) {
				ArrayList<Subtractor> subtractors = new ArrayList<>(); 
				if (sa.length > 4) {
					parseConstraints(value, subtractors, sa, 4);
				}
				return new CoordRelativeToView(sa[0], sa[3], subtractors);
			}
		}
		throw new RuntimeException("Unable to determine length for " + value + ", for line" + lastLine);
	}
	
	protected Length parseLength(String[] sa, String value) {
		return parseLength(sa, value, 0);
	}
	
	protected Length parseLength(String[] sa, String value, int index) {
		if (!sa[index + 1].equals("of")) {
			throw new RuntimeException("Unable to determine length for "
					+ value + ", for line" + lastLine);
		}
		if (sa[index + 2].equals("parent")) {
			ArrayList<Subtractor> subtractors = new ArrayList<>(); 
			if (index + sa.length > 3) {
				parseConstraints(value, subtractors, sa, index + 3);
			}
			return new LengthRelativeToParent(Double.parseDouble(sa[index]), subtractors);
		} else if (sa[index + 2].equals("row")) {
			ArrayList<Subtractor> subtractors = new ArrayList<>(); 
			if (index + sa.length > 3) {
				parseConstraints(value, subtractors, sa, index + 3);
			}
			return new LengthRelativeToRow(Double.parseDouble(sa[index]), subtractors);
		} else if (sa[index + 2].equals("view")) {
			ArrayList<Subtractor> subtractors = new ArrayList<>(); 
			if (index + sa.length > 4) {
				parseConstraints(value, subtractors, sa, index + 4);
			}
			return new LengthRelativeToView(Double.parseDouble(sa[index]),
					sa[index + 3], subtractors);
		} else if (sa[index + 2].equals("layer")) {
			ArrayList<Subtractor> subtractors = new ArrayList<>(); 
			if (index + sa.length > 4) {
				parseConstraints(value, subtractors, sa, index + 4);
			}
			return new LengthRelativeToLayer(Double.parseDouble(sa[index]), Integer.parseInt(sa[index + 3]), subtractors);
		} else {
			throw new RuntimeException("Unable to determine length for " + value + ", for line" + lastLine);
		}
	}
	
	private void parseConstraints(String value, ArrayList<Subtractor> subtractors, String[] sa, int index) {
		if (index > sa.length - 1) {
			return;
		}
		if (sa[index].equals("minus") || sa[index].equals("plus") ) {
			int sign = sa[index].equals("minus") ? 1 : -1;
			if (sa.length > index + 2 && sa[index + 2].equals("of")) {
				if (!sa[index + 3].equals("view")) {
					throw new RuntimeException("Unable to determine length for " + value + ", for line " + lastLine);
				}
				subtractors.add(new Subtractor(Double.parseDouble(sa[index + 1]) * sign, sa[index + 4]));
				parseConstraints(value, subtractors, sa, index + 5);
			} else {
				subtractors.add(new Subtractor(Double.parseDouble(sa[index + 1]) * sign * app.getViewManager().getBaseUnit()));
				parseConstraints(value, subtractors, sa, index + 2);
			}
		} else {
			throw new RuntimeException("Unable to determine length for " + value + ", for line " + lastLine);
		}
	}

	public static String getTableLayoutId(String tableLayoutId, String layoutId) {
		return "_TableLayout." + tableLayoutId + "." + layoutId;
	}
	
	private TableLayout parseTableLayout(String id, String layoutAsString, double baseUnit)  {
		
		KVLReader settings;
		try {
			settings = new KVLReader(layoutAsString);
		} catch (IOException e) {
			Logger.error(this, e);
			throw new RuntimeException(e);
		}
		
		TableLayout tableLayout = new TableLayout(id);

		if (settings.contains("background-color")) {
			tableLayout.setBackgroundColor(app.getViewManager().getColor(
					settings.getValue("background-color")));
		}
		
		String layoutIdPrefix = "_TableLayout." + tableLayout.getId() + ".";
		String sectionHeaderLayoutIdPrefix = "_TableLayout." + tableLayout.getId() + ".header.";
		
		for (KeyValueList section : settings.get("sections")) {
			TableSection tableSection = new TableSection(section.getValue());
			tableSection.setSectionHeaderLayout(sectionHeaderLayoutIdPrefix + section.getValue());
			tableLayout.addSection(tableSection);
			for (String rowId : section.getValues("rows")) {
				tableSection.addLayout(getTableLayoutId(tableLayout.getId(), rowId));
			}
		}
		
		for (KeyValueListModifyable row : (List<KeyValueListModifyable>) settings.get("section-headers")) {
			
			KeyValueListModifyable kvl = new KeyValueListDefault(row.getValue());
			row.add("name", kvl);
			
			Layout layout = parseLayout(sectionHeaderLayoutIdPrefix, row.getValue(), row, app.getViewManager().getBaseUnit());
			app.getViewManager().addLayout(layout);
		}
		
		for (KeyValueListModifyable row : (List<KeyValueListModifyable>) settings.get("rows")) {
			
			KeyValueList kvl = new KeyValueListDefault(row.getValue());
			row.add("name", kvl);
			
			Layout layout = parseLayout(layoutIdPrefix, row.getValue(), row, app.getViewManager().getBaseUnit());
			app.getViewManager().addLayout(layout);
		}
		
		return tableLayout;
	}
	
	@Override
	public String[] getSupportedPlatforms() {
		return null;
	}
	
	static class KeyValueListWithRowProperties implements KeyValueList {

		// Contains properties for a specific view and for a row
		// The order of precedence is specific view, then row
		
		final KeyValueList list;
		final HashMap<String, String> rowProperties;
		final List<? extends KeyValueList> layoutVariables;
		
		HashSet<String> keys;
		
		public KeyValueListWithRowProperties(KeyValueList list,
				HashMap<String, String> rowProperties,
				List<? extends KeyValueList> layoutVariables) {
			this.list = list;
			this.rowProperties = rowProperties;
			this.layoutVariables = layoutVariables;
			keys = new HashSet<>(list.keys());
			keys.add("layout-variables");
		}

		@Override
		public List<? extends KeyValueList> get(String key) {
			
			if (key.equals("layout-variables")) {
				
				return layoutVariables;
			} else {
				
				return list.get(key);
			}
		}

		@Override
		public KeyValueList getWithValue(String key, String value) {
			
			if (key.equals("layout-variables")) {
				
				for (KeyValueList kvl : layoutVariables) {
					
					if (kvl.getValue().equals(value)) {
						
						return kvl;
					}
				}
				return null;
			} else {
			
				return list.getWithValue(key, value);
			}
		}

		@Override
		public boolean contains(String key) {
			return list.contains(key) || rowProperties.containsKey(key) || key.equals("layout-variables");
		}

		@Override
		public List<String> getValues(String key) {
			if (key.equals("layout-variables")) {
				ArrayList<String> list = new ArrayList<>();
				for (KeyValueList kvl : layoutVariables) {
					list.add(kvl.getValue());
				}
				return list;
			} else if (list.contains(key)) {
				return list.getValues(key);
			} else if (rowProperties.containsKey(key)) {
				ArrayList<String> values = new ArrayList<>();
				values.add(rowProperties.get(key));
				return values;
			} else {
				return null;
			}
		}

		@Override
		public String getValue(String key) {
			if (key.equals("layout-variables")) {
				StringBuilder builder = new StringBuilder();
				for (KeyValueList kvl : layoutVariables) {
					builder.append(kvl.getValue() + "\n");
				}
				return builder.toString();
			} else if (list.contains(key)) {
				return list.getValue(key);
			} else if (rowProperties.containsKey(key)) {
				return rowProperties.get(key);
			} else {
				return null;
			}
		}

		public String getSizeValue(String key) {
			
			String literalValue = getValue(key);
			
			KeyValueList kvl = getWithValue("layout-variables", "sizes");
			if (kvl != null) {
				
				if (kvl.contains(literalValue)) {
					
					return kvl.getValue(literalValue);
				}
			}
			
			return literalValue;
		}
		
		@Override
		public String getValue() {
			return list.getValue();
		}

		@Override
		public Set<String> keys() {
			return keys;
		}

	}
}
