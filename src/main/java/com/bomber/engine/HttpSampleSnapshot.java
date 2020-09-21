package com.bomber.engine;

import static com.bomber.util.ValueReplacer.containsReplaceableKeys;

import org.springframework.http.HttpMethod;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpSampleSnapshot {

	private HttpMethod method;

	private String url;

	private String[] headers;

	private String body;

	private String variableNames;

	private String variableFilePath;

	private String payloadId;

	public boolean isMutable() {
		if (containsReplaceableKeys(url)) {
			return true;
		}
		for (String header : headers) {
			if (containsReplaceableKeys(header)) {
				return true;
			}
		}
		return containsReplaceableKeys(body);
	}

}
