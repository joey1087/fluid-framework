package com.sponberg.fluid.util;

import java.util.HashMap;

public class MakeMap {

	public static HashMap of(Object...objects) {
		HashMap map = new HashMap();
		for (int index = 0; index < objects.length; index += 2) {
			map.put(objects[index], objects[index + 1]);
		}
		return map;
	}
	
}
