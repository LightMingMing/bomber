package com.bomber.http;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.http.MediaType.TEXT_XML;

import java.nio.charset.Charset;
import java.util.List;
import java.util.StringJoiner;

import com.bomber.common.util.JsonUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.HtmlUtils;

public class StringEntityRender {

	public static final String DEFAULT_ENCODING = "utf-8";

	private static String join(List<String> tiny) {
		return tiny.size() == 1 ? tiny.get(0) : String.join(", ", tiny);
	}

	private static String convertToString(MultiValueMap<String, String> map) {
		StringJoiner joiner = new StringJoiner("\n");
		map.forEach((key, list) -> joiner.add(key + ": " + join(list)));
		return joiner.toString();
	}

	private static String charset(HttpHeaders httpHeaders) {
		List<Charset> charsets = httpHeaders.getAcceptCharset();
		return charsets.isEmpty() ? DEFAULT_ENCODING : charsets.get(0).name();
	}

	public static String renderPlainText(RequestEntity<String> entity) {
		StringBuilder sb = new StringBuilder();

		sb.append(entity.getMethod());
		sb.append(' ');
		sb.append(entity.getUrl().toString());

		HttpHeaders httpHeaders = entity.getHeaders();
		if (httpHeaders.size() > 0) {
			sb.append('\n');
			sb.append(convertToString(httpHeaders));
		}

		String body = entity.getBody();
		if (body != null) {
			sb.append("\n\n");
			sb.append(entity.getBody());
		}
		return sb.toString();
	}

	public static String renderPlainText(ResponseEntity<String> entity) {
		StringBuilder sb = new StringBuilder();

		sb.append(entity.getStatusCode());

		HttpHeaders httpHeaders = entity.getHeaders();
		if (httpHeaders.size() > 0) {
			sb.append('\n');
			sb.append(convertToString(httpHeaders));
		}

		String body = entity.getBody();
		if (body != null) {
			MediaType contentType = httpHeaders.getContentType();
			if (contentType != null) {
				if (contentType.includes(APPLICATION_JSON)) {
					body = JsonUtils.prettify(body);
				} else if (contentType.includes(TEXT_HTML) || contentType.includes(TEXT_XML)) {
					body = HtmlUtils.htmlEscape(body, charset(httpHeaders));
				}
			}
			sb.append("\n\n");
			sb.append(body);
		}
		return sb.toString();
	}
}
