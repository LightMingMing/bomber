package com.bomber.functions;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.bomber.functions.core.FuncInfo;
import com.bomber.functions.core.Input;
import com.bomber.functions.core.StringFunction;

@FuncInfo(requiredArgs = "script", parallel = true)
public class JavaScript extends StringFunction {

	private static final String ENGINE_NAME = "javascript";

	private static ScriptEngineManager getInstance() {
		return LazyHolder.scriptEngineManager;
	}

	@Override
	public String execute(Input input) {
		try {
			ScriptEngine scriptEngine = getInstance().getEngineByName(ENGINE_NAME);
			return scriptEngine.eval(input.get("script")).toString();
		} catch (ScriptException e) {
			throw new RuntimeException(e);
		}
	}

	private static class LazyHolder {
		private static final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
	}
}
