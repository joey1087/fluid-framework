package com.sponberg.fluid.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

// 
/**
 * Key-ValueList Reader
 * 
 * @author Hans Sponberg
 */
public class KVLReader implements KeyValueListModifyable {

	KeyValueListModifyable root = new KeyValueListDefault("root");
	
	static final List<KeyValueListModifyable> emptyList = Collections.unmodifiableList(new ArrayList<KeyValueListModifyable>());
	
	static final List<String> emptyValueList = Collections.unmodifiableList(new ArrayList<String>());
	
	public KVLReader(String data) throws IOException {
		
		BufferedReader in = new BufferedReader(new StringReader(data));
		try {
			init(in);
		} finally {
			in.close();
		}
	}
	
	public KVLReader(BufferedReader in) throws IOException {
		init(in);
	}
	
	private void init(BufferedReader in) throws IOException {
		
		Stack<KeyValueListModifyable> stack = new Stack<KeyValueListModifyable>();
		stack.push(root);
		
		Stack<String> keys = new Stack<String>();		
		
		String line;
		while ( (line = in.readLine()) != null) {
			
			// Strip comments
			int i = line.indexOf("#");
			while (i != -1) {
				int i2 = -1;
				if (i > 0) {
					i2 = line.indexOf("\\", i - 1);
				}
				if (i != -1) {
					if (i2 != -1 && i2 == i - 1) {
						line = line.substring(0, i2) + line.substring(i);
						i = line.indexOf("#", i);
					} else {
						line = line.substring(0, i);
						break;
					}
				}
			}
			
			if (line.trim().equals("")) {
				// No keys or values on this line
				continue;
			}

			// Find number of tabs
			char c = line.charAt(0);
			int numTabs = 0;
			while (c == '\t') {
				numTabs++;
				c = line.charAt(numTabs);
			}

			// Remove tabs, and trim right
			line = line.substring(numTabs).replaceFirst("\\s+$", "");
			
			int stackPosition = numTabs / 2;
			while (stack.size() > stackPosition + 1) {
				stack.pop();
			}
			
			stackPosition = (numTabs + 1) / 2;
			while (keys.size() > stackPosition) {
				keys.pop();
			}
			
			if (numTabs % 2 == 0) {
				// This is a key
				String key = line;
				if (!key.endsWith(":")) {
					throw new RuntimeException("Key " + key + " must end with a ':'");
				}
				keys.push(key.substring(0, key.length() - 1));
			} else {
				// This is a value
				String value = line;
				KeyValueListModifyable currentKvl = stack.peek();
				KeyValueListModifyable newKvl = new KeyValueListDefault(value);
				currentKvl.add(keys.peek(), newKvl);
				stack.push(newKvl);
			}	
		}
	}

	@Override
	public List<? extends KeyValueList> get(String key) {
		return root.get(key);
	}
	
	@Override
	public KeyValueList getWithValue(String key, String value) {
		return root.getWithValue(key, value);
	}
	
	@Override
	public boolean contains(String key) {
		List<? extends KeyValueList> list = get(key);
		return (list != null);
	}
	
	@Override
	public Set<String> keys() {
		return root.keys();
	}
	
	@Override
	public String getValue(String key) {
		return root.getValue(key);
	}
	
	@Override
	public List<String> getValues(String key) {
		return root.getValues(key);
	}
	
	@Override
	public void add(String key, KeyValueList newKvl) {
		root.add(key, newKvl);
	}

	@Override
	public void remove(String key) {
		root.remove(key);
	}
	
	@Override
	public void removeByValue(String key, String value) {
		root.removeByValue(key, value);
	}
	
	@Override
	public void setToValue(String key, KeyValueList newKvl) {
		root.setToValue(key, newKvl);
	}
	
	@Override
	public String getValue() {
		return null;
	}
	
	public void overwriteSettingsFrom(KeyValueListModifyable fromReader) {
		overwriteSettings(new Stack<String>(), fromReader, this);
	}
	
	protected void overwriteSettings(Stack<String> keys, KeyValueListModifyable fromReader, KeyValueListModifyable toReaderRoot) {
		
		for (String key : fromReader.keys()) {
			
			keys.push(key);
			
			List<KeyValueListModifyable> fromList = (List<KeyValueListModifyable>) fromReader.get(key);
			
			for (KeyValueListModifyable kvlFrom : fromList) {
				
				if (kvlFrom.keys().size() == 0) {
					writeValue(toReaderRoot, 0, keys, kvlFrom.getValue());
				} else {
					keys.push(kvlFrom.getValue());
					overwriteSettings(keys, kvlFrom, toReaderRoot);
					keys.pop();
				}
				
			}
			
			keys.pop();
		}
	}
	
	protected void writeValue(KeyValueListModifyable writeToKvl, int index, Stack<String> keys, String value) {
		
		String key = keys.get(index);
		
		List<KeyValueListModifyable> writeToKvlList = (List<KeyValueListModifyable>) writeToKvl.get(key);
		
		if (index == keys.size() - 1) {
			if (writeToKvlList != null && writeToKvlList.size() > 1) {
				throw new RuntimeException("Unexpected, this should be a single leaf");
			} else {
				writeToKvl.setToValue(key, new KeyValueListDefault(value));
			}
		} else {
			
			String kvlValue = keys.get(index + 1);
			
			KeyValueListModifyable kvlTo = null;
			if (writeToKvlList != null) {
				kvlTo = findOnListByValue(writeToKvlList, kvlValue);
			} 
			
			if (kvlTo == null) {
				kvlTo = new KeyValueListDefault(kvlValue);
				writeToKvl.add(key,  kvlTo);
			}
			writeValue(kvlTo, index + 2, keys, value);
		}
	}
			
	/*
	protected void hans() {
			List<KeyValueListModifyable> toList = (List<KeyValueListModifyable>) getOrReturnEmpty(toReader, key);
			
			if (toList.size() == 1) {
				
				KeyValueListModifyable kvlTo = toList.get(0);
				if (findOnListByValue(kvlTo, fromList) == null) {
					
				}
			}
			
			for (KeyValueListModifyable kvlFrom : fromList) {
				
				KeyValueListModifyable kvlTo = findOnListByValue(kvlFrom, toList);
				if (kvlTo != null) {
					
					overwriteSettings(kvlFrom, kvlTo);
				} else {

					if (toList.size() == 1) {
						// The original list has only value under this key, replace it
						toReader.removeByValue(key, value);
					} else {
						// The original list has a list of values here, so add this one to the list
						toReader.add(key, kvlFrom);
					}
				}
				
			}
			
			ArrayList<String> valuesToRemove = new ArrayList<>();
			for (KeyValueListModifyable kvlTo: toList) {
				
				KeyValueListModifyable kvlFrom = findOnListByValue(fromList, kvlTo);
				if (kvlFrom == null) {
					
					valuesToRemove.add(kvlTo.getValue());
				}
			}
			for (String value : valuesToRemove)
				toReader.removeByValue(key, value);
			
		}
		
	}*/
	
	protected KeyValueListModifyable findOnListByValue(List<KeyValueListModifyable> list, String value) {
		for (KeyValueListModifyable searchKvl : list) {
			if (searchKvl.getValue().equals(value)) {
				return searchKvl;
			}
		}
		return null;
	}
	
	protected List<KeyValueListModifyable> getOrReturnEmpty(KeyValueList list, String key) {
		
		if (list.contains(key)) {
			return (List<KeyValueListModifyable>) list.get(key);
		} else {
			return emptyList;
		}
	}
	
	public static class KeyValueListDefault implements KeyValueListModifyable {
		
		private final String value;
		
		private final HashMap<String, ArrayList<KeyValueList>> kvl = new HashMap<String, ArrayList<KeyValueList>>();
		
		public KeyValueListDefault(String value) {
			this.value = value;
		}
		
		@Override
		public void add(String key, KeyValueList newKvl) {
			ArrayList<KeyValueList> list = kvl.get(key);
			if (list == null) {
				list = new ArrayList<KeyValueList>();
				kvl.put(key, list);
			}
			list.add(newKvl);
		}
	
		@Override
		public void setToValue(String key, KeyValueList newKvl) {
			ArrayList<KeyValueList> list = kvl.get(key);
			if (list == null) {
				list = new ArrayList<KeyValueList>();
				kvl.put(key, list);
			} else {
				list.clear();
			}
			list.add(newKvl);
		}
		
		@Override
		public void removeByValue(String key, String value) {
			ArrayList<KeyValueList> list = kvl.get(key);
			if (list != null) {
				for (Iterator<KeyValueList> i = list.iterator(); i.hasNext();) {
					if (i.next().getValue().equals(value)) {
						i.remove();
						break;
					}
				}
			}
		}
		
		@Override
		public List<KeyValueList> get(String key) {
			return kvl.get(key);
		}
		
		@Override
		public KeyValueList getWithValue(String key, String value) {
			List<KeyValueList> list = get(key);
			if (list == null) {
				return null;
			}
			for (KeyValueList searchKvl : list) {
				if (searchKvl.getValue().equals(value)) {
					return searchKvl;
				}
			}
			return null;
		}
		
		@Override
		public boolean contains(String key) {
			return kvl.get(key) != null;
		}
		
		@Override
		public Set<String> keys() {
			return kvl.keySet();
		}
		
		@Override
		public String getValue(String key) {
			
			List<KeyValueList> list = get(key);
			
			if (list == null) {
				throw new RuntimeException("No value for " + key);
			}
			
			if (list.size() == 1) {
				return list.get(0).getValue();
			}
			
			StringBuilder b = new StringBuilder(list.get(0).getValue());
			for (int index = 1; index < list.size(); index++) {
				b.append("; " + list.get(index).getValue());
			}
			return b.toString();
		}
		
		@Override
		public List<String> getValues(String key) {
			
			if (!contains(key)) {
				return emptyValueList;
			}
			
			ArrayList<String> values = new ArrayList<String>();
			for (KeyValueList kvl : get(key)) {
				values.add(kvl.getValue());
			}
			return values;
		}
		
		@Override
		public String getValue() {
			return value;
		}
		
		@Override
		public String toString() {
			return value;
		}

		@Override
		public void remove(String key) {
			kvl.remove(key);
		}
	}

}
