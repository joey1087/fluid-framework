package com.sponberg.fluid.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@ToString
@Getter
@Setter
@EqualsAndHashCode
public class TableLayout {

	private final String id;

	private Color backgroundColor;
	
	ArrayList<TableSection> tableSections = new ArrayList<>();
	
	private HashMap<String, Layout> layouts = new HashMap<>();

	public TableLayout(String id) {
		this.id = id;
	}
	
	public void addSection(TableSection section) {
		tableSections.add(section);
	}
	
	public List<TableSection> getTableSections() {
		return tableSections;
	}
	
	public static class TableSection {
		
		final String sectionId;
		
		String sectionHeaderLayout;
		
		ArrayList<String> layoutIds = new ArrayList<>();
		
		public TableSection(String sectionId) {
			this.sectionId = sectionId;
		}
		
		public void addLayout(String layoutId) {
			layoutIds.add(layoutId);
		}
		
		public List<String> getLayoutIds() {
			return layoutIds;
		}

		public String getSectionHeaderLayout() {
			return sectionHeaderLayout;
		}

		public void setSectionHeaderLayout(String sectionHeaderLayout) {
			this.sectionHeaderLayout = sectionHeaderLayout;
		}

		public String getSectionId() {
			return sectionId;
		}
		
	}
	
}
