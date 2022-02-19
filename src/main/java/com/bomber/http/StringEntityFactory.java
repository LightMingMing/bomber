package com.bomber.http;

import com.bomber.model.HttpHeader;
import com.bomber.model.HttpSample;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.bomber.common.util.StringReplacer.replace;

public class StringEntityFactory {

	public static RequestEntity<String> create(HttpSample httpSample) {
		return createRequestEntity(httpSample, value -> value);
	}

	public static RequestEntity<String> create(HttpSample httpSample, Map<String, String> context) {
		return createRequestEntity(httpSample, value -> replace(value, context));
	}

	private static RequestEntity<String> createRequestEntity(HttpSample sample, Function<String, String> replacer) {
		URI uri = URI.create(replacer.apply(sample.getUrl()));
		HttpMethod method = sample.getMethod();
		MultiValueMap<String, String> headers = convertToHttpHeaders(sample.getHeaders(), replacer);
		String body = replacer.apply(sample.getBody());
		return new RequestEntity<>(body, headers, method, uri);
	}

	private static MultiValueMap<String, String> convertToHttpHeaders(List<HttpHeader> httpHeaderList, Function<String, String> replacer) {
		if (httpHeaderList == null) {
			return null;
		}
		MultiValueMap<String, String> headers = new HttpHeaders();
		for (HttpHeader httpHeader : httpHeaderList) {
			headers.add(httpHeader.getName(), replacer.apply(httpHeader.getValue()));
		}
		return headers;
	}

}
