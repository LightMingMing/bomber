package com.bomber.function.model;

import java.lang.reflect.Method;
import java.util.Map;

import org.springframework.lang.NonNull;

import com.bomber.function.Function;
import com.bomber.function.Producer;
import com.bomber.function.util.NoExecuteMethodException;

/**
 * 函数方法调用
 *
 * @author MingMing Zhao
 */
public class MethodInvoker {

	private final boolean producer;

	private final Method method;

	public MethodInvoker(Class<? extends Function> clazz) {
		this.method = getExecuteMethod(clazz);
		this.producer = Producer.class.isAssignableFrom(clazz);
	}

	private static Method getExecuteMethod(Class<?> clazz) {
		Class<?> parent = clazz;
		for (; ; ) {
			for (Method method : parent.getDeclaredMethods()) {
				if ("execute".equals(method.getName())) {
					return method;
				}
			}
			if ((parent = parent.getSuperclass()) == null) {
				throw new NoExecuteMethodException(clazz);
			}
		}
	}

	public Object invokeMethod(Function function, @NonNull Map<String, String> initParameterValues, @NonNull Map<String, String> container) {
		if (producer) {
			return executeProducer((Producer<?>) function);
		} else {
			return executeDynamicFunction(function, initParameterValues, container);
		}
	}

	private Object executeProducer(Producer<?> producer) {
		return producer.execute();
	}

	private Object executeDynamicFunction(Function function, Map<String, String> initParameterValues, Map<String, String> container) {
		Object[] args = function.getParameterValues(initParameterValues, container);
		try {
			return method.invoke(function, args);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}
}
