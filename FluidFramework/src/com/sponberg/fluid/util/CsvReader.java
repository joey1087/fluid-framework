package com.sponberg.fluid.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * @author Hans Sponberg
 */
public class CsvReader {

	final private ArrayList<Attribute> attributes = new ArrayList<Attribute>();
	
	final private ArrayList<Row> rows = new ArrayList<Row>();
	
	public CsvReader(BufferedReader in, char sep) throws IOException {
		this(in, sep, 1);
	}
	
	public CsvReader(BufferedReader in, char sep, int headerRows) throws IOException {
	
		if (headerRows < 1) {
			throw new RuntimeException("0 headerRows not supported.");
		}
		
		ArrayList<String> header = tokenize(in.readLine(), sep);
		for (int i = 0; i < header.size(); i++) {
			attributes.add(new Attribute(i, header.get(i)));
		}
		
		for (int i = 1; i < headerRows; i++)
		 {
			in.readLine(); // discard
		}
		
		String line;
		while ( (line = in.readLine()) != null ) {
			
			line = line.trim();
			if (line.equals("")) {
				continue;
			}
			
			Row row = new Row();
			rows.add(row);
			
			ArrayList<String> rowTokens = tokenize(line, sep);
			for (int i = 0; i < attributes.size(); i++) {
				row.map.put(attributes.get(i).name, rowTokens.get(i));
			}
		}
		in.close();
	}
	
	public boolean containsAttribute(String s) {
		for (Attribute a : attributes) {
			if (a.getName().equals(s)) {
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<Attribute> getAttributes() {
		return attributes;
	}

	public ArrayList<Row> getRows() {
		return rows;
	}

	ArrayList<String> tokenize(String line, char sep) {

		ArrayList<String> list = new ArrayList<String>();
		
		StringBuilder token = new StringBuilder();
		boolean insideQuote = false;
		for (int i = 0; i < line.length(); i++) {
			
			char c = line.charAt(i); 
			if (c == sep && !insideQuote) {
				list.add(token.toString());
				token = new StringBuilder();
			} else if (c == sep) {
				if (insideQuote) {
					insideQuote = false;
				} else if (token.length() == 0) {
					insideQuote = true;
				} else {
					token.append(c);
				}
			} else if (c == '\"') {
				insideQuote = true;
			} else {
				token.append(c);
			}
		}
		list.add(token.toString());
		
		return list;
	}
	
	public static class Attribute {
		
		final private int index;
		
		final private String name;
		
		public Attribute(int index, String name) {
			this.index = index;
			this.name = name;
		}

		public int getIndex() {
			return index;
		}

		public String getName() {
			return name;
		}
		
	}
	
	public static class Row {
		
		final private HashMap<String, String> map = new HashMap<String, String>();
		
		public String get(String key) {
			return map.get(key);
		}
		
		public Collection<String> getAllValues() {
			return map.values();
		}
		
	}

}
