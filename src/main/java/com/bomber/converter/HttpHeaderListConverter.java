package com.bomber.converter;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Converter;

import org.ironrhino.core.hibernate.convert.AbstractListConverter;
import org.ironrhino.core.util.JsonUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import com.bomber.model.HttpHeader;
import com.fasterxml.jackson.core.type.TypeReference;

@Converter
public class HttpHeaderListConverter extends AbstractListConverter<HttpHeader> {

	private final static char HTTP_HEADER_SEPARATOR = ':';

	public static String convertToString(HttpHeader httpHeader) {
		return httpHeader.getName() + HTTP_HEADER_SEPARATOR + httpHeader.getValue();
	}

	// ["K1:V1", "K2:V2", "K3:V3"]
	public static String convertToString(@Nullable List<HttpHeader> list) {
		if (list == null)
			return null;

		if (list.isEmpty()) {
			return "[]";
		}

		Iterator<HttpHeader> it = list.iterator();
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (;;) {
			sb.append('"').append(convertToString(it.next())).append('"');
			if (!it.hasNext()) {
				return sb.append(']').toString();
			}
			sb.append(", ");
		}
	}

	@Override
	public List<HttpHeader> convertToEntityAttribute(String s) {
		if (s == null) {
			return null;
		}
		List<String> header;
		try {
			header = JsonUtils.fromJson(s, new TypeReference<List<String>>() {
			});
		} catch (IOException e) {
			throw new IllegalArgumentException("Invalid header format: " + s, e);
		}
		return header.stream().filter(StringUtils::hasText).map(this::convert).collect(Collectors.toList());
	}

	@Override
	public String convertToDatabaseColumn(List<HttpHeader> list) {
		return convertToString(list);
	}

	@Override
	protected HttpHeader convert(String s) {
		if (s == null) {
			return null;
		}
		int splitterIndex = s.indexOf(HTTP_HEADER_SEPARATOR);
		if (splitterIndex <= 0) {
			throw new IllegalArgumentException("Invalid header format: " + s);
		}
		return new HttpHeader(s.substring(0, splitterIndex), s.substring(splitterIndex + 1));
	}
}
