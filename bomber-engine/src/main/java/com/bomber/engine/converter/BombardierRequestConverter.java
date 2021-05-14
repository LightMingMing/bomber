package com.bomber.engine.converter;

import java.util.Set;
import java.util.StringJoiner;

import com.bomber.common.util.StringReplacer;
import com.bomber.engine.model.BomberContext;
import com.bomber.engine.model.HttpRequest;
import com.bomber.engine.model.Scope;
import com.bomber.engine.rpc.BombardierRequest;

/**
 * Converter
 *
 * @author MingMing Zhao
 */
public class BombardierRequestConverter {

	public static BombardierRequestConverter INSTANCE = new BombardierRequestConverter();

	private BombardierRequestConverter() {
	}

	public BombardierRequest convert(BomberContext ctx) {
		BombardierRequest request = new BombardierRequest();

		HttpRequest httpRequest = ctx.getHttpRequest();
		request.setUrl(httpRequest.getUrl());
		request.setMethod(httpRequest.getMethod());
		request.setHeaders(httpRequest.getHeaders());
		request.setBody(httpRequest.getBody());
		httpRequest.getAssertions().forEach(each -> request.addAssertion(each.getAsserter(), each.getExpression(),
				each.getCondition(), each.getExpected()));

		ctx.getPayload().ifPresent(payload -> {
			Scope scope = payload.getScope();
			if (scope == Scope.Request) {
				request.setScope("request");
			} else if (scope == Scope.Thread) {
				request.setScope("thread");
			} else {
				request.setScope("benchmark");
			}
			request.setVariableNames(readVariables(httpRequest));
			request.setPayloadUrl(payload.getUrl());
		});
		return request;
	}

	private String readVariables(HttpRequest request) {
		Set<String> variables = StringReplacer.read(request.getUrl());
		if (request.getHeaders() != null) {
			variables.addAll(StringReplacer.read(request.getHeaders()));
		}
		if (request.getBody() != null) {
			variables.addAll(StringReplacer.read(request.getBody()));
		}
		StringJoiner joiner = new StringJoiner(",");
		for (String variable : variables) {
			joiner.add(variable);
		}
		return joiner.toString();
	}
}
