package com.bomber.functions.core;

import static com.bomber.util.ValueReplacer.readReplaceableKeys;
import static com.bomber.util.ValueReplacer.replace;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.bomber.functions.util.MissingArgumentException;

public abstract class AbstractFunctionContext implements FunctionContext {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected boolean started = false;

	protected Set<String> readRetKeys() {
		if (function() instanceof StringFunction) {
			return Collections.singleton(name());
		} else if (metadata().isRetAllArgs()) {
			return input().keySet();
		}
		String retArg = metadata().getRetArg();
		if (StringUtils.hasText(retArg)) {
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
		Set<String> result = readReplaceableKeys(input().values());
		result.addAll(customerArgs());
		return result;
	}

	protected Set<String> customerArgs() {
		String customArg = metadata().getCustomArg();
		if (customArg.isEmpty()) {
			return Collections.emptySet();
		} else {
			return Arrays.stream(input().get(customArg).split(", *")).collect(Collectors.toSet());
		}
	}

	@Override
	public void fireInit() {
		if (!started) {
			invokeInit();
			started = true;
		}
	}

	private void invokeInit() {
		function().init(input());
	}

	@Override
	public void fireExecute(Output output) {
		this.invokeExecute(output);
	}

	@SuppressWarnings("unchecked")
	private void invokeExecute(Output output) {
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
		function().jump(steps);
	}

	@Override
	public void fireClose() {
		this.invokeClose();
	}

	private void invokeClose() {
		try {
			function().close();
		} catch (Throwable e) {
			logger.warn("Failed to close a function '" + name() + "'", e);
		}
	}

	protected Input newInput(Output output) {
		if (!input().isEmpty() && !output.isEmpty()) {
			Map<String, String> temp = new HashMap<>();
			customerArgs().forEach(k -> temp.put(k, output.get(k)));
			input().getAll().forEach((k, v) -> temp.put(k, replace(v, output.getAll())));
			return new Input(temp);
		}
		return input();
	}
}
