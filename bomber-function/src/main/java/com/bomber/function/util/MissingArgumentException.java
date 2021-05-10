package com.bomber.function.util;

/**
 * 缺失参数异常
 *
 * @author MingMingZhao
 */
public class MissingArgumentException extends RuntimeException {

	private static final long serialVersionUID = 91339374763516624L;

	private static final String format = "Function '%s' missing argument '%s'";

	public MissingArgumentException(String function, String arg) {
		super(String.format(format, function, arg));
	}

}
