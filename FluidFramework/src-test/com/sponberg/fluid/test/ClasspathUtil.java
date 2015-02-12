package com.sponberg.fluid.test;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import com.sponberg.fluid.datastore.DatastoreTest;

public class ClasspathUtil {

	public static void loadSqliteJar() throws Exception {
		loadSqliteJar("");
	}
	
	public static void loadSqliteJar(String relativeDir) throws Exception {
		
		URL url = new File(relativeDir + "lib/sqlite-jdbc-3.7.15-M1.jar").toURI().toURL();
		addJar(url);
	}

	private static void addJar(URL url) throws NoSuchMethodException,
			IllegalAccessException, InvocationTargetException {
		
		URLClassLoader classLoader = (URLClassLoader) DatastoreTest.class.getClassLoader();
		
		final Method addURL = URLClassLoader.class.getDeclaredMethod("addURL",
                new Class[] { URL.class });
        addURL.setAccessible(true);		
        addURL.invoke(classLoader, new Object[] { url });
	}
	
}
