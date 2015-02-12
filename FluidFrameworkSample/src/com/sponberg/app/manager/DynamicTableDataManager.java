package com.sponberg.app.manager;

import lombok.Data;

import com.sponberg.fluid.layout.TableList;
import com.sponberg.fluid.layout.TableRowWithId;

@Data
public class DynamicTableDataManager {

	TableList<RowData> data = new TableList<RowData>();
	
	public DynamicTableDataManager() {

		long ctr = 0;
		RowData rowData = new RowData(ctr++);
		rowData.lines = new String[] { "A", "B", "C" };
		data.add(rowData);
		
		rowData = new RowData(ctr++);
		rowData.lines = new String[] { "G" };
		data.add(rowData);
		
		rowData = new RowData(ctr++);
		rowData.lines = new String[] { "W", "X", "Y", "Z" };
		data.add(rowData);

		rowData = new RowData(ctr++);
		rowData.lines = new String[] { "J", "K" };
		data.add(rowData);
		
		rowData = new RowData(ctr++);
		rowData.lines = new String[] { "<b>Lorem</> <i>ipsum</> <u>dolor</> sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", 
					"2" };
		data.add(rowData);
		
		rowData = new RowData(ctr++);
		rowData.lines = new String[] { "1", "2", "3", "4", "5", "6" };
		data.add(rowData);
		
		rowData = new RowData(ctr++);
		rowData.lines = new String[] { "Start", "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.", 
					"End" };
		data.add(rowData);
		
		rowData = new RowData(ctr++);
		rowData.lines = new String[] { "1", "2", "3" };
		data.add(rowData);
		
	}
	
	@Data
	public static class RowData implements TableRowWithId {
		
		String[] lines;
		
		final long id;
		
		public RowData(long id) {
			this.id = id;
		}
		
		public boolean getShowLine(String index) {
			int i = Integer.parseInt(index);
			return lines.length > i;
		}

		public String getLine(String index) {
			int i = Integer.parseInt(index);
			if (lines.length > i) {
				return lines[i];
			} else {
				return "";
			}
		}
		
		@Override
		public Long getFluidTableRowObjectId() {
			return id;
		}

	}
	
}
