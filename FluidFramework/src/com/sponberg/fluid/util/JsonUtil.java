package com.sponberg.fluid.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonObject.Member;
import com.eclipsesource.json.JsonValue;

public class JsonUtil {

	static boolean underscoreSeparatesWords = true; // If false, camelcase is assumed
	
	public static boolean isUnderscoreSeparatesWords() {
		return underscoreSeparatesWords;
	}

	public static void setUnderscoreSeparatesWords(boolean underscoreSeparatesWords) {
		JsonUtil.underscoreSeparatesWords = underscoreSeparatesWords;
	}

	//@SuppressWarnings("unchecked")
	// @SneakyThrows
	public static void setValuesTo(Object object, JsonObject json) {
		try {
			setValuesToHelper(object, json);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	static void setValuesToHelper(Object object, JsonObject json) throws Exception {

		Field keyField = null;

		Class clazz = object.getClass();

		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			
			String name = method.getName();
			if (!name.startsWith("set")) {
				continue;
			}
			
			if (method.getParameterTypes().length != 1) {
				continue;
			}
			
			String fieldName = name.substring(3, 4).toLowerCase() + name.substring(4);
			
			invokeGetAndSet(clazz, fieldName, object, method, json);
		}

	}

	private native static String iOSGetJsonName(Object object, String name) /*-[
	    SEL selector = NSSelectorFromString(name);
	    if ([object respondsToSelector:selector]) {
	        return [object performSelector:selector withObject:nil];
	    } else {
	        return nil;
	    }
	]-*/;
	
	private native static void invokeIOSNativeSet(Object object, String methodName, Object parameter) /*-[		
	
	    SEL selector = NSSelectorFromString(methodName);
	    
	    id ret;
	    if ([methodName rangeOfString:@":"].location == NSNotFound) {
	        id (*response)(id, SEL) = (id (*)(id, SEL)) objc_msgSend;
	        ret = response(object, selector);
	    } else {
	        id (*response)(id, SEL, id) = (id (*)(id, SEL, id)) objc_msgSend;
	        //ret = response(object, selector, parameter);
	    }
	]-*/;
	    
	public static void invokeGetAndSet(Class clazz, String name,
			Object object, Method setter, JsonObject json) throws Exception {

		String jsonName = getJsonName(name);
		
		Class<?> type = setter.getParameterTypes()[0];
		
		Object value = getJsonValue(jsonName, type, json, object);
		
		if (value == null) {
			return;
		}
		
		setter.invoke(object, value);
	}
	
	public static String objectToJsonString(Object object) throws Exception {
		
		JsonObject json = toJsonObject(object);		
		
		return json.toString();
	}

	public static String listToJsonString(List<? extends Object> list) throws Exception {
		
		JsonArray array = listToJsonArray(list);
		
		return array.toString();
	}

	public static JsonArray listToJsonArray(List<? extends Object> list)
			throws Exception, IllegalAccessException, InvocationTargetException {
		
		JsonArray array = new JsonArray();
		
		for (Object o : list) {
			JsonObject json = toJsonObject(o);
			if (json == null) {
				array.add((String) null);
			} else {
				array.add(json.asObject());
			}
		}
		return array;
	}

	public static Object jsonArrayToObjectArray(String jsonArrayString, Class<?> type) throws Exception {
		
		JsonArray jsonArray = JsonArray.readFrom( jsonArrayString );
		
		return createArrayFromJsonObject(jsonArray, type);
	}
	
	public static Object jsonObjectToObject(String jsonString, Class<?> type) throws Exception {
		
		JsonObject object = JsonObject.readFrom(jsonString);
		
		return createObjectFromJsonObject(object, type);
	}
	
	public static Map jsonObjectToMap(String jsonMapString, Class<?> mapType, Class<?> type) throws Exception {
		
		JsonObject jsonObject = JsonObject.readFrom(jsonMapString);
		
		return createMapFromJsonObject(jsonObject, mapType, type);
	}
	
	public static JsonArray arrayToJsonArray(Object[] list)
			throws Exception, IllegalAccessException, InvocationTargetException {
		
		JsonArray array = new JsonArray();
		
		for (Object o : list) {
			JsonObject json = toJsonObject(o);
			if (json == null) {
				array.add((String) null);
			} else {
				array.add(json.asObject());
			}
		}
		return array;
	}

	public static JsonObject mapToJsonObject(Map<String, ?> map) throws Exception {
		
		JsonObject object = new JsonObject();
		
		for (String key : map.keySet()) {
			Object value = map.get(key);
			JsonObject jsonObject = toJsonObject(value);
			object.add(key, jsonObject);
		}
		
		return object;
	}
	
	public static JsonObject toJsonObject(Object object) throws Exception,
			IllegalAccessException, InvocationTargetException {
		
		if (object == null) {
			return null;
		}
		
		JsonObject json = new JsonObject();
		
		Field keyField = null;

		Class clazz = object.getClass();

		Field[] fields = clazz.getDeclaredFields();
		for (Field f : fields) {

			if ((f.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
				// Don't persist a static field
				continue;
			}

			if ((f.getModifiers() & Modifier.TRANSIENT) == Modifier.TRANSIENT) {
				// Don't persist a transient field
				continue;
			}

			if (Collection.class.isAssignableFrom(f.getType())) {

				// Todo Handle collection
				continue;
			} else if (Map.class.isAssignableFrom(f.getType())) {

				// Todo handle map
				continue;
			}

			String name = f.getName();
			
			Method getter = null;
			try {
				getter = MethodUtil.getGetterMethod(clazz, name + "JsonName");
			} catch (NoSuchMethodException e)  {}
			
			String jsonName;
			if (getter != null) {
				jsonName = (String) getter.invoke(object);
			} else {
				jsonName = getJsonName(name);
			}
			
			getter = MethodUtil.getGetterMethod(clazz, name);
			Object value = getter.invoke(object);
			
			if (value == null) {
				continue;
			}
			
			setJsonValue(jsonName, value, json);
		}
		return json;
	}
	
	public static String getJsonName(String name) {
		
		if (!underscoreSeparatesWords) {
			return name;
		}
		
		StringBuffer buf = new StringBuffer();
		
		for (int index = 0; index < name.length(); index++) {
			char c = name.charAt(index);
			if (Character.isUpperCase(c)) {
				buf.append("_");
				buf.append(Character.toLowerCase(c));
			} else {
				buf.append(c);
			}
		}
        
        return buf.toString();
	}
	
	public static Object getJsonValue(String name, Class<?> type, JsonObject json, Object rootObject) throws Exception {
		String[] names = name.split(":");
		return getJsonValue(names, type, json, 0, rootObject);
	}
	
	public static Object getJsonValue(String[] names, Class<?> type, JsonObject json, int i, Object rootObject) 
			throws Exception {
		
		JsonValue value = json.get(names[i]);
		
		if (value == null || value.isNull()) {
			return null;
		}
		
		if (i < names.length - 1) {
			return getJsonValue(names, type, value.asObject(), i + 1, rootObject);
		}
		
		if (type.equals(String.class)) {
			return value.asString();
		} else if (type == Boolean.TYPE || type.equals(Boolean.class)) {
			if (value.isBoolean()) {
				return value.asBoolean();
			} else if (value.isNumber()) {
				return value.asInt() != 0;
			} else {
				return new Boolean(value.asString());
			}
		} else if (type == Integer.TYPE || type.equals(Integer.class)) {
			if (value.isNumber()) {
				return value.asInt();
			} else {
				return new Integer(value.asString());
			}
		} else if (type == Long.TYPE || type.equals(Long.class)) {
			if (value.isNumber()) {
				return value.asLong();
			} else {
				return new Long(value.asString());
			}
		} else if (type == Double.TYPE || type.equals(Double.class)) {
			if (value.isNumber()) {
				return value.asDouble();
			} else {
				return new Double(value.asString());
			}
		} else if (type == Float.TYPE || type.equals(Float.class)) {
			if (value.isNumber()) {
				return value.asFloat();
			} else {
				return new Float(value.asString());
			}
		} else if (type.isArray()) {
			return createArrayFromJsonObject(value, type.getComponentType());
		} else if (List.class.isAssignableFrom(type)) {
			if (!(rootObject instanceof TypeMapper)) {
				throw new RuntimeException("A class that uses this utility to set Map values must implement TypeMapper");
			}
			return createListFromJsonObject(value, type, ((TypeMapper) rootObject).getTypeForField(names[i]));
		} else if (Map.class.isAssignableFrom(type)) {
			if (!(rootObject instanceof TypeMapper)) {
				throw new RuntimeException("A class that uses this utility to set Map values must implement TypeMapper");
			}
			return createMapFromJsonObject(value, type, ((TypeMapper) rootObject).getTypeForField(names[i]));
		} else {
			return createObjectFromJsonObject(value, type);
		}
	}

	protected static Object createArrayFromJsonObject(JsonValue object, Class<?> type) throws Exception {

		JsonArray jsonArray = object.asArray();
		
        Object array = Array.newInstance(type, jsonArray.size());
		
		for (int index = 0; index < jsonArray.size(); index++) {
			Object val = type.newInstance();
			Array.set(array, index, val);
			JsonUtil.setValuesTo(val, jsonArray.get(index).asObject());
		}
		
		return array;
	}

	protected static Map createMapFromJsonObject(JsonValue object, Class<?> mapType, Class<?> objectType) throws Exception {

		Map<String, Object> map = (Map<String, Object>) mapType.newInstance();

		if (object.isArray() && object.asArray().size() == 0) {
			return map;
		}

		for (Member member : object.asObject()) {
			Object val = objectType.newInstance();
			map.put(member.getName(), val);
			JsonUtil.setValuesTo(val, member.getValue().asObject());
		}
		
		return map;
	}
	
	protected static Object createListFromJsonObject(JsonValue object, Class<?> listType, Class<?> objectType) throws Exception {

		List<Object> list = (List<Object>) listType.newInstance();

		JsonArray jsonArray = object.asArray();
		
		for (int index = 0; index < jsonArray.size(); index++) {
			Object val = objectType.newInstance();
			list.add(val);
			JsonUtil.setValuesTo(val, jsonArray.get(index).asObject());
		}
		
		return list;
	}
	
	protected static Object createObjectFromJsonObject(JsonValue object, Class<?> type) throws Exception {

        Object newObject = type.newInstance();
        if (object.isObject()) {
        	JsonUtil.setValuesTo(newObject, object.asObject());
        }
		return newObject;
	}
	
	public static void setJsonValue(String jsonName, Object value, JsonObject json) {
		
		Type type = value.getClass();
		
		if (type == Boolean.class) {
			json.add(jsonName, (Boolean) value);
		} else if (type == Integer.class) {
			json.add(jsonName, (Integer) value);
		} else if (type == Long.class) {
			json.add(jsonName, (Long) value);
		} else if (type == Double.class) {
			Double v = (Double) value;
			json.add(jsonName, v.floatValue());
		} else if (type == Float.class) {
			json.add(jsonName, (Float) value);
		} else {
			json.add(jsonName, value.toString());
		}
	}
	
	public static Method getSetterMethod(Class<?> object, String name, Class<?> type)
			throws Exception {

		if (name.startsWith("is")) {
			name = name.substring(2);
		}
		name = name.substring(0, 1).toUpperCase() + name.substring(1);
		String mName = "set" + name;
		return MethodUtil.getMethod(object, mName, new Class[] { type });
	}

	public static JsonObject getJsonObject(JsonObject object, String key) {
	
		// This is a safe method to catch on empty object returned as an empty array, 
		// which is common from php
		
		JsonValue value = object.get(key);
		
		if (!value.isObject()) {
			
			return new JsonObject();
		} else {
			
			return value.asObject();
		}
	}
	
	public static String getString(JsonObject object, String key) {
		
		return getString(object, key, null);
	}
	
	public static String getString(JsonObject object, String key, String defaultValue) {
		
		JsonValue value = object.get(key);
		
		if (value.isNull() || !value.isString()) {
			
			return defaultValue;
		} else {
			
			return value.asString();
		}
	}
	
	public static interface TypeMapper {
		public Class<?> getTypeForField(String field);
	}
	
}
