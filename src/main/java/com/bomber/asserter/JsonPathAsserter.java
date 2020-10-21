package com.bomber.asserter;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

public class JsonPathAsserter implements Asserter {

	@Override
	public AssertResult run(Assertion assertion) {
		return Matcher.matches(getActualValue(assertion), assertion.getExpected(), assertion.getCondition(),
				assertion.getExpression());
	}

	private String getActualValue(Assertion assertion) {
		try {
			Object result = JsonPath.parse(assertion.getText()).read(assertion.getExpression());
			if (result instanceof String) {
				return (String) result;
			} else {
				return result.toString();
			}
		} catch (PathNotFoundException notFound) {
			return null;
		}
	}
}
