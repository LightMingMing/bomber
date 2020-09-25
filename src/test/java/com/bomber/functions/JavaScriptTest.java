package com.bomber.functions;

import static org.assertj.core.api.Assertions.assertThat;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.Test;

import com.bomber.functions.core.Input;

public class JavaScriptTest {

	@Test
	public void testScriptEngine() throws ScriptException {
		ScriptEngineManager manager = new ScriptEngineManager();

		ScriptEngine scriptEngine = manager.getEngineByName("javascript");
		assertThat(scriptEngine).isNotNull();

		Object substring = scriptEngine.eval("'012345'.substring(2, 5)");
		assertThat(substring).isInstanceOf(String.class);
		assertThat((String) substring).isEqualTo("234");
	}

	@Test
	public void testJavaScript() {
		JavaScript script = new JavaScript();
		String result = script.execute(new Input("script", "'012345'.substring(2, 5)"));
		assertThat(result).isEqualTo("234");
	}
}