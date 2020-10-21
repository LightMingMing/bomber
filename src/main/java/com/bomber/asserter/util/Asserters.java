package com.bomber.asserter.util;

import java.util.Objects;

import com.bomber.asserter.Asserter;
import com.bomber.asserter.JsonPathAsserter;

public class Asserters {

	public static Asserter create(String name) {
		Objects.requireNonNull(name, "asserterName");
		if ("JsonPath".equals(name) || "jsonPath".equals(name)) {
			return new JsonPathAsserter();
		} else {
			throw new IllegalArgumentException("Unsupported asserter '" + name + "'");
		}
	}
}
