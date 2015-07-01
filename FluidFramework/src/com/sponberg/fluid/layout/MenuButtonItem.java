package com.sponberg.fluid.layout;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.sponberg.fluid.layout.ActionListener.EventInfo;

@ToString
@Getter
@Setter
@EqualsAndHashCode
public class MenuButtonItem {

	public static final String SystemItemCustom = "SystemItemCustom";
	public static final String SystemItemDone = "SystemItemDone";
	public static final String SystemItemCancel = "SystemItemCancel";
	public static final String SystemItemEdit = "SystemItemEdit";
	public static final String SystemItemSave = "SystemItemSave";
	public static final String SystemItemAdd = "SystemItemAdd";
	public static final String SystemItemFlexibleSpace = "SystemItemFlexibleSpace";
	public static final String SystemItemFixedSpace = "SystemItemFixedSpace";
	public static final String SystemItemCompose = "SystemItemCompose";
	public static final String SystemItemReply = "SystemItemReply";
	public static final String SystemItemAction = "SystemItemAction";
	public static final String SystemItemOrganize = "SystemItemOrganize";
	public static final String SystemItemBookmarks = "SystemItemBookmarks";
	public static final String SystemItemSearch = "SystemItemSearch";
	public static final String SystemItemRefresh = "SystemItemRefresh";
	public static final String SystemItemStop = "SystemItemStop";
	public static final String SystemItemCamera = "SystemItemCamera";
	public static final String SystemItemTrash = "SystemItemTrash";
	public static final String SystemItemPlay = "SystemItemPlay";
	public static final String SystemItemPause = "SystemItemPause";
	public static final String SystemItemRewind = "SystemItemRewind";
	public static final String SystemItemFastForward = "SystemItemFastForward";
	public static final String SystemItemUndo = "SystemItemUndo";
	public static final String SystemItemRedo = "SystemItemRedo";
	public static final String SystemItemPageCurl = "SystemItemPageCurl";

	public static final int ActionFlavorNone = 0;
	public static final int ActionFlavorSearch = 1;
	
	public static final String kItemPropertyTextColor = "textColor";
	
	ArrayList<ActionListener> actionListeners = new ArrayList<>();

	final String systemId;

	final String title;
	
	final String iconName;
	
	final int actionFlavor;
	
	boolean showOnMainBar = true;
	
	boolean enabled = true;
	
	boolean preferenceShowOnLeft = false;
	
	HashMap<String, String> properties = new HashMap<>();
	
	public MenuButtonItem(String systemId, String title, String iconName, int actionFlavor) {
		this.systemId = systemId;
		this.title = title;
		this.iconName = iconName;
		this.actionFlavor = actionFlavor;
		if (this.systemId == SystemItemCustom && title == null && iconName == null) {
			throw new RuntimeException("When systemId is Custom, must specify title or icon");
		}
	}

	public MenuButtonItem(String systemId) {
		this(systemId, null, null, ActionFlavorNone);
	}

	public void addActionListener(ActionListener al) {
		this.actionListeners.add(al);
	}

	public void userTapped() {
		for (ActionListener l : actionListeners) {
			l.userTapped(null);
		}
	}

	public void userChangedValueTo(Object value) {
		for (ActionListener l : actionListeners) {
			l.userChangedValueTo(new EventInfo(), value);
		}		
	}
	
	public String getTitle() {
		if (this.title != null) {
			return this.title;
		} else {
			return MenuButtonItem.getTitleFor(this.systemId);
		}
	}
	
	public void setProperty(String name, String value) {
		properties.put(name, value);
	}
	
	public String getProperty(String name) {
		return properties.get(name);
	}
	
	public static String getTitleFor(String systemId) {
		if (systemId.equals(SystemItemDone)) {
			return "Done";
		} else if (systemId.equals(SystemItemCancel)) {
			return "Cancel";
		} else if (systemId.equals(SystemItemEdit)) {
			return "Edit";
		} else if (systemId.equals(SystemItemSave)) {
			return "Save";
		} else if (systemId.equals(SystemItemAdd)) {
			return "Add";
		} else if (systemId.equals(SystemItemFlexibleSpace)) {
			return "FlexibleSpace";
		} else if (systemId.equals(SystemItemFixedSpace)) {
			return "FixedSpace";
		} else if (systemId.equals(SystemItemCompose)) {
			return "Compose";
		} else if (systemId.equals(SystemItemReply)) {
			return "Reply";
		} else if (systemId.equals(SystemItemAction)) {
			return "Share";
		} else if (systemId.equals(SystemItemOrganize)) {
			return "Organize";
		} else if (systemId.equals(SystemItemBookmarks)) {
			return "Bookmarks";
		} else if (systemId.equals(SystemItemSearch)) {
			return "Search";
		} else if (systemId.equals(SystemItemRefresh)) {
			return "Refresh";
		} else if (systemId.equals(SystemItemStop)) {
			return "Stop";
		} else if (systemId.equals(SystemItemCamera)) {
			return "Camera";
		} else if (systemId.equals(SystemItemTrash)) {
			return "Trash";
		} else if (systemId.equals(SystemItemPlay)) {
			return "Play";
		} else if (systemId.equals(SystemItemPause)) {
			return "Pause";
		} else if (systemId.equals(SystemItemRewind)) {
			return "Rewind";
		} else if (systemId.equals(SystemItemFastForward)) {
			return "FastForward";
		} else if (systemId.equals(SystemItemUndo)) {
			return "Undo";
		} else if (systemId.equals(SystemItemRedo)) {
			return "Redo";
		} else if (systemId.equals(SystemItemPageCurl)) {
			return "Next";
		} else {
			return null;
		}
	}

}
