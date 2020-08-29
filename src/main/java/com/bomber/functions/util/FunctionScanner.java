package com.bomber.functions.util;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.stream.Collectors;

import org.ironrhino.core.util.ClassScanner;

import com.bomber.functions.core.Function;

public class FunctionScanner {

	public static boolean isInterfaceOrAbstract(Class<?> clazz) {
		int mod = clazz.getModifiers();
		return Modifier.isInterface(mod) || Modifier.isAbstract(mod);
	}

	@SuppressWarnings("unchecked")
	public static Collection<Class<Function<?>>> scan(String[] packages) {
		return ClassScanner.scanAssignable(ClassScanner.getAppPackages(), Function.class).stream()
				.filter(clazz -> !isInterfaceOrAbstract(clazz)).map(clazz -> (Class<Function<?>>) clazz)
				.collect(Collectors.toList());
	}
}
