package com.bomber.rpc;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BombardierRequest {
	@JsonProperty("numConns")
	private int numberOfConnections;

	@JsonProperty("numReqs")
	private int numberOfRequests;

	@JsonProperty("method")
	private HttpMethod method;

	private List<String> headers;

	private String url;

	private String body;

	private String payloadFile;

	private String payloadUrl;

	private String variableNames;

	private int startLine;

	private String scope;

	private List<Assertion> assertions = new ArrayList<>();

	public void addAssertion(String asserter, String expression, String condition, String expected) {
		Assertion assertion = new Assertion();
		assertion.setAsserter(asserter);
		assertion.setExpression(expression);
		assertion.setCondition(condition);
		assertion.setExpected(expected);
		this.assertions.add(assertion);
	}

	@Getter
	@Setter
	static class Assertion {

		private String asserter;

		private String expression;

		private String condition;

		private String expected;
	}

}
