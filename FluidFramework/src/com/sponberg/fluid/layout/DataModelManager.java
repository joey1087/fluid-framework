package com.sponberg.fluid.layout;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.sponberg.fluid.FluidApp;
import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.util.LRUCacheTree;
import com.sponberg.fluid.util.MethodUtil;

public class DataModelManager {

	static final boolean kUseObjNativeCall = false;
	
	HashMap<String, Object> dataModels = new HashMap<String, Object>();

	HashMap<String, DataChangeListenerGroup> dataChangeListeners = new HashMap<>();

	HashMap<String, Boolean> listenersEnabled = new HashMap<>();
	
	LRUCacheTree<Object> cache = new LRUCacheTree<>(100);
	
	protected void checkOnMainThread() {
		if (!GlobalState.fluidApp.getSystemService().isOnUiThread()) {
			throw new RuntimeException("Not called on main thread");
		}
	}
	
	public void setDataModel(String key, Object dataModel) {
		
		checkOnMainThread();
		
		dataModels.put(key, dataModel);
	}

	public Object getDataModel(String key) {

		// hstdbc allow to read on a different thread 
		// checkOnMainThread();

		return dataModels.get(key);
	}

	public String getValue(String prefix, String keys, String messageFormat, String defaultText) {
		
		if (keys == null) {
			return defaultText;
		}
		
		if (prefix == null) {
			prefix = "";
		} else {
			prefix += ".";
		}
		
		String[] keyA = keys.split(",");
		if (messageFormat == null && keyA.length > 1) {
			throw new RuntimeException("If passing in multiple keys, messageFormat must be specified");
		}
		Object[] values = new Object[keyA.length];
		for (int index = 0; index < keyA.length; index++) {
			String key = makeKey(prefix, keyA[index]);
			values[index] = getValueHelper(key);
		}
		
		boolean oneIsNull = false;
		for (int index = 0; index < keyA.length; index++) {
			if (values[index] == null) {
				oneIsNull = true;
				break;
			}
		}
		
		if (oneIsNull) {
			return defaultText;
		}
		
		if (messageFormat == null) {
			return values[0].toString();	
		}

		StringBuffer buf = new StringBuffer();
		MessageFormat mf = new MessageFormat(messageFormat);
		mf.format(values, buf, null);
		return buf.toString();
	}

	private static String makeKey(String prefix, String oneKey) {
		
		if (oneKey.startsWith("/")) {
			// ignore prefix
			return oneKey.substring(1);
		}
		
		String key = prefix + oneKey.trim();
		while (key.endsWith(".")) {
			key = key.substring(0, key.length() - 1);
		}
		return key;
	}
	
	protected Object getValueHelper(String key) {
		
		// hstdbc allow to read on a different thread checkOnMainThread();

		String[] tokens = tokenize(key);

		boolean hasParameter = FluidApp.useCaching && hasParameter(key);
		
		Object v;
		if (FluidApp.useCaching && !hasParameter) {
			v = cache.get(tokensWithoutParameter(tokens));
			if (v != null) {
				return v;
			}
		}
		
		v = getValue(getDataModel(tokens[0]), tokens, 1);
		
		if (FluidApp.useCaching && !hasParameter) {
			cache.put(tokensWithoutParameter(tokens), v);
		}
		
		return v;
	}

	protected boolean hasParameter(String key) {
		return key.contains("(");
	}
	
	protected String[] tokenize(String key) {
		
		int i = key.indexOf("(");
		int i2 = -1;
		if (i != -1) {
			i2 = key.indexOf(")", i);
		} else {
			return key.split("\\.");
		}
		
		ArrayList<String> tokens = new ArrayList<String>();
		String prefix = key.substring(0, i + 1);
		String parameter = key.substring(i + 1, i2);
		String suffix = key.substring(i2 + 1);

		for (String s : prefix.split("\\.")) {
			tokens.add(s);
		}
		String temp = tokens.get(tokens.size() - 1) + parameter + ")";
		
		tokens.set(tokens.size() - 1, temp);
		
		if (suffix.length() > 0) {
			for (String s : suffix.substring(1).split("\\.")) { // get past the first dot
				tokens.add(s);
			}
		}
		return tokens.toArray(new String[tokens.size()]);
	}
	
	public Object getObject(String key) {
		String[] tokens = tokenize(key);
		return getValue(getDataModel(tokens[0]), tokens, 1);
	}
	
	public List<?> getValueList(String key) {
		return getValueList(null, key);
	}
	
	public List<?> getValueList(String prefix, String key) {
		
		if (prefix == null) {
			prefix = "";
		} else {
			prefix += ".";
		}
		
		if (key == null) {
			throw new RuntimeException("key may not be null");
		}
		
		key = prefix + key;
		
		String[] tokens = tokenize(key);
		Object value = getValue(getDataModel(tokens[0]), tokens, 1);
		if (value instanceof TableList) {
			return ((TableList) value).getRows();
		} else if (value instanceof Object[]) {
			return Arrays.asList((Object[]) value); 
		} else {
			return (List<?>) value;
		}
	}

	protected Object getValue(Object value, String[] tokens, int token) {
		
		// hstdbc allow to read on a different thread checkOnMainThread();

		//if (value == null)
			//throw new RuntimeException("Value is null for: " + tokens[token - 1]);
		if (value == null) {
			return null;
		}
		
		boolean finalToken = false;
		boolean onlyRootToken = false;
		if (token == tokens.length) {
			finalToken = true;
			onlyRootToken = true;
		} else if (token == tokens.length - 1) {
			finalToken = true;
		}
		
		if (!onlyRootToken) {
			value = getValueReflective(value, tokens[token]);			
		}
		
		if (finalToken) {
			return value;
		} else {
			return getValue(value, tokens, token + 1);
		}
	}

	private Object getValueReflective(Object object, String key) {

		try {
			
			String parameter = null;
			int i = key.indexOf("(");
			if (i != -1) {
				parameter = key.substring(i + 1, key.length() - 1);
				key = key.substring(0, i);
			}
			
			if (parameter != null) {
				if ( (parameter.startsWith("\"") && parameter.endsWith("\"")) ||
						(parameter.startsWith("\'") && parameter.endsWith("\'")) ) {
					
					// literal string
					parameter = parameter.substring(1, parameter.length() - 1);
				} else {
					
					// get from data model
					parameter = getValueHelper(parameter).toString();
				}
			}
			
			return getValueReflectiveHelper(object, key, parameter);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static Object getValueReflectiveHelper(Object object, String key, String parameter)
			throws Exception {
		if (object instanceof TableList) {
			return ((TableList) object).getById(Long.parseLong(key));
		//} hstdbc
		} else if (object instanceof List) {
			return ((List<?>) object).get(Integer.parseInt(key));
		} else if (object instanceof Object[]) {
			return ((Object[]) object)[Integer.parseInt(key)];
		} else {
			Method m = null;
			try {
				if (parameter != null) {
					m = MethodUtil.getGetterMethodWithStringParameter(object.getClass(), key);
				} else {
					m = MethodUtil.getGetterMethod(object.getClass(), key);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (m == null) {
				throw new RuntimeException(
						"Data model does not contain getter for " + key);
			}
			
			// The following is a hack to get around J2ObjC bug. The bug is that this code lives in a static library
			// on iOS, and Reflection can't find the Java source in a project that includes the static library. We check
			// first for the runtime name. If it exists, then we are in a JVM, and the reflection will work. If not,
			// we use native Objective-C code with the objc_msgSend command.
			String javaRuntime = System.getProperties().getProperty(
					"java.runtime.name");
			if (javaRuntime != null || !kUseObjNativeCall) {
				if (parameter != null) {
					return m.invoke(object, parameter);
				} else {
					return m.invoke(object);
				}
			} else {
				return invokeIOSNativeSendMessage(m, object, parameter);
			}
		}
	}

	native static Object invokeIOSNativeSendMessage(Method m, Object object, String parameter) /*-[
	    NSString *methodName = [m internalName];
	    SEL selector = NSSelectorFromString(methodName);
	    
	    id ret;
	    if ([methodName rangeOfString:@":"].location == NSNotFound) {
	        id (*response)(id, SEL) = (id (*)(id, SEL)) objc_msgSend;
	        ret = response(object, selector);
	    } else {
	        id (*response)(id, SEL, id) = (id (*)(id, SEL, id)) objc_msgSend;
	        ret = response(object, selector, parameter);
	    }
	    return ret;
   	]-*/;

	public void setValue(String prefix, String key, Object toValue) {
		
		if (prefix == null) {
			prefix = "";
		} else {
			prefix += ".";
		}

		key = prefix + key;
		
		String[] tokens = tokenize(key);
		
		String[] getterTokens = new String[tokens.length - 1];
		for (int index = 0; index < getterTokens.length; index++) {
			getterTokens[index] = tokens[index];
		}
		
		Object object = getValue(getDataModel(getterTokens[0]), getterTokens, 1);
		DataModelManager.setValueReflective(object, tokens[tokens.length - 1], toValue);
	}

	private static void setValueReflective(Object object, String key, Object value) {
		try {
			setValueReflectiveHelper(object, key, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static void setValueReflectiveHelper(Object object, String key, Object value) throws Exception {
		if (object instanceof List) {
			List<Object> l = (List<Object>) object;
			l.set(Integer.parseInt(key), value);
		} else {
			Method m = null;
			try {
				m = MethodUtil.getSetterMethod(object.getClass(), key, value.getClass());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (m == null) {
				throw new RuntimeException(
						"Data model does not contain setter for " + key);
			}
			
			// The following is a hack to get around J2ObjC bug. The bug is that this code lives in a static library
			// on iOS, and Reflection can't find the Java source in a project that includes the static library. We check
			// first for the runtime name. If it exists, then we are in a JVM, and the reflection will work. If not,
			// we use native Objective-C code with the objc_msgSend command.
			String javaRuntime = System.getProperties().getProperty(
					"java.runtime.name");
			if (javaRuntime != null || !kUseObjNativeCall) {
				m.invoke(object, value);
			} else {
				//invokeIOSNativeSendMessage(m, object, value);
				throw new RuntimeException("Not supported");
			}
		}
	}

	
	public void addDataChangeListener(String prefix, String keys, String listenerId,
			DataChangeListener dataChangeListener) {
		addDataChangeListener(prefix, keys, listenerId, false, dataChangeListener);
	}
	
	public void addDataChangeListener(String prefix, String keys, String listenerId,
			 boolean listenForChildren, DataChangeListener dataChangeListener) {
		
		if (prefix == null || prefix.isEmpty()) {
			prefix = "";
		} else {
			prefix += ".";
		}
		
		String[] keyA = keys.split(",");
		for (String k : keyA) {
			
			// If this is parameterized, remove the parameter
			k = withoutParameter(k);
			
			String key = makeKey(prefix, k);
			addDataChangeListener(key, listenerId, listenForChildren, dataChangeListener);
		}
		
	}

	private String withoutParameter(String k) {
		int i = k.indexOf("(");
		if (i != -1) {
			k = k.substring(0, i);
		}
		return k;
	}

	private String[] tokensWithoutParameter(String[] tokens) {
		
		String[] t = new String[tokens.length];
		for (int index = 0; index < tokens.length; index++) {
			t[index] = withoutParameter(tokens[index]);
		}
		return t;
	}
	
	public static String getFullKey(String prefix, String key) {
		if (prefix == null || prefix.isEmpty()) {
			prefix = "";
		} else {
			prefix += ".";
		}
		return makeKey(prefix, key);
	}
	
	public void addDataChangeListener(String key, String listenerId,
			DataChangeListener dataChangeListener) {
		addDataChangeListener(key, listenerId, false, dataChangeListener);
	}	
	
	public void addDataChangeListener(String key, String listenerId,
			boolean listenForChildren, DataChangeListener dataChangeListener) {
		
		// hstdbc ok to add if not on main thread? checkOnMainThread();

		DataChangeListenerWrapper dclWrapper = new DataChangeListenerWrapper();
		dclWrapper.listener = dataChangeListener;
		dclWrapper.dataChangeListenerId = listenerId;
		dclWrapper.listenForChildren = listenForChildren;
		
		key = makeKey("", key);
		String[] tokens = key.split("\\.");
		
		addDataChangeListenerHelper(dataChangeListeners, tokens, 0,
				dclWrapper);
		listenersEnabled.put(listenerId, true);
	}

	private void addDataChangeListenerHelper(
			HashMap<String, DataChangeListenerGroup> dataChangeListeners,
			String[] tokens, int index, DataChangeListenerWrapper dataChangeListener) {

		DataChangeListenerGroup group = dataChangeListeners.get(tokens[index]);
		if (group == null) {
			group = new DataChangeListenerGroup();
			dataChangeListeners.put(tokens[index], group);
		}

		if (index == tokens.length - 1) {
			group.listeners.add(dataChangeListener);
		} else {
			addDataChangeListenerHelper(group.listenerGroups, tokens,
					index + 1, dataChangeListener);
		}
	}

	public void removeDataChangeListenersForChildrenOf(String key) {
		
		this.removeDataChangeListenersFor(key, false, true);
	}
	
	public void removeDataChangeListenersFor(String key, boolean includeChildren) {
		
		this.removeDataChangeListenersFor(key, true, includeChildren);
	}
	
	private void removeDataChangeListenersFor(String key, boolean includeExact, boolean includeChildren) {
		
		checkOnMainThread();

		String[] tokens = key.split("\\.");
		
		removeDataChangeListenersForHelper(dataChangeListeners, tokens, 0, includeExact, includeChildren);
	}	
	
	private void removeDataChangeListenersForHelper(
			HashMap<String, DataChangeListenerGroup> dataChangeListeners,
			String[] tokens, int index,
			boolean includeExact, boolean includeChildren) {

		DataChangeListenerGroup group = dataChangeListeners.get(tokens[index]);
		if (group == null) {
			return;
		}

		if (index == tokens.length - 1) {
			group.removeListenersRecursive(includeExact, includeChildren);
		} else {
			removeDataChangeListenersForHelper(group.listenerGroups, tokens, index + 1, includeExact, includeChildren);
		}
	}
	
	public void removeDataChangeListener(String listenerId) {
		
		checkOnMainThread();

		for (DataChangeListenerGroup group : dataChangeListeners.values()) {
			removeDataChangeListenerHelper(group, listenerId);
		}
		listenersEnabled.remove(listenerId);
	}
	
	public void removeDataChangeListenerHelper(DataChangeListenerGroup group, String listenerId) {
		for (Iterator<DataChangeListenerWrapper> i = group.listeners.iterator(); i.hasNext();) {
			if (i.next().dataChangeListenerId.equals(listenerId)) {
				i.remove();
			}
		}
		for (DataChangeListenerGroup g : group.listenerGroups.values()) {
			removeDataChangeListenerHelper(g, listenerId);
		}
	}
	
	public void dataDidChange(String key, String...subKeys) {
		dataDidChange(false, true, key, subKeys);
	}
	
	public void dataDidChange(boolean notifyListeners, String key, String...subKeys) {
		dataDidChange(false, notifyListeners, key, subKeys);
	}
	
	public void dataWasRemoved(String key) {
		dataDidChange(true, true, key);
		removeDataChangeListenersFor(key, true);
	}
	
	private void dataDidChange(boolean removed, boolean notifyListeners, String key, String...subKeys) {
		
		checkOnMainThread();

		HashSet<DataChangeListenerWrapper> dclWrappers = new HashSet<>();
		
		String[] tokens = key.split("\\.");
		
		String[] tokensWithoutParameter = tokensWithoutParameter(tokens);
		
		if (subKeys.length == 0) {
			cache.remove(tokensWithoutParameter);
		} else {
			String[] keyChain = new String[tokens.length + 1];
			System.arraycopy(tokens, 0, keyChain, 0, tokens.length);
			for (String subkey : subKeys) {
				keyChain[keyChain.length - 1] = subkey;
				cache.remove(tokensWithoutParameter);
			}
		}

		if (!notifyListeners) {
			return;
		}
		
		dataDidChangeHelper(dataChangeListeners, tokens, 0, subKeys,
				dclWrappers);
		for (DataChangeListenerWrapper dclWrapper : dclWrappers) {
			Boolean enabled = listenersEnabled.get(dclWrapper.dataChangeListenerId);
			if (enabled != null && enabled) {
				if (removed) {
					dclWrapper.listener.dataRemoved(key);
				} else {
					dclWrapper.listener.dataChanged(key, subKeys);
				}
			}
		}
	}
	
	private void dataDidChangeHelper(
			HashMap<String, DataChangeListenerGroup> dataChangeListeners,
			String[] tokens, int index, String[] subKeys,
			HashSet<DataChangeListenerWrapper> listeners) {
		
		DataChangeListenerGroup group = dataChangeListeners.get(tokens[index]);
		if (group == null) {
			return;
		}
		
		if (index == tokens.length - 1) {
			if (subKeys.length > 0) {
				for (String subkey : subKeys) {
					if (group.listenerGroups.get(subkey) == null) {
						System.out.println("Warning: subkey: " + subkey + " not a property of " + tokens[index]);
						continue;
					}
						
					DataChangeListenerGroup listenerGroup = group.listenerGroups.get(subkey);
					if (listenerGroup != null) {
						listenerGroup.addListenersRecursive(listeners);
					}
				}
			} else {
				group.addListenersRecursive(listeners);
			}
		} else {
			
			for (DataChangeListenerWrapper wrapper : group.listeners) {
				if (wrapper.listenForChildren) {
					listeners.add(wrapper);
				}
			}
			
			dataDidChangeHelper(group.listenerGroups, tokens,
					index + 1, subKeys, listeners);
		}
	}
	
	public void enableDataChangeListener(String listenerId) {

		checkOnMainThread();

		listenersEnabled.put(listenerId, true);
	}
	
	public void disableDataChangeListener(String listenerId) {
		
		checkOnMainThread();

		listenersEnabled.put(listenerId, false);
	}
	
	static class DataChangeListenerGroup {

		HashMap<String, DataChangeListenerGroup> listenerGroups = new HashMap<>();

		ArrayList<DataChangeListenerWrapper> listeners = new ArrayList<>();

		public void addListenersRecursive(HashSet<DataChangeListenerWrapper> listeners) {
			listeners.addAll(this.listeners);
			for (DataChangeListenerGroup g : listenerGroups.values()) {
				g.addListenersRecursive(listeners);
			}
		}
		
		public void removeListenersRecursive(boolean includeExact, boolean includeChildren) {
			
			if (includeExact) {
				listeners.clear();
			}
			
			if (includeChildren) {
				for (DataChangeListenerGroup g : listenerGroups.values()) {
					g.removeListenersRecursive(true, true);
				}
				listenerGroups.clear();
			}
		}
		
	}
	
	static class DataChangeListenerWrapper {
		
		DataChangeListener listener;
		String dataChangeListenerId;
		boolean listenForChildren = false;
		
		// Use the DataChangeListener object for the hashCode/equals, 
		// because when notifying listeners of a data change, they
		// are collected into a HashSet. This way, the listener can
		// be notified only once per change, which reduces the amount
		// of work the listener will perform. The listener often updates
		// all the views of a UI screen, so this savings can be substantial.
		
		@Override
		public int hashCode() {
			return listener.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof DataChangeListenerWrapper)) {
				return false;
			}
			DataChangeListenerWrapper o2 = (DataChangeListenerWrapper) obj;
			return listener.equals(o2.listener);
		}
	}
	
	public List<String> getConditionalKeys(String fluidConditionSyntax, String dataModelPrefix) {
		ArrayList<String> conditionalKeys = new ArrayList<>();
		checkCondition(fluidConditionSyntax, dataModelPrefix, conditionalKeys);
		return conditionalKeys;
	}
	
	public boolean checkCondition(String fluidConditionSyntax, String dataModelPrefix) {
		
		return checkCondition(fluidConditionSyntax, dataModelPrefix, null);
	}
	
	protected boolean checkCondition(String fluidConditionSyntax, String dataModelPrefix, Collection<String> conditionalKeys) {
	
		String[] conditions = fluidConditionSyntax.split("&&");
		
		boolean result = true;
		for (String condition : conditions) {
			result &= checkIndividualCondition(condition.trim(), dataModelPrefix, conditionalKeys);
		}
		
		return result;
	}
	
	protected boolean checkIndividualCondition(String fluidConditionSyntax, String dataModelPrefix, Collection<String> conditionalKeys) {
		
		String[] tokens = fluidConditionSyntax.split(" ");
		
		if (dataModelPrefix == null) {
			dataModelPrefix = "";
		} else if (!dataModelPrefix.endsWith(".")) {
			dataModelPrefix += ".";
		}
			
		String sideA;
		String operator;
		String sideB;
		if (tokens.length == 1) {
			operator = kEqual;
			if (fluidConditionSyntax.startsWith("!")) {
				operator = kNotEqual;
				fluidConditionSyntax = fluidConditionSyntax.substring(1);
			}
			sideA = dataModelPrefix + fluidConditionSyntax;
			sideB = "true";
		} else {
			sideA = dataModelPrefix + tokens[0];
			operator = tokens[1];
			sideB = tokens[2];
		}
		
		return evaluateCondition(sideA, operator, sideB, conditionalKeys);
	}
	
	final static String kEqual = "==";
	final static String kNotEqual = "!=";
	
	protected boolean evaluateCondition(String sideA, String operator, String sideB, Collection<String> conditionalKeys) {
		
		if (conditionalKeys != null) {
			conditionalKeys.add(sideA);
		}
		
		String resultA = GlobalState.fluidApp.getDataModelManager().getValue(null, sideA, null, null);
		String resultB;
		if (sideB.equals("true") || sideB.equals("false")) {
			
			resultB = sideB;
		} else if (sideB.equals("''") || sideB.equals("\"\"")) {
			
			resultB = "''";
		} else if ( (sideB.startsWith("'") && sideB.endsWith("'")) || 
					(sideB.startsWith("\"") && sideB.endsWith("\"")) ) {
			
			resultB = sideB.substring(1, sideB.length() - 1);
		} else if (sideB.matches("\\d+")) {
			
			resultB = sideB;
		} else {
			if (conditionalKeys != null) {
				conditionalKeys.add(sideB);
			}
			resultB = GlobalState.fluidApp.getDataModelManager().getValue(null, sideB, null, null);
		}
		
		boolean equals;
		if (resultA == null && (resultB == null || resultB.equals("''") || sideB.equals("\"\""))) {
			equals = true;
		} else if (resultA == null || resultB == null) {
			equals = false;
		} else if (resultB.equals("''") || sideB.equals("\"\"")) {
			equals = resultA.isEmpty();
		} else {
			equals = resultA.equals(resultB);
		}
		
		if (operator.equals(kEqual)) {
			return equals;
		} else {
			return !equals;
		}
	}
	
}
