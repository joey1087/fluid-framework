package com.sponberg.fluid.layout;

import java.util.ArrayList;
import java.util.Stack;

import lombok.Data;

import com.sponberg.fluid.GlobalState;

@Data
public class AttributedText {

	String text;
	
	ArrayList<Attribute> attributes = new ArrayList<>();
	
	public AttributedText(String text) {
		
		ViewManager viewManager = GlobalState.fluidApp.getViewManager();
		
		if (text == null || text.trim().equals("")) {
			this.text = "";
			return;
		}
		
		Stack<Tag> stack = new Stack<>();
		
		boolean disabled = false;
		
		boolean openTag = false;
		boolean startTag = true;
		StringBuilder tagBuilder = new StringBuilder();
		StringBuilder builder = new StringBuilder();
		int textIndex = 0;
		for (int index = 0; index < text.length(); index++) {
			char c = text.charAt(index);
			if (disabled) {
				builder.append(c);
				textIndex++;
				continue;
			}
			if (c == '<') {
				openTag = true;
				startTag = true;
			} else if (openTag && c == '/') {
				startTag = false;
			} else if (openTag && c == '>') {
				if (startTag) { 
					if (!stack.empty()) {
						stack.peek().setEndIndex(textIndex);
					}
					stack.push(new Tag(tagBuilder.toString(), textIndex));
					tagBuilder = new StringBuilder();
				} else {
					Tag tag = stack.peek();
					tag.setEndIndex(textIndex);

					for (Tag.Range range : tag.ranges) {
						if (range.startIndex == range.endIndex) {
							continue;
						}
						Attribute attribute = new Attribute();
						attributes.add(attribute);
						attribute.startIndex = range.startIndex;
						attribute.endIndex = range.endIndex;
						for (Tag t : stack) {
							String s = t.tag;
							if (s.equalsIgnoreCase("b")) {
								attribute.bold = true;
							} else if (s.equalsIgnoreCase("i")) {
								attribute.italic = true;
							} else if (s.equalsIgnoreCase("u")) {
								attribute.underline = true;
							} else if (s.startsWith("background-color")) {
								Color color = viewManager.getColor(s.substring(17));
								attribute.backgroundColor = color;
							} else if (s.startsWith("color")) {
								Color color = viewManager.getColor(s.substring(6));
								attribute.color = color;
							} else if (s.startsWith("disable-attributed-text")) {
								disabled = true;
							}
						}
					}

					stack.pop();
					if (!stack.empty()) {
						stack.peek().addStartIndex(textIndex);
					}
				}
				openTag = false;
			} else if (openTag) {
				tagBuilder.append(c);
			} else {
				builder.append(c);
				textIndex++;
			}
		}
		
		this.text = builder.toString();
	}
	
	public static class Tag {
		
		String tag;
		
		ArrayList<Range> ranges = new ArrayList<>();
		
		public Tag(String tag, int startIndex) {
			this.tag = tag;
			ranges.add(new Range(startIndex));
		}
		
		public void setEndIndex(int index) {
			ranges.get(ranges.size() - 1).endIndex = index;
		}
		
		public void addStartIndex(int index) {
			ranges.add(new Range(index));
		}
		
		public static class Range {
			int startIndex;
			int endIndex;
			public Range(int startIndex) {
				this.startIndex = startIndex;
			}
		}
	}
	
	@Data
	public static class Attribute {
		
		boolean bold;
		boolean italic;
		boolean underline;
		
		Color backgroundColor;
		Color color;
		
		int startIndex;
		int endIndex;
		
	}
	
}
