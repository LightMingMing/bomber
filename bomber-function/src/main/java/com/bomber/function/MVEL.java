package com.bomber.function;

import static java.util.Objects.requireNonNull;
import static javax.script.ScriptContext.ENGINE_SCOPE;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.mvel2.jsr223.MvelScriptEngine;
import org.springframework.lang.NonNull;

import com.bomber.common.util.StringReplacer;

/**
 * MVEL 脚本函数
 *
 * @author MingMing Zhao
 */
@Group(Type.SCRIPT)
@FuncInfo(requiredArgs = "script, args", customArg = "args", parallel = true)
public class MVEL implements Function {

	private static final Map<String, Serializable> cachedScripts = new ConcurrentHashMap<>();
	private static final MvelScriptEngine engine = new MvelScriptEngine();

	public String execute(String script, Map<String, String> args) {
		Serializable compiledScript = requireNonNull(compileScriptIfNecessary(script), "compiledScript");
		try {
			return engine.evaluate(compiledScript, createScriptContext(args)).toString();
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

	@Override
	public Object[] getParameterValues(@NonNull Map<String, String> initParameterValues,
									   @NonNull Map<String, String> container) {
		String script = StringReplacer.replace(initParameterValues.get("script"), container);
		Map<String, String> args = new HashMap<>();
		Arrays.stream(initParameterValues.get("args").split(", *"))
			.forEach(k -> args.put(k, container.get(k)));
		return new Object[]{script, args};
	}
}
