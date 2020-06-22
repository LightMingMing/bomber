package com.bomber.converter;

import static com.bomber.converter.HttpHeaderListConverter.convertToString;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.bomber.model.HttpHeader;

public class HttpHeaderListConverterTest {

	@Test
	public void testConvertToString() {
		assertThat(convertToString((List<HttpHeader>) null)).isNull();

		assertThat(convertToString(new ArrayList<>(0))).isEqualTo("[]");

		HttpHeader accept = new HttpHeader("Accept", "text/html");
		HttpHeader connection = new HttpHeader("Connection", "keep-alive");

		assertThat(convertToString(Collections.singletonList(accept))).isEqualTo("[\"Accept:text/html\"]");

		assertThat(convertToString(Arrays.asList(accept, connection)))
				.isEqualTo("[\"Accept:text/html\", \"Connection:keep-alive\"]");
	}

	@Test
	public void testConvertToHttpHeader() {
		HttpHeaderListConverter converter = new HttpHeaderListConverter();

		assertThat(converter.convertToEntityAttribute(null)).isNull();

		assertThat(converter.convertToEntityAttribute("[]")).isEmpty();

		List<HttpHeader> headerList = converter.convertToEntityAttribute("[\"Accept:text/html\"]");
		assertThat(headerList).hasSize(1);
		assertThat(headerList.get(0)).isEqualTo(new HttpHeader("Accept", "text/html"));

		headerList = converter.convertToEntityAttribute("[\"Accept:text/html\", \"Connection:keep-alive\"]");
		assertThat(headerList).hasSize(2);
		assertThat(headerList.get(0)).isEqualTo(new HttpHeader("Accept", "text/html"));
		assertThat(headerList.get(1)).isEqualTo(new HttpHeader("Connection", "keep-alive"));
	}
}