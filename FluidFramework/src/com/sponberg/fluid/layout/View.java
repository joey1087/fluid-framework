package com.sponberg.fluid.layout;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.sponberg.fluid.GlobalState;

@ToString(exclude="layout")
@Getter
@Setter
public class View {

	final String id;
	
	final String key;
	
	final String visibleCondition;
	
	// Specified Constraints
	final Constraints givenConstraints;
	
	final ViewBehavior viewBehavior;
	
	// Set by Layout
	protected Double x, y, x2, y2, width, height;

	protected Double middleX; // not used, except in figuring out dynamic width for this view when center aligned
	
	static class OrientationProperties {
	
		protected Layout.Direction direction;
		
		protected int horizontalChain;
	
		ArrayList<LayoutAction> actionX = new ArrayList<>();
		ArrayList<LayoutAction> actionX2 = new ArrayList<>();
		ArrayList<LayoutAction> actionY = new ArrayList<>();
		ArrayList<LayoutAction> actionY2 = new ArrayList<>();	
		ArrayList<LayoutAction> actionWidth = new ArrayList<>();
		ArrayList<LayoutAction> actionHeight = new ArrayList<>();
		
	}
	
	protected OrientationProperties portrait = new OrientationProperties();
	protected OrientationProperties landscape = new OrientationProperties();	
	protected OrientationProperties currentLayout = portrait;
	
	private final Layout layout; // of parent
	
	protected boolean visible = true;
	
	public View(String id, String key, String visibleCondition, Layout layout, Constraints givenConstraints, ViewBehavior viewBehavior) {
		this.id = id;
		this.key = key;
		this.visibleCondition = visibleCondition;
		this.layout = layout;
		this.givenConstraints = givenConstraints;
		
		this.viewBehavior = viewBehavior;
	}
	
	public void addActionX(LayoutAction a) {
		currentLayout.actionX.add(a);
	}
	
	public void addActionX2(LayoutAction a) {
		currentLayout.actionX2.add(a);
	}
	
	public void addActionY(LayoutAction a) {
		currentLayout.actionY.add(a);
	}
	
	public void addActionY2(LayoutAction a) {
		currentLayout.actionY2.add(a);
	}
	
	public void addActionWidth(LayoutAction a) {
		currentLayout.actionWidth.add(a);
	}
	
	public void addActionHeight(LayoutAction a) {
		currentLayout.actionHeight.add(a);
	}
	
	
	public void runActions(ArrayList<LayoutAction> actions) {
		for (LayoutAction action : actions) {
			action.run();
		}
	}
	
	public void setX(Double v) {
		this.x = v;
		runActions(currentLayout.actionX);
		
		if (width != null && x2 == null) {
			setX2(x + width);
		} else if (x2 != null && width == null) {
			setWidth(x2 - x);
		}
	}
	
	public void setY(Double v) {
		this.y = v;
		runActions(currentLayout.actionY);
		
		if (height != null && y2 == null) {
			setY2(y + height);
		} else if (y2 != null && height == null) {
			setHeight(y2 - y);
		}
	}
	
	public void setMiddleX(Double v) {
		this.middleX = v;
		
		if (width != null && x2 == null) {
			setX2(middleX + width / 2);
		}
		if (width != null && x == null) {
			setX(middleX - width / 2);
		}
	}
	
	public void setX2(Double v) {
		this.x2 = v;
		runActions(currentLayout.actionX2);

		if (width != null && x == null) {
			setX(x2 - width);
		} else if (x != null && width == null) {
			setWidth(x2 - x);
		}
	}
	
	public void setY2(Double y) {
		this.y2 = y;
		runActions(currentLayout.actionY2);
		
		if (height != null && y == null) {
			setY(y2 - height);
		} else if (y != null && height == null) {
			setHeight(y2 - y);
		}
	}

	public void setWidth(Double width) {
		
		if (width < 0) {
			throw new RuntimeException("Width cannot be < 0, " + this.getId());
		}
		
		this.width = width;
		runActions(currentLayout.actionWidth);
		
		if (middleX != null && x == null) {
			setX(middleX - width / 2);
		}
		if (middleX != null && x2 == null) {
			setX2(middleX + width / 2);
		}
		
		if (x != null && x2 == null) {
			setX2(x + width);
		} else if (x2 != null && x == null) {
			setX(x2 - width);
		}
	}
	
	public void setHeight(Double height) {
		this.height = height;
		runActions(currentLayout.actionHeight);
		
		if (y != null && y2 == null) {
			setY2(y + height);
		} else if (y2 != null && y == null) {
			setY(y2 - height);
		}
	}
	
	@Override
	public boolean equals(Object o) {
		return this.id.equals(((View) o).id);
	}
	
	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

	public String getValue(String prefix, String keys, String messageFormat) {
		String unkownText = viewBehavior.getUnknownText();
		return GlobalState.fluidApp.getDataModelManager().getValue(prefix, keys, messageFormat, unkownText);
	}
	
	public void setValue(String prefix, String key, Object value) {
		GlobalState.fluidApp.getDataModelManager().setValue(prefix, key, value);
	}
	
	protected void viewDidLoad() {}
	
}
