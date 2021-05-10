package com.bomber.function.util;

import java.util.Collection;
import java.util.StringJoiner;

/**
 * 没有匹配的构造器异常
 *
 * @author MingMing Zhao
 */
public class NoSuchConstructorException extends RuntimeException {

	private static final long serialVersionUID = -1585411799798923810L;

	public NoSuchConstructorException(Class<?> clazz, Collection<String> args) {
		super(getConstructorMessage(clazz, args));
	}

	private static String getConstructorMessage(Class<?> clazz, Collection<String> args) {
		StringJoiner joiner = new StringJoiner(",", clazz.getName() + "(", ")");
		for (String arg : args) {
			joiner.add(arg);
		}
		return joiner.toString();
	}
}
