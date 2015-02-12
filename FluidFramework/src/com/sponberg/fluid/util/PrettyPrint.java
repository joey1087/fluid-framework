package com.sponberg.fluid.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonObject.Member;
import com.eclipsesource.json.JsonValue;

public class PrettyPrint {

	public static <K, V> String toString(Map<K, V> map) {
		if (map == null) {
			return "";
		}
		return toString(map, 0);
	}

	@SuppressWarnings("unchecked")
	protected static <K, V> String toString(Map<K, V> map, int indent) {
		StringBuilder sb = new StringBuilder("\n");
		Iterator<Entry<K, V>> iter = map.entrySet().iterator();
		String indentString = "";
		for (int i = 0; i < indent; i++) {
			indentString += "\t";
		}
		while (iter.hasNext()) {
			Entry<K, V> entry = iter.next();
			sb.append(indentString).append("\"").append(entry.getKey()).append("\"");
			sb.append(" = ");
			V v = entry.getValue();
			if (v instanceof Map) {
				sb.append("{");
				sb.append(toString((Map<K,V>) v, indent + 1));
				sb.append("\n").append(indentString).append("}");
			} else {
				sb.append('"');
				sb.append(entry.getValue());
				sb.append('"');
			}
			if (iter.hasNext()) {
				sb.append(',').append('\n');
			}
		}
		return sb.toString();
	}

	public static String toString(JsonObject object) {
		return toString(object, 0);
	}

	protected static <K, V> String toString(JsonObject object, int indent) {
		StringBuilder sb = new StringBuilder("\n");
		Iterator<Member> iter = object.iterator();
		String indentString = "";
		for (int i = 0; i < indent; i++) {
			indentString += "\t";
		}
		while (iter.hasNext()) {
			Member entry = iter.next();
			sb.append(indentString).append("\"").append(entry.getName()).append("\"");
			sb.append(" = ");
			JsonValue value = entry.getValue();
			if (value.isObject()) {
				sb.append("{");
				sb.append(toString(value.asObject(), indent + 1));
				sb.append("\n").append(indentString).append("}");
			} else if (value.isArray()) {
				for (JsonValue arrValue : value.asArray()) {
					sb.append("[");
					sb.append(toString(arrValue.asObject(), indent + 1));
					sb.append("\n").append(indentString).append("]");
				}
			} else {
				sb.append(value.toString());
			}
			if (iter.hasNext()) {
				sb.append(',').append('\n');
			}
		}
		return sb.toString();
	}

	
}
