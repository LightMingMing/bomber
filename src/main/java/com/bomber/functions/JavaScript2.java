package com.bomber.functions;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import com.bomber.functions.core.FuncInfo;
import com.bomber.functions.core.Input;
import com.bomber.functions.core.StringFunction;

import static javax.script.ScriptContext.ENGINE_SCOPE;

@FuncInfo(requiredArgs = "script", customArg = "args", parallel = true)
public class JavaScript2 extends StringFunction {

	private static final String ENGINE_NAME = "javascript";

	private static ScriptEngineManager getInstance() {
		return LazyHolder.scriptEngineManager;
	}

	@Override
	public String execute(Input input) {
		try {
			ScriptEngine engine = getInstance().getEngineByName(ENGINE_NAME);
			Bindings bindings = engine.createBindings();
			bindings.putAll(input.getAll());

			ScriptContext context = new SimpleScriptContext();
			context.setBindings(bindings, ENGINE_SCOPE);

			return engine.eval(input.get("script"), context).toString();
		} catch (ScriptException e) {
			throw new RuntimeException(e);
		}
	}

	private static class LazyHolder {
		private static final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
	}
}
