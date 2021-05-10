package com.bomber.function.util;

/**
 * 不支持的参数类型异常
 *
 * @author MingMing Zhao
 */
public class NotSupportedParameterTypeException extends RuntimeException {

	private static final long serialVersionUID = 3419286937518395708L;

	public NotSupportedParameterTypeException(Class<?> type) {
		super(type.getName());
	}
}
