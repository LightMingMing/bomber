package com.bomber.functions;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.bomber.functions.core.Input;

public class JavaScript2Test {

	@Test
	public void testSubstring() {
		JavaScript2 script = new JavaScript2();
		String result = script.execute(new Input("script", "str.substring(2, 5)", "str", "012345"));
		assertThat(result).isEqualTo("234");
	}

	@Test
	public void testMultipleStatements() {
		JavaScript2 script = new JavaScript2();
		String result = script.execute(new Input("script", "pa=a*a; pb=b*b; pa+pb;", "a", "3", "b", "4"));
		assertThat(result).startsWith("25");
	}
}