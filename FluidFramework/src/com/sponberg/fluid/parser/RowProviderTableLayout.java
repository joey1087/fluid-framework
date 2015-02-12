package com.sponberg.fluid.parser;

import java.util.ArrayList;
import java.util.List;

import com.sponberg.fluid.layout.TableLayout;
import com.sponberg.fluid.layout.TableLayout.TableSection;
import com.sponberg.fluid.layout.TableRow;
import com.sponberg.fluid.layout.ViewBehaviorTable.RowProvider;

public class RowProviderTableLayout implements RowProvider {

	final TableLayout tableLayout;
	
	final ArrayList<ArrayList<TableRow>> sectionRows = new ArrayList<>();
	
	public RowProviderTableLayout(TableLayout tableLayout) {
		
		this.tableLayout = tableLayout;
		
		long rowId = 0;
		for (TableSection section : tableLayout.getTableSections()) {
			ArrayList<TableRow> rows = new ArrayList<>();
			sectionRows.add(rows);
			for (String id : section.getLayoutIds()) {
				TableRow tableRow = new TableRow();
				tableRow.setLayout(id);
				tableRow.setId(rowId++);
				tableRow.setListenToDataModelChanges(true); // hstdbc should we make this decision or default to false?
				rows.add(tableRow);
			}
		}		
		
	}
	
	//@Override
	private List<TableRow> getRows(int section) {
		return sectionRows.get(section);
	}
	
	private List<TableSection> getSections() {
		return tableLayout.getTableSections();
	}

	@Override
	public String getRowLayout() {
		return null;
	}
	
	@Override
	public int getCount() {
		int count = 0;
		for (int index = 0; index < getSections().size(); index++) {
			count++;
			count += getRows(index).size();
		}
		return count;
	}
	
	@Override
	// For use by Android, which doesn't have concept of sections
	public Object getRowOrSectionAt(int index) {
		int count = 0;
		for (int sectionIndex = 0; sectionIndex < getSections().size(); sectionIndex++) {
			TableSection section = getSections().get(sectionIndex);
			count++;
			if (count - 1 == index) {
				return section;
			}			
			if (count + getRows(sectionIndex).size() > index) {
				return getRows(sectionIndex).get(index - count);
			}
			count += getRows(sectionIndex).size();
		}
		return null;	
	}

	// For use by iOS
	@Override
	public TableRow getRowInSectionAt(int sectionIndex, int rowIndex) {
		return getRows(sectionIndex).get(rowIndex);
	}

	@Override
	public TableSection getSectionAt(int sectionIndex) {
		return getSections().get(sectionIndex);
	}
	
	@Override
	public int getNumSections() {
		return getSections().size();
	}
	
	 // Android
	@Override
	public long getItemIdAt(int index) {
		Object o = getRowOrSectionAt(index);
		if (o instanceof TableSection) {
			return ((TableSection) o).getSectionId().hashCode(); // hstdbc make sure this doesn't conflict with any row id
		} else if (o instanceof TableRow) {
			return ((TableRow) o).getId();
		} else {
			return -1;
		}
	}

	 // iOS
	@Override
	public long getItemIdAt(int sectionIndex, int rowIndex) {
		return getRowInSectionAt(sectionIndex, rowIndex).getId();
	}

	@Override
	public double getHeightFromObjectWith(long id) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public int getNumRowsInSection(int sectionIndex) {
		return getSectionAt(sectionIndex).getLayoutIds().size();
	}
	
	@Override
	public int getRowIndexOfObject(long id) {
		throw new RuntimeException("Not implemented");
	}
	
	@Override
	public int getIndexOfRecentlyDeletedObject(long id) {
		throw new RuntimeException("Not implemented");		
	}
}
