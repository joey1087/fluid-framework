package com.sponberg.fluid.test;

import java.util.HashMap;

public class MockCounter {

	static class MockNode {
		
		int count = 0;
		
		HashMap<String, MockNode> nodes = new HashMap<>();
		
	}
	
	MockNode root = new MockNode();
	
	public void increment(String...path) {
		
		incrementHelper(path, 0, root);	
	}
	
	void incrementHelper(String[] path, int index, MockNode parent) {
		
		MockNode node = parent.nodes.get(path[index]);
		if (node == null) {
			node = new MockNode();
			parent.nodes.put(path[index], node);
		}
		
		if (path.length - 1 == index) {
			node.count++;
		} else {
			incrementHelper(path, index + 1, node);
		}
	}
	
	public int getCount(String...path) {
		
		return getCountHelper(path, 0, root);
	}
	
	int getCountHelper(String[] path, int index, MockNode parent) {
		
		MockNode node = parent.nodes.get(path[index]);
		if (node == null) {
			node = new MockNode();
			parent.nodes.put(path[index], node);
		}
		
		if (path.length - 1 == index) {
			return node.count;
		} else {
			return getCountHelper(path, index + 1, node);
		}
	}
	
}
