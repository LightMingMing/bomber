package com.bomber.converter;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Converter;

import org.ironrhino.core.hibernate.convert.AbstractListConverter;
import org.springframework.lang.Nullable;

import com.bomber.model.HttpHeader;

@Converter
public class HttpHeaderListConverter extends AbstractListConverter<HttpHeader> {

	private final static char HTTP_HEADER_SEPARATOR = ':';

	public static String convertToString(HttpHeader httpHeader) {
		return httpHeader.getName() + HTTP_HEADER_SEPARATOR + httpHeader.getValue();
	}

	public static String convertToString(@Nullable List<HttpHeader> list) {
		if (list == null)
			return null;
		return list.stream().map(HttpHeaderListConverter::convertToString).collect(Collectors.joining(SEPARATOR));
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
			throw new IllegalArgumentException(s + " is not valid httpHeader");
		}
		return new HttpHeader(s.substring(0, splitterIndex), s.substring(splitterIndex + 1));
	}
}
