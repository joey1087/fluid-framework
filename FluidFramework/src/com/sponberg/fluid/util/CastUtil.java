package com.sponberg.fluid.util;

import java.util.HashMap;

public class CastUtil {

	static HashMap<Class<?>, Object> primitiveDefaults = new HashMap<Class<?>, Object>();
	
	static {
		primitiveDefaults.put(int.class, 0);
		primitiveDefaults.put(byte.class, 0);
		primitiveDefaults.put(double.class, 0);
		primitiveDefaults.put(float.class, 0);
		primitiveDefaults.put(long.class, 0);
		primitiveDefaults.put(boolean.class, false);
	}
	
	public static boolean isArrayOfPrimitives(Class<?> c) {
		return !(c.getName().startsWith("[L"));
	}

	public static Object getNullOrPrimitiveDefault(Class<?> to) {
		return primitiveDefaults.get(to); // If not a primitive, will return null
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object cast(Object o, Class<?> to) {

		if (o == null) {
			return getNullOrPrimitiveDefault(to);
		}
		
		if (o.getClass() == to) {
			// No cast necessary
			return o;
		}

		if (to.isAssignableFrom(o.getClass())) {
			// No cast necessary
			return o;
		}
		
		try {
			/*
			if (to.isArray() && !isArrayOfPrimitives(to) && isArrayOfPrimitives(o.getClass())) {
				Method m = MethodUtil.getMethod(ArrayUtils.class, "toObject", new Class[] { o
						.getClass() });
				return m.invoke(ArrayUtils.class, new Object[] { o });
			}

			if (to.isArray() && isArrayOfPrimitives(to) && !isArrayOfPrimitives(o.getClass())) {
				Method m = MethodUtil.getMethod(ArrayUtils.class, "toPrimitive", new Class[] { o
						.getClass() });
				return m.invoke(ArrayUtils.class, new Object[] { o });
			}*/

			String s = o.toString();
			if (to.isAssignableFrom(Integer.class) || to.isAssignableFrom(int.class)) {
				if (s != null && s.trim().equals("")) {
					o = 0;
				} else {
					o = Integer.parseInt(s);
				}
			} else if (to.isAssignableFrom(Long.class) || to.isAssignableFrom(long.class)) {
					if (s != null && s.trim().equals("")) {
						o = 0;
					} else {
						o = Long.parseLong(s);
					}
			} else if (to.isAssignableFrom(Double.class) || to.isAssignableFrom(double.class)) {
				o = Double.parseDouble(s);
			} else if (to.isAssignableFrom(Byte.class) || to.isAssignableFrom(byte.class)) {
				o = Byte.parseByte(s);
			} else if (to.isAssignableFrom(Boolean.class) || to.isAssignableFrom(boolean.class)) {
				o = Boolean.parseBoolean(s);
			} else if (to.getSuperclass() != null
					&& to.getSuperclass() == Enum.class) {
				o = Enum.valueOf((Class<Enum>) to, s.replaceAll(" ", ""));
			} else {
				// See if to class has a constructor that takes a string
				o = to.getConstructor(String.class).newInstance(o);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Can't cast " + o + " to " + to);
		}

		return o;
	}

}
