package com.sponberg.fluid.datastore;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class SQLResultList<R> implements Iterable<R>, Iterator<R> {

	SQLExecutableQuery query;
	
	LinkedList<R> linkedList = new LinkedList<>();
	
	boolean endReached = false;
	
	public SQLResultList(SQLExecutableQuery query) {
		this.query = query;
	}
	
	public void add(R result) {
		linkedList.add(result);
	}
	
	@Override
	public Iterator<R> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		
		if (endReached) {
			return false;
		} else if (linkedList.size() > 0) {
			return true;
		}
		
		refreshResults();
		if (linkedList.size() == 0) {
			endReached = true;
		}
		return !endReached;
	}

	@Override
	public R next() {
		
		if (endReached) {
			throw new NoSuchElementException();
		} else if (linkedList.size() == 0) {
			refreshResults();
		}
		
		if (linkedList.size() > 0) {
			return linkedList.poll();
		} else {
			endReached = true;
			throw new NoSuchElementException();
		}
	}

	@Override
	public void remove() {
		throw new RuntimeException("Not supported");
	}

	public void refreshResults() throws DatastoreException {
		
		if (query.getLimit() == null) {
			return;
		}
		
		if (!query.isAllowRefresh()) {
			return;
		}
		
		int offset = query.getOffset();
		offset += query.getLimit();
		query.setOffset(offset);
		query.stepQuery();
	}
	
	// The size will change as next() is called
	public int size() {
		return linkedList.size();
	}
	
	public R get(int i) {
		return linkedList.get(i);
	}
	
}
