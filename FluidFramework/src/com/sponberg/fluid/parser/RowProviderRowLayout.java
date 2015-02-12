package com.sponberg.fluid.parser;

import java.util.ArrayList;
import java.util.List;

import com.sponberg.fluid.FluidApp;
import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.layout.DataChangeListener;
import com.sponberg.fluid.layout.TableLayout.TableSection;
import com.sponberg.fluid.layout.TableList;
import com.sponberg.fluid.layout.TableRow;
import com.sponberg.fluid.layout.TableRowWithId;
import com.sponberg.fluid.layout.TableRowWithIdAndHeight;
import com.sponberg.fluid.layout.ViewBehaviorTable.RowProvider;
import com.sponberg.fluid.util.Logger;

public class RowProviderRowLayout implements RowProvider {

	TableList<TableRowWithId> list = null;

	List<TableRow> rowItems = null;
	
	final String dataModelKey;
	
	final String rowLayout;
	
	final String listenerId;
	
	TableSection defaultSection = new TableSection("default");
	
	public RowProviderRowLayout(String dataModelKey, String rowLayout) {
		
		this.dataModelKey = dataModelKey;
		this.rowLayout = rowLayout;
		
		listenerId = "rows-provider-" + dataModelKey + "-" + rowLayout;
	}
	
	protected void populate() {
		
		if (list != null) {
			return;
		}
		
		final FluidApp app = GlobalState.fluidApp;

		this.list = (TableList<TableRowWithId>) app.getDataModelManager().getObject(dataModelKey);
		this.rowItems = new ArrayList<>();
		
		for (TableRowWithId tableRowWithId : this.list.getRows()) {
			TableRow row = new TableRow();
			row.setLayout(rowLayout);
			row.setKey(dataModelKey + "." + tableRowWithId.getFluidTableRowObjectId());
			row.setId(tableRowWithId.getFluidTableRowObjectId());

			this.rowItems.add(row);
		}
		
		GlobalState.fluidApp.getDataModelManager().addDataChangeListener(dataModelKey, listenerId, true, new DataChangeListener() {
			
			@Override
			public void dataRemoved(String key) {
				list = null;
				rowItems = null;
				GlobalState.fluidApp.getDataModelManager().removeDataChangeListener(listenerId);
			}
			
			@Override
			public void dataChanged(String key, String... subKeys) {
				list = null;
				rowItems = null;
				GlobalState.fluidApp.getDataModelManager().removeDataChangeListener(listenerId);
			}
		});
		
	}
	
	TableList<TableRowWithId> getList() {

		populate();
		
		return this.list;
	}

	List<TableRow> getRows() {
		
		populate();
		
		return rowItems;
	}
	
	@Override
	public int getCount() {
		return getRows().size() + 1;
	}
	
	public Object getRowItemAt(int index) {
		return getRows().get(index);
	}
	
	@Override
	public String getRowLayout() {
		return rowLayout;
	}

	@Override
	public Object getRowOrSectionAt(int index) {
		if (index == 0) {
			return defaultSection;
		} else {
			return getRows().get(index - 1);
		}
	}
	
	@Override
	public long getItemIdAt(int index) {
		if (index == 0) {
			return 0;
		} else {
			return getRows().get(index - 1).getId();
		}
	}

	@Override
	public double getHeightFromObjectWith(long id) {
		TableRowWithIdAndHeight row = (TableRowWithIdAndHeight) getList().getById(id);
		if (row == null) {
			Logger.warn(this, "Row is null for id {}", id);
			return 0;
		}
		return row.getFluidTableRowHeight();
	}

	@Override
	public TableRow getRowInSectionAt(int sectionIndex, int rowIndex) {
		if (sectionIndex == 0) {
			return getRows().get(rowIndex);
		} else {
			throw new RuntimeException("Excpected sectionIndex == 0, was " + sectionIndex);
		}
	}

	@Override
	public long getItemIdAt(int sectionIndex, int rowIndex) {
		return getRowInSectionAt(sectionIndex, rowIndex).getId();
	}

	@Override
	public TableSection getSectionAt(int sectionIndex) {
		return null;
	}

	@Override
	public int getNumSections() {
		return 1;
	}

	@Override
	public int getNumRowsInSection(int sectionIndex) {
		if (sectionIndex == 0) {
			return getRows().size();
		} else {
			throw new RuntimeException("Excpected sectionIndex == 0, was " + sectionIndex);
		}
	}

	@Override
	public int getRowIndexOfObject(long id) {
		return getList().getIndex(id);
	}
	
	@Override
	public int getIndexOfRecentlyDeletedObject(long id) {
		return getList().getIndexOfRecentlyDeleted(id);
	}
	
}
