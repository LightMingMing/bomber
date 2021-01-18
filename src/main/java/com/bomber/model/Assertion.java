package com.bomber.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import javax.persistence.Embeddable;

import org.ironrhino.core.metadata.UiConfig;

import com.bomber.asserter.Condition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class Assertion implements Serializable {

	private static final long serialVersionUID = -6113340615564382808L;

	@UiConfig(alias = "asserter", width = "100px", type = "select", listKey = "key", listValue = "value", listOptions = "statics['com.bomber.model.Assertion'].getLabelValues()")
	private String asserter;

	@UiConfig(width = "200px")
	private String expression;

	@UiConfig(width = "100px")
	private Condition condition;

	@UiConfig(width = "200px")
	private String expected;

	public static Map<String, String> getLabelValues() {
		return Collections.singletonMap("JsonPath", "JsonPath");
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Assertion assertion = (Assertion) o;
		return Objects.equals(asserter, assertion.asserter) && Objects.equals(expression, assertion.expression)
				&& condition == assertion.condition && Objects.equals(expected, assertion.expected);
	}

	@Override
	public int hashCode() {
		return Objects.hash(asserter, expression, condition, expected);
	}
}
