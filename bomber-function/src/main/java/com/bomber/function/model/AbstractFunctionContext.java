package com.bomber.function.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;

import com.bomber.common.util.StringReplacer;
import com.bomber.function.Function;
import com.bomber.function.util.MissingArgumentException;

public abstract class AbstractFunctionContext implements FunctionContext {

	protected String name;
	protected Set<String> retKeys;
	protected Map<String, String> initParameterValues;
	protected Function function;
	protected FunctionMetadata metadata;
	protected MethodInvoker methodInvoker;

	protected AbstractFunctionContext(@NonNull String name, @NonNull FunctionMetadata metadata,
									  @NonNull Map<String, String> initParameterValues) {
		this.name = name;
		this.metadata = metadata;
		this.initParameterValues = initParameterValues;
		this.function = metadata.getConstructors().invokeMethod(initParameterValues);
		this.methodInvoker = metadata.getMethodInvoker();
		this.retKeys = readRetKeys();
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public FunctionMetadata metadata() {
		return metadata;
	}

	@Override
	public Function function() {
		return function;
	}

	@Override
	public Map<String, String> initParameterValues() {
		return initParameterValues;
	}

	@Override
	public Set<String> dependentKeys() {
		if (initParameterValues.isEmpty()) {
			return Collections.emptySet();
		}
		Set<String> result = StringReplacer.read(initParameterValues().values());
		result.addAll(customArgs());
		return result;
	}

	@Override
	public Set<String> retKeys() {
		return retKeys;
	}

	protected Set<String> readRetKeys() {
		Map<String, String> parameterValues = initParameterValues();
		if (metadata().isRetAllArgs()) {
			return parameterValues.keySet();
		}
		String retArg = metadata().getRetArg();
		if (retArg.length() > 0) {
			Set<String> outputKeys = new HashSet<>();
			String arg = metadata().getRetArg();
			if (parameterValues.containsKey(arg)) {
				String value = parameterValues.get(arg);
				if (value == null || value.isEmpty()) {
					throw new MissingArgumentException(metadata().getName(), arg);
				}
				outputKeys.addAll(Arrays.asList(value.split(", *")));
			}
			return outputKeys;
		} else {
			return Collections.singleton(name());
		}
	}

	protected Set<String> customArgs() {
		String customArg = metadata().getCustomArg();
		if (customArg.isEmpty()) {
			return Collections.emptySet();
		} else {
			return Arrays.stream(initParameterValues.get(customArg).split(", *")).collect(Collectors.toSet());
		}
	}

	@Override
	public void fireExecute(Map<String, String> container) {
		this.invokeExecute(container);
	}

	@SuppressWarnings("unchecked")
	private void invokeExecute(Map<String, String> container) {
		Object result = methodInvoker.invokeMethod(function, initParameterValues, container);
		if (result instanceof String) {
			container.put(name, (String) result);
		} else if (result instanceof Map) {
			container.putAll((Map<String, String>) result);
		}
	}

	@Override
	public void fireJump(int steps) {
		this.function.jump(steps);
	}

}
