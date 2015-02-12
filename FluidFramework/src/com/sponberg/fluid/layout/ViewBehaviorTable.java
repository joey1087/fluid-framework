package com.sponberg.fluid.layout;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.sponberg.fluid.layout.TableLayout.TableSection;
import com.sponberg.fluid.util.KeyValueList;

@ToString
@Getter
@Setter
public class ViewBehaviorTable extends ViewBehavior {

	private Double rowHeight;
	private Double sectionFooterHeight;
	private Double sectionHeaderHeight;
	private Double headerHeight;
	private Double footerHeight;
	private Boolean scrollEnabled;
	private Boolean showsVerticalScrollIndicator;

	private boolean showRowDivider;

	// TODO: these sizes should be specified in our units (mm)

	private RowProvider rowProvider;

	private String tableLayoutId;

	private boolean stickyHeaders;

	private Double paddingBottom;

	private boolean scrollToBottomOnLoad;

	public ViewBehaviorTable(KeyValueList properties, RowProvider rowProvider) {
		super(ViewBehavior.table, properties);
		this.sectionFooterHeight = getUnitsToPixelsProperty("section-footer-height", null, properties);
		this.sectionHeaderHeight = getUnitsToPixelsProperty("section-header-height", null, properties);
		this.headerHeight = getDoubleProperty("header-height", null, properties);
		this.footerHeight = getDoubleProperty("footer-height", null, properties);
		this.scrollEnabled = getBooleanProperty("scroll-enabled", true, properties);
		this.showsVerticalScrollIndicator = getBooleanProperty("shows-vertical-scroll-indicator", true, properties);
		this.rowProvider = rowProvider;
		this.tableLayoutId = getStringProperty("table-layout", null, properties);
		this.rowHeight = getDoubleProperty("row-height", null, properties);
		this.showRowDivider = getBooleanProperty("show-row-divider", true, properties);
		this.stickyHeaders = getBooleanProperty("sticky-headers", false, properties);
		this.paddingBottom = getSizeProperty("padding-bottom", null, properties);
		this.scrollToBottomOnLoad = getBooleanProperty("scroll-to-bottom-on-load", false, properties);
	}

	public void setRowProvider(RowProvider rowProvider) {
		this.rowProvider = rowProvider;
	}

	public Object getRowOrSectionAt(int index) {
		return rowProvider.getRowOrSectionAt(index);
	}

	public long getItemId(int index) {
		return rowProvider.getItemIdAt(index);
	}

	public int getCount() {
		return rowProvider.getCount();
	}

	public double getHeightFromObjectWith(long id) {
		return rowProvider.getHeightFromObjectWith(id);
	}

	public interface RowProvider {

		public String getRowLayout();

		public Object getRowOrSectionAt(int index);

		public TableRow getRowInSectionAt(int sectionIndex, int rowIndex);

		public long getItemIdAt(int index);

		public long getItemIdAt(int sectionIndex, int rowIndex);

		public int getCount();

		public double getHeightFromObjectWith(long id);

		public TableSection getSectionAt(int sectionIndex);

		public int getNumSections();

		public int getNumRowsInSection(int sectionIndex);

		public int getRowIndexOfObject(long id);

		public int getIndexOfRecentlyDeletedObject(long id);
	}

}
