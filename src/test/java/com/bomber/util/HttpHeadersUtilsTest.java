package com.bomber.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

public class HttpHeadersUtilsTest {

	@Test
	public void testHeaderFields() {
		List<String> headerFields = HttpHeadersUtils.getHeaderFields();
		assertThat(headerFields).contains("Accept", "Accept-Charset", "Accept-Encoding", "Accept-Language");
		assertThat(headerFields).contains("Content-Type", "Content-Encoding", "Content-Language");
	}
}