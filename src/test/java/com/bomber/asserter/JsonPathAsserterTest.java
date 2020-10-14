package com.bomber.asserter;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonPathAsserterTest {

	@Test
	public void test() {
		Asserter asserter = new JsonPathAsserter();

		Assertion assertion = new Assertion();
		assertion.setText(json());
		assertion.setExpression("$[0]['gender']");
		assertion.setCondition(Condition.NOT_NULL);
		assertThat(asserter.run(assertion).isSuccessful()).isTrue();

		assertion.setCondition(Condition.EQUAL);
		assertion.setExpectedValue("male");
		assertThat(asserter.run(assertion).isSuccessful()).isTrue();

		assertion.setExpression("$[0]['error']");
		assertion.setCondition(Condition.NULL);
		assertThat(asserter.run(assertion).isSuccessful()).isTrue();
	}

	public String json() {
		return "[{\"name\" : \"john\",\"gender\" : \"male\"}]";
	}
}