package com.bomber.functions;

import java.util.Arrays;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginFunctionTest {

	@Test
	public void testHandleCookie() {
		String c1 = "U=a";
		String c2 = "U=a; path=/";
		String c3 = "T=1";
		String c4 = "T=1";
		String cookies = LoginFunction.handleCookie(Arrays.asList(c1, c2, c3, c4));
		assertThat(cookies).isEqualTo("U=a;T=1");
	}
}