package com.bomber.asserter;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

public class JsonPathAsserter implements Asserter {

	@Override
	public AssertResult run(Assertion assertion) {
		return Matcher.matches(getActualValue(assertion), assertion.getExpected(),
			assertion.getCondition(), assertion.getExpression());
	}

	private String getActualValue(Assertion assertion) {
		String content = assertion.getText();
		if (content == null || content.isEmpty()) {
			return null;
		}
		try {
			Object result = JsonPath.parse(content).read(assertion.getExpression());
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
