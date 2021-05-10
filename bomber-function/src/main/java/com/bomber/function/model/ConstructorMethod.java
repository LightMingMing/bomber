package com.bomber.function.model;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Objects;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.lang.NonNull;
import org.springframework.util.ObjectUtils;

import com.bomber.function.util.NotSupportedParameterTypeException;

/**
 * 函数构造方法
 *
 * @author MingMing Zhao
 */
public class ConstructorMethod<T> implements Comparable<ConstructorMethod<T>> {

	private final Constructor<T> constructor;

	private final MethodParameter[] methodParameters;

	private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

	public ConstructorMethod(Constructor<T> constructor) {
		this.constructor = constructor;
		this.methodParameters = initMethodParameters();
	}

	private MethodParameter[] initMethodParameters() {
		int count = this.constructor.getParameterCount();
		MethodParameter[] result = new MethodParameter[count];
		for (int i = 0; i < count; i++) {
			result[i] = new MethodParameter(constructor, i);
			result[i].initParameterNameDiscovery(this.parameterNameDiscoverer);
		}
		return result;
	}

	public boolean supports(@NonNull Map<String, String> parameterValues) {
		for (MethodParameter methodParameter : methodParameters) {
			if (!Map.class.isAssignableFrom(methodParameter.getParameterType()) &&
					Objects.isNull(parameterValues.get(methodParameter.getParameterName()))) {
				return false;
			}
		}
		return true;
	}

	public T invokeMethod(@NonNull Map<String, String> parameterValues) {
		Object[] args = getParameterValues(parameterValues);
		try {
			return constructor.newInstance(args);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	public Object[] getParameterValues(Map<String, String> parameterValues) {
		MethodParameter[] parameters = methodParameters;
		if (ObjectUtils.isEmpty(parameters)) {
			return new Object[0];
		}

		Object[] args = new Object[parameters.length];

		for (int i = 0; i < parameters.length; i++) {
			MethodParameter parameter = parameters[i];
			parameter.initParameterNameDiscovery(this.parameterNameDiscoverer);

			Class<?> type = parameter.getParameterType();
			String name = parameter.getParameterName();
			String value = parameterValues.get(name);

			if (String.class.isAssignableFrom(type)) {
				args[i] = value;
			} else if (type.isPrimitive()) {
				args[i] = convertToPrimitive(type, value);
			} else if (Integer.class.isAssignableFrom(type)) {
				args[i] = value == null ? null : Integer.parseInt(value);
			} else if (Long.class.isAssignableFrom(type)) {
				args[i] = value == null ? null : Long.parseLong(value);
			} else if (Boolean.class.isAssignableFrom(type)) {
				args[i] = value == null ? null : Boolean.parseBoolean(value);
			} else if (Map.class.isAssignableFrom(type)) {
				args[i] = parameterValues;
			} else {
				throw new NotSupportedParameterTypeException(type);
			}
		}
		return args;
	}

	private Object convertToPrimitive(Class<?> type, String value) {
		if (value == null)
			return 0;
		if (type == Integer.TYPE) {
			return Integer.parseInt(value);
		} else if (type == Long.TYPE) {
			return Long.parseLong(value);
		} else if (type == Boolean.TYPE) {
			return Boolean.parseBoolean(value);
		}
		throw new NotSupportedParameterTypeException(type);
	}

	@Override
	public int compareTo(ConstructorMethod<T> o) {
		return Integer.compare(o.methodParameters.length, this.methodParameters.length);
	}
}
