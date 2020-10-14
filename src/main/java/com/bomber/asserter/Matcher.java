package com.bomber.asserter;

import java.util.Objects;

public class Matcher {

	private static AssertResult isNull(String actual) {
		if (actual == null) {
			return AssertResult.success();
		} else {
			return AssertResult.error("Expecting null but was '" + actual + "'");
		}
	}

	private static AssertResult isEqual(String actual, String expected) {
		if (Objects.equals(actual, expected)) {
			return AssertResult.success();
		} else {
			return AssertResult.error("Expecting '" + expected + "' but actual was '" + actual + "'");
		}
	}

	private static AssertResult isNotNull(String actual) {
		if (actual != null) {
			return AssertResult.success();
		} else {
			return AssertResult.error("Expecting actual is to be null");
		}

	}

	public static AssertResult matches(String actual, String expected, Condition condition) {
		switch (condition) {
			case NULL :
				return isNull(actual);
			case NOT_NULL :
				return isNotNull(actual);
			case EQUAL :
				return isEqual(actual, expected);
			default :
				throw new IllegalArgumentException("Unsupported condition '" + condition + "'");
		}
	}
}
