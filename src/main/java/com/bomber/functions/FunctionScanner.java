package com.bomber.functions;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.ironrhino.core.util.ClassScanner;

public class FunctionScanner {

	public static void main(String[] args) {
		new FunctionScanner().scan().forEach(System.out::println);
	}

	public List<Class<?>> scan() {
		String packageName = Function.class.getPackage().getName();
		Collection<Class<?>> classes = ClassScanner.scanAssignable(packageName, Function.class);
		return classes.stream().filter(clazz -> !isInterfaceOrAbstract(clazz)).collect(Collectors.toList());
	}

	public boolean isInterfaceOrAbstract(Class<?> clazz) {
		int mod = clazz.getModifiers();
		return Modifier.isInterface(mod) || Modifier.isAbstract(mod);
	}
}