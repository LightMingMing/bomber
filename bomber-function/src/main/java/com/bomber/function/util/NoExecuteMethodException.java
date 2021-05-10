package com.bomber.function.util;

/**
 * 没有Execute方法异常
 *
 * @author MingMing Zhao
 */
public class NoExecuteMethodException extends RuntimeException {
	private static final long serialVersionUID = -2777129031385037232L;

	public NoExecuteMethodException(Class<?> type) {
		super(type.getName());
	}
}
