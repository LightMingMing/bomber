package com.bomber.functions.core;

import static java.util.Objects.requireNonNull;

import java.util.Set;

import com.bomber.functions.util.FunctionHelper;

public class DefaultFunctionContext extends AbstractFunctionContext {

	private final FunctionMetadata metadata;

	private final Function<?> function;

	private final String name;

	private final Set<String> retKeys;

	private final Input input;

	public DefaultFunctionContext(String name, String functionName) {
		this(name, functionName, Input.EMPTY);
	}

	public DefaultFunctionContext(String name, String functionName, String... args) {
		this(name, functionName, new Input(args));
	}

	public DefaultFunctionContext(String name, String functionName, Input input) {
		this(name, FunctionHelper.createQuietly(functionName), input);
	}

	public DefaultFunctionContext(String name, Function<?> function) {
		this(name, function, Input.EMPTY);
	}

	public DefaultFunctionContext(String name, Function<?> function, String... args) {
		this(name, function, new Input(args));
	}

	public DefaultFunctionContext(String name, Function<?> function, Input input) {
		this.name = requireNonNull(name, "name");
		this.input = requireNonNull(input, "input");
		this.function = requireNonNull(function, "function");

		this.metadata = requireNonNull(FunctionHelper.getFunctionMetadata(function), "functionMetadata");
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
	public Function<?> function() {
		return function;
	}

	@Override
	public Input input() {
		return input;
	}

	@Override
	public Set<String> retKeys() {
		return retKeys;
	}
}
