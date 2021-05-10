package com.bomber.function.model;

import java.util.Map;
import java.util.Set;

import com.bomber.function.Function;

public interface FunctionContext {

	String name();

	FunctionMetadata metadata();

	Function function();

	Map<String, String> initParameterValues();

	Set<String> dependentKeys();

	Set<String> retKeys();

	void fireExecute(Map<String, String> container);

	void fireJump(int steps);

}
