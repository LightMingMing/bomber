package com.bomber.function;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import javax.script.ScriptException;

import org.junit.jupiter.api.Test;
import org.mvel2.jsr223.MvelScriptEngine;

/**
 * @author MingMing Zhao
 */
class MVELTest {

	private static final MVEL mvel = new MVEL();

	@Test
	public void testCompile() throws ScriptException {
		assertThat(new MvelScriptEngine().compiledScript("str.substring(2)")).isNotNull();
	}

	@Test
	public void testSubstring() {
		String script = "str.substring(2)";
		assertThat(mvel.execute(script, Map.of("str", "01234"))).isEqualTo("234");
	}

	@Test
	public void testMultipleStatements() {
		String script = "pa=a*a;pb=b*b;(pa+pb)+''";
		assertThat(mvel.execute(script, Map.of("a", "3", "b", "4"))).startsWith("25");
	}
}