package com.bomber.functions;

import static org.assertj.core.api.Assertions.assertThat;

import org.ironrhino.core.util.DateUtils;
import org.junit.Test;

import com.bomber.functions.core.Function;
import com.bomber.functions.core.Input;
import org.mvel2.jsr223.MvelScriptEngine;

import javax.script.ScriptException;

public class MVELTest {

	private static final Function<String> mvel = new MVEL();

	@Test
	public void testCompile() throws ScriptException {
		assertThat(new MvelScriptEngine().compiledScript("str.substring(2)")).isNotNull();
	}

	@Test
	public void testSubstring() {
		Input input = new Input("script", "str.substring(2)", "str", "01234");
		assertThat(mvel.execute(input)).isEqualTo("234");
	}

	@Test
	public void testMultipleStatements() {
		Input input = new Input("script", "pa=a*a;pb=b*b;(pa+pb)+'';", "a", "3", "b", "4");
		assertThat(mvel.execute(input)).startsWith("25");
	}

	@Test
	public void testDate() {
		Input input = new Input("script", "org.ironrhino.core.util.DateUtils.parseDate10(date).getTime()+''", "date",
				"2020-10-27");
		String expected = DateUtils.parseDate10("2020-10-27").getTime() + "";
		assertThat(mvel.execute(input)).isEqualTo(expected);
	}
}