package com.sponberg.fluid.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

public class MethodUtil {

	static HashMap<Class<?>, HashMap<MethodSignature, Method>> classMethodMap = new HashMap<Class<?>, HashMap<MethodSignature, Method>>();

	// Do we actually don't need this class?
	// Probably not, and cache can probably be set to false
	// Looking up some methods does not take very long at all
	// Looking up   100 methods takes about 0 milliseconds
	// Looking up  1000 methods takes about 16 milliseconds, 1 millisecond less than caching
	// Looking up 10000 methods takes about 46 milliseconds
	
	static boolean cache = false;

	private static HashMap<MethodSignature, Method> getMethods(Class<?> c) {
		HashMap<MethodSignature, Method> methodMap = classMethodMap.get(c);
		if (methodMap == null) {
			methodMap = new HashMap<MethodSignature, Method>();
			classMethodMap.put(c, methodMap);
			
			Method[] methods = c.getMethods();
			for (Method method : methods) {
				methodMap.put(new MethodSignature(method.getName(), method
						.getParameterTypes()), method);
			}
		}
		return methodMap;
	}

	public static Collection<Method> getAllMethods(Class<?> c) {
		if (!cache) {
			return Arrays.asList(c.getMethods());
		}

		HashMap<MethodSignature, Method> methodMap = getMethods(c);
		return methodMap.values();
	}

	public static Method getMethod(Class<?> c, String name,
			Class<?>[] parameters) throws NoSuchMethodException {
		if (!cache) {
			return c.getMethod(name, parameters);
		}

		if (parameters == null) {
			parameters = new Class<?>[0];
		}

		Method method = getMethods(c)
				.get(new MethodSignature(name, parameters));

		if (method == null) {
			StringBuffer msg = new StringBuffer();
			msg.append("No such method " + name + "(");
			for (int index=0; index < parameters.length; index++) {
				msg.append(parameters[index].getSimpleName() + 
					((index < parameters.length - 1) ? ", " : "") );
			}
			msg.append(") on class " + c.getName());
			throw new NoSuchMethodException(msg.toString());
		} else {
			return method;
		}
	}
	
	public static Method getGetterMethod(Class<?> rowObject, String name)
		throws Exception {
		if (name.startsWith("is") || name.startsWith("has")) {
			try {
				return MethodUtil.getMethod(rowObject, name,
						new Class[0]);
			} catch (NoSuchMethodException e) {
				name = name.substring(0, 1).toUpperCase() + name.substring(1);
				String mName = "is" + name;
				return MethodUtil.getMethod(rowObject, mName,
					new Class[0]);				
			}
		}
		name = name.substring(0, 1).toUpperCase() + name.substring(1);
		try {
			String mName = "get" + name;
			return MethodUtil.getMethod(rowObject, mName,
				new Class[0]);
		} catch (NoSuchMethodException e) {
			// Try is
			String mName = "is" + name;
			return MethodUtil.getMethod(rowObject, mName,
				new Class[0]);
		}
	}

	public static Method getGetterMethodWithStringParameter(Class<?> rowObject, String name)
			throws Exception {
			name = name.substring(0, 1).toUpperCase() + name.substring(1);
			String mName = "get" + name;
			Class[] parameterTypes = { String.class };
			return MethodUtil.getMethod(rowObject, mName,
				parameterTypes);
		}

	public static Method getSetterMethod(Class<?> object, String name, Class<?> type)
			throws Exception {

		name = name.substring(0, 1).toUpperCase() + name.substring(1);
		String mName = "set" + name;
		return MethodUtil.getMethod(object, mName, new Class[] { type });

	}
	
}

class MethodSignature {

	final String name;

	final Class<?>[] parameters;

	final int hashcode;

	public MethodSignature(String name, Class<?>[] parameters) {
		this.name = name;
		this.parameters = parameters;

		int hashcode = name.hashCode();
		
		for (int index = 0; index < parameters.length; index++) {
			hashcode ^= parameters[index].hashCode();
		}
		
		this.hashcode = hashcode;
	}

	public String getName() {
		return name;
	}

	public Object[] getParameters() {
		return parameters;
	}

	@Override
	public int hashCode() {
		return hashcode;
	}

	@Override
	public boolean equals(Object obj) {
		MethodSignature castOther = (MethodSignature) obj;

		if (castOther.parameters.length != parameters.length) {
			return false;
		}

		if (!this.name.equals(castOther.name)) {
			return false;
		}
		
		for (int index = 0; index < parameters.length; index++) {
			if (!parameters[index].equals(castOther.parameters[index])) {
				return false;
			}
		}

		return true;
	}

}