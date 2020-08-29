package com.bomber.functions.util;

public class MissingArgumentException extends RuntimeException {

	private static final String format = "Function '%s' missing argument '%s'";

	public MissingArgumentException(String function, String arg) {
		super(String.format(format, function, arg));
	}
}
