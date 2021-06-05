package com.bomber.asserter;

import java.util.Objects;

public class Matcher {

	private static AssertResult isNull(String actual, String message) {
		if (actual == null) {
			return AssertResult.success();
		} else {
			return AssertResult.error(String.format("Expecting [%s] is null but actual was '%s'", message, actual));
		}
	}

	private static AssertResult isEqual(String actual, String expected, String message) {
		if (Objects.equals(actual, expected)) {
			return AssertResult.success();
		} else {
			return AssertResult
					.error(String.format("Expecting [%s] is '%s' but actual was '%s'", message, expected, actual));
		}
	}

	private static AssertResult isNotNull(String actual, String message) {
		if (actual != null) {
			return AssertResult.success();
		} else {
			return AssertResult.error("Expecting [" + message + "] is not null");
		}

	}

	public static AssertResult matches(String actual, String expected, Condition condition, String message) {
		switch (condition) {
		case NULL:
			return isNull(actual, message);
		case NOT_NULL:
			return isNotNull(actual, message);
		case EQUAL:
			return isEqual(actual, expected, message);
		default:
			throw new IllegalArgumentException("Unsupported condition '" + condition + "'");
		}
	}
}
