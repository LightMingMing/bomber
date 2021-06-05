package com.bomber.asserter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Assertion {

	private String text;

	private String expression;

	private Condition condition;

	private String expected;
}
