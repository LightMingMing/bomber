package com.bomber.function.model;

import java.util.Collections;
import java.util.Map;

import com.bomber.function.util.FunctionHelper;

public class DefaultFunctionContext extends AbstractFunctionContext {

	public DefaultFunctionContext(String name, String functionName) {
		this(name, FunctionHelper.getFunctionMetadata(functionName));
	}

	public DefaultFunctionContext(String name, String functionName, Map<String, String> parameterValues) {
		this(name, FunctionHelper.getFunctionMetadata(functionName), parameterValues);
	}

	public DefaultFunctionContext(String name, FunctionMetadata metadata) {
		this(name, metadata, Collections.emptyMap());
	}

	public DefaultFunctionContext(String name, FunctionMetadata metadata, Map<String, String> parameterValues) {
		super(name, metadata, parameterValues);
	}
}
