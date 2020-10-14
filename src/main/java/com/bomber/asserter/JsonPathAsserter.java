package com.bomber.asserter;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

public class JsonPathAsserter implements Asserter {

	@Override
	public AssertResult run(Assertion assertion) {
		return Matcher.matches(getActualValue(assertion), assertion.getExpectedValue(), assertion.getCondition());
	}

	private String getActualValue(Assertion assertion) {
		try {
			return JsonPath.parse(assertion.getText()).read(assertion.getExpression());
		} catch (PathNotFoundException notFound) {
			return null;
		}
	}
}
