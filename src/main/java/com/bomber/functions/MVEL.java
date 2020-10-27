package com.bomber.functions;

import static java.util.Objects.requireNonNull;
import static javax.script.ScriptContext.ENGINE_SCOPE;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.mvel2.jsr223.MvelScriptEngine;

import com.bomber.functions.core.FuncInfo;
import com.bomber.functions.core.Input;
import com.bomber.functions.core.StringFunction;

@FuncInfo(requiredArgs = "script, args", customArg = "args", parallel = true)
public class MVEL extends StringFunction {

	private static final Map<String, Serializable> cachedScripts = new ConcurrentHashMap<>();
	private static final MvelScriptEngine engine = new MvelScriptEngine();

	@Override
	public String execute(Input input) {
		String script = input.get("script");
		Serializable compiledScript = requireNonNull(compileScriptIfNecessary(script), "compiledScript");
		try {
			return engine.evaluate(compiledScript, createScriptContext(input.getAll())).toString();
		} catch (ScriptException e) {
			throw new IllegalArgumentException("Failed to execute script: " + script, e);
		}
	}

	private Serializable compileScriptIfNecessary(String script) {
		return cachedScripts.computeIfAbsent(script, s -> {
			try {
				return engine.compiledScript(s);
			} catch (ScriptException e) {
				throw new IllegalArgumentException("Failed to compile script: " + script, e);
			}
		});
	}

	private ScriptContext createScriptContext(Map<String, String> map) {
		SimpleScriptContext context = new SimpleScriptContext();
		Bindings bindings = engine.createBindings();
		bindings.putAll(map);
		context.setBindings(bindings, ENGINE_SCOPE);
		return context;
	}

}
