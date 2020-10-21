package com.bomber.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

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

}
