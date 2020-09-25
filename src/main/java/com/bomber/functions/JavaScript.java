package com.bomber.functions;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.bomber.functions.core.FuncInfo;
import com.bomber.functions.core.Input;
import com.bomber.functions.core.StringFunction;

@FuncInfo(requiredArgs = "script")
public class JavaScript extends StringFunction {

	private static final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();

	private final ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("javascript");

	@Override
	public String execute(Input input) {
		try {
			return scriptEngine.eval(input.get("script")).toString();
		} catch (ScriptException e) {
			throw new RuntimeException(e);
		}
	}
}
