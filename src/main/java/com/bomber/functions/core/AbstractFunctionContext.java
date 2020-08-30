package com.bomber.functions.core;

import static com.bomber.util.ValueReplacer.readReplaceableKeys;
import static com.bomber.util.ValueReplacer.replace;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.util.StringUtils;

import com.bomber.functions.util.MissingArgumentException;

public abstract class AbstractFunctionContext implements FunctionContext {

	protected boolean started = false;

	protected Set<String> readRetKeys() {
		if (function() instanceof StringFunction) {
			return Collections.singleton(name());
		} else if (metadata().isRetAllArgs()) {
			return input().keySet();
		}
		String retArg = metadata().getRetArg();
		if (!StringUtils.isEmpty(retArg)) {
			Set<String> outputKeys = new HashSet<>();
			String arg = metadata().getRetArg();
			if (input().containsKey(arg)) {
				String value = input().get(arg);
				if (StringUtils.isEmpty(value)) {
					throw new MissingArgumentException(metadata().getName(), arg);
				}
				outputKeys.addAll(Arrays.asList(value.split(", *")));
			}
			return outputKeys;
		} else {
			throw new IllegalArgumentException("Unknown function " + metadata().getName() + " ret keys");
		}
	}

	@Override
	public Set<String> dependentKeys() {
		if (input().isEmpty()) {
			return Collections.emptySet();
		}
		return readReplaceableKeys(input().values());
	}

	@Override
	public void fireExecute(Output output) {
		this.invokeExecute(output);
	}

	@SuppressWarnings("unchecked")
	private void invokeExecute(Output output) {
		if (!started) {
			function().init(input());
			started = true;
		}

		Object ret = function().execute(newInput(output));

		if (ret instanceof String) {
			output.put(name(), (String) ret);
		} else if (ret instanceof Map) {
			output.putAll((Map<String, String>) ret);
		}
	}

	@Override
	public void fireJump(int steps) {
		this.invokeJump(steps);
	}

	private void invokeJump(int steps) {
		if (!started) {
			function().init(input());
			started = true;
		}
		function().jump(steps);
	}

	@Override
	public void fireClose() {
		this.invokeClose();
	}

	private void invokeClose() {
		try {
			function().close();
		} catch (Exception e) {
			// TODO log
		}
	}

	protected Input newInput(Output output) {
		if (!input().isEmpty() && !output.isEmpty()) {
			Map<String, String> temp = new HashMap<>();
			input().getAll().forEach((k, v) -> temp.put(k, replace(v, output.getAll())));
			return new Input(temp);
		}
		return input();
	}
}
