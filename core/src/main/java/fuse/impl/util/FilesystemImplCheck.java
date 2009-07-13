package fuse.impl.util;

import java.lang.reflect.Method;

import fuse.Filesystem;

public class FilesystemImplCheck {
	private FilesystemImplCheck() {
	}
	
	private final static Method[] defaultMethods = Filesystem.class.getMethods();
	
	private static Method getFirstMethod(String name) throws NoSuchMethodException {
		for(Method m : defaultMethods) {
			if(m.getName().equals(name)) {
				return m;
			}
		}
		throw new NoSuchMethodException("No method " + name);
	}
	
	public static boolean isImplemented(Class<? extends Filesystem> cls, String name) {
		try {
			Method defaultMethod = getFirstMethod(name);
			Method currentMethod = cls.getMethod(name, defaultMethod.getParameterTypes());
	
			return currentMethod.equals(defaultMethod);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("Invalid method name", e);
		}
	}
}
