package com.sponberg.fluid.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

// This class can implement Iterable, but in Objective-C it throws an error
// that the collection was modified. It could be a bug in J2ObjC, or a problem
// with this implementation. In the mean time, the user can simply iterate over
// the rows.
public class TableList<E extends TableRowWithId> { //implements Iterable<E> {

	private final ArrayList<E> rows = new ArrayList<>();
	
	private final HashMap<Long, Integer> rowById = new HashMap<>();
	
	private final HashMap<Long, Integer> recentlyDeletedRowById = new HashMap<>();
	
	private int changeId = 0;
	
	// TODO: evaluate sychronized on row vs ReentrantReadWriteLock
	
	public void add(E row) {
		if (row.getFluidTableRowObjectId() == null) {
			throw new RuntimeException("id may not be null");
		}
		synchronized (rows) {
			changeId++;
			rows.add(row);
			rowById.put(row.getFluidTableRowObjectId(), rows.size() - 1);
		}
	}
	
	public void add(int index, E row) {
		if (row.getFluidTableRowObjectId() == null) {
			throw new RuntimeException("id may not be null");
		}
		synchronized (rows) {
			changeId++;
			rows.add(index, row);
			populateIndexMapFrom(index);
		}
	}

	private void populateIndexMapFrom(int startingIndex) {
		E row;
		for (int i = startingIndex; i < rows.size(); i++) {
			row = rows.get(i);
			rowById.put(row.getFluidTableRowObjectId(), i);
		}
	}
	
	public E getById(Long id) {
		synchronized (rows) {
			Integer rowIndex = getIndex(id);
			if (rowIndex == null) {
				return null;
			} else {
				return rows.get(rowIndex);
			}
		}
	}
	
	public Integer getIndex(Long id) {
		synchronized (rows) {
			return rowById.get(id);
		}
	}
	
	public Integer getIndexOfRecentlyDeleted(Long id) {
		synchronized (rows) {
			return recentlyDeletedRowById.get(id);
		}
	}
	
	public int size() {
		synchronized (rows) {
			return rows.size();
		}
	}
	
	public boolean contains(E row) {
		synchronized (rows) {
			return rowById.containsKey(row.getFluidTableRowObjectId());
		}
	}
	
	public boolean remove(E row) {
		synchronized (rows) {
			changeId++;
			Integer i = rowById.remove(row.getFluidTableRowObjectId());
			if (i == null) {
				return false;
			}
			if (i != null) {
				int rowIndex = i.intValue();
				rows.remove(rowIndex);
				rowById.remove(row.getFluidTableRowObjectId());
				populateIndexMapFrom(rowIndex);
				
				recentlyDeletedRowById.put(row.getFluidTableRowObjectId(), i);
			}
			return i != null;
		}
	}

	protected int removeByIndexAndReturnChangeId(int index) {
		synchronized (rows) {
			changeId++;
			E row = rows.remove(index);
			rowById.remove(row.getFluidTableRowObjectId());
			populateIndexMapFrom(index);
			return changeId;
		}
	}
	
	public List<E> getRows() {
		synchronized (rows) {
			return Collections.unmodifiableList(rows);
		}
	}

	public void addAll(TableList<E> list) {
		for (E e: list.getRows()) {
			add(e);
		}
	}
	
	public void addElements(List<E> elements) {
		for (E e : elements) {
			add(e);
		}
	}
	
	public void sort(Comparator<E> comparator) {
		synchronized (rows) {
			Collections.sort(rows, comparator);
			populateIndexMapFrom(0);
		}
	}
	
	/*
	 * See comment at the top of the class as to why this is commented out
	@Override
	public Iterator<E> iterator() {
		return new TableListIterator(this);
	}*/
	
	public class TableListIterator implements Iterator<E> {

		final TableList<E> list;
		
		int index = 0;
		
		int changeId;
		
		public TableListIterator(TableList<E> list) {
			this.list = list;
			this.changeId = list.changeId;
		}
		
		@Override
		public boolean hasNext() {
			synchronized (rows) {
				if (changeId != list.changeId) {
					throw new ConcurrentModificationException();
				}
				return index < list.size();
			}
		}

		@Override
		public E next() {
			synchronized (rows) {
				if (changeId != list.changeId) {
					throw new ConcurrentModificationException();
				}
				return list.getRows().get(index++);
			}
		}

		@Override
		public void remove() {
			synchronized (rows) {
				if (changeId != list.changeId) {
					throw new ConcurrentModificationException();
				}
				changeId = list.removeByIndexAndReturnChangeId(index - 1);
			}
		}
		
	}
	
}
