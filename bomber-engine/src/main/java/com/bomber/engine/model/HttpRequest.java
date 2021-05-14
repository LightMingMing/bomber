package com.bomber.engine.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import lombok.Getter;
import lombok.Setter;

/**
 * HTTP 请求
 *
 * @author MingMing Zhao
 */
@Getter
@Setter
public class HttpRequest {

	@NonNull
	private String url;

	@NonNull
	private HttpMethod method;

	@Nullable
	private List<String> headers;

	@Nullable
	private String body;

	private List<Assertion> assertions = new ArrayList<>();

	public void addAssertion(String asserter, String expression, String condition, String expected) {
		Assertion assertion = new Assertion();
		assertion.setAsserter(asserter);
		assertion.setExpression(expression);
		assertion.setCondition(condition);
		assertion.setExpected(expected);
		this.assertions.add(assertion);
	}

	@Setter
	@Getter
	public static class Assertion {

		private String asserter;

		private String expression;

		private String condition;

		private String expected;
	}

}
