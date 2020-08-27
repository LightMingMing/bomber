package com.bomber.functions;

import org.ironrhino.core.util.ReflectionUtils;

import java.util.Map;

@SuppressWarnings({ "unchecked" })
public abstract class BaseFunctionExecutor<T extends Function<String>> {

	private final Class<T> clazz;

	public BaseFunctionExecutor() {
		clazz = (Class<T>) ReflectionUtils.getGenericClass(this.getClass());
	}

	public static String execute(Function<String> func, int count) {
		String result = null;

		for (int i = 0; i < count; i++) {
			result = func.execute();
		}

		return result;
	}

	public T newFunction(Map<String, String> params) {
		try {
			T func = clazz.newInstance();
			func.init(params);
			return func;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("test", e);
		}
	}
}