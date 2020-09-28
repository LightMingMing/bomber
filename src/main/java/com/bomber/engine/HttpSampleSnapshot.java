package com.bomber.engine;

import static com.bomber.util.ValueReplacer.readReplaceableKeys;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpMethod;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpSampleSnapshot {

	private HttpMethod method;

	private String url;

	private List<String> headers;

	private String body;

	private String variableNames;

	private String payloadFile;

	private String payloadUrl;

	public String readVariables() {
		Set<String> result = new HashSet<>(readReplaceableKeys(url));
		if (headers != null) {
			headers.forEach(header -> result.addAll(readReplaceableKeys(header)));
		}
		if (body != null) {
			result.addAll(readReplaceableKeys(body));
		}
		return String.join(",", result);
	}

}
