package com.bomber.function.model;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Map;

import org.springframework.lang.NonNull;

import com.bomber.function.Function;
import com.bomber.function.util.NoSuchConstructorException;

/**
 * 函数构造器
 *
 * @author MingMing Zhao
 */
public class FunctionConstructors<T extends Function> {

	private final ConstructorMethod<T>[] constructorMethods;
	private final Class<T> clazz;

	public FunctionConstructors(Class<T> clazz) {
		this.clazz = clazz;
		this.constructorMethods = initConstructorMethods(clazz);
	}

	@SuppressWarnings("unchecked")
	private ConstructorMethod<T>[] initConstructorMethods(Class<T> clazz) {
		Constructor<T>[] constructors = (Constructor<T>[])
				clazz.getDeclaredConstructors();
		ConstructorMethod<T>[] constructorMethods = new ConstructorMethod[constructors.length];
		for (int i = 0; i < constructors.length; i++) {
			constructorMethods[i] = new ConstructorMethod<>(constructors[i]);
		}
		Arrays.sort(constructorMethods);
		return constructorMethods;
	}

	public T invokeMethod(@NonNull Map<String, String> parameterValues) {
		for (ConstructorMethod<T> constructorMethod : constructorMethods) {
			if (constructorMethod.supports(parameterValues)) {
				return constructorMethod.invokeMethod(parameterValues);
			}
		}
		throw new NoSuchConstructorException(clazz, parameterValues.keySet());
	}
}
